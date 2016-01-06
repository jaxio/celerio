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
import com.jaxio.celerio.spi.support.AbstractAttributeSpi;
import com.jaxio.celerio.util.AnnotationBuilder;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class CustomAttribute extends AbstractAttributeSpi {
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

    /**
     * Return the custom annotations declared in the configuration.
     */
    public List<String> getAnnotations() {
        if (attribute.getColumnConfig().getCustomAnnotations() == null) {
            return newArrayList();
        }

        AnnotationBuilder builder = new AnnotationBuilder();
        for (CustomAnnotation ca : attribute.getColumnConfig().getCustomAnnotations()) {
            addImport(ca.extractAnnotationImport());
            builder.add(ca.getAnnotation());
        }
        return builder.getAnnotations();
    }
}
