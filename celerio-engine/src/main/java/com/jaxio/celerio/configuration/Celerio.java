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

package com.jaxio.celerio.configuration;

import com.jaxio.celerio.configuration.entity.EntityConfig;
import com.jaxio.celerio.configuration.entity.EnumConfig;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.jaxio.celerio.configuration.Util.firstNonNull;
import static com.jaxio.celerio.configuration.Util.nonNull;

public class Celerio {
    private List<Include> includes = newArrayList();
    private Configuration configuration = new Configuration();
    private List<EntityConfig> entityConfigs = newArrayList();
    private List<EnumConfig> sharedEnumConfigs = newArrayList();

    /*
     * For large projects, you can split the content of the entityConfigs tag into multiple files and 'include' the files here.
     */
    public List<Include> getIncludes() {
        return includes;
    }

    public void setIncludes(List<Include> includes) {
        this.includes = nonNull(includes);
    }

    /*
     * Configure the celerio generator, such as conventions, jdbc connectivity, and other
     */
    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = firstNonNull(configuration, this.configuration);
    }

    // ------------------
    // EntityConfig
    // ------------------

    /*
     * Configure the generated entities.
     */
    public List<EntityConfig> getEntityConfigs() {
        return entityConfigs;
    }

    public void addSecondaryEntityConfigs(List<EntityConfig> secondaryEntityConfigs) {
        entityConfigs.addAll(secondaryEntityConfigs);
    }

    public EntityConfig getEntityConfigByEntityName(String entityName) {
        for (EntityConfig entityConfig : entityConfigs) {
            if (entityName.equals(entityConfig.getEntityName())) {
                return entityConfig;
            }
        }
        return null;
    }

    // TODO: there may be several entities per table!!!
    public EntityConfig getEntityConfigByTableName(String tableName) {
        for (EntityConfig entityConfig : entityConfigs) {
            if (tableName.equalsIgnoreCase(entityConfig.getTableName())) {
                return entityConfig;
            }
        }
        return null;
    }

    public void setEntityConfigs(List<EntityConfig> entityConfigs) {
        this.entityConfigs = nonNull(entityConfigs);
    }

    // ------------------
    // EnumConfig
    // ------------------

    /*
     * Configure enums that will be used in multiple entities, and referenced by their name in ColumnConfig
     */
    public List<EnumConfig> getSharedEnumConfigs() {
        return sharedEnumConfigs;
    }

    public void setSharedEnumConfigs(List<EnumConfig> sharedEnumConfigs) {
        this.sharedEnumConfigs = nonNull(sharedEnumConfigs);
    }

    public boolean hasSharedEnumConfig(String enumName) {
        return getSharedEnumConfigByName(enumName) != null;
    }

    public EnumConfig getSharedEnumConfigByName(String enumName) {
        for (EnumConfig enumConfig : sharedEnumConfigs) {
            if (enumConfig.getName().equals(enumName)) {
                return enumConfig;
            }
        }
        return null;
    }
}
