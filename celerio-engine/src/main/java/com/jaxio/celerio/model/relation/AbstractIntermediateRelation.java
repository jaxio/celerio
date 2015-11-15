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

import com.jaxio.celerio.configuration.entity.AssociationDirection;
import com.jaxio.celerio.model.Attribute;
import com.jaxio.celerio.model.Entity;
import com.jaxio.celerio.model.Relation;
import com.jaxio.celerio.support.AccessorNamer;
import com.jaxio.celerio.support.Namer;
import lombok.Getter;

@Getter
public abstract class AbstractIntermediateRelation extends AbstractRelation {
    private Entity middleEntity;
    private Relation middleToLeft;
    private Relation middleToRight;
    private Entity fromEntity;
    private Entity toEntity;
    private Namer from;
    private Namer to;
    private Namer middle;
    private Relation inverse;
    private AssociationDirection associationDirection;

    public AbstractIntermediateRelation(Namer from, Namer to, Entity middleEntity, Relation middleToLeft, Relation middleToRight) {
        this.middleEntity = middleEntity;
        this.middleToLeft = middleToLeft;
        this.middleToRight = middleToRight;
        this.fromEntity = middleToLeft.getToEntity(); // Yes! getToEntity()
        this.toEntity = middleToRight.getToEntity();
        this.from = from;
        this.to = to;
        this.middle = new AccessorNamer(middleEntity.getModel());
    }

    @Override
    final public boolean isIntermediate() {
        return true;
    }

    @Override
    public String toString() {
        return getFromEntity().getTableName() + " <== " + getMiddleEntity().getTableName() + " ==> " + getToEntity().getTableName();
    }

    @Override
    public Attribute getFromAttribute() {
        throw new IllegalStateException("Not supported for intermediate relation (" + toString() + ")");
    }

    @Override
    public Attribute getToAttribute() {
        throw new IllegalStateException("Not supported for intermediate relation (" + toString() + ")");
    }

    @Override
    public String getLabelName() {
        return getFromEntity().getModel().getVar() + "_" + getTo().getVars();
    }
}