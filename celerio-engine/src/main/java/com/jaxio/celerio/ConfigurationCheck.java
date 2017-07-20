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

package com.jaxio.celerio;

import com.jaxio.celerio.configuration.Celerio;
import com.jaxio.celerio.configuration.database.Column;
import com.jaxio.celerio.configuration.database.Metadata;
import com.jaxio.celerio.configuration.database.Table;
import com.jaxio.celerio.configuration.entity.ColumnConfig;
import com.jaxio.celerio.configuration.entity.EntityConfig;
import com.jaxio.celerio.configuration.entity.EnumType;
import com.jaxio.celerio.model.Attribute;
import com.jaxio.celerio.model.AttributePair;
import com.jaxio.celerio.model.Entity;
import com.jaxio.celerio.model.Relation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.sort;
import static javax.persistence.InheritanceType.JOINED;

/**
 * recursively checks for null values
 */
@Service
@Slf4j
public class ConfigurationCheck {

    @SuppressWarnings("rawtypes")
    public Class[] classesToSkip = {java.util.HashMap.class};

    public boolean check(Config config) {
        List<String> errors = newArrayList();
        checkConfigVsMetadataConsistency(errors, config);
        checkOrdinalEnums(errors, config);
        checkVersionOnJoinedInheritance(errors, config);
        checkForeignKeyMapping(errors, config);
        dumpErrors(errors);
        return errors.isEmpty();
    }

    private void checkConfigVsMetadataConsistency(List<String> errors, Config config) {
        Celerio celerio = config.getCelerio();
        Metadata metadata = config.getMetadata();

        for (EntityConfig ec : celerio.getEntityConfigs()) {
            String schemaName = ec.getSchemaName();
            String tableName = ec.getTableName();

            if (checkTablePresence(ec.getEntityName(), errors, metadata, schemaName, tableName)) {
                for (ColumnConfig cc : ec.getColumnConfigs()) {
                    if (!cc.hasTrueIgnore()) {
                        checkColumnPresence(ec.getEntityName(), errors, metadata, cc);
                    }
                }
            }
        }
    }

    private void checkOrdinalEnums(List<String> errors, Config config) {
        for (Entity entity : config.getProject().getEntities().getList()) {
            for (Attribute attribute : entity.getAttributes().getList()) {
                if (attribute.isEnum() && attribute.getEnumConfig().isOrdinal() && !attribute.getMappedType().isNumeric()) {
                    errors.add(attribute.getFullColumnName() + " is not numeric, but it is mapped on enum " + attribute.getEnumModel().getFullType()
                            + " which is " + EnumType.ORDINAL.name());
                }
            }
        }
    }

    /**
     * Check that foreign keys have the same mapped type as the target.
     */
    private void checkForeignKeyMapping(List<String> errors, Config config) {
        for (Entity entity : config.getProject().getEntities().getList()) {
            for (Relation relation : entity.getRelations().getList()) {
                if (relation.isInverse() || relation.isIntermediate()) {
                    continue;
                }

                for (AttributePair attributePair : relation.getAttributePairs()) {
                    if (attributePair.getFromAttribute().getMappedType() != attributePair.getToAttribute().getMappedType()) {
                        String errorMsg = "Inconsistent types: Column " + attributePair.getFromAttribute().getFullColumnName() + "["
                                + attributePair.getFromAttribute().getJdbcType() + "] references column " + attributePair.getToAttribute().getFullColumnName()
                                + "[" + attributePair.getToAttribute().getJdbcType() + "]. " + "You should really fix your SQL schema.";
                        if (attributePair.getFromAttribute().isInCpk()) {
                            // we may get compile failure as the property is mapped (inside the cpk)
                            log.warn(errorMsg + ". To avoid this error you can force the mapped type using configuration.");
                        } else {
                            // we should not get compile failure as the property is not mapped.
                            log.warn(errorMsg);
                        }
                    }
                }
            }
        }
    }

    /**
     * In case of JOINED inheritance we may have added some columns that are in the table but that are not in the entityConfigs. We check here that we have not
     * added a version column in a child.
     *
     * @param errors
     * @param config
     */
    private void checkVersionOnJoinedInheritance(List<String> errors, Config config) {
        for (Entity entity : config.getProject().getRootEntities().getList()) {
            if (entity.hasInheritance() && entity.getInheritance().is(JOINED)) {
                for (Entity child : entity.getAllChildrenRecursive()) {
                    for (Attribute attribute : child.getAttributes().getList()) {
                        if (attribute.isVersion()) {
                            errors.add(attribute.getFullColumnName() + " is a version column, you should not have @Version in a child joined entity."
                                    + " Use ignore=true in columnConfig or remove it from your table.");
                        }
                    }
                }
            }
        }
    }

    private boolean checkTablePresence(String entityName, List<String> errors, Metadata metadata, String schemaName, String tableName) {
        Table table = metadata.getTableBySchemaAndName(schemaName, tableName);
        if (table == null) {
            String errorMsg = "Entity '"
                    + entityName
                    + "': The table '"
                    + tableName
                    + "' used in Celerio configuration could not be found. "
                    + "Please check that 1) this table is present in your database. 2) you have reversed this table. 3) this table is not not filtered out in your configuration.";
            errors.add(errorMsg);
            if (log.isDebugEnabled()) {
                log.debug(errorMsg, new Exception());
            }
            return false;
        }

        return true;
    }

    private boolean checkColumnPresence(String entityName, List<String> errors, Metadata metadata, ColumnConfig cc) {
        Table table = metadata.getTableByName(cc.getTableName()); // the field may come from a different table (ie secondary table)
        if (table == null) {
            String errorMsg = "Entity '"
                    + entityName
                    + "."
                    + cc.getFieldName()
                    + "': The table '"
                    + cc.getTableName()
                    + "' used in Celerio configuration could not be found. "
                    + "Please check that 1) this table is present in your database. 2) you have reversed this table. 3) this table is not not filtered out in your configuration.";

            errors.add(errorMsg);
            if (log.isDebugEnabled()) {
                log.debug(errorMsg, new Exception());
            }
            return false;
        }

        Column column = table.getColumnByName(cc.getColumnName());
        if (column == null) {
            String errorMsg = "Entity '"
                    + entityName
                    + "': The table.column '"
                    + cc.getTableName()
                    + "."
                    + cc.getColumnName()
                    + "' used in Celerio configuration could not be found. "
                    + "Please check that 1) this table.column is present in your database. 2) you have reversed this table. 3) this table is not not filtered out in your configuration.";

            errors.add(errorMsg);
            if (log.isDebugEnabled()) {
                log.debug(errorMsg, new Exception());
            }
            return false;
        }
        return true;
    }

    private void dumpErrors(List<String> errors) {
        sort(errors);
        for (String error : errors) {
            log.error(error);
        }
    }
}
