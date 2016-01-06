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

import static com.jaxio.celerio.convention.MethodConvention.*;
import static org.apache.commons.lang.WordUtils.capitalize;

/**
 * Identifiable.java is a central interface in the generated code. Its property can be configured through configuration. Velocity templates must use the
 * identifiableProperty to get getter/setter/iser.
 */
public class IdentifiableProperty {
    private String getter;
    private String setter;
    private String iser;
    private String var;
    private String varUp;

    public IdentifiableProperty(String property) {
        getter = GET.build(property);
        setter = SET.build(property);
        iser = HAS.build(property);
        var = Character.toLowerCase(property.charAt(0)) + property.substring(1);
        varUp = capitalize(property);
    }

    public String getGetter() {
        return getter;
    }

    public String getSetter() {
        return setter;
    }

    public String getIser() {
        return iser;
    }

    public String getVar() {
        return var;
    }

    public String getVarUp() {
        return varUp;
    }
}
