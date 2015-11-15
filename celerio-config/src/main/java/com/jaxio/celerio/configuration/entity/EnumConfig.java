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
import lombok.Setter;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.jaxio.celerio.configuration.Util.nonNull;
import static com.jaxio.celerio.convention.CommentStyle.JAVA;
import static org.springframework.util.StringUtils.hasLength;

/*
 * Entry point to configure an enum generation.
 */
@Setter
public class EnumConfig {
    private String name;
    private String rootPackage;
    private String subPackage;
    private EnumType type;
    private String userType;
    private List<EnumValue> enumValues = newArrayList();
    private List<String> comments = newArrayList();

    /*
     * Set the name of the generated enum.<br>
     * Example: name="CreditCardEnum"
     */
    public String getName() {
        return name;
    }

    public boolean hasName() {
        return hasLength(name);
    }

    /*
     * Allows you to override the default root package.<br>
     * Example: com.yourcompany
     */
    public String getRootPackage() {
        return rootPackage;
    }

    public boolean hasRootPackage() {
        return hasLength(rootPackage);
    }

    /*
     * When you define a sub-package, the resulting enum's package becomes <code>rootPackage.domain.subPackage</code> instead of <code>rootPackage.domain</code>.
     * Same applies for other enum related classes. There is no sub-package by default.
     */
    public String getSubPackage() {
        return subPackage;
    }

    public boolean hasSubPackage() {
        return hasLength(subPackage);
    }

    /*
     * The JPA enum type. The CUSTOM type (not a JPA type) allows you to do advanced enum mapping with Jadira or a custom user type.
     */
    public EnumType getType() {
        return type;
    }

    public boolean hasType() {
        return type != null;
    }

    /*
     * Specify the enum constants.
     */
    public List<EnumValue> getEnumValues() {
        return enumValues;
    }

    public void setEnumValues(List<EnumValue> enumValues) {
        this.enumValues = nonNull(enumValues);
    }

    public void addEnumValue(EnumValue enumValue) {
        enumValues.add(enumValue);
    }

    public boolean hasValues() {
        return enumValues != null && !enumValues.isEmpty();
    }

    public boolean hasEnum() {
        return hasValues();
    }

    /*
     * Set comments for this enumeration.
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

    /*
     * Specify the user type implementation to use to be given to hibernate <br>
     * Example: name="com.yourcompany.hibernate.support.CustomDateUserType"
     */
    public String getUserType() {
        return userType;
    }

    public boolean hasUserType() {
        return hasLength(getUserType());
    }

    public String getJavadoc(String prepend) {
        return JAVA.decorate(comments, prepend);
    }

    public boolean isOrdinal() {
        return EnumType.ORDINAL == getType();
    }

    public boolean isCustomType() {
        return EnumType.CUSTOM == getType();
    }

    public boolean isString() {
        return EnumType.STRING == getType();
    }

    public String getEnumNameByValue(String value) {
        if (isOrdinal()) {
            return enumValues.get(Integer.parseInt(value)).getName();
        } else {
            for (EnumValue ev : enumValues) {
                if (value.equalsIgnoreCase(ev.getValue())) {
                    return ev.getName();
                }
            }
        }
        throw new IllegalArgumentException("Unexpected value: " + value);
    }
}
