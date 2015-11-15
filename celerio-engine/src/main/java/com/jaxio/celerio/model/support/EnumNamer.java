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

import com.jaxio.celerio.configuration.entity.EnumConfig;
import com.jaxio.celerio.convention.ClassType;
import com.jaxio.celerio.support.AbstractNamer;
import org.springframework.util.Assert;

public class EnumNamer extends AbstractNamer {
    private final EnumConfig enumConfig;
    private final ClassType classType;
    private String packageName; // lazy init
    private String type; // lazy init

    public EnumNamer(EnumConfig enumConfig, ClassType classType) {
        Assert.notNull(enumConfig);
        Assert.notNull(classType);
        this.enumConfig = enumConfig;
        this.classType = classType;
    }

    @Override
    public String getPackageName() {
        if (packageName != null) {
            return packageName;
        }

        packageName = "";

        // root
        if (enumConfig.hasRootPackage()) {
            packageName = enumConfig.getRootPackage();
        } else if (classType.getRootPackage() != null) {
            packageName = classType.getRootPackage();
        }

        // sub (follows enumConfig doc)
        if (classType.hasSubPackage() && enumConfig.hasSubPackage()) {
            packageName += "." + classType.getSubPackage() + "." + enumConfig.getSubPackage();
        } else if (enumConfig.hasSubPackage()) {
            packageName += "." + enumConfig.getSubPackage();
        } else if (classType.hasSubPackage()) {
            packageName += "." + classType.getSubPackage();
        }

        return packageName;
    }

    @Override
    public String getType() {
        if (type == null) {
            type = classType.build(enumConfig.getName());
        }
        return type;
    }
}