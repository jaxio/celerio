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

package com.jaxio.celerio.configuration;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static java.io.File.separatorChar;

public class Util {

    public final static String CLASSPATH_CELERIO_PACK = "classpath*:META-INF/celerio-pack.xml";
    public final static String LOCAL_CELERIO_PACK = "META-INF" + separatorChar + "celerio-pack.xml";

    public static boolean hasSize(List<?> l) {
        return l != null && l.size() > 0;
    }

    public static <T> T firstNonNull(T... ts) {
        for (T t : ts) {
            if (t != null) {
                return t;
            }
        }
        return null;
    }

    public static String firstNotEmpty(String... values) {
        for (String value : values) {
            if (value != null && !value.isEmpty()) {
                return value;
            }
        }
        return null;
    }

    public static <T> List<T> nonNull(List<T> l) {
        if (l != null) {
            return l;
        } else {
            return newArrayList();
        }
    }

    public static <T> Set<T> nonNull(Set<T> l) {
        if (l != null) {
            return l;
        } else {
            return newHashSet();
        }
    }

    public static <T> Map<T, T> nonNull(Map<T, T> l) {
        if (l != null) {
            return l;
        } else {
            return newHashMap();
        }
    }
}
