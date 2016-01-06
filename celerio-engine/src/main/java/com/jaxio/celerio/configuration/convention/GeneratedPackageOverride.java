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

import com.jaxio.celerio.convention.GeneratedPackage;
import lombok.Setter;

/*
 * Override the convention for a given GeneratedPackage
 */
public class GeneratedPackageOverride {
    @Setter
    private GeneratedPackage generatedPackage;
    @Setter
    private String rootPackage;
    @Setter
    private String subPackage;

    /*
     * The GeneratedPackage to override
     */
    public GeneratedPackage getGeneratedPackage() {
        return generatedPackage;
    }

    /*
     * Override the root package<br>
     * Example: com.yourcompany
     */
    public String getRootPackage() {
        return rootPackage;
    }

    /*
     * Override the sub package, if rootPackage is also specified they will be merged.<br>
     * Example: my.subpackage
     */
    public String getSubPackage() {
        return subPackage;
    }

    public void apply() {
        if (generatedPackage == null) {
            throw new IllegalStateException(GeneratedPackageOverride.class + " not set");
        }
        if (rootPackage != null) {
            generatedPackage.setRootPackage(rootPackage);
        }

        if (subPackage != null) {
            generatedPackage.setSubPackage(subPackage);
        }
    }
}