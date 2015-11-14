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

package com.jaxio.celerio.configuration.entity;

import lombok.Setter;

import org.springframework.util.StringUtils;

public class CustomAnnotation {

    @Setter
    private String annotation;

    /**
     * The full qualified custom annotation to apply to this property. For example: @com.mycompany.MyAnnotation(debug = true)
     */
    public String getAnnotation() {
        return annotation;
    }

    public boolean hasAnnotation() {
        return StringUtils.hasLength(annotation);
    }

    public String extractAnnotationImport() {
        if (annotation.contains("(")) {
            return annotation.substring(1, annotation.indexOf("("));
        } else {
            return annotation.substring(1);
        }
    }
}