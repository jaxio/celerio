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

import com.jaxio.celerio.configuration.EntityContextProperty;
import com.jaxio.celerio.configuration.pack.CelerioPack;
import com.jaxio.celerio.util.StringUtil;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang.StringUtils.substringAfterLast;
import static org.apache.commons.lang.StringUtils.substringBeforeLast;

@Getter
public class TemplatePackInfo {
    final private String name;
    final private String version;
    final private String description;
    final private String description2;
    final private String command;
    final private String commandHelp;
    final private String projectLink;
    final private List<EntityContextProperty> entityContextPropertyList;

    public TemplatePackInfo(String name) {
        this.name = name;
        this.version = "";
        this.description = "";
        this.description2 = "";
        this.command = "";
        this.commandHelp = "";
        this.projectLink = "";
        this.entityContextPropertyList = new ArrayList<EntityContextProperty>();
    }

    public TemplatePackInfo(CelerioPack celerioPack) {
        this.name = celerioPack.getPackName().getValue();
        this.version = celerioPack.getPackVersion().getValue();
        this.description = celerioPack.getPackDescription().getValue();
        this.description2 = celerioPack.getPackDescription2().getValue();
        this.command = celerioPack.getPackCommand().getValue();
        this.commandHelp = celerioPack.getPackCommandHelp().getValue();
        this.projectLink = celerioPack.getProjectLink().getValue();
        if (celerioPack.getCelerioTemplateContext() != null && celerioPack.getCelerioTemplateContext().getEntityContextProperties() != null) {
            this.entityContextPropertyList = celerioPack.getCelerioTemplateContext().getEntityContextProperties();
        } else {
            this.entityContextPropertyList = new ArrayList<EntityContextProperty>();
        }
    }

    /**
     * Used to create an invoker.properties file in bootstrap integration test.
     * <p>
     * Input:<br>
     * <code>packCommand=mvn -Ph2,db,metadata,gen test</code>
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