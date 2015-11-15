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
import com.jaxio.celerio.util.Named;
import lombok.Getter;

import static org.apache.commons.lang.StringUtils.removeStart;

public class PackageImport implements Named, Comparable<PackageImport> {

    private static final String ORG = "org.";
    private static final String JAVAX = "javax.";
    private static final String JAVA = "java.";
    private static final String STATIC_JAVA = "static ";
    @Getter
    private String packageName;
    private String packageNameWithoutType;
    private String forComparison = "";

    public PackageImport(GeneratedPackage generatedPackage, String type) {
        this(generatedPackage.getPackageName() + "." + type);
    }

    public PackageImport(String fullType) {
        if (fullType.contains("(")) {
            // chop off the annotation... "e.g x.y.z.Named("sthg")" => e.g x.y.z.Named
            fullType = fullType.substring(0, fullType.indexOf("("));
        }

        this.packageName = fullType;
        initForComparison();

        int i = fullType.lastIndexOf(".");
        if (i > 0) {
            this.packageNameWithoutType = fullType.substring(0, i);
        } else {
            throw new IllegalArgumentException("empty package is not supported. fullType: " + fullType);
        }
    }

    public PackageImport(String pkg, String clazz) {
        this(pkg + "." + clazz);
    }

    @Override
    public String getName() {
        return getPackageName();
    }

    public boolean samePackageAs(String packageNameWithoutType) {
        return this.packageNameWithoutType.equals(packageNameWithoutType);
    }

    /**
     * Special compare that take into account developer practice that list first java imports, then javax imports and finally other ones...
     */
    @Override
    public int compareTo(PackageImport o) {
        return forComparison.compareTo(o.forComparison);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof PackageImport)) {
            return false;
        }

        return getName().equals(((PackageImport) other).getName());
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    private void initForComparison() {
        if (packageName.startsWith(STATIC_JAVA)) {
            forComparison = forceOrder(1) + removeStart(packageName, STATIC_JAVA);
        } else if (packageName.startsWith(JAVA)) {
            forComparison = forceOrder(2) + removeStart(packageName, JAVA);
        } else if (packageName.startsWith(JAVAX)) {
            forComparison = forceOrder(3) + removeStart(packageName, JAVAX);
        } else if (packageName.startsWith(ORG)) {
            forComparison = forceOrder(4) + removeStart(packageName, ORG);
        } else {
            forComparison = forceOrder(5) + packageName;
        }
    }

    private String forceOrder(int i) {
        return "force_order_" + i + "_";
    }

    /**
     * no need to import simple types like java.lang.Integer
     */
    public boolean isImportNeeded() {
        return !packageName.matches("java\\.lang\\.\\w+");
    }
}
