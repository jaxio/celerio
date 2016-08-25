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

package com.jaxio.celerio.template;

import org.fest.assertions.Assertions;
import org.junit.Test;

public class TemplateExecutionTest {


    @Test
    public void dynamicAnnotationSupport() {
        TemplateExecution te = new TemplateExecution();

        String result = te.dynamicAnnotationSupport("com.test.Toto(\"abc\")", false, false);
        Assertions.assertThat(result).isEqualTo("@Toto(\"abc\")");

        result = te.dynamicAnnotationSupport("com.test.Toto", false, false);
        Assertions.assertThat(result).isEqualTo("@Toto");

        result = te.dynamicAnnotationSupport("Toto", false, false);
        Assertions.assertThat(result).isEqualTo("@Toto");
    }

    @Test
    public void dynamicAnnotationTakeOver1() {
        ImportsContext.setIsExtendedByUser(true); // bad, need fix
        ImportsHolder ih = new ImportsHolder("");
        ImportsContext.setCurrentImportsHolder(ih);
        TemplateExecution te = new TemplateExecution();

        String result = te.dynamicAnnotationTakeOver("com.test.Toto(\"abc\")");
        Assertions.assertThat(result).isEqualTo("// Make sure you use this annotation in your subclass\n// @com.test.Toto(\"abc\")\n");
        Assertions.assertThat(ih.getList()).isEmpty();

    }

    @Test
    public void dynamicAnnotationTakeOver2() {
        ImportsContext.setIsExtendedByUser(false); // bad, need fix
        ImportsHolder ih = new ImportsHolder("");
        ImportsContext.setCurrentImportsHolder(ih);
        TemplateExecution te = new TemplateExecution();

        String result = te.dynamicAnnotationTakeOver("com.test.Toto(\"abc\")");
        Assertions.assertThat(result).isEqualTo("@Toto(\"abc\")\n");
        Assertions.assertThat(ih.getList().get(0).getPackageName()).isEqualTo("com.test.Toto");
    }
}
