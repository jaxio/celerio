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

package com.jaxio.celerio.spi.example;

import com.jaxio.celerio.model.Relation;
import com.jaxio.celerio.spi.RelationSpi;

public class ExampleRelation implements RelationSpi {

    private Relation relation;

    public boolean compatibleWith(Relation relation) {
        return true;
    }

    @Override
    public void init(Relation relation) {
        this.relation = relation;
    }

    @Override
    public String velocityVar() {
        return "example";
    }

    @Override
    public Object getTarget() {
        return this;
    }

    public String getHello() {
        return "Hello from ExampleRelation: " + relation.getKind();
    }
}