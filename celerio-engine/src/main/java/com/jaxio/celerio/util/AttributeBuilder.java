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

import static org.apache.commons.lang.StringUtils.isNotBlank;

public class AttributeBuilder {
    private StringBuilder ret = new StringBuilder();
    private boolean commentMode = false;

    public AttributeBuilder(boolean commentMode) {
        this.commentMode = commentMode;
    }

    public AttributeBuilder(String... attributes) {
        for (String attribute : attributes) {
            add(attribute);
        }
    }

    public void add(String attributeNameAndValue) {
        if (isNotBlank(attributeNameAndValue)) {
            if (ret.length() > 0) {
                ret.append(", ");
            }
            ret.append(attributeNameAndValue);
        }
    }

    public void addString(String value) {
        add("\"" + value + "\"");
    }

    public void addString(String name, String value) {
        add(name + " = \"" + value + "\"");
    }


    public void addInt(String name, int value) {
        add(name + " = " + value);
    }

    public void add(String attributeName, String[] values) {
        if (values.length == 1) {
            add(attributeName + " = " + values[0]);
            return;
        } else if (values.length == 0) {
            return;
        }

        if (ret.length() > 0) {
            ret.append(", ");
        }

        ret.append(attributeName).append(" = {");

        for (int i = 0; i < values.length - 1; i++) {
            ret.append(values[i]).append(", ");
        }
        ret.append(values[values.length - 1]).append("}");
    }

    public String getAttributes() {
        return ret.toString();
    }

    public boolean isEmpty() {
        return ret.length() == 0;
    }

    public String bindAttributesTo(String annotation) {
        String result = "";
        if (commentMode) {
            result = "// (comment in subclass) ";
        }
        if (isEmpty()) {
            return result + annotation;
        } else {
            return result + annotation + "(" + getAttributes() + ")";
        }
    }
}
