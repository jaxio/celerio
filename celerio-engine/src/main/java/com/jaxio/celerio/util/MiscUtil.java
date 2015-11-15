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
 * Helper class to cast collections
 */
public abstract class MiscUtil {

    public static String toVar(String name) {
        return convertName(StringUtil.escape(name), true);
    }

    /**
     * Converts name into a more Java-ish style name. <br>
     * Basically it looks for underscores, removes them, and makes the letter after the underscore a capital letter. If wimpyCaps is true, then the first letter
     * of the name will be lower case, otherwise it will be upper case. Here are some examples:
     * <p>
     * member_profile becomes MemberProfile <br>
     * firstname&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp becomes&nbsp Firstname Please note that if no "underscore" is found, we will try of do our
     * best to make the converted name pretty
     * <p>
     * It is your responsibility to use this method appropriately, if you are calling this method to create a method like getColumn, if you give get_column or
     * get_Column it will break your java code.
     *
     * @param name      the field name to convert into a name
     * @param wimpyCaps if false, upper case the first letter
     * @return the converted name
     */
    private static String convertName(String name, boolean wimpyCaps) {
        if (!containsSeparator(name) && !name.toLowerCase().equals(name) && !name.toUpperCase().equals(name)) {
            return convertNameNoUnderscores(name, wimpyCaps);
        }
        name = name.toLowerCase();
        name = name.replace("-", "_");
        // we are in a specific case, we cannot allow lower case
        if (isJavaBeanFirstTwoCharsUpperCaseSpecificCaseInInternalFormat(name)) {
            wimpyCaps = true;
        } else {
            if (ignoreFirstUnderscore(name)) {
                name = name.substring(1);
            }
        }
        StringBuffer buffer = new StringBuffer(name.length());
        char list[] = name.toCharArray();
        char lastChar = 'x';
        for (int i = 0; i < list.length; i++) {
            if (isSeparator(lastChar)) {
                if (isWithinAnUnderscorePatternButIsNotJavaBeanSpecificCase(name, i)) {
                    lastChar = Character.toLowerCase(list[i]);
                } else {
                    lastChar = Character.toUpperCase(list[i]);
                }
            } else if (isSeparator(list[i]) && (i + 1) < list.length) {
                if (isWithinAnUnderscorePatternButIsNotJavaBeanSpecificCase(name, i)) {
                    lastChar = Character.toLowerCase(list[++i]);
                } else {
                    lastChar = Character.toUpperCase(list[++i]);
                }
            } else {
                // ok use this char
                lastChar = Character.toLowerCase(list[i]);
            }
            buffer.append(lastChar);
        }
        if (!wimpyCaps) {
            return StringUtil.getFirstCharacterUppered(buffer);
        } else {
            return buffer.toString();
        }
    }

    private static String convertNameNoUnderscores(String name, boolean wimpyCaps) {
        if (!wimpyCaps || isJavaBeanFirstTwoCharsUpperCaseSpecificCaseInInternalFormat(name)) {
            return StringUtil.getFirstCharacterUppered(name);
        } else {
            return StringUtil.getFirstCharacterLowered(name);
        }
    }

