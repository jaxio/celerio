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
import com.jaxio.celerio.model.Unique;

import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.not;

public class UniquePredicates {
    public static Predicate<Unique> COMPOSITE_UNIQUE = new Predicate<Unique>() {
        @Override
        public boolean apply(Unique unique) {
            return unique.getAttributes().size() > 1;
        }
    };

    public static Predicate<Unique> SIMPLE_PK_UNIQUE = new Predicate<Unique>() {
        @Override
        public boolean apply(Unique unique) {
            return unique.getAttributes().size() == 1 && unique.getAttributes().get(0).isSimplePk();
        }
    };

    public static Predicate<Unique> SIMPLE_UNIQUE = and(not(COMPOSITE_UNIQUE), not(SIMPLE_PK_UNIQUE));
}
