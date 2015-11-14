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

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import com.jaxio.celerio.spi.support.AbstractEntitySpi;

public class SearchEntity extends AbstractEntitySpi {

    /**
     * @return "search"
     */
    @Override
    public String velocityVar() {
        return "search";
    }
    
    public List<String> getAnnotations() {
        return newArrayList(getIndexedAnnotation());
    }
    
    public String getIndexedAnnotation() {
        if (!entity.hasChildren() && entity.isIndexed()) {
            addImport("org.hibernate.search.annotations.Indexed");
            return appendComment("@Indexed");
        }
        return "";
    }
}