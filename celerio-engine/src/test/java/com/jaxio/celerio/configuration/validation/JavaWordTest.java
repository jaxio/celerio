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

public class JavaWordTest {

    private Validator validator = ValidatorUtil.getValidator();

    @Test
    public void validJavaWords() {
        assertThat(validate("a")).isEmpty();
        assertThat(validate("A")).isEmpty();
        assertThat(validate("Aa")).isEmpty();
        assertThat(validate("aa1")).isEmpty();
        assertThat(validate("aa1__")).isEmpty();
        assertThat(validate("aa1__a")).isEmpty();
        assertThat(validate("_a")).isEmpty();
    }

    @Test
    public void emptyIsValidByDefault() {
        assertThat(validate("")).isEmpty();
        assertThat(validate(null)).isEmpty();
    }

    @Test
    public void invalidJavaWords() {
        assertThat(validate(" ")).isNotEmpty();
        assertThat(validate("1")).isNotEmpty();
        assertThat(validate("a a")).isNotEmpty();
        assertThat(validate("a.a")).isNotEmpty();
        assertThat(validate("aéa")).isNotEmpty();
    }

    private Set<ConstraintViolation<Dummy>> validate(String s) {
        return validator.validate(new Dummy(s));
    }

    public class Dummy {
        @JavaWord
        public String javaWord;

        public Dummy(String s) {
            this.javaWord = s;
        }
    }
}
