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
import com.jaxio.celerio.model.Entity;

import static com.jaxio.celerio.model.support.SuffixPrefixPredicates.*;

public class EntityPredicates {
    public static Predicate<Entity> VIEW = new Predicate<Entity>() {
        @Override
        public boolean apply(Entity entity) {
            return entity.isView();
        }
    };

    public static Predicate<Entity> VIRTUAL = new Predicate<Entity>() {
        @Override
        public boolean apply(Entity entity) {
            return entity.isVirtual();
        }
    };

    public static Predicate<Entity> TABLE = new Predicate<Entity>() {
        @Override
        public boolean apply(Entity entity) {
            return entity.isTable();
        }
    };

    public static Predicate<Entity> ROOT = new Predicate<Entity>() {
        @Override
        public boolean apply(Entity entity) {
            return entity.isRoot();
        }
    };

    public static Predicate<Entity> ACCOUNT = new Predicate<Entity>() {
        @Override
        public boolean apply(Entity entity) {
            return entity.isAccount();
        }
    };

    public static Predicate<Entity> ROLE = new Predicate<Entity>() {
        @Override
        public boolean apply(Entity entity) {
            return entity.isRole();
        }
    };

    public static Predicate<Entity> AUDIT_LOG = new Predicate<Entity>() {
        @Override
        public boolean apply(Entity entity) {
            return entity.isAuditLog();
        }
    };

    public static Predicate<Entity> SAVED_SEARCH = new Predicate<Entity>() {
        @Override
        public boolean apply(Entity entity) {
            return entity.isSavedSearch();
        }
    };

    public static Predicate<Entity> NOT_MANY_TO_MANY_JOIN = new Predicate<Entity>() {
        @Override
        public boolean apply(Entity entity) {
            return !entity.isManyToManyJoinEntity();
        }
    };

    public static Predicate<Entity> SEARCH = new Predicate<Entity>() {
        @Override
        public boolean apply(Entity entity) {
            return entity.isIndexed() && entity.getHibernateSearchAttributes().isNotEmpty();
        }
    };

    public static Predicate<Entity> IS_INDEXED = new Predicate<Entity>() {
        @Override
        public boolean apply(Entity entity) {
            return entity.isIndexed();
        }
    };

    public static Predicate<Entity> HAS_FILE = new Predicate<Entity>() {
        @Override
        public boolean apply(Entity entity) {
            return entity.hasFileAttributes();
        }
    };

    public static class ExcludeEntity implements Predicate<Entity> {
        private Entity entity;

        public ExcludeEntity(Entity entity) {
            this.entity = entity;
        }

        @Override
        public boolean apply(Entity entity) {
            return this.entity != entity;
        }
    }

    public static class HasFileAttributesPredicate implements Predicate<Entity> {
        @Override
        public boolean apply(Entity input) {
            boolean contentTypeFound = false;
            boolean nameFound = false;
            boolean sizeFound = false;
            boolean binaryFound = false;

            for (Attribute attribute : input.getCurrentAttributes()) {
                if (attribute.isInPk()) {
                    continue; // We do not support it. Would it make sense anyway?
                }
                if (attribute.isString() && IS_CONTENT_TYPE_SUFFIX.apply(attribute)) {
                    contentTypeFound = true;
                } else if (attribute.isString() && IS_FILE_NAME_SUFFIX.apply(attribute)) {
                    nameFound = true;
                } else if (attribute.isNumeric() && IS_FILE_SIZE_SUFFIX.apply(attribute)) {
                    sizeFound = true;
                } else if (attribute.isBlob() && IS_BINARY_SUFFIX.apply(attribute)) {
                    binaryFound = true;
                } else if (attribute.isBlob()) {
                    // simple isolated blob column found
                    return true;
                }
            }
            return contentTypeFound && nameFound && sizeFound && binaryFound;
        }
    }

    public static Predicate<Entity> HAS_FILE_ATTRIBUTES = new HasFileAttributesPredicate();
}