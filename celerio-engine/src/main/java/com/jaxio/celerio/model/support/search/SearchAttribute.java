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

package com.jaxio.celerio.model.support.search;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import repackaged.org.hibernate.search.annotations.Analyze;
import repackaged.org.hibernate.search.annotations.Norms;
import repackaged.org.hibernate.search.annotations.Store;
import repackaged.org.hibernate.search.annotations.TermVector;

import com.jaxio.celerio.configuration.entity.IndexedField;
import com.jaxio.celerio.spi.support.AbstractAttributeSpi;
import com.jaxio.celerio.util.AnnotationBuilder;
import com.jaxio.celerio.util.AttributeBuilder;

public class SearchAttribute extends AbstractAttributeSpi {

    /**
     * @return "search"
     */
    @Override
    public String velocityVar() {
        return "search";
    }

    public boolean isIndexed() {
        return attribute.isIndexed();
    }
    
    // --------------------------------------------
    // Search Annotations, used from templates
    // --------------------------------------------

    /**
     * Returns all the search annotations for the attribute. Imports are processed automatically.
     */
    public List<String> getAnnotations() {
        AnnotationBuilder annotations = new AnnotationBuilder();
        annotations.add( //
                getFieldAnnotation(), //
                getFieldBridgeAnnotation(), //
                getTikaBridgeAnnotation());
        return annotations.getAnnotations();
    }


    public String getFieldAnnotation() {
        IndexedField field = attribute.getColumnConfig().getIndexedField();
        if (field == null) {
            return "";
        }
        
        AttributeBuilder attributes = new AttributeBuilder();
        // name
        String name = field.getName();
        if (name != null && !IndexedFieldDefaults.name.equals(name)) {
            attributes.add("name = " + name);
        }
        // store
        Store store = field.getStore();
        if (store != null && store != IndexedFieldDefaults.store) {
            addImport("org.hibernate.search.annotations.Store");
            attributes.add("store = Store." + store.name());
        } else if (attribute.isFile()) {
            addImport("org.hibernate.search.annotations.Store");
            attributes.add("store = Store.YES");
        }
        // index
        Boolean index = field.getIndex();
        if (index != null && index != IndexedFieldDefaults.index) {
            addImport("org.hibernate.search.annotations.Index");
            attributes.add("index = Index." + (index == true ? "YES" : "NO"));
        }
        // analyze
        Analyze analyze = field.getAnalyze();
        if (analyze != null && analyze != IndexedFieldDefaults.analyze) {
            addImport("org.hibernate.search.annotations.Analyze");
            attributes.add("analyze = Analyze." + analyze.name());
        }
        // analyzer
        String analyzer = field.getAnalyzer();
        if (analyzer != null) {
            addImport("org.hibernate.search.annotations.Analyzer");
            attributes.add("analyzer = @Analyzer(definition = \"" + analyzer + "\")");
        } else {
            addImport("org.hibernate.search.annotations.Analyzer");
            attributes.add("analyzer = @Analyzer(definition = \"custom\")");
        }
        // norms
        Norms norms = field.getNorms();
        if (norms != null && norms != IndexedFieldDefaults.norms) {
            addImport("org.hibernate.search.annotations.Norms");
            attributes.add("norms = Norms." + norms.name());
        }
        // termVector
        TermVector termVector = field.getTermVector();
        if (termVector != null && termVector != IndexedFieldDefaults.termVector) {
            addImport("org.hibernate.search.annotations.TermVector");
            attributes.add("termVector = TermVector." + termVector.name());
        }

        addImport("org.hibernate.search.annotations.Field");
        return attributes.bindAttributesTo("@Field");
    }

    public String getFieldBridgeAnnotation() {
        IndexedField field = attribute.getColumnConfig().getIndexedField();
        if (field == null || !field.hasBridgeImpl()) {
            return "";
        }
        
        addImport("org.hibernate.search.annotations.FieldBridge");
        addImport(field.getBridgeImpl());
        String type = StringUtils.substringAfterLast(field.getBridgeImpl(), ".");
        return "@FieldBridge(impl = " + type + ".class)";
    }

    public String getTikaBridgeAnnotation() {
        IndexedField field = attribute.getColumnConfig().getIndexedField();
        if (field == null || !attribute.isFile()) {
            return "";
        }

        addImport("org.hibernate.search.annotations.TikaBridge");
        return "@TikaBridge";
    }
}