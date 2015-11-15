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

import com.jaxio.celerio.configuration.entity.Label;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Labels {

    @Setter
    private String fallBack;

    private Map<String, String> labels = new HashMap<String, String>();

    public Labels() {
    }

    public Labels(List<Label> labels) {
        if (labels == null) {
            return;
        }

        for (Label label : labels) {
            String key = label.getLang() == null ? "base" : label.getLang();
            this.labels.put(key, label.getValue());
        }
    }

    /**
     * Is there any base label? The fallback does not count.
     */
    public boolean hasBaseLabel() {
        return labels.get("base") != null;
    }

    public String getLabel() {
        String label = labels.get("base");
        return label != null ? label : fallBack;
    }

    public String getLabel(String lang) {
        String label = labels.get(lang);
        return label != null ? label : getLabel();
    }
}