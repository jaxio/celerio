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

import static java.util.Collections.unmodifiableList;
import static org.apache.commons.lang.StringUtils.removeEnd;

@Getter
public class CompositeUnique implements Unique {
    private List<Attribute> attributes;
    private String name;

    public CompositeUnique(String name, List<Attribute> attributes) {
        this.name = name;
        this.attributes = unmodifiableList(attributes);
    }

    @Override
    public boolean isPotentialKey() {
        for (Attribute attribute : attributes) {
            if (attribute.isNullable()) {
                return false;
            }
        }

        return attributes.size() > 0;
    }

    @Override
    public boolean isGoodBusinessKeyCandidate() {
        for (Attribute attribute : attributes) {
            if (attribute.isNullable() || attribute.isInPk() || attribute.isInFk() || attribute.isDate()) {
                return false;
            }
        }

        return attributes.size() > 0;
    }

    /**
     * H2 does append _INDEX_2 to unique constraints names
     */
    @Override
    public String getName() {
        if (name == null) {
            return null;
        }
        if (name.endsWith("_INDEX_2")) {
            return removeEnd(name, "_INDEX_2");
        }
        return name;
    }
}
