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
public abstract class CompositeRelation extends AbstractRelation {
    private List<Attribute> fromAttributes;
    private Entity fromEntity;
    private Entity toEntity;
    private List<Attribute> toAttributes;
    private Namer from;
    private Namer to;
    private List<Relation> relations;

    public CompositeRelation(Namer fromNamer, Namer toNamer, List<Attribute> fromAttributes, Entity fromEntity, Entity toEntity, List<Attribute> toAttributes) {
        this.from = checkNotNull(fromNamer);
        this.to = checkNotNull(toNamer);

        this.fromAttributes = checkNotNull(fromAttributes);
        this.fromEntity = checkNotNull(fromEntity);
        this.toEntity = checkNotNull(toEntity);
        this.toAttributes = checkNotNull(toAttributes);

        this.relations = unmodifiableList(newArrayList((Relation) this));
    }

    @Override
    final public boolean isSimple() {
        return false;
    }

    @Override
    final public boolean isComposite() {
        return true;
    }

    @Override
    public Attribute getFromAttribute() {
        return getFromAttributes().get(0); // we tolerate calling it as it is needed for cascade stuff
    }

    @Override
    public Attribute getToAttribute() {
        throw new IllegalStateException("do not call it please");
    }

    @Override
    public String toString() {
        return "// relation involving composite FK";
    }
}
