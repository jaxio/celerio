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

public class JavaPackageTest {

    private Validator validator = ValidatorUtil.getValidator();

    @Test
    public void validJavaPackages() {
        assertThat(validate("d")).isEmpty();
        assertThat(validate("d.d")).isEmpty();
        assertThat(validate("d.d.d")).isEmpty();
        assertThat(validate("d-.d.d")).isEmpty();
        assertThat(validate("d-.d-.d")).isEmpty();
        assertThat(validate("d-12.d-12.d")).isEmpty();
    }

    @Test
    public void emptyIsValidByDefault() {
        assertThat(validate("")).isEmpty();
        assertThat(validate(null)).isEmpty();
    }

    @Test
    public void emptyIsInvalidWhenRequested() {
        assertThat(validateNotEmpty("")).isNotEmpty();
        assertThat(validateNotEmpty(null)).isNotEmpty();
    }

    @Test
    public void invalidJavaPackages() {
        assertThat(validate(" ")).isNotEmpty();
        assertThat(validate("-")).isNotEmpty();
        assertThat(validate(",")).isNotEmpty();
        assertThat(validate("a b")).isNotEmpty();
        assertThat(validate("1.a")).isNotEmpty();
        assertThat(validate("a.1")).isNotEmpty();
        assertThat(validate("a.-aaa")).isNotEmpty();
    }

    private Set<ConstraintViolation<Dummy>> validate(String s) {
        return validator.validate(new Dummy(s));
    }

    private Set<ConstraintViolation<DummyDoNotAllowEmpty>> validateNotEmpty(String s) {
        return validator.validate(new DummyDoNotAllowEmpty(s));
    }

    public class Dummy {
        @JavaPackage
        public String javaPackage;

        public Dummy(String s) {
            this.javaPackage = s;
        }
    }

    public class DummyDoNotAllowEmpty {
        @JavaPackage(allowEmpty = false)
        public String javaPackage;

        public DummyDoNotAllowEmpty(String s) {
            this.javaPackage = s;
        }
    }
}
