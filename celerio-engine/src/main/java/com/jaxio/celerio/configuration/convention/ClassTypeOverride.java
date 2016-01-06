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

import com.jaxio.celerio.convention.ClassType;
import com.jaxio.celerio.convention.GeneratedPackage;
import lombok.Setter;

/*
 * Override the class conventions such as GeneratedPackage, suffix and prefixes
 */
public class ClassTypeOverride {
    @Setter
    private ClassType classType;
    @Setter
    private String prefix;
    @Setter
    private String suffix;
    @Setter
    private GeneratedPackage generatedPackage;

    /*
     * The ClassType to override
     */
    public ClassType getClassType() {
        return classType;
    }

    /*
     * Override the prefix for this ClassType
     */
    public String getPrefix() {
        return prefix;
    }

    /*
     * Override the suffix for this ClassType
     */
    public String getSuffix() {
        return suffix;
    }

    /*
     * Override the GeneratedPackage for this ClassType
     */
    public GeneratedPackage getGeneratedPackage() {
        return generatedPackage;
    }

    public void apply() {
        if (classType == null) {
            throw new IllegalStateException(ClassTypeOverride.class + " not set");
        }
        if (prefix != null) {
            classType.setPrefix(prefix);
        }
        if (suffix != null) {
            classType.setSuffix(suffix);
        }
        if (generatedPackage != null) {
            classType.setGeneratedPackage(generatedPackage);
        }
    }
}