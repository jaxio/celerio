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

import com.google.common.base.Predicate;
import com.jaxio.celerio.model.Entity;
import com.jaxio.celerio.util.support.CurrentAndFlatListHolder;
import com.jaxio.celerio.util.support.ListGetter;

/**
 * Enable you to retrieve {@link PackageImport} required by the entity's ancestors,
 * the entity's descendants or simply the whole entity's family tree.
 * <p>
 * Note that this class is just a strongly typed {@link CurrentAndFlatListHolder}.
 * It is intended to reduce the definition of {@link PackageImport} lists in the entity object.
 */
public class EntityPackageImports extends CurrentAndFlatListHolder<PackageImport, Entity> {

    public EntityPackageImports(Entity entity, ListGetter<PackageImport, Entity> listGetter) {
        super(entity, listGetter);
    }

    public EntityPackageImports(Entity entity, ListGetter<PackageImport, Entity> listGetter, Predicate<PackageImport> predicate) {
        super(entity, listGetter, predicate);
    }
}
