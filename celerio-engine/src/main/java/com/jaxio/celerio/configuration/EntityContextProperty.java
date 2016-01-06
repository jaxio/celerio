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

import lombok.Setter;

/**
 * Used to introduce a new 'namer' property on each Celerio's entity context instance. Entity namer properties are convenient when writing template for classes
 * that support entities, for example: Controller, Service, Dao, Validator etc... example: var name is obtained with ${entity.property.var} and has the value
 * "prefixEntityNameSuffix".
 */
@Setter
public class EntityContextProperty {
    private String property;
    private String rootPackage;
    private String subPackage;
    private String prefix;
    private String suffix;

    /*
     * The property name.
     */
    public String getProperty() {
        return property;
    }

    /*
     * The root package. Defaults to the globally defined root package.
     */
    public String getRootPackage() {
        return rootPackage;
    }

    /*
     * The sub package. For example: "security", "web.security". No defaults.
     */
    public String getSubPackage() {
        return subPackage;
    }

    /*
     * Prefix prepended to the entity name.
     */
    public String getPrefix() {
        return prefix;
    }

    /*
     * Prefix appended to the entity name.
     */
    public String getSuffix() {
        return suffix;
    }
}
