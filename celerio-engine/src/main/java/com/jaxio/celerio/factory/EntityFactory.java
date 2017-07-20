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
import com.jaxio.celerio.configuration.CelerioTemplateContext;
import com.jaxio.celerio.configuration.Configuration;
import com.jaxio.celerio.configuration.EntityContextProperty;
import com.jaxio.celerio.configuration.database.Column;
import com.jaxio.celerio.configuration.database.Table;
import com.jaxio.celerio.configuration.database.TableType;
import com.jaxio.celerio.configuration.entity.ColumnConfig;
import com.jaxio.celerio.configuration.entity.EntityConfig;
import com.jaxio.celerio.model.Attribute;
import com.jaxio.celerio.model.Entity;
import com.jaxio.celerio.model.support.ClassNamer2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import static javax.persistence.InheritanceType.*;

@Service
public class EntityFactory {

    @Autowired
    private Config config;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private EntityConfigFactory entityConfigFactory;

    @Autowired
    private ColumnConfigFactory columnConfigFactory;

    @Autowired
    private AttributeFactory attributeFactory;

    // -------------------------------------------
    // Public service
    // -------------------------------------------

    public Entity buildEntity(EntityConfig entityConfig) {
        Entity entity = applicationContext.getBean(Entity.class);
        entityConfigFactory.applyFallBacks(entityConfig);
        entity.setEntityConfig(entityConfig);

        Table table = getTableStrict(entityConfig);
        if (table.getType() == TableType.VIEW) {
            entity.setView(true);
        }
        entity.setTable(table);

        namerDefault(entity);
        namerExtension(entity, config.getCelerio().getConfiguration());

        if (entityConfig.hasInheritance()) {
            if (entityConfig.is(JOINED)) {
                buildEntityInvolvedWithJoinedInheritance(entityConfig, entity);
            } else if (entityConfig.is(SINGLE_TABLE)) {
                buildEntityInvolvedWithSingleTableInheritance(entityConfig, entity);
            } else if (entityConfig.is(TABLE_PER_CLASS)) {
                buildEntityInvolvedWithTablePerClassInheritance(entityConfig, entity);
            } else {
                throw new IllegalStateException("An inheritance strategy should have been found in entity (or its ancestor) " + entityConfig.getEntityName());
            }
        } else {
            buildSimpleEntity(entityConfig, entity);
        }
        return entity;
    }

    public void setupAttributes(Entity entity) {
        for (Attribute attribute : entity.getCurrentAttributes()) {
            attributeFactory.setup(attribute);
        }
    }

    // -------------------------------------------
    // Implementation details
    // -------------------------------------------

    /**
     * Build simple entity using user configuration
     */
    private void buildSimpleEntity(EntityConfig entityConfig, Entity entity) {
        Table table = entity.getTable();

        // 1st phase: process all column and look for corresponding columnConfig
        for (Column column : table.getColumns()) {
            boolean processed = processColumnUsingColumnConfigIfAny(entityConfig, entity, column);

            // no columnConfig, we just use defaults
            if (!processed) {
                entity.addAttribute(attributeFactory.build(entity, table, column));
            }
        }

        // detect invalid column and jpa secondary case
        processInvalidColumnConfigAndJpaSecondaryTable(entityConfig, entity);
    }

    private void buildEntityInvolvedWithJoinedInheritance(EntityConfig entityConfig, Entity entity) {
        Table table = entity.getTable();
        String pk = table.getPrimaryKey();

        // 1st phase: process all column and look for corresponding columnConfig
        for (Column column : table.getColumns()) {
            if (entity.hasParentEntityName() && pk.equalsIgnoreCase(column.getName())) {
                // Assume PK has same name as in parent entity ==> we should not map it.
                // TODO: more complex case where pk's name is different in child entity.
                continue;
            }

            boolean processed = processColumnUsingColumnConfigIfAny(entityConfig, entity, column);
            // no columnConfig, we just use defaults
            if (!processed) {
                entity.addAttribute(attributeFactory.build(entity, table, column));
            }
        }

        // detect invalid column and jpa secondary case
        processInvalidColumnConfigAndJpaSecondaryTable(entityConfig, entity);
    }

    private void buildEntityInvolvedWithSingleTableInheritance(EntityConfig entityConfig, Entity entity) {
        Table table = entity.getTable();

        // 1st phase: process all column and look for corresponding columnConfig
        for (Column column : table.getColumns()) {
            processColumnUsingColumnConfigIfAny(entityConfig, entity, column);
            // if processColumnUsingColumnConfigIfAny returned false, we do not
            // do anything as the column is certainly part of another entity, but we cannot guess which one.
        }

        // detect invalid column and jpa secondary case
        processInvalidColumnConfigAndJpaSecondaryTable(entityConfig, entity);
    }

