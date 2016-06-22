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

package com.jaxio.celerio.configuration.entity;

import com.jaxio.celerio.configuration.Util;
import com.jaxio.celerio.util.Labels;
import lombok.Setter;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.jaxio.celerio.convention.CommentStyle.JAVA;
import static org.springframework.util.StringUtils.hasLength;

import static com.jaxio.celerio.util.FallBackUtil.fallBack;
import static com.jaxio.celerio.util.MiscUtil.toReadableLabel;

@Setter
public class EnumValue {
    private String name;
    private String value;
    private String label;
    private List<String> comments = newArrayList();
    private List<Label> labels;

    public EnumValue() {
    }

    public EnumValue(String value) {
        this.name = value;
        this.value = value;
    }

    public EnumValue(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /*
     * The enum constant that defines an instance of the enum type. Example: For a Civility enum, this could be MISS or MISTER.
     */
    public String getName() {
        return name;
    }

    public boolean hasName() {
        return hasLength(name);
    }

    /*
     * The value really stored in the database. Only applies if the enumConfig's type is CUSTOM. Note that the mapping leverages Jadira as it is not supported
     * natively by JPA.
     */
    public String getValue() {
        return value;
    }

    public boolean hasValue() {
        return hasLength(value);
    }

    /*
     * The base label for this enum value. You may either set it here or in a nested labels/label.
     */
    public String getLabel() {
        return label;
    }

    /*
     * Set comments for this enum value.
     */
    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = Util.nonNull(comments);
    }

    public String getJavadoc() {
        return getJavadoc("");
    }

    public String getJavadoc(String prepend) {
        return JAVA.decorate(comments, prepend);
    }

    /*
     * The labels for this enum value. They appear in the enum properties file located under 'src/main/resources/localization/domain'.
     */
    public List<Label> getLabels() {
        return labels;
    }

    public Labels labels() {
        Labels labels = new Labels(getLabels());
        labels.setFallBack(fallBack(getLabel(), toReadableLabel(fallBack(getName(), getValue()))));
        return labels;
    }
}