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

package com.jaxio.celerio.model.primarykey;

import com.jaxio.celerio.model.Attribute;
import com.jaxio.celerio.model.Entity;
import com.jaxio.celerio.model.PrimaryKey;
import com.jaxio.celerio.support.AbstractNamer;
import lombok.Getter;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.unmodifiableList;

@Getter
public class SimplePrimaryKey extends AbstractNamer implements PrimaryKey {
    private final Entity entity;
    private final Attribute attribute;
    private final List<Attribute> attributes;

    public SimplePrimaryKey(Entity entity, Attribute attribute) {
        this.attribute = checkNotNull(attribute, "Unique PK must have a non null attribute");
        this.entity = checkNotNull(entity);
        this.attributes = unmodifiableList(newArrayList(checkNotNull(attribute)));
        attribute.setSimplePk(true);
    }

    public boolean isAllNullable() {
        return attribute.getColumnConfig().getNullable();
    }

    @Override
    final public boolean isNoPk() {
        return false;
    }

    @Override
    final public boolean isSimple() {
        return true;
    }

    @Override
    final public boolean isComposite() {
        return false;
    }

    @Override
    final public boolean isJavaBaseClass() {
        return attribute.isJavaBaseClass();
    }

    @Override
    final public boolean isDate() {
        return isSimple() && getAttribute().isDate();
    }

    @Override
    final public boolean isEnum() {
        return isSimple() && getAttribute().isEnum();
    }

    /**
     * Is there a XtoOne relation pointing to this PK
     */
    @Override
    final public boolean isImported() {
        return getEntity().getInverseRelations().getFlatUp().getSize() > 0;
    }

    @Override
    public String getVar() {
        return attribute.getVar();
    }

    @Override
    public String getType() {
        return attribute.getType();
    }

    @Override
    public String getFullType() {
        return attribute.getFullType();
    }

    @Override
    public String getPackageName() {
        return attribute.getPackageName();
    }

    @Override
    public String getToStringMethod() {
        return attribute.getMappedType().getToStringMethod();
    }

    @Override
    public String toString() {
        return "Simple PK: " + attribute.getFullColumnName();
    }
}