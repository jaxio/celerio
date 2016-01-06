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

package com.jaxio.celerio.model.support.jquery;

import com.jaxio.celerio.model.Attribute;
import com.jaxio.celerio.spi.AttributeSpi;

public class JQueryAttribute implements AttributeSpi {
    private Attribute attribute;

    @Override
    public void init(Attribute attribute) {
        this.attribute = attribute;
    }

    /**
     * @return "jQuery"
     */
    @Override
    public String velocityVar() {
        return "jQuery";
    }

    @Override
    public Object getTarget() {
        return this;
    }

    public String getConstraints() {
        StringBuffer ret = new StringBuffer();
        if (attribute.isEmail()) {
            addConstraint(ret, "email");
        }
        if (attribute.isNumeric() && !attribute.isFileSize()) {
            addConstraint(ret, "number");
        }
        if (attribute.isRequired()) {
            addConstraint(ret, "required");
        }
        if (attribute.isFixedSize()) {
            addConstraint(ret, "{maxlength: " + attribute.getSize() + " " + "minlength: " + attribute.getSize() + "}");
        } else if (attribute.getColumnConfig().hasMin()) {
            addConstraint(ret, "{minlength: " + attribute.getColumnConfig().getMin() + "}");
        } else if (attribute.isString()) {
            addConstraint(ret, "{maxlength: " + attribute.getSize() + "}");
        }

        return ret.toString();
    }

    private void addConstraint(StringBuffer ret, String constraint) {
        if (ret.length() != 0) {
            ret.append(" ");
        }
        ret.append(constraint);
    }

}
