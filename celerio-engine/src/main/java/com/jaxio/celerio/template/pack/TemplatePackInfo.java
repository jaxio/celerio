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

import static com.jaxio.celerio.util.FallBackUtil.fallBack;
import static org.apache.commons.lang.StringUtils.substringAfterLast;
import static org.apache.commons.lang.StringUtils.substringBeforeLast;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import com.jaxio.celerio.util.StringUtil;
import lombok.Getter;

import org.springframework.core.io.Resource;

@Getter
public class TemplatePackInfo {
    private String name;
    private String version;
    private String description;
    private String description2;
    private String command;
    private String commandHelp = ""; // optional command help
    private String projectLink = "";

    public TemplatePackInfo(Resource packInfoAsResource) throws IOException {
        Properties packInfoAsProperties = new Properties();
        packInfoAsProperties.load(new InputStreamReader(packInfoAsResource.getInputStream(), "UTF-8"));
        init(packInfoAsProperties);
    }

    public TemplatePackInfo(String packName, Resource packInfoAsResource) throws IOException {
        Properties packInfoAsProperties = new Properties();
        packInfoAsProperties.load(new InputStreamReader(packInfoAsResource.getInputStream(), "UTF-8"));
        init(packName, packInfoAsProperties);
    }

    public TemplatePackInfo(String packName, Properties packInfoAsProperties) {
        init(packName, packInfoAsProperties);
    }

    private void init(Properties packInfoAsProperties) {
        init(null, packInfoAsProperties);
    }

    private void init(String packName, Properties packInfoAsProperties) {
        this.name = fallBack(packName, packInfoAsProperties.getProperty("packName"));
        this.version = packInfoAsProperties.getProperty("packVersion");
        this.description = packInfoAsProperties.getProperty("packDescription");
        this.description2 = packInfoAsProperties.getProperty("packDescription2");
        this.command = packInfoAsProperties.getProperty("packCommand");
        this.commandHelp = packInfoAsProperties.getProperty("packCommandHelp");
        this.projectLink = packInfoAsProperties.getProperty("projectLink");
    }

    /**
     * Used to create an invoker.properties file in bootstrap integration test.
     * 
     * Input:<br>
     * <code>packCommand=mvn -Ph2,db,metadata,gen test<code>
     * <p>
     * Ouput:<br>
     * <code>h2,db,metadata,gen</code>
     */
    public String getProfilesCSV() {
        String profiles = substringBeforeLast(command, " "); // remove goal
        profiles = substringAfterLast(profiles, "-P");
        return profiles.trim();
    }

    public boolean hasProjectLink() {
        return StringUtil.hasLength(projectLink);
    }

    @Override
    public String toString() {
        return name + " : " + " : " + description + " : " + command;
    }
}