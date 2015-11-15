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

package com.jaxio.celerio.metadata;

import com.jaxio.celerio.configuration.database.Column;
import com.jaxio.celerio.configuration.database.Table;
import lombok.Getter;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

@Getter
public class RuleReport {
    private Map<Table, String> tableWarnings = newHashMap();
    private Map<Table, String> tableErrors = newHashMap();
    private Map<Column, String> columnWarnings = newHashMap();
    private Map<Column, String> columnErrors = newHashMap();

    public void warn(Table t, String message) {
        tableWarnings.put(t, message);
    }

    public void error(Table t, String message) {
        tableErrors.put(t, message);
    }

    public void warn(Column c, String message) {
        columnWarnings.put(c, message);
    }

    public void error(Column c, String message) {
        columnErrors.put(c, message);
    }

    public boolean hasErrors() {
        return !tableErrors.isEmpty() || !columnErrors.isEmpty();
    }

    public boolean hasWarning() {
        return !tableWarnings.isEmpty() || !columnWarnings.isEmpty();
    }
}