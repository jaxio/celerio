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

package com.jaxio.celerio.template;

import com.jaxio.celerio.model.support.PackageImport;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newTreeSet;

/**
 * The ImportsHolder accumulates imports needed by the generated java files.
 * It handles duplicate entries, ignores well know imports such as import java.lang.* or
 * imports from the current package.
 */
public class ImportsHolder {
    private String currentPackage;
    private Set<PackageImport> imports = newTreeSet();
    private Set<PackageImport> samePackageImports = newTreeSet();

    public ImportsHolder(String currentPackage) {
        this.currentPackage = currentPackage.replace("/", ".");
    }

    public void addImports(List<PackageImport> packageImports) {
        for (PackageImport pi : packageImports) {
            addImport(pi);
        }
    }

    /**
     * @return true if it is imported for the first time.
     */
    public boolean addImport(PackageImport packageImport) {
        // no need to import well known types
        if (!packageImport.isImportNeeded()) {
            return false;
        }

        // no need to import class in same package
        // however, in order to support TemplateExcution.requireFirstTime
        // we must track the import and return true if it is
        // imported for the first time/.
        if (packageImport.samePackageAs(currentPackage)) {
            return samePackageImports.add(packageImport);
        }

        // we import
        return imports.add(packageImport);
    }

    public boolean add(String fullType) {
        return addImport(new PackageImport(fullType));
    }

    public List<PackageImport> getList() {
        return newArrayList(imports);
    }

    public boolean hasImports() {
        return imports.size() > 0;
    }

    public String toJavaImportString() {
        StringBuilder sb = new StringBuilder();
        String lastDomain = "";
        boolean first = true;
        for (PackageImport pi : imports) {
            String name = pi.getName();
            if (!name.startsWith("static ")) {
                String domain = name.split("\\.")[0];
                if (!first && !lastDomain.equals(domain)) {
                    sb.append("\n");
                }
                lastDomain = domain;
            }
            sb.append("import ").append(name).append(";\n");
            first = false;
        }

        return sb.toString();
    }
}
