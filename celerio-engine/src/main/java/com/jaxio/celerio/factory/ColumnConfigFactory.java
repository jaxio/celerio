/*
 * Copyright 2015 JAXIO http://www.jaxio.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jaxio.celerio.factory;

import com.jaxio.celerio.Config;
import com.jaxio.celerio.configuration.Configuration;
import com.jaxio.celerio.configuration.MetaAttribute;
import com.jaxio.celerio.configuration.convention.Renamer;
import com.jaxio.celerio.configuration.database.Column;
import com.jaxio.celerio.configuration.database.JdbcType;
import com.jaxio.celerio.configuration.database.Table;
import com.jaxio.celerio.configuration.entity.*;
import com.jaxio.celerio.model.Entity;
import com.jaxio.celerio.util.MiscUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import static com.jaxio.celerio.factory.ProjectFactory.DEFAULT_ENTITY_ROOTPACKAGE;
import static com.jaxio.celerio.util.FallBackUtil.fallBack;
import static com.jaxio.celerio.util.MiscUtil.toName;
import static com.jaxio.celerio.util.MiscUtil.toVar;
import static com.jaxio.celerio.util.StringUtil.getFirstCharacterLowered;
import static com.jaxio.celerio.util.StringUtil.getFirstCharacterUppered;

@Slf4j
@Service
public class ColumnConfigFactory {
    private static final AssociationDirection DEFAULT_ASSOCIATION_DIRECTION = AssociationDirection.UNIDIRECTIONAL;
    private static final boolean DEFAULT_ENABLE_ONE_TO_VITUAL_ONE = false;

    @Autowired
    private Config config;
    @Autowired
    private EnumCollisionUtil enumCollisionUtil;

    public ColumnConfig buildColumnConfig(Entity entity, Table table, Column column) {
        ColumnConfig columnConfig = new ColumnConfig();
        columnConfig.setColumnName(column.getName());
        applyFallBacks(columnConfig, table, column);
        buildEnum(entity, columnConfig, column);
        return columnConfig;
    }

    public void applyFallBacks(ColumnConfig columnConfig, Table table, Column column) {
        Configuration conf = config.getCelerio().getConfiguration();

        columnConfig.setType(fallBack(columnConfig.getType(), getTypeFromMetaAttributes(column), column.getType()));
        columnConfig.setFieldName(fallBack(getFirstCharacterLowered(columnConfig.getFieldName()), getDefaultFieldName(column)));

        // Important: preserve case sensitivity intact (JPA is case sensitive when dealing with TABLE or COLUMNS)
        columnConfig.setTableName(table.getName());
        columnConfig.setColumnName(column.getName());

        columnConfig.setColumnName(fallBack(columnConfig.getColumnName(), column.getName()));
        columnConfig.setSize(fallBack(columnConfig.getSize(), column.getSize()));
        columnConfig.setOrdinalPosition(fallBack(columnConfig.getOrdinalPosition(), column.getOrdinalPosition()));
        columnConfig.setDisplayOrder(fallBack(columnConfig.getDisplayOrder(), columnConfig.getOrdinalPosition(), column.getOrdinalPosition()));
        columnConfig.setFormFieldOrder(fallBack(columnConfig.getFormFieldOrder(), columnConfig.getDisplayOrder(), columnConfig.getOrdinalPosition(), column.getOrdinalPosition()));
        columnConfig.setSearchFieldOrder(fallBack(columnConfig.getSearchFieldOrder(), columnConfig.getDisplayOrder(), columnConfig.getOrdinalPosition(), column.getOrdinalPosition()));
        columnConfig.setSearchResultOrder(fallBack(columnConfig.getSearchResultOrder(), columnConfig.getDisplayOrder(), columnConfig.getOrdinalPosition(), column.getOrdinalPosition()));

        // typeConverter: no fallback
        // pointsTo: no fallback (TODO ?)
        columnConfig.setComment(fallBack(columnConfig.getComment(), column.getRemarks()));
        columnConfig.setDecimalDigits(fallBack(columnConfig.getDecimalDigits(), column.getDecimalDigits()));
        columnConfig.setDefaultValue(fallBack(columnConfig.getDefaultValue(), column.getColumnDef()));
        // messageKey: done at runtime
        // isInverse: TODO
        // isFormField: TODO

        columnConfig.setAssociationDirection(fallBack(columnConfig.getAssociationDirection(), conf.getAssociationDirection(), DEFAULT_ASSOCIATION_DIRECTION));
        columnConfig.setEnableOneToVirtualOne(fallBack(columnConfig.getEnableOneToVirtualOne(), conf.getEnableOneToVirtualOne(),
                DEFAULT_ENABLE_ONE_TO_VITUAL_ONE));
        columnConfig.setAutoIncrement(fallBack(columnConfig.getAutoIncrement(), column.getAutoIncrement()));
        columnConfig.setNullable(fallBack(columnConfig.getNullable(), column.isNullable()));
        // isSearchField: TODO
        // isSearchResult: TODO
        // isSelectLabel: TODO

        columnConfig.setUnique(fallBack(columnConfig.getUnique(), table.isUnique(column.getName())));
        // isVersion: TODO
        // isVisible: TODO
        // isPassword: TODO
    }

    private JdbcType getTypeFromMetaAttributes(Column column) {
        if (!column.hasMetaAttribute()) {
            return null;
        }
        for (MetaAttribute attribute : column.getMetaAttributes()) {
            if ("type".equals(attribute.getName()) && "year(4)".equals(attribute.getValue())) {
                return JdbcType.SMALLINT;
            }
        }
        return null;
    }

    protected String getDefaultFieldName(Column c) {
        for (Renamer renamer : config.getCelerio().getConfiguration().getConventions().getColumnRenamers()) {
            if (renamer.match(c.getName())) {
                return toVar(renamer.rename(c.getName()));
            }
        }
        return toVar(c.getName());
    }

    public void buildEnum(Entity entity, ColumnConfig columnConfig, Column column) {
        if (column.hasEnum()) {
            buildDefaultEnumConfig(entity, columnConfig, column);
        }
        if (columnConfig.hasEnumConfig()) {
            buildEnumWithEnumConfig(entity, columnConfig);
        } else if (columnConfig.hasSharedEnum()) {
            associateSharedEnum(columnConfig);
        }
    }

    private void buildDefaultEnumConfig(Entity entity, ColumnConfig columnConfig, Column column) {
        EnumConfig enumConfig = new EnumConfig();
        if (columnConfig.getEnumConfig() != null) {
            enumConfig = columnConfig.getEnumConfig();
        }
        EnumType enumType = columnConfig.getType().isStringOrChar() ? EnumType.STRING : EnumType.ORDINAL;
        for (String value : column.getEnumValues()) {
            String name = MiscUtil.toName(value);
            if (!name.equals(value)) {
                enumType = EnumType.CUSTOM;
            }
            enumConfig.addEnumValue(new EnumValue(name, value));
        }
        enumConfig.setType(enumType);
    }

    private void associateSharedEnum(ColumnConfig columnConfig) {
        String sharedEnumName = columnConfig.getSharedEnumName();
        Validate.isTrue(config.getCelerio().hasSharedEnumConfig(sharedEnumName), "Shared enum " + sharedEnumName + " does not exist");
        columnConfig.setEnumConfig(config.getCelerio().getSharedEnumConfigByName(sharedEnumName));
    }

    private void buildEnumWithEnumConfig(Entity entity, ColumnConfig columnConfig) {
        EnumConfig enumConfig = columnConfig.getEnumConfig();

        Configuration conf = config.getCelerio().getConfiguration();
        enumConfig.setType(fallBack(enumConfig.getType(), columnConfig.getType().isStringOrChar() ? EnumType.STRING : EnumType.ORDINAL));
        String name = enumCollisionUtil.getClashSafeName(getFirstCharacterUppered(fallBack(enumConfig.getName(), getDefaultEnumClassName(columnConfig))));
        enumConfig.setName(name);
        enumConfig.setRootPackage(fallBack(enumConfig.getRootPackage(), entity.getEntityConfig().getRootPackage(), conf.getRootPackage(),
                DEFAULT_ENTITY_ROOTPACKAGE));
        enumConfig.setSubPackage(fallBack(enumConfig.getSubPackage(), entity.getEntityConfig().getSubPackage()));

        for (EnumValue ev : enumConfig.getEnumValues()) {
            Assert.isTrue(ev.hasValue(), "The 'value' attribute is mandatory for the enumConfig " + enumConfig.getName());
            ev.setName(fallBack(ev.getName(), ev.getValue()));

            if (!enumConfig.isCustomType()) {
                ev.setValue(null);
            }

            // fix for evdev
            if (NumberUtils.isNumber(ev.getName().substring(0, 1))) {
                ev.setName("TODO_" + ev.getName());
                log.warn("Please review your enum " + enumConfig.getName() + " configuration. Some constant are numeric!");
            }
        }
    }

    private String getDefaultEnumClassName(ColumnConfig columnConfig) {
        return toName(columnConfig.getFieldName());
    }
}
