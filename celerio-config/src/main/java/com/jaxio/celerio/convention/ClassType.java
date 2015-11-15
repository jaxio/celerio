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

package com.jaxio.celerio.convention;

import lombok.Getter;
import lombok.Setter;

import static com.jaxio.celerio.configuration.Util.firstNonNull;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.stripToNull;
import static org.apache.commons.lang.WordUtils.capitalize;
import static org.springframework.util.StringUtils.hasLength;

public enum ClassType {
    // backend related
    model("", GeneratedPackage.Model), //
    primaryKey("Pk", GeneratedPackage.Model), //
    dao("Dao", GeneratedPackage.Dao), //
    formatter("Formatter", GeneratedPackage.Formatter), //
    printer("Printer", GeneratedPackage.Printer), //
    converter("Converter", GeneratedPackage.Converter), //
    repository("Repository", GeneratedPackage.Repository), //
    repositorySupport("RepositorySupport", GeneratedPackage.RepositorySupport), //
    // service is like repository... just a matter of taste.
    service("Service", GeneratedPackage.Service), //
    serviceImpl("ServiceImpl", GeneratedPackage.ServiceImpl), //
    serviceSupport("ServiceSupport", GeneratedPackage.ServiceSupport), //
    // --
    validator("Validator", GeneratedPackage.WebModelValidator), //
    enumModel("", GeneratedPackage.EnumModel), //
    enumItems("Items", GeneratedPackage.EnumItems), //
    modelGenerator("Generator", GeneratedPackage.Repository),
    // web related
    controllerWithPathVariable("ControllerWithPathVariable", GeneratedPackage.WebController), //
    restController("RestController", GeneratedPackage.RestController), //
    entityForm("Form", GeneratedPackage.WebModelEntityForm), //
    context("Context", GeneratedPackage.WebModelSearchForm), //
    formService("FormService", GeneratedPackage.WebController), //
    formValidator("FormValidator", GeneratedPackage.WebController), //
    searchController("SearchController", GeneratedPackage.WebController), //
    webSupport("WebSupport", GeneratedPackage.WebController), //
    webModel("", GeneratedPackage.WebModel), //
    webModelConverter("JsfConverter", GeneratedPackage.WebModelConverter), //
    webController("Controller", GeneratedPackage.WebModel), //
    webConverter("Converter", GeneratedPackage.WebConverter), //
    webModelItems("Items", GeneratedPackage.WebModelItems), //
    webPermission("Permission", GeneratedPackage.WebPermission), //
    seleniumEditPage("Edit", GeneratedPackage.SeleniumPage), //
    seleniumSearchPage("Search", GeneratedPackage.SeleniumPage);

    @Getter
    @Setter
    private String prefix = "";
    @Getter
    @Setter
    private String suffix = "";
    @Setter
    private String subPackage = "";
    private GeneratedPackage generatedPackage;

    ClassType(String suffix, GeneratedPackage generatedPackage) {
        this.suffix = suffix;
        this.generatedPackage = generatedPackage;
    }

    public String getRootPackage() {
        return generatedPackage.getRootPackage();
    }

    public String getPackageName() {
        return generatedPackage.getPackageName();
    }

    public String getSubPackage() {
        if (stripToNull(generatedPackage.getSubPackage()) != null) {
            return (stripToNull(subPackage) != null) ? subPackage + "." : generatedPackage.getSubPackage();
        }
        return subPackage;
    }

    public boolean hasSubPackage() {
        return hasLength(getSubPackage());
    }

    public void setGeneratedPackage(GeneratedPackage generatedPackage) {
        this.generatedPackage = firstNonNull(generatedPackage, this.generatedPackage);
    }

    public String build(String baseName) {
        if (!isBlank(prefix)) {
            baseName = capitalize(baseName);
        }
        return prefix + baseName + suffix;
    }

}