    private void buildEntityInvolvedWithTablePerClassInheritance(EntityConfig entityConfig, Entity entity) {
        Table table = entity.getTable();
        String pk = table.getPrimaryKey();

        // 1st phase: process all column and look for corresponding columnConfig
        for (Column column : table.getColumns()) {
            if (entity.hasParentEntityName() && pk.equalsIgnoreCase(column.getName())) {
                // Assume PK has same name as in parent entity ==> we should not map it.
                // TODO: more complex case where pk's name is different in child entity.
                continue;
            }

            processColumnUsingColumnConfigIfAny(entityConfig, entity, column);
            // if processColumnUsingColumnConfigIfAny returned false, we do not
            // do anything as the column is certainly part of another entity.
        }

        // detect invalid column and jpa secondary case
        processInvalidColumnConfigAndJpaSecondaryTable(entityConfig, entity);
    }

    // -------------------------------
    // Commons
    // -------------------------------

    private Table getTableStrict(EntityConfig entityConfig) {
        Assert.isTrue(entityConfig.hasTableName(), "A tableName is expected for the entityConfig " + entityConfig.getEntityName());
        Table table = config.getMetadata().getTableBySchemaAndName(entityConfig.getSchemaName(), entityConfig.getTableName());
        Assert.notNull(table, "Could not find table named " + entityConfig.getTableName());
        return table;
    }

    private boolean processColumnUsingColumnConfigIfAny(EntityConfig entityConfig, Entity entity, Column column) {
        ColumnConfig columnConfig = entityConfig.getColumnConfigByColumnName(column.getName());
        if (columnConfig != null) {
            if (columnConfig.hasTrueIgnore()) {
                // just skip it
                return true;
            } else {
                columnConfigFactory.applyFallBacks(columnConfig, entity.getTable(), column);
                columnConfigFactory.buildEnum(entity, columnConfig, column);
                entity.addAttribute(attributeFactory.build(entity, columnConfig));
                return true;
            }
        }
        return false;
    }

    private void processInvalidColumnConfigAndJpaSecondaryTable(EntityConfig entityConfig, Entity entity) {
        for (ColumnConfig cc : entityConfig.getColumnConfigs()) {
            if (cc.hasTrueIgnore()) {
                continue;
            }

            // 2d phase: We may have some invalid columnConfig in our conf (not detected in 1st phase above),
            // for example column config that binds to unknown column.
            // We can detect them if their tableName is null. Indeed not null table name
            // means it was processed in the 1st phase above or it is a secondary table (handled in 3d phase below)
            if (cc.getTableName() == null) {
                throw new IllegalArgumentException("The entityConfig '" + entityConfig.getEntityName()
                        + "' has a columnConfig that refers to the following unknown column: '" + cc.getColumnName()
                        + "'. Please make sure that this column exists and has been reversed. Code Generation aborted.");
            }

            // skip same table (was processed in the 1st phase)
            if (entity.getTableName().equalsIgnoreCase(cc.getTableName())) {
                continue;
            }

            // 3d phase: process columnConfigs that belong to another table (jpa secondary table feature)
            Table secondaryTable = entity.getConfig().getMetadata().getTableByName(cc.getTableName());
            Assert.notNull(secondaryTable, "Could not find the table " + cc.getTableName() + ". Is it ignored? Has it been reversed?"
                    + "Please review the columnConfig " + cc.getColumnName() + " in entity " + entityConfig.getEntityName());

            Column secondaryColumn = secondaryTable.getColumnByName(cc.getColumnName());
            Assert.notNull(secondaryColumn, "Could not find the column " + cc.getColumnName() + ". Is it ignored? Has it been reversed?"
                    + "Please review the columnConfig " + cc.getColumnName() + " in entity " + entityConfig.getEntityName());

            columnConfigFactory.applyFallBacks(cc, secondaryTable, secondaryColumn);
            columnConfigFactory.buildEnum(entity, cc, secondaryColumn);
            entity.addAttribute(attributeFactory.build(entity, cc));
        }
    }

    /**
     * Namers declared here can be overridden easily through configuration.
     *
     * @param entity
     */
    private void namerDefault(Entity entity) {
        entity.put("searchForm", new ClassNamer2(entity, null, "web.domain", null, "SearchForm"));
        entity.put("editForm", new ClassNamer2(entity, null, "web.domain", null, "EditForm"));
        entity.put("graphLoader", new ClassNamer2(entity, null, "web.domain", null, "GraphLoader"));
        entity.put("controller", new ClassNamer2(entity, null, "web.domain", null, "Controller"));
        entity.put("excelExporter", new ClassNamer2(entity, null, "web.domain", null, "ExcelExporter"));
        entity.put("lazyDataModel", new ClassNamer2(entity, null, "web.domain", null, "LazyDataModel"));
        entity.put("fileUpload", new ClassNamer2(entity, null, "web.domain", null, "FileUpload"));
        entity.put("fileDownload", new ClassNamer2(entity, null, "web.domain", null, "FileDownload"));
    }

    private void namerExtension(Entity entity, Configuration configuration) {
        CelerioTemplateContext celerioContext = configuration.getCelerioTemplateContext();
        if (celerioContext == null || celerioContext.getEntityContextProperties() == null || celerioContext.getEntityContextProperties().size() == 0) {
            return;
        }

        for (EntityContextProperty ecp : celerioContext.getEntityContextProperties()) {
            entity.put(ecp.getProperty(), new ClassNamer2(entity, ecp.getRootPackage(), ecp.getSubPackage(), ecp.getPrefix(), ecp.getSuffix()));
        }
    }
}