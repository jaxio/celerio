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

package com.jaxio.celerio.maven.plugin.celerio;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CelerioConfigurationExtractor {

    public static String CELERIO_XML_START = "<celerio xmlns=\"http://www.jaxio.com/schema/celerio\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.jaxio.com/schema/prototype/celerio http://www.jaxio.com/schema/prototype/celerio-2.6.xsd\">";
    public static String CELERIO_XML_END = "</celerio>";
    private String pluginXmlConfiguration;

    public CelerioConfigurationExtractor(String pluginXmlConfiguration) {
        this.pluginXmlConfiguration = pluginXmlConfiguration;
    }

    public String getPluginXmlConfiguration() {
        return pluginXmlConfiguration;
    }

    public String getCelerioConfig() {
        String pluginConfiguration = extractPluginConfiguration();
        if (pluginConfiguration != null) {
            return extractCelerioConfig(pluginConfiguration);
        }
        return null;
    }

    private String extractPattern(String toMatch, String extractorPattern) {
        Pattern pattern = Pattern.compile(extractorPattern, Pattern.MULTILINE | Pattern.DOTALL | Pattern.UNIX_LINES);
        Matcher matcher = pattern.matcher(toMatch);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private String extractPluginConfiguration() {
        String extractionPattern = "<configuration>(.*)</configuration>";
        String xml = getPluginXmlConfiguration();
        return extractPattern(xml, extractionPattern);
    }

    private String extractCelerioConfig(String xml) {
        String celerio = extractPattern(xml, "<celerio:celerio>(.*)</celerio:celerio>");
        if (celerio == null) {
            return null;
        }
        celerio = celerio.replaceAll("<celerio:", "<");
        celerio = celerio.replaceAll("</celerio:", "</");

        return CELERIO_XML_START + celerio + CELERIO_XML_END;
    }

}
