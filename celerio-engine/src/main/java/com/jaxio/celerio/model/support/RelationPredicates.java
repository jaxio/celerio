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
import com.jaxio.celerio.model.Relation;

import static com.google.common.base.Predicates.*;

public class RelationPredicates {

    public static Predicate<Relation> COMPOSITE = new Predicate<Relation>() {
        @Override
        public boolean apply(Relation relation) {
            return relation.isComposite();
        }
    };
    // SIMPLE / COMPOSITE
    public static Predicate<Relation> SIMPLE = new Predicate<Relation>() {
        @Override
        public boolean apply(Relation relation) {
            return relation.isSimple();
        }
    };

    public static Predicate<Relation> RELATION_IS_INVERSE = new Predicate<Relation>() {
        @Override
        public boolean apply(Relation relation) {
            return relation.isInverse();
        }
    };

    public static Predicate<Relation> COMPOSITE_RELATION = not(SIMPLE);

    // ------- MANY TO MANY
    public static Predicate<Relation> MANY_TO_MANY = new Predicate<Relation>() {
        @Override
        public boolean apply(Relation relation) {
            return relation.isManyToMany();
        }
    };
    public static Predicate<Relation> NOT_MANY_TO_MANY = not(MANY_TO_MANY);
    public static Predicate<Relation> SIMPLE_MANY_TO_MANY = and(SIMPLE, MANY_TO_MANY);
    public static Predicate<Relation> COMPOSITE_MANY_TO_MANY = and(COMPOSITE_RELATION, MANY_TO_MANY);

    // ------- ONE TO MANY
    public static Predicate<Relation> ONE_TO_MANY = new Predicate<Relation>() {
        @Override
        public boolean apply(Relation relation) {
            return relation.isOneToMany();
        }
    };

    public static Predicate<Relation> SIMPLE_ONE_TO_MANY = and(SIMPLE, ONE_TO_MANY);
    public static Predicate<Relation> COMPOSITE_ONE_TO_MANY = and(COMPOSITE_RELATION, ONE_TO_MANY);

    public static Predicate<Relation> X_TO_MANY = or(ONE_TO_MANY, MANY_TO_MANY);

    // ------- ONE TO VIRTUAL ONE
    public static Predicate<Relation> ONE_TO_VIRTUAL_ONE = new Predicate<Relation>() {
        @Override
        public boolean apply(Relation relation) {
            return relation.isOneToVirtualOne();
        }
    };

    public static Predicate<Relation> SIMPLE_ONE_TO_VIRTUAL_ONE = and(SIMPLE, ONE_TO_VIRTUAL_ONE);
    public static Predicate<Relation> COMPOSITE_ONE_TO_VIRTUAL_ONE = and(COMPOSITE_RELATION, ONE_TO_VIRTUAL_ONE);

    // ------- ONE TO ONE
    public static Predicate<Relation> ONE_TO_ONE = new Predicate<Relation>() {
        @Override
        public boolean apply(Relation relation) {
            return relation.isOneToOne();
        }
    };

    public static Predicate<Relation> SIMPLE_ONE_TO_ONE = and(SIMPLE, ONE_TO_ONE);
    public static Predicate<Relation> SIMPLE_INVERSE_ONE_TO_ONE = and(SIMPLE, and(ONE_TO_ONE, RELATION_IS_INVERSE));
    public static Predicate<Relation> COMPOSITE_ONE_ONE = and(COMPOSITE_RELATION, ONE_TO_ONE);

    // ------- MANY TO ONE
    public static Predicate<Relation> MANY_TO_ONE = new Predicate<Relation>() {
        @Override
        public boolean apply(Relation relation) {
            return relation.isManyToOne();
        }
    };

    public static Predicate<Relation> UNIDIRECTIONAL = new Predicate<Relation>() {
        @Override
        public boolean apply(Relation relation) {
            return !relation.hasInverse();
        }
    };

    public static Predicate<Relation> BIDIRECTIONAL = new Predicate<Relation>() {
        @Override
        public boolean apply(Relation relation) {
            return relation.hasInverse();
        }
    };

    public static Predicate<Relation> INTERMEDIATE = new Predicate<Relation>() {
        @Override
        public boolean apply(Relation relation) {
            return relation.isIntermediate();
        }
    };

    public static Predicate<Relation> X_TO_ONE = or(ONE_TO_ONE, MANY_TO_ONE);
    public static Predicate<Relation> FORWARD_X_TO_ONE = and(X_TO_ONE, not(RELATION_IS_INVERSE));
    public static Predicate<Relation> NON_SIMPLE_X_TO_ONE = and(X_TO_ONE, not(SIMPLE));
    public static Predicate<Relation> COMPOSITE_X_TO_ONE = and(COMPOSITE, X_TO_ONE);
    public static Predicate<Relation> INTERMEDIATE_X_TO_ONE = and(INTERMEDIATE, X_TO_ONE);
    public static Predicate<Relation> UNIDIRECTIONAL_X_TO_ONE = and(UNIDIRECTIONAL, X_TO_ONE);
    public static Predicate<Relation> BIDIRECTIONAL_X_TO_ONE = and(BIDIRECTIONAL, X_TO_ONE);
    public static Predicate<Relation> UNIDIRECTIONAL_MANY_TO_MANY = and(UNIDIRECTIONAL, MANY_TO_MANY);
    public static Predicate<Relation> BIDIRECTIONAL_MANY_TO_MANY = and(BIDIRECTIONAL, MANY_TO_MANY);

    public static Predicate<Relation> SIMPLE_MANY_TO_ONE = and(SIMPLE, MANY_TO_ONE);
    public static Predicate<Relation> COMPOSITE_MANY_TO_ONE = and(COMPOSITE_RELATION, MANY_TO_ONE);

    // ------- COLLECTION
    public static Predicate<Relation> COLLECTION = or(ONE_TO_MANY, or(ONE_TO_VIRTUAL_ONE, MANY_TO_MANY));
    public static Predicate<Relation> SIMPLE_COLLECTION = and(SIMPLE, COLLECTION);
    public static Predicate<Relation> COMPOSITE_COLLECTION = and(COMPOSITE_RELATION, COLLECTION);

    // -------- FORM SUPPORT
    public static Predicate<Relation> FORM_INPUT_FIELD_RELATION = or(UNIDIRECTIONAL_MANY_TO_MANY, X_TO_ONE);
}
