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

package com.jaxio.celerio.spi.support;

import com.jaxio.celerio.model.Entity;
import com.jaxio.celerio.model.PackageImportAdder;
import com.jaxio.celerio.spi.EntitySpi;
import com.jaxio.celerio.template.ImportsContext;

public abstract class AbstractEntitySpi implements EntitySpi, PackageImportAdder {
    protected Entity entity;

    @Override
    public void init(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void addImport(String fullType) {
        if (!ImportsContext.isExtendedByUser()) {
            ImportsContext.addImport(fullType);
        }
    }

    protected String appendComment(String annotation) {
        if (ImportsContext.isExtendedByUser()) {
            return "// (uncomment it in subclass) " + annotation;
        }
        return annotation;
    }
}