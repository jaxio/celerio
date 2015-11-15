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

package com.jaxio.celerio.model.relation;

import com.jaxio.celerio.model.Attribute;
import com.jaxio.celerio.model.Entity;
import com.jaxio.celerio.model.Relation;
import com.jaxio.celerio.support.Namer;
import lombok.Getter;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.unmodifiableList;

@Getter
public abstract class SimpleRelation extends AbstractRelation {
    private Attribute fromAttribute;
    private Entity fromEntity;
    private Entity toEntity;
    private Attribute toAttribute;
    private Namer from;
    private Namer to;
    private List<Relation> relations;

    public SimpleRelation(Namer fromNamer, Namer toNamer, Attribute fromAttribute, Entity fromEntity, Entity toEntity, Attribute toAttribute) {
        this.from = checkNotNull(fromNamer);
        this.to = checkNotNull(toNamer);
        this.fromAttribute = checkNotNull(fromAttribute);
        this.fromEntity = checkNotNull(fromEntity);
        this.toEntity = checkNotNull(toEntity);
        this.toAttribute = checkNotNull(toAttribute);
        this.relations = unmodifiableList(newArrayList((Relation) this));
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
    public String toString() {
        return getClass().getSimpleName() + " " + fromAttribute.getFullColumnName() + " ==> " + toAttribute.getFullColumnName();
    }
}
