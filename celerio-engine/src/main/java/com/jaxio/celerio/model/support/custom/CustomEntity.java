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

package com.jaxio.celerio.model.support.custom;

import com.jaxio.celerio.configuration.entity.CustomAnnotation;
import com.jaxio.celerio.spi.support.AbstractEntitySpi;
import com.jaxio.celerio.util.AnnotationBuilder;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class CustomEntity extends AbstractEntitySpi {
    /**
     * @return "custom"
     */
    @Override
    public String velocityVar() {
        return "custom";
    }

    @Override
    public Object getTarget() {
        return this;
    }

    public List<String> getAnnotations() {
        if (entity.getEntityConfig().getCustomAnnotations() == null) {
            return newArrayList();
        }

        // add import from custom entity imports and annotations
        AnnotationBuilder ab = new AnnotationBuilder();
        for (CustomAnnotation customAnnotation : entity.getEntityConfig().getCustomAnnotations()) {
            addImport(customAnnotation.extractAnnotationImport());
            ab.add(customAnnotation.getAnnotation());
        }
        return ab.getAnnotations();
    }
}
