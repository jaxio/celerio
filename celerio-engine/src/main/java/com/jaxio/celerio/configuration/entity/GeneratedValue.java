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

import lombok.Setter;

import javax.persistence.GenerationType;

import static org.springframework.util.StringUtils.hasLength;

@Setter
public class GeneratedValue {
    protected String generator;
    protected GenerationType strategy;

    /*
     * The name of the primary key generator to use
     */
    public String getGenerator() {
        return generator;
    }

    public boolean hasGenerator() {
        return hasLength(generator);
    }

    /*
     * The primary key generation strategy that the persistence provider must use to generate the annotated entity primary key.
     */
    public GenerationType getStrategy() {
        return strategy;
    }

    public boolean hasStrategy() {
        return strategy != null;
    }
}
