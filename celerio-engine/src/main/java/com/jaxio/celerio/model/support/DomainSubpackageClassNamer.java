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

import com.jaxio.celerio.convention.ClassType;
import com.jaxio.celerio.model.Entity;

/**
 * create a subpackage with then entity name.
 */
public class DomainSubpackageClassNamer extends ClassNamer {
    private Entity entity;

    public DomainSubpackageClassNamer(Entity entity, ClassType classType) {
        super(entity, classType);
        this.entity = entity;
    }

    @Override
    public String getPackageName() {
        return super.getPackageName() + "." + entity.getModel().getType().toLowerCase();
    }
}
