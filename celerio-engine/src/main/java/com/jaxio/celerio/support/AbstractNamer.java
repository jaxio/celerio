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

package com.jaxio.celerio.support;

import com.jaxio.celerio.aspects.ForbiddenWhenBuilding;
import com.jaxio.celerio.util.StringUtil;
import org.apache.commons.lang.StringUtils;

import java.io.File;

import static com.jaxio.celerio.convention.MethodConvention.*;
import static com.jaxio.celerio.util.StringUtil.getFirstCharacterUppered;
import static com.jaxio.celerio.util.StringUtil.getPlural;

public abstract class AbstractNamer implements Namer {

    @Override
    public abstract String getPackageName();

    @Override
    public abstract String getType();

    private String var;

    @ForbiddenWhenBuilding
    @Override
    public String getVar() {
        if (var == null) {
            String typeEscaped = StringUtil.escape(getType());
            if (typeEscaped.length() >= 3) {
                String first3cars = typeEscaped.substring(0, 3);
                if (first3cars.equals(first3cars.toUpperCase())) {
                    return typeEscaped;
                }
            }
            var = StringUtil.getFirstCharacterLowered(typeEscaped);
        }
        return var;
    }

    @Override
    public String getPath() {
        return getPackageName().replace('.', File.separatorChar);
    }

    String fullType;

    @Override
    public String getFullType() {
        if (fullType == null) {
            if (getPackageName().isEmpty()) {
                fullType = getType();
            } else {
                fullType = getPackageName() + "." + getType();
            }
        }
        return fullType;
    }

    @Override
    public String getTypeLow() {
        return StringUtil.getFirstCharacterLowered(getType());
    }

    /**
     * ex: com.jaxio.toto.tutu ==> getPackageNode(0) = tutu ==> getPackageNode(1) = toto etc.. note: useful on evdev project.
     */
    String[] packageNodes;

    /**
     * Get node of the package name.
     * <p>
     * Ex: Assuming the package name is<code>com.jaxio.toto.tutu</code> you get <br>
     * <code>getPackageNode(0) -&gt; tutu</code> <br>
     * <code>getPackageNode(1) = toto</code> etc.
     */
    public String getPackageNode(int index) {
        if (packageNodes == null) {
            packageNodes = StringUtils.split(getPackageName(), '.');
        }

        int i = packageNodes.length - 1 - index;
        if (i >= 0 && i < packageNodes.length) {
            return packageNodes[i];
        }
        return "";
    }

    /**
     * Same as {@link #getPackageNode} with the first character upper cased.
     */
    public String getPackageNodeUp(int index) {
        return getFirstCharacterUppered(getPackageNode(index));
    }

    /**
     * return the concatenation of #{@link #getType} + <code>"Test"</code>.
     */
    public String getTestType() {
        return getType() + "Test";
    }

    @Override
    public String getVarUp() {
        return getFirstCharacterUppered(getVar());
    }

    private String vars;

    @Override
    public String getVars() {
        if (vars == null) {
            vars = getPlural(getVar());
        }
        return vars;
    }

    @Override
    public String getVarsUp() {
        return getFirstCharacterUppered(getVars());
    }

    String with;

    @ForbiddenWhenBuilding
    @Override
    public String getWith() {
        if (with == null) {
            with = WITH.build(getVar());
        }
        return with;
    }

    String getter;

    @ForbiddenWhenBuilding
    @Override
    public String getGetter() {
        if (getter == null) {
            getter = GET.build(getVar());
        }
        return getter;
    }

    @Override
    public String getGetters() {
        return GET.build(getVars());
    }

    private String setter;

    @Override
    public String getSetter() {
        if (setter == null) {
            setter = SET.build(getVar());
        }
        return setter;
    }

    @Override
    public String getSetters() {
        return SET.build(getVars());
    }

    @Override
    public String getAdder() {
        return ADD.build(getVar());
    }

    @Override
    public String getAdders() {
        return ADD.build(getVars());
    }

    @Override
    public String getContains() {
        return CONTAINS.build(getVar());
    }

    @Override
    public String getRemover() {
        return REMOVE.build(getVar());
    }

    @Override
    public String getRemovers() {
        return REMOVE.build(getVars());
    }

    public String getUniqueGetter() {
        return GET_BY.build(getVar());
    }

    public String getUniqueDeleter() {
        return DELETE_BY.build(getVar());
    }

    public String getGetterLocalized() {
        return GET_LOCALIZED.build(getVar());
    }

    String editer;

    @Override
    public String getEditer() {
        if (editer == null) {
            editer = EDIT.build(getVar());
        }
        return editer;
    }

    public String getVarLocalized() {
        return getVar() + GET_LOCALIZED.getSuffix();
    }

    @Override
    public String getHibernateFilterName() {
        return "my" + getVarUp() + "Filter";
    }

    public String getRandomGetter() {
        return RANDOM_GETTER.build(getType());
    }

    String has;

    public String getHas() {
        if (has == null) {
            has = HAS.build(getVar());
        }
        return has;
    }

    public String getHasPlural() {
        return HAS.build(getVars());
    }
}
