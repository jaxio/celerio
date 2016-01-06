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

package com.jaxio.celerio.model.support.validation;

import com.jaxio.celerio.model.Relation;
import com.jaxio.celerio.spi.RelationSpi;
import com.jaxio.celerio.template.ImportsContext;
import com.jaxio.celerio.util.AnnotationBuilder;

import java.util.List;

public class ValidationRelation implements RelationSpi {
    private Relation relation;

    // ---------------------------------------
    // Relation SPI Implementation
    // ---------------------------------------

    @Override
    public boolean compatibleWith(Relation relation) {
        return relation.isManyToOne() || relation.isOneToOne();
    }

    @Override
    public String velocityVar() {
        return "validation";
    }

    @Override
    public Object getTarget() {
        return this;
    }

    @Override
    public void init(Relation relation) {
        this.relation = relation;
    }

    // --------------------------------------------
    // Validation Annotations, used from templates
    // --------------------------------------------

    public List<String> getAnnotations() {
        AnnotationBuilder result = new AnnotationBuilder();
        result.add(getNotNullAnnotation());
        return result.getAnnotations();
    }

    /**
     * Applies only to ManyToOne or OneToOne.
     * When x to one has an inverse relation, we never mark it as @NotNull
     * as it would break navigation (indeed, the mandatory value is set transparently once the entity is added to the collection)
     * However, in test (XxxGenerator), we must take mandatory into account...
     */
    public String getNotNullAnnotation() {
        if (!relation.isManyToOne() && !relation.isOneToOne()) {
            return "";
        }

        if (relation.isMandatory() && !relation.hasInverse()) {
            if (relation.getFromEntity().getName().equals(relation.getToEntity().getName())) {
                // TODO ?: mandatory relation on self is complicated to handled
                // in tests (model generator)... so we just ignore it.
                return "";
            }

            if (relation.getFromAttribute().isInPk()) {
                // TODO: not sure why, but integration tests fail if we don't skip this case.
                return "";
            }

            ImportsContext.addImport("javax.validation.constraints.NotNull");
            return "@NotNull";
        } else {
            return "";
        }
    }
}
