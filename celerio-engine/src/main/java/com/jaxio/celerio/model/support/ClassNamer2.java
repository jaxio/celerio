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

import com.jaxio.celerio.model.Entity;
import com.jaxio.celerio.support.AbstractNamer;
import org.springframework.util.Assert;

import static com.jaxio.celerio.configuration.Util.firstNonNull;
import static com.jaxio.celerio.util.PackageUtil.assemblePackage;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.WordUtils.capitalize;


/**
 * ClassNamer2 are constructed using <code>entityContextProperty</code> declared in the configuration.
 * <p>
 * Note: ideally this should replace {@link ClassNamer} with ClassNamer2,
 * but it implies a non trivial refactoring (ClassType, GeneratedPackage etc...)
 */
public class ClassNamer2 extends AbstractNamer {

    private String type;
    private String packageName;

    public ClassNamer2(Entity entity, String rootPackage, String subPackage, String prefix, String suffix) {
        Assert.notNull(entity);
        prefix = isBlank(prefix) ? "" : prefix;
        suffix = isBlank(suffix) ? "" : suffix;
        this.type = buildType(prefix, entity.getName(), suffix);
        this.packageName = buildPackageName(rootPackage, subPackage, entity);
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getPackageName() {
        return packageName;
    }

    private static String buildPackageName(String rootPackage, String subPackage, Entity entity) {
        String root = firstNonNull(rootPackage, entity.getEntityConfig().getRootPackage());
        if (entity.getConfig().getCelerio().getConfiguration().getConventions().prependEntitySubPackage()) {
            return assemblePackage(root, subPackage, entity.getEntityConfig().getSubPackage());
        } else {
            return assemblePackage(root, entity.getEntityConfig().getSubPackage(), subPackage);
        }
    }

    private static String buildType(String prefix, String baseName, String suffix) {
        if (!isBlank(prefix)) {
            baseName = capitalize(baseName);
        }
        return prefix + baseName + suffix;
    }
}