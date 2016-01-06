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

package com.jaxio.celerio.configuration.database.h2;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class H2EnumValuesExtractorTest {
    @Test
    public void nothing() {
        assertThat(new H2EnumValuesExtractor().extract("nothing")) //
                .isEmpty();
    }

    @Test
    public void strings() {
        assertThat(new H2EnumValuesExtractor().extract("(CONTENT_TYPE IN('a', 'b', 'c'))")) //
                .hasSize(3) //
                .containsExactly("a", "b", "c");
        assertThat(new H2EnumValuesExtractor().extract("(CONTENT_TYPE   IN('a ','b','c'))")) //
                .hasSize(3) //
                .containsExactly("a", "b", "c");
    }

    @Test
    public void numerics() {
        assertThat(new H2EnumValuesExtractor().extract("(ONE_TWO_OR_THREE IN(1, 2, 3))")) //
                .hasSize(3) //
                .containsExactly("1", "2", "3");
    }
}
