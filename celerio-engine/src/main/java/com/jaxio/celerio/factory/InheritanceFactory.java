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
import com.jaxio.celerio.configuration.entity.EntityConfig;
import com.jaxio.celerio.model.Entity;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.persistence.InheritanceType;

@Log
@Service
public class InheritanceFactory {

    @Autowired
    private Config config;

    public void wireEntityHierarchies() {
        // wire the entities parent/child
        for (Entity entity : config.getProject().getCurrentEntities()) {
            if (entity.hasParentEntityName()) {
                Entity parentEntity = config.getProject().getEntityByName(entity.getParentEntityName());
                Assert.isTrue(!parentEntity.equals(entity), "The entity " + entity.getName() + " inherits from itself! Please fix your configuration.");
                entity.setParent(parentEntity);
            }
        }

        putEntityByTableNameForEntityWithInheritance();
    }

    private void putEntityByTableNameForEntityWithInheritance() {
        // Attention, for SINGLE_TABLE inheritance strategy, we only put the root entity.

        for (EntityConfig entityConfig : config.getCelerio().getEntityConfigs()) {
            Entity entity = config.getProject().getEntityByName(entityConfig.getEntityName());

            if (entity.hasInheritance() && !config.getProject().hasEntityBySchemaAndTableName(entity.getTable().getSchemaName(), entity.getTable().getName())) {
                InheritanceType inheritanceType = entity.getInheritance().getStrategy();

                if (inheritanceType == InheritanceType.SINGLE_TABLE) {
                    if (entity.isRoot()) {
                        config.getProject().putEntity(entity);
                    }
                } else if (inheritanceType == InheritanceType.JOINED || inheritanceType == InheritanceType.TABLE_PER_CLASS) {
                    config.getProject().putEntity(entity);
                } else {
                    log.warning("Invalid case, there should be an inheritance type");
                }
            }
        }
    }
}