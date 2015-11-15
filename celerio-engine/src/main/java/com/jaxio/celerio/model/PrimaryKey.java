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

package com.jaxio.celerio.model;

import com.jaxio.celerio.support.Namer;

import java.util.List;

public interface PrimaryKey extends Namer {

    boolean isNoPk();

    /**
     * Returns true if the number of attributes is exactly 1.
     */
    boolean isSimple();

    /**
     * Returns true if the number of attributes is greater than 1.
     */
    boolean isComposite();

    /**
     * Returns true if the type is not a simple java type
     */
    boolean isJavaBaseClass();

    /**
     * Returns true if one of its attribute is an imported key
     */
    boolean isImported();

    /**
     * Returns true if the only pk attribute is a date. Composite pk always return false.
     */
    boolean isDate();

    /**
     * Returns true if the only pk attribute is an Enum. Composite pk always return false.
     */
    boolean isEnum();

    /**
     * Return the only attribute when isSimple returns true. Throw an IllegalStateException otherwise.
     */
    Attribute getAttribute();

    /**
     * Return the attributes that form this PrimaryKey.
     */
    List<Attribute> getAttributes();

    /**
     * The entity owning this PrimaryKey.
     */
    Entity getEntity();

    /**
     * Return ".toString()" if this pk is not a string.
     */
    String getToStringMethod();
}