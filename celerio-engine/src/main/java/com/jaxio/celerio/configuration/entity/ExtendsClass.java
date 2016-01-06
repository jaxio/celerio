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

package com.jaxio.celerio.configuration.entity;

import lombok.Setter;

import static org.springframework.util.StringUtils.hasLength;

public class ExtendsClass {

    @Setter
    private String fullType;

    /*
     * The full class name that this entity extends. For example 'com.mycompany.MyClass'. This is taken into account only if the entity is a root entity.
     */
    public String getFullType() {
        return fullType;
    }

    public boolean hasFullType() {
        return hasLength(fullType);
    }
}