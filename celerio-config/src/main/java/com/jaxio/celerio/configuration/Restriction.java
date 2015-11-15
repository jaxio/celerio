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

package com.jaxio.celerio.configuration;

import com.jaxio.celerio.convention.ClassType;
import com.jaxio.celerio.convention.GeneratedPackage;
import com.jaxio.celerio.convention.WellKnownFolder;

import javax.validation.constraints.NotNull;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.jaxio.celerio.configuration.Util.nonNull;

public class Restriction {
    @NotNull
    private List<ClassType> classTypes = newArrayList();
    @NotNull
    private List<WellKnownFolder> wellKnownFolders = newArrayList();
    @NotNull
    private List<GeneratedPackage> generatedPackages = newArrayList();

    /*
     * Restrict the generation to the following classTypes
     */
    public List<ClassType> getClassTypes() {
        return this.classTypes;
    }

    public void setClassTypes(List<ClassType> classTypes) {
        this.classTypes = nonNull(classTypes);
    }

    public boolean canGenerate(ClassType classType) {
        return classTypes.isEmpty() || classTypes.contains(classType);
    }

    /*
     * Restrict the generation to the following wellKnownFolders
     */
    public List<WellKnownFolder> getWellKnownFolders() {
        return this.wellKnownFolders;
    }

    public void setWellKnownFolders(List<WellKnownFolder> wellKnownFolders) {
        this.wellKnownFolders = nonNull(wellKnownFolders);
    }

    public boolean canGenerate(WellKnownFolder classType) {
        return wellKnownFolders.isEmpty() || wellKnownFolders.contains(classType);
    }

    /*
     * Restrict the generation to the following generatedPackages
     */
    public List<GeneratedPackage> getGeneratedPackages() {
        return this.generatedPackages;
    }

    public void setGeneratedPackages(List<GeneratedPackage> generatedPackages) {
        this.generatedPackages = nonNull(generatedPackages);
    }

    public boolean canGenerate(GeneratedPackage generatedPackage) {
        return generatedPackages.isEmpty() || generatedPackages.contains(generatedPackage);
    }
}
