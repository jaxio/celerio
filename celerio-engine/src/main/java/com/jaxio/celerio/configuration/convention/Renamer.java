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

package com.jaxio.celerio.configuration.convention;

import lombok.Setter;

import java.util.regex.Pattern;

/**
 * By default Celerio calculates Java field name based on the underlying column name. <br>
 * This setting allows you to change the column name that is passed to Celerio to calculate the default field name. <br>
 * You can for example remove well known prefix pattern from your column names.
 */
public class Renamer {

    @Setter
    private String regexp;

    @Setter
    private String replace;

    /*
     * The regular expression to apply on the column name. For example, assuming you want to remove from all column names the prefix string that consists of 3
     * chars and a '_', you can use 'regexp="^.{3}_" replace=""' or 'regexp="^qrtz_" replace="Quartz_"'
     */
    public String getRegexp() {
        return regexp;
    }

    /*
     * The replacement String. For example, assuming you want to remove from all column names the prefix string that consists of 3 chars and a '_', you can use
     * 'regexp="^.{3}_" replace=""' or 'regexp="^qrtz_" replace="Quartz_"'
     */
    public String getReplace() {
        return replace;
    }

    @Override
    public String toString() {
        return "regexp=" + regexp + " replace=" + replace;
    }

    public boolean match(String value) {
        return Pattern.compile(regexp).matcher(value).find();
    }

    public String rename(String value) {
        if (value == null) {
            return null;
        }
        return value.replaceAll(regexp, replace);
    }
}