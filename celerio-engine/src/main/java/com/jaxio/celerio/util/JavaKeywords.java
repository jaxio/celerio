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

import static com.google.common.collect.Lists.newArrayList;

@SuppressWarnings("unchecked")
public class JavaKeywords {
    private static List<String> javaWords = newArrayList("abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const",
            "continue", "default", "do", "double", "else", "enum", "extends", "final", "false", "final", "finally", "float", "for", "goto", "if", "implements",
            "import", "instanceof", "int", "interface", "long", "native", "new", "null", "package", "private", "protected", "public", "return", "short",
            "static", "strictfp", "super", "switch", "synchronized", "this", "throw", "throws", "transient", "true", "try", "void", "volatile", "while");
    private static List<String> junit = newArrayList("test");
    private static List<String> spring = newArrayList("service", "autowired", "component", "controller");
    private static List<String> jpa = newArrayList("entity");
    private static List<List<String>> reservedLists = newArrayList(javaWords, junit, spring, jpa);

    public static boolean isReserved(String s) {
        for (List<String> reservedList : reservedLists) {
            for (String javaWord : reservedList) {
                if (s.equalsIgnoreCase(javaWord)) {
                    return true;
                }
            }
        }
        return false;
    }
}
