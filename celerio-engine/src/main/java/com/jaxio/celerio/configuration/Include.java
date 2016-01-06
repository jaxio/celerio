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

package com.jaxio.celerio.configuration;

import lombok.Setter;

/**
 * Include a configuration file dedicated to entityConfigs. Use it on large project to split your entityConfigs configuration into smaller pieces.
 */
public class Include {
    @Setter
    private String filename;

    /*
     * The path to a configuration file whose entityConfigs tag will be loaded. The path must be relative to the folder containing the main configuration file.
     * Beware, only the entityConfigs tag will be loaded from this file. For example: includes/ref/country.xml
     */
    public String getFilename() {
        return filename;
    }
}
