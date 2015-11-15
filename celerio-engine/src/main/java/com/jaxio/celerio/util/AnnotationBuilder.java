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

package com.jaxio.celerio.util;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newTreeSet;

public class AnnotationBuilder {
    private Set<String> annotations = newTreeSet();
    private boolean commentMode;

    public AnnotationBuilder(String... annotations) {
        add(annotations);
    }

    public void add(String... annotations) {
        for (String annotation : annotations) {
            if (annotation != null && !"".equals(annotation)) {
                if (commentMode) {
                    this.annotations.add("// (uncomment it in subclass): " + annotation);
                } else {
                    this.annotations.add(annotation);
                }
            }
        }
    }

    public List<String> getAnnotations() {
        return newArrayList(annotations);
    }

    public void enableCommentMode() {
        commentMode = true;
    }
}