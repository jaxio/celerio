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
import org.apache.commons.lang.Validate;

import static com.jaxio.celerio.convention.GeneratedPackage.Model;
import static org.apache.commons.io.FilenameUtils.normalize;
import static org.apache.commons.lang.StringUtils.replace;

@Getter
public enum WellKnownFolder {
    // java
    JAVA("src/main/java", "src/main/generated-java", false), //
    JAVA_TEST("src/test/java", "src/test/generated-java", false),
    // web
    WEBAPP("src/main/webapp", false), //
    WEBINF(WEBAPP.getFolder() + "/WEB-INF", false), //
    VIEWS(WEBINF.getFolder() + "/views", false), //
    FLOWS(WEBINF.getFolder() + "/flows", WEBINF.getFolder() + "/flows-generated", false),
    // resources
    RESOURCES("src/main/resources", "src/main/resources", true), //
    SPRING(RESOURCES.getFolder() + "/spring", true), //
    LOCALIZATION(RESOURCES.getFolder() + "/localization", true), //
    DOMAIN_LOCALIZATION(LOCALIZATION.getFolder() + "/" + Model.getSubPackagePath(), LOCALIZATION.getFolder() + "/" + Model.getSubPackagePath() + "-generated", true), //
    // test resources
    RESOURCES_TEST("src/test/resources", "src/test/resources", true), //
    SPRING_TEST(RESOURCES_TEST.getFolder() + "/spring", true),
    // others
    CELERIO_LOCAL_TEMPLATE("src/main/celerio/", false), //
    COLLISION("target/celerio-maven-plugin/", false), //
    SQL("src/main/sql", false), //
    CONFIG("src/main/config", false), //
    SITE("src/site/", false);

    private String folder;
    private String generatedFolder;
    private boolean isResource;

    WellKnownFolder(String folder, boolean isResource) {
        setFolder(folder);
        setGeneratedFolder(folder);
        this.isResource = isResource;
    }

    WellKnownFolder(String folder, String generatedFolder, boolean isResource) {
        setFolder(folder);
        setGeneratedFolder(generatedFolder);
        this.isResource = isResource;
    }

    public void setFolder(String folder) {
        this.folder = normalize(folder);
    }

    public void setGeneratedFolder(String generatedFolder) {
        this.generatedFolder = normalize(generatedFolder);
    }

    private String unixNormalize(String folder) {
        return replace(folder, "\\", "/");
    }

    public String getUnixFolder() {
        return unixNormalize(getFolder());
    }

    public String getUnixGeneratedFolder() {
        return unixNormalize(getGeneratedFolder());
    }

    /*
     * Beware1: this method does not take into account the outputDirectory/baseDir Beware2: must be called after initialization (so override are taken into
     * account)
     */
    public boolean sameAsGeneratedFolder() {
        return folder.equalsIgnoreCase(generatedFolder);
    }

    public String getResourcePath() {
        Validate.isTrue(isResource(), name() + " is not a resource");
        // works for src/test/resources/ as there is the same number of letters
        return folder.substring(RESOURCES.getFolder().length()).replace('\\', '/');
    }
}
