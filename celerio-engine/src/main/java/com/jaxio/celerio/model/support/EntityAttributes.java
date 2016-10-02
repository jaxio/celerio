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

package com.jaxio.celerio.model.support;

import com.google.common.base.Predicate;
import com.jaxio.celerio.model.Attribute;
import com.jaxio.celerio.model.AttributeOrder;
import com.jaxio.celerio.model.Entity;
import com.jaxio.celerio.util.support.CurrentAndFlatListHolder;
import com.jaxio.celerio.util.support.ListGetter;

/**
 * Enable you to retrieve {@link Attribute} belonging to the entity's ancestors,
 * the entity's descendants or simply the whole entity's family tree.
 * <p>
 * Note that this class is just a strongly typed {@link CurrentAndFlatListHolder}.
 * It is intended to reduce the definition of {@link Attribute} lists in the entity object.
 */
public class EntityAttributes extends CurrentAndFlatListHolder<Attribute, Entity> {

    public EntityAttributes(Entity entity, ListGetter<Attribute, Entity> listGetter) {
        this(entity, listGetter, AttributeOrder.DISPLAY);
    }

    public EntityAttributes(Entity entity, ListGetter<Attribute, Entity> listGetter, AttributeOrder attributeOrder) {
        super(entity, listGetter);
        // watch out: the target property must be a String.
        setSortProperty(attributeOrder.attributeSortProperty());
    }

    public EntityAttributes(Entity entity, ListGetter<Attribute, Entity> listGetter, Predicate<Attribute> predicate) {
        this(entity, listGetter, predicate, AttributeOrder.DISPLAY);
    }

    public EntityAttributes(Entity entity, ListGetter<Attribute, Entity> listGetter, Predicate<Attribute> predicate,  AttributeOrder attributeOrder) {
        super(entity, listGetter, predicate);
        setSortProperty(attributeOrder.attributeSortProperty());
    }
}