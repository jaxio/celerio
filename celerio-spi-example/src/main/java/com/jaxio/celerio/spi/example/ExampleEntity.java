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

import com.jaxio.celerio.model.Entity;
import com.jaxio.celerio.spi.EntitySpi;

public class ExampleEntity implements EntitySpi {

    private Entity entity;

    @Override
    public void init(Entity entity) {
        this.entity = entity;
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
        return "Hello from ExampleEntity: this entity has " + entity.getCurrentAttributes().size() + " attributes";
    }
}