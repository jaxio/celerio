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

package com.jaxio.celerio.model.support.jsf;

import com.jaxio.celerio.model.Attribute;
import com.jaxio.celerio.spi.AttributeSpi;

public class JsfAttribute implements AttributeSpi {
    private static final int SIZE_FOR_SIGN_AND_COMMA = "+,".length();
    private static final int SIZE_MAXIMAL_LENGTH = 20;
    private Attribute attribute;

    @Override
    public void init(Attribute attribute) {
        this.attribute = attribute;
    }

    /**
     * @return "jsf"
     */
    @Override
    public String velocityVar() {
        return "jsf";
    }

    @Override
    public Object getTarget() {
        return this;
    }

    private String getRequired() {
        return !attribute.isRequired() ? "" : "required=\"true\" ";
    }

    private String getMaxlength() {
        if (!(attribute.isNumeric() || attribute.isString()) || attribute.isEnum()) {
            return "";
        }
        int maxLength = 0;
        if (attribute.hasDigits()) {
            maxLength = attribute.getSize() + attribute.getColumnConfig().getDecimalDigits() + SIZE_FOR_SIGN_AND_COMMA;
        } else {
            maxLength = attribute.getSize();
        }
        return "maxlength=\"" + maxLength + "\" ";
    }

    private String getSize() {
        if (!(attribute.isNumeric() || attribute.isString()) || attribute.isEnum()) {
            return "";
        }
        int size = Integer.MAX_VALUE;
        if (attribute.hasDigits()) {
            size = attribute.getSize() + attribute.getColumnConfig().getDecimalDigits() + SIZE_FOR_SIGN_AND_COMMA;
        } else if (attribute.isFixedSize()) {
            size = attribute.getSize();
        }
        return size < SIZE_MAXIMAL_LENGTH ? "size=\"" + size + "\" " : "";
    }

    public String getFormAttributes() {
        StringBuilder sb = new StringBuilder();
        sb.append(getRequired());
        sb.append(getSearchAttributes());
        sb.append(getPaddingConverter());
        return sb.toString();
    }

    public String getSearchAttributes() {
        StringBuilder sb = new StringBuilder();
        sb.append(getJodaConverter());
        sb.append(getMaxlength());
        sb.append(getSize());
        return sb.toString();
    }

    public String getJodaConverter() {
        if (attribute.isLocalDate()) {
            return "converter=\"#{localDateConverter}\"";
        }

        if (attribute.isLocalDateTime()) {
            return "converter=\"#{localDateTimeConverter}\"";
        }

        return "";
    }

    public String getPaddingConverter() {
        if (attribute.isString() && attribute.isCharPadding()) {
            return "converter=\"#{paddingConverter}\"";
        }

        return "";
    }

    public String getCalendarPattern() {
        if (attribute.isLocalDate()) {
            return "pattern=\"#{localDateConverter.pattern}\"";
        }

        if (attribute.isLocalDateTime()) {
            return "pattern=\"#{localDateTimeConverter.pattern}\"";
        }

        return "pattern=\"yyyy-MM-dd\""; // TODO: do not hardcode it
    }
}
