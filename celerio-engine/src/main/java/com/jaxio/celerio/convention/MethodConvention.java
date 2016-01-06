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

import static org.apache.commons.lang.StringUtils.*;
import static org.apache.commons.lang.WordUtils.capitalize;

public enum MethodConvention {
    GET("get"), //
    SET("set"), //
    ADD("add"), //
    WITH(), //
    EDIT("edit"), //
    CONTAINS("contains"), //
    GET_BY("getBy"), //
    DELETE_BY("deleteBy"), //
    REMOVE("remove"), //
    REMOVE_ALL("removeAll"), //
    HAS("is", "Set"), //
    GET_LOCALIZED("get", "Localized"), //
    RANDOM_GETTER("get", "Random");
    @Getter
    private String prefix;
    @Getter
    private String suffix;

    MethodConvention() {
    }

    MethodConvention(String prefix) {
        this.prefix = prefix;
    }

    MethodConvention(String prefix, String suffix) {
        setPrefix(prefix);
        setSuffix(suffix);
    }

    public String build(String name) {
        if (!isEmpty(prefix)) {
            name = capitalize(name);
        }
        return stripToEmpty(prefix) + name + stripToEmpty(suffix);
    }

    public void setPrefix(String prefix) {
        this.prefix = stripToNull(prefix);
    }

    public void setSuffix(String suffix) {
        this.suffix = stripToNull(suffix);
    }
}
