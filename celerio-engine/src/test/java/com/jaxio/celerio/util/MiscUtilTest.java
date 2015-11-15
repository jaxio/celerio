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

package com.jaxio.celerio.util;

import org.junit.Test;

import static com.jaxio.celerio.util.MiscUtil.*;
import static org.fest.assertions.Assertions.assertThat;

public class MiscUtilTest {

    @Test
    public void reserved() {
        assertThat(toName("int")).isEqualTo("MyInt");
        assertThat(toVar("int")).isEqualTo("myInt");
        assertThat(toVar("public")).isEqualTo("myPublic");
    }

    @Test
    public void noUnderscores() {
        assertThat(toName("AlreadyCapsed")).isEqualTo("AlreadyCapsed");
        assertThat(toVar("AlreadyCapsed")).isEqualTo("alreadyCapsed");
    }

    public void capitalizations() {
        assertThat(toName("ACCOUNT")).isEqualTo("Account");
        assertThat(toVar("ACCOUNT")).isEqualTo("account");
        assertThat(toName("account")).isEqualTo("Account");
        assertThat(toVar("account")).isEqualTo("account");
    }

    @Test
    public void convertions() {
        assertThat(toName("toto")).isEqualTo("Toto");
        assertThat(toName("Toto")).isEqualTo("Toto");
        assertThat(toName("To_to")).isEqualTo("ToTo");
        assertThat(toName("To__to")).isEqualTo("To_To");
        assertThat(toName("To-to")).isEqualTo("ToTo");
        assertThat(toName("To--to")).isEqualTo("To_To");

        assertThat(toVar("toto")).isEqualTo("toto");
        assertThat(toVar("Toto")).isEqualTo("toto");
        assertThat(toVar("To_to")).isEqualTo("toTo");
        assertThat(toVar("To__to")).isEqualTo("to_To");
        assertThat(toVar("To-to")).isEqualTo("toTo");
        assertThat(toVar("To--to")).isEqualTo("to_To");
    }

    @Test
    public void ignoreFirstUnderscore() {
        assertThat(toName("_toto")).isEqualTo("Toto");
        assertThat(toName("_Toto")).isEqualTo("Toto");
        assertThat(toName("_To_to")).isEqualTo("ToTo");
        assertThat(toName("_To__to")).isEqualTo("To_To");

        assertThat(toVar("_toto")).isEqualTo("toto");
        assertThat(toVar("_Toto")).isEqualTo("toto");
        assertThat(toVar("_To_to")).isEqualTo("toTo");
        assertThat(toVar("_To__to")).isEqualTo("to_To");

        assertThat(toName("-toto")).isEqualTo("Toto");
        assertThat(toName("-Toto")).isEqualTo("Toto");
        assertThat(toName("-To-to")).isEqualTo("ToTo");
        assertThat(toName("-To--to")).isEqualTo("To_To");

        assertThat(toVar("-toto")).isEqualTo("toto");
        assertThat(toVar("-Toto")).isEqualTo("toto");
        assertThat(toVar("-To-to")).isEqualTo("toTo");
        assertThat(toVar("-To--to")).isEqualTo("to_To");
    }

    @Test
    public void javaBeanNorm() {
        assertThat(toName("_x_xx")).isEqualTo("XXx");
        assertThat(toVar("_x_xx")).isEqualTo("XXx");
        assertThat(toName("AAb")).isEqualTo("AAb");
        assertThat(toVar("AAb")).isEqualTo("AAb");
    }

    @Test
    public void readableLabel() {
        assertThat(toReadableLabel("a")).isEqualTo("A");
        assertThat(toReadableLabel("OneOrTwo")).isEqualTo("One Or Two");
        assertThat(toReadableLabel("one_or_two")).isEqualTo("One Or Two");
        assertThat(toReadableLabel("one-or-two")).isEqualTo("One Or Two");
        assertThat(toReadableLabel("one-or_two")).isEqualTo("One Or Two");
        assertThat(toReadableLabel("one or two")).isEqualTo("One Or Two");
    }

    @Test
    public void readableLabelWithIs() {
        assertThat(toReadableLabel("is ok")).isEqualTo("Is Ok?");
        assertThat(toReadableLabel("has salt")).isEqualTo("Has Salt?");
    }

    @Test
    public void readablePlural() {
        assertThat(toReadablePluralLabel("banque")).isEqualTo("Banque");
        assertThat(toReadablePluralLabel("banquePays")).isEqualTo("Banque Pays");
        assertThat(toReadablePluralLabel("banquePayss")).isEqualTo("Banque Pays");
        assertThat(toReadablePluralLabel("BanquePayss")).isEqualTo("Banque Pays");
        assertThat(toReadablePluralLabel("Banque-Payss")).isEqualTo("Banque Pays");
        assertThat(toReadablePluralLabel("Banque__Payss")).isEqualTo("Banque Pays");
    }
}
