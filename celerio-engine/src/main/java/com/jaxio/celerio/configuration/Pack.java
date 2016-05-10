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

import lombok.Setter;
import lombok.ToString;
import org.springframework.util.Assert;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.jaxio.celerio.configuration.Pattern.hasPattern;
import static com.jaxio.celerio.configuration.Util.nonNull;
import static org.springframework.util.StringUtils.hasLength;

/*
 * A pack is the aggregation of templates and static files that produces functionalities.
 */
@Setter
@ToString
public class Pack {
    private String name;
    private String path;
    private boolean enable = true;
    private Integer order;
    protected List<MetaAttribute> properties = newArrayList();
    protected List<Pattern> filenames = newArrayList();
    protected List<Pattern> templates = newArrayList();

    public Pack() {
    }

    public Pack(String name) {
        Assert.isTrue(!name.endsWith(".pack"));
        this.name = name;
    }

    /*
     * Name of the pack
     */
    public String getName() {
        return name;
    }

    public boolean hasName() {
        return hasLength(name);
    }

    /*
     * Path of the pack's resources folder, that is the path to the folder that contains both the META-INF folder and the celerio folder.
     * It should be relative to the project, or absolute.<br>
     * Example: pack-something/src/main/resources
     */
    public String getPath() {
        return path;
    }

    public boolean hasPath() {
        return hasLength(path);
    }

    /*
     * Should this pack be used ?
     */
    public boolean isEnable() {
        return enable;
    }

    /*
     * Specify the pack order, its main interest is when two packs produce the same artifacts.
     */
    public Integer getOrder() {
        return order;
    }

    public boolean hasPackOrder() {
        return order != null;
    }

    public List<MetaAttribute> getProperties() {
        return properties;
    }

    public void setProperties(List<MetaAttribute> properties) {
        this.properties = nonNull(properties);
    }


    /*
     * Control the generation output by filtering the generated files based on their filename.
     */
    public List<Pattern> getFilenames() {
        return filenames;
    }

    public void setFilenames(List<Pattern> filenames) {
        this.filenames = nonNull(filenames);
    }

    public void addFilename(Pattern filenamePattern) {
        filenames.add(filenamePattern);
    }

    public boolean hasFilename(String value) {
        return hasPattern(getFilenames(), value);
    }

    /*
     * Control the generation output by filtering the execution of the generation templates based on their filename.
     */
    public List<Pattern> getTemplates() {
        return templates;
    }

    public void setTemplates(List<Pattern> templates) {
        this.templates = nonNull(templates);
    }

    public void addTemplate(Pattern templatePattern) {
        templates.add(templatePattern);
    }

    public boolean hasTemplate(String value) {
        return hasPattern(getTemplates(), value);
    }

    public boolean nameMatch(String otherName) {
        return name.toLowerCase().equalsIgnoreCase(otherName.toLowerCase());
    }
}
