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

package com.jaxio.celerio.configuration.validation;

import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

import static org.fest.assertions.Assertions.assertThat;

public class NotReservedJavaTest {

    private Validator validator = ValidatorUtil.getValidator();

    @Test
    public void validJavaWord() {
        assertThat(validate("d")).isEmpty();
        assertThat(validate("a_")).isEmpty();
        assertThat(validate("a1")).isEmpty();
        assertThat(validate("a1_a")).isEmpty();
    }

    @Test
    public void emptyIsValidByDefault() {
        assertThat(validate("")).isEmpty();
        assertThat(validate(null)).isEmpty();
    }

    @Test
    public void reservedJavaWords() {
        assertThat(validate("continue")).isNotEmpty();
        assertThat(validate("entity")).isNotEmpty();
    }

    private Set<ConstraintViolation<Dummy>> validate(String s) {
        return validator.validate(new Dummy(s));
    }

    public class Dummy {
        @NotReservedJava
        public String java;

        public Dummy(String s) {
            this.java = s;
        }
    }
}
