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
import com.jaxio.celerio.configuration.SequencePattern;
import com.jaxio.celerio.configuration.convention.CollectionType;
import com.jaxio.celerio.configuration.convention.Renamer;
import com.jaxio.celerio.configuration.database.Table;
import com.jaxio.celerio.configuration.entity.AssociationDirection;
import com.jaxio.celerio.configuration.entity.EntityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.persistence.InheritanceType;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.jaxio.celerio.configuration.entity.AssociationDirection.UNIDIRECTIONAL;
import static com.jaxio.celerio.factory.ProjectFactory.DEFAULT_ENTITY_ROOTPACKAGE;
import static com.jaxio.celerio.util.FallBackUtil.fallBack;
import static com.jaxio.celerio.util.MiscUtil.toName;

@Service
public class EntityConfigFactory {
    private static final AssociationDirection DEFAULT_ASSOCIATION_DIRECTION = UNIDIRECTIONAL;
    private static final CollectionType DEFAULT_COLLECTION_TYPE = CollectionType.ArrayList;

    @Autowired
    private Config config;

    public List<EntityConfig> filterEntityConfigs(List<EntityConfig> entityConfigs) {
        List<EntityConfig> result = newArrayList();
        for (EntityConfig entityConfig : entityConfigs) {
            if (!entityConfig.hasTableName() || config.getCelerio().getConfiguration().hasTable(entityConfig.getTableName())) {
                result.add(entityConfig);
            }
        }
        return result;
    }

    public void assertEntityConfigListIsConsistent(List<EntityConfig> entityConfigs) {
        Map<String, EntityConfig> entityConfigsByEntityName = newHashMap();

        for (EntityConfig entityConfig : entityConfigs) {
            assertEntityConfigIsValid(entityConfig);
            if (!entityConfig.hasEntityName() && entityConfig.hasTableName()) {
                applyEntityNameFallBack(entityConfig);
            }
            Assert.isTrue(!entityConfigsByEntityName.containsKey(entityConfig.getEntityName().toUpperCase()),
                    "Two entity config cannot have the same entity name: " + entityConfig.getEntityName());
            entityConfigsByEntityName.put(entityConfig.getEntityName().toUpperCase(), entityConfig);
        }

        // please respect the call ordering
        resolveMissingTableNameOnEntityConfigs(entityConfigsByEntityName);
        resolveMissingInheritanceStrategyOnEntityConfigs(entityConfigsByEntityName);
    }

    private void assertEntityConfigIsValid(EntityConfig entityConfig) {
        if (entityConfig.hasTableName()) {
            Table table = config.getMetadata().getTableBySchemaAndName(entityConfig.getSchemaName(), entityConfig.getTableName());
            Assert.notNull(table, "The table named " + entityConfig.getTableName() + " could not be found. Schema: " + entityConfig.getSchemaName());
            // Important: preserve case sensitivity intact (JPA is case sensitive when dealing with TABLES)
            entityConfig.setTableName(table.getName());
        } else {
            Assert.isTrue(entityConfig.hasEntityName(), "One of your entity has no tableName and no entityName");
            Assert.isTrue(entityConfig.hasParentEntityName(), "You must also set a tableName or a parentEntityName for entity " + entityConfig.getEntityName());
            Assert.isTrue(!entityConfig.getParentEntityName().equalsIgnoreCase(entityConfig.getEntityName()), "The entity " + entityConfig.getEntityName()
                    + " inherits from itself! Please fix your configuration.");
        }
    }

    private void resolveMissingTableNameOnEntityConfigs(Map<String, EntityConfig> entityConfigsByEntityName) {
        for (EntityConfig entityConfig : entityConfigsByEntityName.values()) {
            if (entityConfig.hasTableName()) {
                continue;
            }

            EntityConfig current = entityConfig;
            while (!current.hasTableName()) {
                current = entityConfigsByEntityName.get(current.getParentEntityName().toUpperCase());
                Assert.notNull(current, "The parent entity " + current.getParentEntityName() + " could not be found in the configuration.");
            }
            entityConfig.setCatalog(current.getCatalog());
            entityConfig.setSchemaName(current.getSchemaName());
            entityConfig.setTableName(current.getTableName());
        }
    }

