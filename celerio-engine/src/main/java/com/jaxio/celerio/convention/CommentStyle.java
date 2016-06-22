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
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;

import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Lists.newArrayList;

public enum CommentStyle {
    JAVA("/*", " *", " */", "java", "css"), //
    JAVADOC("/**", " *", " */", "java"), //
    JSP("<%--", " |", "--%>", "jsp"), //
    XML("<!--", " !", "-->", "htm", "xhtml", "xml", "classpath", "project", "fml", "xsl"), //
    DOUBLE_DASH("##", "##", "##", "vm.txt", "vm.html"), //
    DASH("#", "#", "#", "properties", "mime.types", "txt"), //
    DOUBLE_QUOTE("--", "--", "--", "sql"), //
    DOUBLE_TILDE("~~", "~~", "~~", "apt"), //
    DOUBLE_SLASH("//", "//", "//", "js", "ts", "graphviz");

    @Getter
    @Setter
    private String start;
    @Getter
    @Setter
    private String startLine;
    @Getter
    @Setter
    private String end;
    @Getter
    @Setter
    private List<String> extensions;

    CommentStyle(String commentStart, String commentLine, String commentEnd, String... extensions) {
        this.start = commentStart;
        this.startLine = commentLine;
        this.end = commentEnd;
        this.extensions = newArrayList(extensions);
    }

    public String decorate(String line, String prepend) {
        if (null == line) {
            return "";
        }
        if (this == JAVADOC || this == JAVA) {
            line = StringUtils.replace(line, "/**", "\n");
            line = StringUtils.replace(line, "/*", "\n");
            line = StringUtils.replace(line, "**/", "\n");
            line = StringUtils.replace(line, "*/", "\n");
            line = StringUtils.replace(line, " *", "\n");
        }
        line = StringUtils.replace(line, "  ", " ");
        line = StringUtils.replace(line, "  ", " ");
        line = StringUtils.replace(line, "  ", " ");
        line = StringUtils.replace(line, "\\n", "\n");
        line = StringUtils.replace(line, "\n\n", "\n");
        line = StringUtils.replace(line, "\n\n", "\n");
        line = StringUtils.replace(line, "\n\n", "\n");
        return decorate(newArrayList(line.split("\n")), prepend);
    }

    public String decorate(Iterable<String> lines) {
        return decorate(lines, "");
    }

    public String decorate(Iterable<String> lines, String preprend) {
        if (lines == null || isEmpty(lines)) {
            return "";
        }
        String s = preprend + start + "\n";
        for (String comment : lines) {
            s += preprend + startLine + " " + comment.trim() + "\n";
        }
        s += preprend + end + "\n";

        return s;
    }

    public static CommentStyle fromFilename(String filename) {
        for (CommentStyle style : values()) {
            if (FilenameUtils.isExtension(filename, style.getExtensions())) {
                return style;
            }
        }
        return null;
    }
}
