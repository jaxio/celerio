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

import com.glaforge.i18n.io.CharsetToolkit;
import lombok.Getter;

import java.io.UnsupportedEncodingException;

@Getter
public class Template {
    private String name;
    private TemplatePackInfo templatePackInfo;
    private byte[] bytes;

    public Template(String name, TemplatePackInfo templatePackInfo, byte[] bytes) {
        this.name = name;
        this.templatePackInfo = templatePackInfo;
        this.bytes = bytes;
    }

    public String getTemplate() throws UnsupportedEncodingException {
        return new String(bytes, getEncoding());
    }

    public boolean hasProjectLink() {
        return templatePackInfo != null && templatePackInfo.hasProjectLink();
    }

    public String getEncoding() {
        return new CharsetToolkit(getBytes()).guessEncoding().displayName();
    }

    @Override
    public String toString() {
        return getName() + " (" + bytes.length + ")";
    }
}
