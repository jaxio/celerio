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

import lombok.Getter;
import lombok.Setter;

public class XmlFormatter {
    @Setter
    private boolean enableXmlFormatter = false;

    @Setter
    @Getter
    private Integer maximumLineWidth = 160;

    @Setter
    @Getter
    private Integer indent = 4;

    /*
     * Enable Formatter for all XML generated file. Default to false. Note: currently formatting sort attributes in alphabetical order. This is not convenient
     * for certain tags.
     */
    public boolean isEnableXmlFormatter() {
        return enableXmlFormatter;
    }
}