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

import java.sql.ResultSet;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

public class ResultSetColumns extends ResultSetWrapper {

    private static Map<String, Integer> colNums = newHashMap();

    static {
        colNums.put("TABLE_CAT", 1);
        colNums.put("TABLE_SCHEM", 2);
        colNums.put("TABLE_NAME", 3);
        colNums.put("COLUMN_NAME", 4);
        colNums.put("DATA_TYPE", 5);
        colNums.put("TYPE_NAME", 6);
        colNums.put("COLUMN_SIZE", 7);
        colNums.put("BUFFER_LENGTH", 8);
        colNums.put("DECIMAL_DIGITS", 9);
        colNums.put("NUM_PREC_RADIX", 10);
        colNums.put("NULLABLE", 11);
        colNums.put("REMARKS", 12);
        colNums.put("COLUMN_DEF", 13);
        colNums.put("SQL_DATA_TYPE", 14);
        colNums.put("SQL_DATETIME_SUB", 15);
        colNums.put("CHAR_OCTET_LENGTH", 16);
        colNums.put("ORDINAL_POSITION", 17);
        colNums.put("IS_NULLABLE", 18);
        colNums.put("SCOPE_CATLOG", 19);
        colNums.put("SCOPE_SCHEMA", 20);
        colNums.put("SCOPE_TABLE", 21);
        colNums.put("SOURCE_DATA_TYPE", 22);
        colNums.put("IS_AUTOINCREMENT", 23);
    }

    @Override
    protected int toInt(String label) {
        return colNums.get(label);
    }

    public ResultSetColumns(ResultSet rs, boolean useLabel) {
        super(rs, useLabel);
    }
}