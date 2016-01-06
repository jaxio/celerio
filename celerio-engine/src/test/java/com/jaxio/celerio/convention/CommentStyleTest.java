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

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class CommentStyleTest {
    @Test
    public void javadoc() {
        assertThat(CommentStyle.JAVADOC.decorate("un", "")).isEqualTo("" //
                + "/**\n" //
                + " * un\n" //
                + " */\n");
        assertThat(CommentStyle.JAVADOC.decorate("un\ndeux", "")).isEqualTo("" //
                + "/**\n" //
                + " * un\n" //
                + " * deux\n" //
                + " */\n");
    }

    @Test
    public void dash() {
        assertThat(CommentStyle.DASH.decorate("un\\ndeux", "")).isEqualTo("" //
                + "#\n" //
                + "# un\n" //
                + "# deux\n" //
                + "#\n");
    }

    @Test
    public void resolveEncodedReturns() {
        assertThat(CommentStyle.JAVADOC.decorate("un\\ndeux", "")).isEqualTo("" //
                + "/**\n" //
                + " * un\n" //
                + " * deux\n" //
                + " */\n");
    }

    @Test
    public void removeExtraSpaces() {
        assertThat(CommentStyle.JAVADOC.decorate("un   deux", "")).isEqualTo("" //
                + "/**\n" //
                + " * un deux\n" //
                + " */\n");
    }

    @Test
    public void resolveConflicts() {
        assertThat(CommentStyle.JAVADOC.decorate("Input a si /* comment truncated */", "")).isEqualTo("" //
                + "/**\n" //
                + " * Input a si\n" //
                + " * comment truncated\n" //
                + " */\n");

        assertThat(CommentStyle.JAVADOC.decorate("Input /** here */ a si /* comment truncated */", "")).isEqualTo("" //
                + "/**\n" //
                + " * Input\n" //
                + " * here\n" //
                + " * a si\n" //
                + " * comment truncated\n" //
                + " */\n");

    }
}
