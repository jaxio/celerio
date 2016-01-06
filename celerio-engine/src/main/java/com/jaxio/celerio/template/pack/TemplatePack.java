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

package com.jaxio.celerio.template.pack;

import java.io.IOException;
import java.util.List;

public interface TemplatePack {

    /**
     * Meta info for this pack.
     */
    TemplatePackInfo getTemplatePackInfo();

    /**
     * Returns the template pack name. For example "pack-backend".
     */
    String getName();

    /**
     * Returns the name of all the templates contained in this pack. If the template pack is a folder, the name may contains a relative path.
     */
    List<String> getTemplateNames();

    /**
     * Lookup a template by its name (as returned by getTemplateNames()).
     */
    Template getTemplateByName(String name) throws IOException;
}
