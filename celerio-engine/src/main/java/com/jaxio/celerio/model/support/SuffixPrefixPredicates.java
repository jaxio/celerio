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

package com.jaxio.celerio.model.support;

import com.google.common.base.Predicate;
import com.jaxio.celerio.model.Attribute;
import com.jaxio.celerio.util.MiscUtil;

import java.util.List;
import java.util.Locale;

import static com.google.common.base.Predicates.and;
import static com.google.common.collect.Lists.newArrayList;
import static com.jaxio.celerio.model.support.AttributePredicates.*;

public class SuffixPrefixPredicates {

    private static abstract class PatternPredicate implements Predicate<Attribute> {
        private Iterable<String> patterns;

        public PatternPredicate(Iterable<String> patterns) {
            this.patterns = patterns;
        }

        public Iterable<String> getPatterns() {
            return patterns;
        }

        @Override
        public abstract boolean apply(Attribute input);
    }

    public static class HasSuffixPredicate extends PatternPredicate {
        public HasSuffixPredicate(Iterable<String> patterns) {
            super(patterns);
        }

        @Override
        public boolean apply(Attribute input) {
            if (MiscUtil.endsWithIgnoreCase(input.getColumnConfig().getFieldName(), getPatterns())) {
                return true;
            }
            return false;
        }
    }

    public static class ColumnNameContainsPredicate extends PatternPredicate {
        public ColumnNameContainsPredicate(Iterable<String> patterns) {
            super(patterns);
        }

        @Override
        public boolean apply(Attribute input) {
            if (MiscUtil.contains(input.getColumnName(), getPatterns())) {
                return true;
            }
            return false;
        }
    }

    public static class ColumnNameEqualsPredicate implements Predicate<Attribute> {
        private Iterable<String> matches;

        public ColumnNameEqualsPredicate(Iterable<String> matches) {
            this.matches = matches;
        }

