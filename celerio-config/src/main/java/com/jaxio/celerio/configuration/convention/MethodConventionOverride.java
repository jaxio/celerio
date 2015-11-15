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

import com.jaxio.celerio.convention.MethodConvention;
import lombok.Setter;

/**
 * change the prefix/suffix conventions for a given method
 */
public class MethodConventionOverride {
    @Setter
    private MethodConvention methodConvention;
    @Setter
    private String prefix;
    @Setter
    private String suffix;

    /*
     * Method type to override<br>
     * Example: GET_LOCALIZED
     */
    public MethodConvention getMethodConvention() {
        return methodConvention;
    }

    /*
     * Override the prefix for this methodConvention<br>
     * Example: get
     */
    public String getPrefix() {
        return prefix;
    }

    /*
     * Override the suffix for this methodConvention<br>
     * Example: Localized
     */
    public String getSuffix() {
        return suffix;
    }

    public void apply() {
        if (methodConvention == null) {
            throw new IllegalStateException(MethodConventionOverride.class + " not set");
        }
        if (prefix != null) {
            methodConvention.setPrefix(prefix);
        }
        if (suffix != null) {
            methodConvention.setSuffix(suffix);
        }
    }
}
