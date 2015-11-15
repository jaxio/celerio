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

package com.jaxio.celerio.output;

import com.jaxio.celerio.template.TemplateEngine;
import com.jaxio.celerio.template.pack.Template;
import com.jaxio.celerio.template.pack.TemplatePack;
import org.springframework.util.Assert;

import java.io.File;
import java.io.Serializable;

public class FileMetaData implements Serializable {
    private static final long serialVersionUID = 1L;

    private String pack;
    private String template;
    private long size;
    private String path;
    private long lastMod;

    /**
     * @param pack
     * @param template
     * @param fileRelativePath relative to the project dir. That is the dir containing ".celerio/generated.xml"
     * @param file
     */
    public FileMetaData(TemplatePack pack, Template template, String fileRelativePath, File file) {
        Assert.notNull(fileRelativePath, "When creating a new FileMetaData, you must pass a relativePath as it is used in equals comparison");
        Assert.isTrue(file.exists(), "When creating a new FileMetaData, you must be sure that the passed file exists.");
        this.pack = pack == null ? "" : pack.getName();
        this.template = template == null ? "" : template.getName();
        this.size = file.length();
        this.path = fileRelativePath;
        this.lastMod = file.lastModified();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof FileMetaData)) {
            return false;
        }

        FileMetaData otherFmd = (FileMetaData) other;
        return toString().equals(otherFmd.toString());
    }

    public long getSize() {
        return size;
    }

    public long getLastMod() {
        return lastMod;
    }

    public String getFileRelativePath() {
        return path;
    }

    /**
     * Whether this file comes from a bootstrap. Bootstrap file must not be deleted. However we can regenerate as any other file.
     */
    public boolean isBootstrapFile() {
        return template.contains(TemplateEngine.TemplateType.bootstrap.getVelocityPattern());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    final public String toString() {
        return path + " " + size + " " + lastMod;
    }
}