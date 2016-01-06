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

package com.jaxio.celerio.configuration.entity;

import com.jaxio.celerio.configuration.MetaAttribute;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.jaxio.celerio.configuration.Util.nonNull;

@Setter
@Getter
public class GenericGenerator {
    public static final String HIBERNATE_GENERATOR_UUIDHEX = "uuid";
    public static final String HIBERNATE_GENERATOR_ASSIGNED = "assigned";
    public static final String HIBERNATE_GENERATOR_NATIVE = "native";

    protected List<MetaAttribute> parameters = newArrayList();
    protected String name;
    protected String strategy;

    public void setParameters(List<MetaAttribute> parameters) {
        this.parameters = nonNull(parameters);
    }

    public boolean isStrategy(String check) {
        return check.equals(strategy);
    }
}
