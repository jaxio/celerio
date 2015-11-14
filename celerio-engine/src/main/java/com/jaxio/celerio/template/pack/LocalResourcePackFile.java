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

import com.jaxio.celerio.util.IOUtil;
import org.springframework.context.annotation.Scope;
import org.springframework.util.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.io.FilenameUtils.normalize;
import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.apache.commons.io.IOUtils.toByteArray;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

@Scope(SCOPE_PROTOTYPE)
public class LocalResourcePackFile implements TemplatePack {
    private String packName;
    private String packAbsolutePath;

    public LocalResourcePackFile(String folder) {
        this("local", folder);
    }

    public LocalResourcePackFile(String packName, String folder) {
        this.packName = packName;
        this.packAbsolutePath = normalize(convertToFile(folder).getAbsolutePath());
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
        List<String> unfilteredTemplateFiles = newArrayList();
        for (String absoluteFilename : new IOUtil().listFiles(new File(this.packAbsolutePath))) {
            unfilteredTemplateFiles.add(remove(absoluteFilename));
        }

        // Force execution order to natural ordering
        // by analogy with ClasspathResourceUncryptedPack even if here
        // it is probably not necessary.
        Collections.sort(unfilteredTemplateFiles);
        return unfilteredTemplateFiles;
    }

    @Override
    public Template getTemplateByName(String template) throws IOException {
        return new Template(template, null, getAsByteArray(normalize(template)));
    }

    // -----------------------------------------
    // Helpers
    // -----------------------------------------

    private File convertToFile(String folder) {
        Assert.notNull(folder, "LocalResourcePackFile pack points to an null folder");
        File result = new File(folder);
        Assert.isTrue(result.exists(), "LocalResourcePackFile pack points to an unknown folder : " + folder);
        return result;
    }

    private String remove(String absoluteFilename) {
        return absoluteFilename.substring(packAbsolutePath.length() + 1);
    }

    private boolean containsTemplate(String templateName) {
        return getTemplateNames().contains(templateName);
    }

    /**
     * Extract a template from the jar as a byte array
     */
    private byte[] getAsByteArray(String template) throws IOException {
        if (isTemplateGivenAsAnAbsoluteFile(template)) {
            throw new IllegalArgumentException("Template " + template + " should not be given as an absolute file");
        }
        if (!containsTemplate(template)) {
            throw new IllegalArgumentException("Template " + template + " is not a template");
        }
        java.io.InputStream is = null;
        try {
            is = new FileInputStream(getAbsoluteTemplate(template));
            return toByteArray(is);
        } finally {
            closeQuietly(is);
        }
    }

    private boolean isTemplateGivenAsAnAbsoluteFile(String template) {
        return normalize(template).startsWith(packAbsolutePath);
    }

    private String getAbsoluteTemplate(String template) {
        return normalize(packAbsolutePath + File.separatorChar + template);
    }
}
