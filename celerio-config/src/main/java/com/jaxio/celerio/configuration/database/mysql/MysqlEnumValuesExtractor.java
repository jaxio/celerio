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

package com.jaxio.celerio.configuration.database.mysql;

import com.jaxio.celerio.configuration.database.support.EnumExtractor;

import java.util.List;

public class MysqlEnumValuesExtractor {
    private static final String MYSQL_ENUM_VALUES = "[e|E][n|N][u|U][m|M]\\((.*)\\)$";
    private EnumExtractor enumExtractor = new EnumExtractor(MYSQL_ENUM_VALUES);

    public List<String> extract(String content) {
        return enumExtractor.extract(content);
    }
}
