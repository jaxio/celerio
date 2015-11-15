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

import org.junit.Test;

import static org.apache.commons.io.FilenameUtils.normalize;
import static org.fest.assertions.Assertions.assertThat;

public class PreviousEngineTest {

    @Test
    public void convertToFullFilename() throws Exception {
        PreviousEngine pe = new PreviousEngine();

        String ffn = pe.convertToFullFilename("com.jaxio", "MyUtil.java", "src/main/something");
        assertThat(normalize("src/main/something/com/jaxio/MyUtil.java")).isEqualTo(ffn);

        ffn = pe.convertToFullFilename(null, "MyUtil.java", "src/main/something");
        assertThat(normalize("src/main/something/MyUtil.java")).isEqualTo(ffn);

        ffn = pe.convertToFullFilename(null, "MyUtil.java", null);
        assertThat("MyUtil.java").isEqualTo(ffn);

        ffn = pe.convertToFullFilename("com\\jaxio", "MyUtil.java", "src/main\\something");
        assertThat(normalize("src/main/something/com/jaxio/MyUtil.java")).isEqualTo(ffn);
    }
}
