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

package com.jaxio.celerio.configuration.database.support;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.trimToEmpty;

public class EnumExtractor {
    private final Pattern pattern;

    public EnumExtractor(String regex) {
        this.pattern = Pattern.compile(regex);
    }

    public List<String> extract(String content) {
        if (isBlank(content)) {
            return emptyList();
        }
        String listAsString = extractListAsString(content);
        if (listAsString == null) {
            return emptyList();
        }
        return extractList(listAsString);
    }

    private List<String> extractList(String content) {
        List<String> ret = newArrayList();
        String[] values = content.split(",");
        for (String value : values) {
            ret.add(cleanEnumValue(trimToEmpty(value)));
        }
        return ret;
    }

    private String cleanEnumValue(String value) {
        if (value.startsWith("'") && value.endsWith("'")) {
            return trimToEmpty(value.substring(1, value.length() - 1));
        }
        return value;
    }

    private String extractListAsString(String content) {
        Matcher matcher = pattern.matcher(content);
        if (!matcher.find()) {
            return null;
        }
        return matcher.group(1);
    }
}
