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

import com.jaxio.celerio.convention.WellKnownFolder;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.jaxio.celerio.convention.WellKnownFolder.JAVA;
import static com.jaxio.celerio.convention.WellKnownFolder.JAVA_TEST;

@Service
public class OverwriteFilesService {

    private List<String> excludedPatterns = newArrayList("pom.xml");
    private List<WellKnownFolder> excludedFolders = newArrayList(JAVA, JAVA_TEST);

    public boolean canOverwrite(String file) {
        for (String pattern : excludedPatterns) {
            if (file.endsWith(pattern)) {
                return false;
            }
        }

        for (WellKnownFolder excludedFolder : excludedFolders) {
            if (file.contains(excludedFolder.getGeneratedFolder())) {
                return false;
            }
        }

        return true;
    }
}