        @Override
        public boolean apply(Attribute input) {
            for (String match : matches) {
                if (input.getVar().equalsIgnoreCase(match)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static class NameEqualsPredicate extends PatternPredicate {
        public NameEqualsPredicate(Iterable<String> patterns) {
            super(patterns);
        }

        @Override
        public boolean apply(Attribute input) {
            return MiscUtil.equalsIgnoreCase(input.getColumnConfig().getFieldName(), getPatterns());
        }
    }

    public static class HasPrefixPredicate extends PatternPredicate {
        public HasPrefixPredicate(Iterable<String> patterns) {
            super(patterns);
        }

        @Override
        public boolean apply(Attribute input) {
            return MiscUtil.startsWithIgnoreCase(input.getColumnConfig().getFieldName(), getPatterns());
        }
    }

    public static class HasFileAttributesPredicate implements Predicate<Attribute> {
        @Override
        public boolean apply(Attribute input) {
            return input.getEntity().hasFileAttributes();
        }
    }

    public static class AttributeShareSameSuffix implements Predicate<Attribute> {
        private Attribute attribute;

        public AttributeShareSameSuffix(Attribute attribute) {
            this.attribute = attribute;
        }

        @Override
        public boolean apply(Attribute input) {
            if (!input.isInFileDefinition()) {
                return false;
            }
            String currentFieldName = input.getColumnConfig().getFieldName().toUpperCase();
            for (String suffixPattern : getSuffixes(input)) {
                String patternUpper = suffixPattern.toUpperCase();
                if (currentFieldName.equals(patternUpper) || currentFieldName.endsWith(patternUpper)) {
                    String currentFieldNameWithoutPrefix = currentFieldName.substring(0, currentFieldName.length() - patternUpper.length());
                    if (attribute.getColumnConfig().getFieldName().toUpperCase().startsWith(currentFieldNameWithoutPrefix)) {
                        return true;
                    }
                }
            }

            return false;
        }

        private Iterable<String> getSuffixes(Attribute input) {
            if (input.isFileSize()) {
                return FILE_SIZE_SUFFIX;
            } else if (input.isFile()) {
                return BINARY_SUFFIX;
            } else if (input.isFilename()) {
                return FILE_NAME_SUFFIX;
            } else if (input.isContentType()) {
                return CONTENT_TYPE_SUFFIX;
            } else {
                return newArrayList();
            }
        }
    }

    // conventions
    public static List<String> BOOLEAN_PREFIX = newArrayList("is", "has", "use", "can", "est");
    public static List<String> VERSION_SUFFIX = newArrayList("version");
    public static List<String> LOCALE_SUFFIX = newArrayList("locale");
    public static List<String> LANGUAGE_SUFFIX = buildLanguagesSuffixes();
    public static List<String> EMAIL_SUFFIX = newArrayList("email", "emailaddress", "mail");
    public static List<String> PASSWORD_SUFFIX = newArrayList("password", "pwd", "passwd", "motdepasse");
    public static List<String> BINARY_SUFFIX = newArrayList("binary", "content", "file", "picture");
    public static List<String> CONTENT_TYPE_SUFFIX = newArrayList("contenttype");
    public static List<String> FILE_NAME_SUFFIX = newArrayList("filename");
    public static List<String> FILE_SIZE_SUFFIX = newArrayList("size", "length", "contentlength");
    public static List<String> ROLE_SUFFIX = newArrayList("authority", "nameLocale", "roleName", "role");
    public static List<String> URL_SUFFIX = newArrayList("url");
    public static List<String> HTML_FRAGMENT = newArrayList("_html");
    public static List<String> LABELS = newArrayList("libelle", "label", "title", "titre", "firstname", "prenom", "lastname", "name", "nom");

    // prefix
    public static Predicate<Attribute> IS_BOOLEAN_PREFIX = new HasPrefixPredicate(BOOLEAN_PREFIX);

    // suffix
    public static Predicate<Attribute> IS_VERSION_SUFFIX = new HasSuffixPredicate(VERSION_SUFFIX);
    public static Predicate<Attribute> IS_LOCALE_SUFFIX = new HasSuffixPredicate(LOCALE_SUFFIX);
    public static Predicate<Attribute> IS_LANGUAGE_SUFFIX = new HasSuffixPredicate(LANGUAGE_SUFFIX);
    public static Predicate<Attribute> IS_EMAIL_SUFFIX = new HasSuffixPredicate(EMAIL_SUFFIX);
    public static Predicate<Attribute> IS_PASSWORD_SUFFIX = new HasSuffixPredicate(PASSWORD_SUFFIX);
    public static Predicate<Attribute> IS_BINARY_SUFFIX = new HasSuffixPredicate(BINARY_SUFFIX);
    public static Predicate<Attribute> IS_CONTENT_TYPE_SUFFIX = new HasSuffixPredicate(CONTENT_TYPE_SUFFIX);
    public static Predicate<Attribute> IS_FILE_NAME_SUFFIX = new HasSuffixPredicate(FILE_NAME_SUFFIX);
    public static Predicate<Attribute> IS_FILE_SIZE_SUFFIX = new HasSuffixPredicate(FILE_SIZE_SUFFIX);
    public static Predicate<Attribute> IS_ROLE_SUFFIX = new HasSuffixPredicate(ROLE_SUFFIX);
    public static Predicate<Attribute> IS_URL_SUFFIX = new HasSuffixPredicate(URL_SUFFIX);
    public static Predicate<Attribute> HAS_FILE_ATTRIBUTES = new HasFileAttributesPredicate();

    // contains
    public static Predicate<Attribute> CONTAINS_HTML = new ColumnNameContainsPredicate(HTML_FRAGMENT);

    // equals
    public static Predicate<Attribute> IS_LABEL = and(STRING, new ColumnNameEqualsPredicate(LABELS));

    // derived
    public static Predicate<Attribute> IS_FILE_SIZE = and(NUMERIC, and(IS_FILE_SIZE_SUFFIX, HAS_FILE_ATTRIBUTES));
    public static Predicate<Attribute> IS_FILE_NAME = and(STRING, and(IS_FILE_NAME_SUFFIX, HAS_FILE_ATTRIBUTES));
    public static Predicate<Attribute> IS_FILE_BINARY = and(BLOB, and(IS_BINARY_SUFFIX, HAS_FILE_ATTRIBUTES));
    public static Predicate<Attribute> IS_FILE_CONTENT_TYPE = and(STRING, and(IS_CONTENT_TYPE_SUFFIX, HAS_FILE_ATTRIBUTES));
    public static Predicate<Attribute> IS_EMAIL = and(STRING, IS_EMAIL_SUFFIX);
    public static Predicate<Attribute> IS_PASSWORD = and(STRING, IS_PASSWORD_SUFFIX);

    private static List<String> buildLanguagesSuffixes() {
        List<String> ret = newArrayList();
        for (Locale locale : Locale.getAvailableLocales()) {
            ret.add("_" + locale.getLanguage());
        }
        return ret;
    }
}
