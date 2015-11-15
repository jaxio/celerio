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

package com.jaxio.celerio.model.unique;

import com.jaxio.celerio.model.Attribute;
import com.jaxio.celerio.model.Unique;
import lombok.Getter;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.unmodifiableList;

@Getter
public class SimpleUnique implements Unique {
    private Attribute attribute;
    private List<Attribute> attributes;
    private String name;

    public SimpleUnique(String name, Attribute attribute) {
        this.name = name;
        this.attribute = attribute;
        this.attributes = unmodifiableList(newArrayList(attribute));
    }

    @Override
    public boolean isPotentialKey() {
        return !attribute.isNullable();
    }

    @Override
    public boolean isGoodBusinessKeyCandidate() {
        return !attribute.isNullable() && !attribute.isInPk() && !attribute.isInFk() && !attribute.isDate();
    }
}