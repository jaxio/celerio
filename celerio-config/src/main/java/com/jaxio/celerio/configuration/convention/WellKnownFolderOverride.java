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

package com.jaxio.celerio.configuration.convention;

import com.jaxio.celerio.convention.WellKnownFolder;
import lombok.Setter;

/*
 * change the convention for a given well known folder
 */
public class WellKnownFolderOverride {
    @Setter
    private WellKnownFolder wellKnownFolder;
    @Setter
    private String folder;
    @Setter
    private String generatedFolder;

    /*
     * WellKnownFolder to override
     */
    public WellKnownFolder getWellKnownFolder() {
        return wellKnownFolder;
    }

    /*
     * Override the folder for this WellKnownFolder
     */
    public String getFolder() {
        return folder;
    }

    /*
     * Override the generated folder for this WellKnownFolder
     */
    public String getGeneratedFolder() {
        return generatedFolder;
    }

    public void apply() {
        if (wellKnownFolder == null) {
            throw new IllegalStateException(WellKnownFolderOverride.class + " not set");
        }
        if (folder != null) {
            wellKnownFolder.setFolder(folder);
        }

        if (generatedFolder != null) {
            wellKnownFolder.setGeneratedFolder(generatedFolder);
        }
    }
}
