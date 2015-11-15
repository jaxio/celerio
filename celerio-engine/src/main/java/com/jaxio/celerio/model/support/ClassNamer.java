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
import com.jaxio.celerio.support.AbstractNamer;
import lombok.Getter;
import org.springframework.util.Assert;

import static com.jaxio.celerio.configuration.Util.firstNonNull;
import static com.jaxio.celerio.util.PackageUtil.assemblePackage;

public class ClassNamer extends AbstractNamer {
    private Entity entity;
    @Getter
    private ClassType classType;
    String type;

    public ClassNamer(Entity entity, ClassType classType) {
        Assert.notNull(entity);
        Assert.notNull(classType);

        this.entity = entity;
        this.classType = classType;
    }

    @Override
    public String getPackageName() {
        String rootPackage = firstNonNull(classType.getRootPackage(), entity.getEntityConfig().getRootPackage());
        return assemblePackage(rootPackage, getSubPackageName());
    }

    public String getSubPackageName() {
        if (entity.getConfig().getCelerio().getConfiguration().getConventions().prependEntitySubPackage()) {
            return assemblePackage(classType.getSubPackage(), entity.getEntityConfig().getSubPackage());
        } else {
            return assemblePackage(entity.getEntityConfig().getSubPackage(), classType.getSubPackage());
        }
    }

    public String getSubPackagePath() {
        return getSubPackageName().replace(".", "/");
    }

    @Override
    public String getType() {
        if (type == null) {
            type = classType.build(entity.getName());
        }
        return type;
    }
}