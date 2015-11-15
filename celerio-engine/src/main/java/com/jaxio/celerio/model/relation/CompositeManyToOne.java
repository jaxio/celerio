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
import com.jaxio.celerio.support.Namer;

import java.util.List;

public abstract class CompositeManyToOne extends CompositeRelation {

    public CompositeManyToOne(Namer fromNamer, Namer toNamer, List<Attribute> fromAttributes, Entity fromEntity, Entity toEntity, List<Attribute> toAttributes) {
        super(fromNamer, toNamer, fromAttributes, fromEntity, toEntity, toAttributes);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()).append(" (");
        for (Attribute a : getFromAttributes()) {
            sb.append(a.getFullColumnName()).append(", ");
        }
        sb.append(") ==> (");
        for (Attribute a : getToAttributes()) {
            sb.append(a.getFullColumnName()).append(", ");
        }
        sb.append(")");
        return sb.toString().replaceAll(", \\)", ")");
    }

    @Override
    final public boolean isManyToOne() {
        return true;
    }

    // ---------------------------
    // VIEW SUPPORT
    // ---------------------------
    @Override
    public String getLabelName() {
        return getFromEntity().getModel().getVar() + "_" + getTo().getVar();
    }
}