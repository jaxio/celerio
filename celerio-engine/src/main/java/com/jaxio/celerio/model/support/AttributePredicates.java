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

import static com.google.common.base.Predicates.not;
import static com.google.common.base.Predicates.or;

public class AttributePredicates {

    public static Predicate<Attribute> LOCALE_KEY = new Predicate<Attribute>() {
        @Override
        public boolean apply(Attribute attribute) {
            return attribute.isLocaleKey();
        }
    };

    public static Predicate<Attribute> LOCALIZABLE = new Predicate<Attribute>() {
        @Override
        public boolean apply(Attribute attribute) {
            return attribute.isLocalizable();
        }
    };

    public static Predicate<Attribute> FILE = new Predicate<Attribute>() {
        @Override
        public boolean apply(Attribute attribute) {
            return attribute.isFile();
        }
    };

    public static Predicate<Attribute> SIMPLE = new Predicate<Attribute>() {
        @Override
        public boolean apply(Attribute attribute) {
            return attribute.isSimple();
        }
    };

    public static Predicate<Attribute> VISIBLE = new Predicate<Attribute>() {
        @Override
        public boolean apply(Attribute attribute) {
            return attribute.isVisible();
        }
    };

    public static Predicate<Attribute> HIDDEN = not(VISIBLE);

    public static Predicate<Attribute> SORTABLE = new Predicate<Attribute>() {
        @Override
        public boolean apply(Attribute attribute) {
            return attribute.isSortable();
        }
    };

    public static Predicate<Attribute> FORM_FIELD = new Predicate<Attribute>() {
        @Override
        public boolean apply(Attribute attribute) {
            return attribute.isFormField();
        }
    };

    public static Predicate<Attribute> SEARCH_FIELD = new Predicate<Attribute>() {
        @Override
        public boolean apply(Attribute attribute) {
            return attribute.isSearchField();
        }
    };

    public static Predicate<Attribute> RANGEABLE_FIELD = new Predicate<Attribute>() {
        @Override
        public boolean apply(Attribute attribute) {
            return attribute.isRangeable();
        }
    };

    /**
     * Fields that can be used with the PropertySelector.
     */
    public static Predicate<Attribute> MULTI_SELECTABLE_FIELD = new Predicate<Attribute>() {
        @Override
        public boolean apply(Attribute attribute) {
            return attribute.isMultiSelectable();
        }
    };

    public static Predicate<Attribute> SEARCH_RESULT_CONVENTION = new Predicate<Attribute>() {
        @Override
        public boolean apply(Attribute attribute) {
            return attribute.isSearchResultFieldConvention();
        }
    };

    public static Predicate<Attribute> SEARCH_RESULT_FIELD_DEFINED_MANUALLY = new Predicate<Attribute>() {
        @Override
        public boolean apply(Attribute attribute) {
            if (attribute.getColumnConfig().getSearchResult() != null) {
                return attribute.getColumnConfig().getSearchResult();
            }
            return false;
        }
    };

    public static Predicate<Attribute> HIBERNATE_SEARCH_FIELD = new Predicate<Attribute>() {
        @Override
        public boolean apply(Attribute attribute) {
            return attribute.getColumnConfig().getIndexedField() != null;
        }
    };

    public static Predicate<Attribute> SIMPLE_PK = new Predicate<Attribute>() {
        @Override
        public boolean apply(Attribute attribute) {
            return attribute.isSimplePk();
        }
    };

    public static Predicate<Attribute> IS_UNIQUE = new Predicate<Attribute>() {
        @Override
        public boolean apply(Attribute attribute) {
            return attribute.isUnique();
        }
    };

    public static Predicate<Attribute> IN_COMPOSITE_PK = new Predicate<Attribute>() {
        @Override
        public boolean apply(Attribute attribute) {
            return attribute.isInCpk();
        }
    };

    public static Predicate<Attribute> IN_PK = or(SIMPLE_PK, IN_COMPOSITE_PK);

    public static Predicate<Attribute> NOT_IN_PK = not(IN_PK);

    public static Predicate<Attribute> NOT_IN_COMPOSITE_PK = not(IN_COMPOSITE_PK);

