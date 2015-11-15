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

package com.jaxio.celerio.template.pack;

import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static org.apache.commons.io.FilenameUtils.normalize;
import static org.apache.commons.io.IOUtils.toByteArray;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

@Scope(SCOPE_PROTOTYPE)
public class ClasspathResourceUncryptedPack implements TemplatePack {

    private String packName;
    private List<String> templateNames = newArrayList();
    private Map<String, Template> templates = newHashMap();

    public ClasspathResourceUncryptedPack(TemplatePackInfo packInfo, Resource[] templatesAsResources) throws IOException {
        this.packName = packInfo.getName();

        for (Resource r : templatesAsResources) {
            if (!isFolder(r)) {
                String templateName = r.getURI().toString();
                String root = "celerio/" + packName + "/";
                templateName = normalize(templateName.substring(templateName.indexOf(root) + root.length()));

                templateNames.add(templateName);

                Template template = new Template(templateName, packInfo, toByteArray(r.getInputStream()));
                templates.put(templateName, template);
            }
        }

        // Force execution order to natural ordering.
        // Important otherwise it behaves differently on Mac/Linux.
        // Note: thanks to Jean-LouisL Boudart for reporting this.
        Collections.sort(templateNames);
    }

    // -----------------------------------------
    // TemplatePack implementation
    // -----------------------------------------

    @Override
    public String getName() {
        return packName;
    }

    @Override
    public List<String> getTemplateNames() {
        return Collections.unmodifiableList(templateNames);
    }

    @Override
    public Template getTemplateByName(String templateName) throws IOException {
        return templates.get(templateName);
    }

    // -----------------------------------------
    // Helper & toString
    // -----------------------------------------

    private boolean isFolder(Resource r) throws IOException {
        return r.getURI().toString().endsWith("/");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getName());
        for (String templateName : getTemplateNames()) {
            sb.append("\n - ").append(templateName);
        }
        return sb.toString();
    }
}