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

import lombok.Setter;
import org.springframework.util.AntPathMatcher;

import java.io.File;
import java.util.List;

/*
 * A pattern is a structure to help handling inclusion and exclusion of resources
 */
public class Pattern {
    @Setter
    private String pattern;
    @Setter
    private boolean include;
    private transient AntPathMatcher antPathMatcher = new AntPathMatcher();

    public Pattern() {
    }

    public Pattern(String pattern, boolean include) {
        this.pattern = plateformIndependant(pattern);
        this.include = include;
    }

    /*
     * if the pattern contains '?', '*', '**' the matching will be done using an ant matcher, otherwise it will do a equalsIgnoreCase
     * <ul>
     * <li>? matches one character</li>
     * <li>* matches zero or more characters</li>
     * <li>** matches zero or more 'directories' in a path</li>
     * </ul>
     * Some examples:
     * <ul>
     * <li>com/t?st.jsp - matches com/test.jsp but also com/tast.jsp or com/txst.jsp</li>
     * <li>com/yourcompany/*\/*.jsp - matches all .jsp files in the com/yourcompany directory</li>
     * </ul>
     */
    public String getPattern() {
        return pattern;
    }

    /*
     * True is is an inclusion pattern, false for an exclusion ?
     */
    public boolean isInclude() {
        return include;
    }

    public AntPathMatcher getAntPathMatcher() {
        return antPathMatcher;
    }

    public boolean match(String value) {
        if (value == null) {
            return false;
        }
        String cleanValue = plateformIndependant(value);
        if (antPathMatcher.isPattern(pattern)) {
            return antPathMatcher.match(pattern.toLowerCase(), cleanValue.toLowerCase());
        } else {
            return cleanValue.toLowerCase().equalsIgnoreCase(pattern.toLowerCase());
        }
    }

    private String plateformIndependant(String value) {
        return value.replace(File.separator, AntPathMatcher.DEFAULT_PATH_SEPARATOR);
    }

    public boolean isExcluded() {
        return !isIncluded();
    }

    public boolean isIncluded() {
        return include;
    }

    public static boolean hasPattern(List<Pattern> patterns, String value) {
        // no definition : everything is ok
        if (patterns.isEmpty()) {
            return true;
        }

        for (Pattern pattern : patterns) {
            if (pattern.match(value)) {
                return pattern.isIncluded();
            }
        }

        if (hasOnlyExcludes(patterns)) {
            return true;
        } else if (hasOnlyIncludes(patterns)) {
            return false;
        } else {
            return false;
        }
    }

    private static boolean hasOnlyExcludes(List<Pattern> patterns) {
        for (Pattern pattern : patterns) {
            if (pattern.isIncluded()) {
                return false;
            }
        }
        return true;
    }

    private static boolean hasOnlyIncludes(List<Pattern> patterns) {
        for (Pattern pattern : patterns) {
            if (pattern.isExcluded()) {
                return false;
            }
        }
        return true;
    }
}