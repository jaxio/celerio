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

import com.jaxio.celerio.convention.GeneratedPackage;
import com.jaxio.celerio.model.Entity;
import com.jaxio.celerio.support.AbstractNamer;

import static org.apache.commons.lang.StringUtils.stripToNull;

public class ArbitraryClassNamer extends AbstractNamer {
    private Entity entity;
    private String suffix;
    private GeneratedPackage generatedPackage;

    public ArbitraryClassNamer(Entity entity, String suffix) {
        this.entity = entity;
        this.suffix = suffix == null ? "" : suffix;
    }

    public ArbitraryClassNamer(Entity entity, GeneratedPackage generatedPackage, String suffix) {
        this.entity = entity;
        this.generatedPackage = generatedPackage;
        this.suffix = suffix == null ? "" : suffix;
    }

    @Override
    public String getPackageName() {
        String packageName = entity.getEntityConfig().getRootPackage();

        if (generatedPackage != null && stripToNull(generatedPackage.getSubPackage()) != null) {
            packageName += (stripToNull(packageName) == null ? "" : ".") + generatedPackage.getSubPackage();
        }
        if (stripToNull(entity.getEntityConfig().getSubPackage()) != null) {
            packageName += (stripToNull(packageName) == null ? "" : ".") + entity.getEntityConfig().getSubPackage();
        }
        return packageName;
    }

    @Override
    public String getType() {
        return entity.getName() + suffix;
    }
}
