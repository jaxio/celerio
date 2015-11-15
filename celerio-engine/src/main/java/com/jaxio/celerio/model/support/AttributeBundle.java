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

package com.jaxio.celerio.model.support;

import com.jaxio.celerio.model.Attribute;
import com.jaxio.celerio.support.AbstractNamer;
import com.jaxio.celerio.util.MiscUtil;
import lombok.Getter;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@Getter
public class AttributeBundle extends AbstractNamer {

    private List<Attribute> attributes = newArrayList();
    private String base;
    private String var;
    private String type;
    private String packageName;

    public AttributeBundle(Attribute firstAttribute) {
        base = firstAttribute.getColumnNameWithoutLanguage();
        var = MiscUtil.toVar(base); // TODO: apply field naming (see columnConfig Factory)
        type = firstAttribute.getType();
        packageName = firstAttribute.getPackageName();
        addAttribute(firstAttribute);
    }

    public void addAttribute(Attribute attribute) {
        if (base.equals(attribute.getColumnNameWithoutLanguage())) {
            attributes.add(attribute);
        } else {
            throw new IllegalStateException("Attempting to add unrelated attribute");
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Attribute a : attributes) {
            sb.append(a.getColumnName()).append(" ");
        }
        return sb.toString();
    }
}