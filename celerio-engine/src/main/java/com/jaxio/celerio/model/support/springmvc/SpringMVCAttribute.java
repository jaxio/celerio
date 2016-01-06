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

package com.jaxio.celerio.model.support.springmvc;

import com.jaxio.celerio.model.Attribute;
import com.jaxio.celerio.spi.AttributeSpi;

public class SpringMVCAttribute implements AttributeSpi {
    private Attribute attribute;

    @Override
    public void init(Attribute attribute) {
        this.attribute = attribute;
    }

    /**
     * @return springMvc
     */
    @Override
    public String velocityVar() {
        return "springMvc";
    }

    @Override
    public Object getTarget() {
        return this;
    }

    // TODO : fix the isInFk for pureMany2Many
    public String getTagName() {
        if (attribute.isHidden() && !attribute.isFile()) {
            return "hidden";
        } else if (attribute.isInFk() && attribute.hasXToOneRelation()) {
            return "select";
        } else if (attribute.isFile()) {
            return "file";
        } else if (attribute.isPassword()) {
            return "password";
        } else if (attribute.isString() && (attribute.getColumnConfig().getSize() > 256 || attribute.getColumnConfig().getSize() == -1)) {
            return "textarea";
        } else if (attribute.isBoolean()) {
            return "checkbox";
        } else {
            return "input";
        }
    }

    public boolean isFile() {
        return "file".equals(getTagName());
    }

    public boolean isTextArea() {
        return "textarea".equals(getTagName());
    }

    public boolean isPassword() {
        return "password".equals(getTagName());
    }

    public boolean isCheckbox() {
        return "checkbox".equals(getTagName());
    }

    public boolean isSelect() {
        return "select".equals(getTagName());
    }

    public boolean isHidden() {
        return "hidden".equals(getTagName());
    }

    public String getParamList() {
        return attribute.getEntity().getWebSupport().getVar() + ".selectMap";
    }

    public String getVarPath() {
        return attribute.getVarPath();
    }

    public String getExtraAttributes() {
        if (attribute.isString() && "input".equals(getTagName())) {
            String ret = " maxlength=\"" + attribute.getSize() + "\"";
            if (attribute.getSize() < 10) {
                return ret + " size=\"" + attribute.getSize() + "\"";
            } else if (attribute.getSize() < 30) {
                return ret + " size=\"30\"";
            } else {
                return ret + " size=\"100\"";
            }
        } else if (attribute.hasXToOneRelation()) {
            return " size=\"100\"";
        } else if (attribute.isDate()) {
            return " size=\"11\"";
        }
        return "";
    }

    public boolean hasCssClass() {
        return !"".equals(getCssClass());
    }

    public String getCssClass() {
        StringBuffer ret = new StringBuffer();
        if (attribute.isDate()) {
            addClass(ret, "datepicker");
        }
        return ret.toString();
    }

    private void addClass(StringBuffer ret, String cssClass) {
        if (cssClass == null || cssClass.length() == 0) {
            return;
        }
        if (ret.length() != 0) {
            ret.append(" ");
        }
        ret.append(cssClass);
    }
}
