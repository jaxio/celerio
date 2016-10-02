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

/**
 * Convenient String manipulation class.
 */
public class StringUtil {

    /**
     * Is the given string not empty ?
     *
     * @param s the string to check
     * @return true if not empty, false otherwise
     */
    public static boolean hasLength(String s) {
        return s != null && s.trim().length() > 0;
    }

    /**
     * If the given value collides with java reserved words, modify accordingly the value
     *
     * @param s the value to escape
     * @return a string that is saved to be used in a java program
     */
    public static String escape(String s) {
        return JavaKeywords.isReserved(s) ? ("my_" + s) : s;
    }

    public static String getPlural(String s) {
        if (s.toLowerCase().endsWith("ss")) {
            return s + "es";
        } else if (s.toLowerCase().endsWith("x")) {
            return s + "es";
        } else if (s.toLowerCase().endsWith("y")) {
            return s.substring(0, s.length() - 1) + "ies";
        } else if (s.toLowerCase().endsWith("child")) {
            return s + "ren";
        }

        return s + "s";
    }

    public static String getPlural(String s, boolean doPlural) {
        return doPlural ? getPlural(s) : s;
    }

    public static String getFirstCharacterLowered(String buffer) {
        if (buffer == null) { // convenient case when used with fallback.
            return null;
        }

        if (buffer.length() > 1) {
            return Character.toLowerCase(buffer.charAt(0)) + buffer.substring(1);
        }
        return "" + Character.toLowerCase(buffer.charAt(0));
    }

    public static String getFirstCharacterUppered(StringBuffer buffer) {
        return getFirstCharacterUppered(buffer.toString());
    }

    public static String getFirstCharacterUppered(String buffer) {
        if (buffer.length() > 1) {
            return Character.toUpperCase(buffer.charAt(0)) + buffer.substring(1);
        }
        return String.valueOf(Character.toUpperCase(buffer.charAt(0)));
    }

    public static String orderToString(Integer order, String fallback) {
        return order == null ? fallback : orderToString(order);
    }

    public static String orderToString(int order) {
        if (order < 10) {
            return "00" + order;
        } else if (order < 100) {
            return "0" + order;
        }
        return "" + order;
    }
}
