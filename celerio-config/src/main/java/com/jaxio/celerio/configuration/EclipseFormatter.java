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

import lombok.Data;

import static com.jaxio.celerio.configuration.Util.firstNonNull;

@Data
public class EclipseFormatter {
    private FormatterEnum formatterChoice = FormatterEnum.USE_ECLIPSE_DEFAULT;
    private String formatterFile;

    public void setFormatterChoice(FormatterEnum formatterChoice) {
        this.formatterChoice = firstNonNull(formatterChoice, this.formatterChoice);
    }
}