    private void resolveMissingInheritanceStrategyOnEntityConfigs(Map<String, EntityConfig> entityConfigsByEntityName) {
        for (EntityConfig entityConfig : entityConfigsByEntityName.values()) {
            if (!entityConfig.hasInheritance()) {
                continue;
            }

            EntityConfig current = entityConfig;
            while (current.hasParentEntityName()) {
                current = entityConfigsByEntityName.get(current.getParentEntityName().toUpperCase());
                Assert.notNull(current, "The parent entity " + current.getParentEntityName() + " could not be found in the configuration.");
            }
            // root may use default...
            if (!current.getInheritance().hasStrategy()) {
                // default...
                current.getInheritance().setStrategy(InheritanceType.SINGLE_TABLE);
            }

            if (entityConfig.getInheritance().hasStrategy()) {
                Assert.isTrue(
                        entityConfig.getInheritance().getStrategy() == current.getInheritance().getStrategy(),
                        "The entityConfig " + entityConfig.getEntityName()
                                + " must not declare an inheritance strategy that is different from the strategy declared in the root entity "
                                + current.getEntityName());
            }

            // for internal convenient purposes we propagate it
            entityConfig.getInheritance().setStrategy(current.getInheritance().getStrategy());
        }
    }

    public EntityConfig buildEntityConfig(Table table) {
        EntityConfig entityConfig = new EntityConfig();
        entityConfig.setCatalog(table.getCatalog());
        entityConfig.setSchemaName(table.getSchemaName());
        entityConfig.setTableName(table.getName());
        applyFallBacks(entityConfig);
        return entityConfig;
    }

    protected String getDefaultEntityName(Table table) {
        for (Renamer renamer : config.getCelerio().getConfiguration().getConventions().getTableRenamers()) {
            if (renamer.match(table.getName())) {
                return toName(renamer.rename(table.getName()));
            }
        }
        return toName(table.getName());
    }

    private void applyEntityNameFallBack(EntityConfig entityConfig) {
        Table table = config.getMetadata().getTableBySchemaAndName(entityConfig.getSchemaName(), entityConfig.getTableName());
        entityConfig.setEntityName(fallBack(entityConfig.getEntityName(), getDefaultEntityName(table)));
    }

    public void applyFallBacks(EntityConfig entityConfig) {
        Configuration conf = config.getCelerio().getConfiguration();
        Table table = config.getMetadata().getTableBySchemaAndName(entityConfig.getSchemaName(), entityConfig.getTableName());
        Assert.notNull(table, "The table named " + entityConfig.getTableName() + " could not be found");

        applyEntityNameFallBack(entityConfig);
        entityConfig.setCatalog(fallBack(entityConfig.getCatalog(), table.getCatalog()));
        entityConfig.setSchemaName(fallBack(entityConfig.getSchemaName(), table.getSchemaName()));
        entityConfig.setComment(fallBack(entityConfig.getComment(), table.getRemarks()));
        entityConfig.setRootPackage(fallBack(entityConfig.getRootPackage(), conf.getRootPackage(), DEFAULT_ENTITY_ROOTPACKAGE));
        entityConfig.setAssociationDirection(fallBack(entityConfig.getAssociationDirection(), conf.getAssociationDirection(), DEFAULT_ASSOCIATION_DIRECTION));
        entityConfig.setCollectionType(fallBack(entityConfig.getCollectionType(), conf.getConventions().getCollectionType(), DEFAULT_COLLECTION_TYPE));
        entityConfig.setSequenceName(fallBack(entityConfig.getSequenceName(), getSequenceNameFromGlobalConfigIfNeeded(table)));
    }

    private String getSequenceNameFromGlobalConfigIfNeeded(Table table) {
        List<SequencePattern> sequences = config.getCelerio().getConfiguration().getSequences();
        if (sequences == null || sequences.isEmpty()) {
            return null;
        }

        for (SequencePattern sequence : sequences) {
            if (sequence.match(table.getName())) {
                return sequence.getSequenceName().replace("{TABLE_NAME}", table.getName());
            }
        }

        return null;
    }
}
