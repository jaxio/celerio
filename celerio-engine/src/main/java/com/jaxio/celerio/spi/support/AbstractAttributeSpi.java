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

import com.jaxio.celerio.model.Attribute;
import com.jaxio.celerio.model.PackageImportAdder;
import com.jaxio.celerio.spi.AttributeSpi;
import com.jaxio.celerio.template.ImportsContext;

import static java.lang.Boolean.TRUE;

public abstract class AbstractAttributeSpi implements AttributeSpi, PackageImportAdder {
    protected Attribute attribute;

    @Override
    public void init(Attribute attribute) {
        this.attribute = attribute;
    }

    @Override
    public void addImport(String fullType) {
        if (!isCommentMode()) {
            ImportsContext.addImport(fullType);
        }
    }

    /**
     * Determines if the annotation should be commented.<br>
     * By default annotation are commented if the attribute is set as transient in the configuration.
     */
    protected boolean isCommentMode() {
        return attribute.getColumnConfig() != null && attribute.getColumnConfig().getAsTransient() == TRUE;
    }

    protected String appendComment(String annotation) {
        if (isCommentMode()) {
            return "// (uncomment it in subclass) " + annotation;
        }
        return annotation;
    }
}