    private static boolean containsSeparator(String value) {
        for (int i = 0; i < value.length(); i++) {
            if (isSeparator(value.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private static boolean isSeparator(char c) {
        return c == '_' || c == '.' || c == ' ' || c == '-';
    }

    /**
     * Helper method, do the same as convertName with wimpsy caps as false
     *
     * @see #convertName(String, boolean)
     */
    public static String toName(String name) {
        return convertName(StringUtil.escape(name), false);
    }

    /**
     * Convert strings such as "banquePayss" to "Banque Pays" it will remove trailing ss and replace them with s
     */
    public static String toReadablePluralLabel(String value) {
        String label = toReadableLabel(value);
        return label.endsWith("ss") ? label.substring(0, label.length() - 1) : label;
    }

    /**
     * Convert strings such as bankAccountSummary to "Bank Account Summary"
     */
    public static String toReadableLabel(String value) {
        StringBuilder ret = new StringBuilder();
        char lastChar = 'x';
        for (int i = 0; i < value.length(); i++) {
            char currentChar = value.charAt(i);
            // we are the begining of the output
            // or the last char was a space
            // then uppercase
            if ((i == 0 || lastChar == ' ') && !(isSeparator(currentChar))) {
                currentChar = Character.toUpperCase(currentChar);
            } else if (Character.isLowerCase(lastChar) && Character.isUpperCase(currentChar)) {
                // we switched case --> add a space
                ret.append(' ');
            } else if (isSeparator(currentChar)) {
                // we are on a _, this is a space
                currentChar = ' ';
            }

            if (!(lastChar == ' ' && currentChar == ' ')) {
                ret.append(currentChar);
                lastChar = currentChar;
            }
        }

        if (ret.toString().startsWith("Is ") || ret.toString().startsWith("Has ")) {
            ret.append('?');
        }

        return ret.toString();
    }

    /**
     * Does the given column name ends with one of pattern given in parameter. Not case sensitive
     */
    public static boolean endsWithIgnoreCase(String name, Iterable<String> patterns) {
        String nameUpper = name.toUpperCase();
        for (String pattern : patterns) {
            String patternUpper = pattern.toUpperCase();
            if (nameUpper.equals(patternUpper) || nameUpper.endsWith(patternUpper)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Does the given column name starts with one of pattern given in parameter Not case sensitive
     */
    public static boolean startsWithIgnoreCase(String name, Iterable<String> patterns) {
        String nameUpper = name.toUpperCase();
        for (String pattern : patterns) {
            String patternUpper = pattern.toUpperCase();
            if (nameUpper.equals(patternUpper) || nameUpper.startsWith(patternUpper)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Does the given column name contains one of pattern given in parameter Not case sensitive
     */
    public static boolean contains(String name, Iterable<String> patterns) {
        String nameUpper = name.toUpperCase();
        for (String pattern : patterns) {
            String patternUpper = pattern.toUpperCase();
            if (nameUpper.equals(patternUpper) || nameUpper.contains(patternUpper)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Does the given column name equals ignore case with one of pattern given in parameter
     *
     * @param name     the column
     * @param patterns table of patterns as strings
     * @return true if the column name equals ignore case with one of the given patterns, false otherwise
     */
    public static boolean equalsIgnoreCase(String name, Iterable<String> patterns) {
        for (String pattern : patterns) {
            if (name.equalsIgnoreCase(pattern)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Return true if the passed value is all lower or all upper case.
     */
    public static boolean isUniCase(String value) {
        if (value.toLowerCase().equals(value) || value.toUpperCase().equals(value)) {
            return true;
        }
        return false;
    }

    static private boolean isJavaBeanFirstTwoCharsUpperCaseSpecificCaseInInternalFormat(String value) {
        if (value.length() > 2 && Character.isUpperCase(value.charAt(0)) && Character.isUpperCase(value.charAt(1))) {
            return true;
        }
        if (value.length() > 5 && isSeparator(value.charAt(0)) && !isSeparator(value.charAt(1)) && isSeparator(value.charAt(2))
                && !isSeparator(value.charAt(3)) && !isSeparator(value.charAt(4))) {
            // something that looks like that: _x_xx
            return true;
        } else {
            return false;
        }
    }

    static public boolean isWithinAnUnderscorePatternButIsNotJavaBeanSpecificCase(String value, int position) {
        String ret = getUnderscorePatternButIsNotJavaBeanSpecificCase(value);
        if (ret.length() == 0) {
            return false;
        } else {
            if (position <= ret.length()) {
                return true;
            } else {
                return false;
            }
        }
    }

    static private String getUnderscorePatternButIsNotJavaBeanSpecificCase(String value) {
        // this cannot be at least _a_b_cXXXXXX
        if (value.length() < 6) {
            return "";
        }
        if (isJavaBeanFirstTwoCharsUpperCaseSpecificCaseInInternalFormat(value)) {
            return "";
        }
        for (int i = 0; i < value.length(); i++) {
            if ((i % 2) == 0 && !isSeparator(value.charAt(i))) {
                if (i < 4) { // 4 is at least two _
                    return "";
                } else {
                    return value.substring(0, i);
                }
            }
            if ((i % 2) == 1 && isSeparator(value.charAt(i))) {
                if (i < 4) { // 4 is at least two _
                    return "";
                } else {
                    return value.substring(0, i);
                }
            }
        }
        return value;
    }

    static private boolean ignoreFirstUnderscore(String value) {
        if (value.length() > 3 && isSeparator(value.charAt(0)) && !isSeparator(value.charAt(1)) && !isSeparator(value.charAt(2))) {
            return true;
        } else {
            return false;
        }
    }
}