    public static Predicate<Attribute> NOT_SIMPLE_PK = not(SIMPLE_PK);

    public static Predicate<Attribute> SIMPLE_FK = new Predicate<Attribute>() {
        @Override
        public boolean apply(Attribute attribute) {
            return attribute.isSimpleFk();
        }
    };

    public static Predicate<Attribute> IN_COMPOSITE_FK = new Predicate<Attribute>() {
        @Override
        public boolean apply(Attribute attribute) {
            return attribute.isInCompositeFk();
        }
    };

    public static Predicate<Attribute> IN_FK = or(SIMPLE_FK, IN_COMPOSITE_FK);

    public static Predicate<Attribute> NOT_IN_FK = not(IN_FK);

    public static Predicate<Attribute> BUSINESS_KEY_BY_CONFIGURATION = new Predicate<Attribute>() {
        @Override
        public boolean apply(Attribute attribute) {
            return attribute.getColumnConfig().hasTrueBusinessKey();
        }
    };

    public static Predicate<Attribute> ENUM = new Predicate<Attribute>() {
        @Override
        public boolean apply(Attribute attribute) {
            return attribute.isEnum();
        }
    };

    public static Predicate<Attribute> HAS_PERTINENT_DEFAULT_VALUE = new Predicate<Attribute>() {
        @Override
        public boolean apply(Attribute attribute) {
            return attribute.hasPertinentDefaultValue();
        }
    };

    public static Predicate<Attribute> NUMERIC = new Predicate<Attribute>() {
        @Override
        public boolean apply(Attribute input) {
            return input.isNumeric();
        }
    };

    public static Predicate<Attribute> DATE = new Predicate<Attribute>() {
        @Override
        public boolean apply(Attribute input) {
            return input.isDate();
        }
    };

    public static Predicate<Attribute> DEFAULT_SORT = new Predicate<Attribute>() {
        @Override
        public boolean apply(Attribute input) {
            return input.isSimple() && input.isInBk();
            // TODO: default sort by configuration
        }
    };

    public static Predicate<Attribute> BIG_INTEGER = new Predicate<Attribute>() {
        @Override
        public boolean apply(Attribute input) {
            return input.isBigInteger();
        }
    };

    public static Predicate<Attribute> VERSION = new Predicate<Attribute>() {
        @Override
        public boolean apply(Attribute input) {
            return input.isVersion();
        }
    };

    public static Predicate<Attribute> NOT_VERSION = not(VERSION);

    public static Predicate<Attribute> STRING = new Predicate<Attribute>() {
        @Override
        public boolean apply(Attribute input) {
            return input.isString();
        }
    };

    public static Predicate<Attribute> BOOLEAN = new Predicate<Attribute>() {
        @Override
        public boolean apply(Attribute input) {
            return input.isBoolean();
        }
    };

    public static Predicate<Attribute> BLOB = new Predicate<Attribute>() {
        @Override
        public boolean apply(Attribute input) {
            return input.isBlob();
        }
    };

    public static Predicate<Attribute> IS_PATTERN_SEARCHABLE = new Predicate<Attribute>() {
        @Override
        public boolean apply(Attribute input) {
            return input.isPatternSearchable();
        }
    };

    public static Predicate<Attribute> IS_CPK_PATTERN_SEARCHABLE = new Predicate<Attribute>() {
        @Override
        public boolean apply(Attribute input) {
            return input.isCpkPatternSearchable();
        }
    };


    public static Predicate<Attribute> WITH_PUBLIC_SETTER_ACCESSIBILITY = new Predicate<Attribute>() {
        @Override
        public boolean apply(Attribute input) {
            return input.isSetterAccessibilityPublic();
        }
    };

    public static Predicate<Attribute> IS_DATE = new Predicate<Attribute>() {
        @Override
        public boolean apply(Attribute input) {
            return input.isDate();
        }
    };

    public static Predicate<Attribute> IS_STRING = new Predicate<Attribute>() {
        @Override
        public boolean apply(Attribute input) {
            return input.isString();
        }
    };

    public static Predicate<Attribute> IS_LABEL = new Predicate<Attribute>() {
        @Override
        public boolean apply(Attribute input) {
            return input.isLabel();
        }
    };
}
