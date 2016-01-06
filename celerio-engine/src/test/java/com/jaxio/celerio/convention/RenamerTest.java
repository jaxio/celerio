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

package com.jaxio.celerio.convention;

import com.jaxio.celerio.configuration.convention.Renamer;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class RenamerTest {

    @Test
    public void match() {
        Renamer renamer = new Renamer();
        renamer.setRegexp("^qrtz_");
        renamer.setReplace("");

        assertThat(renamer.match("glork")).isFalse();
        assertThat(renamer.match("qrtz_toto")).isTrue();
        assertThat(renamer.rename("qrtz_toto")).isEqualTo("toto");
        assertThat(renamer.rename("glork")).isEqualTo("glork");
    }

    @Test
    public void doc() {
        Renamer renamer = new Renamer();
        renamer.setRegexp("^.{3}_{1}");
        renamer.setReplace("Hop");

        assertThat(renamer.match("glork")).isFalse();
        assertThat(renamer.match("DTC_Abb")).isTrue();
        assertThat(renamer.rename("DTC_A")).isEqualTo("HopA");
        assertThat(renamer.rename("glork")).isEqualTo("glork");
    }
}