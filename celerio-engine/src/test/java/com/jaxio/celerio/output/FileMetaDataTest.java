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

package com.jaxio.celerio.output;

import com.jaxio.celerio.util.IOUtil;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.fest.assertions.Assertions.assertThat;

public class FileMetaDataTest {

    @Test
    public void equalsTest() throws IOException {

        // fmd1
        File f1 = new File("target/test/my-file-1");
        File parent = f1.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }

        (new IOUtil()).stringToFile("some content", f1);
        FileMetaData fmd1 = new FileMetaData(null, null, "target/test/my-file-1", f1);

        // fmd2
        File f2 = new File("target/test/my-file-1");
        FileMetaData fmd2 = new FileMetaData(null, null, "target/test/my-file-1", f2);
        assertThat(fmd1).isEqualTo(fmd2);

        // now let's change the file.
        File f3 = new File("target/test/my-file-1");
        (new IOUtil()).stringToFile("some content...", f3);
        assertThat(fmd2).isNotEqualTo(new FileMetaData(null, null, "target/test/my-file-1", f3));
    }
}
