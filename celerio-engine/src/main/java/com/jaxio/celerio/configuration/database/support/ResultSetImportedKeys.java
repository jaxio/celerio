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

public class ResultSetImportedKeys extends ResultSetWrapper {

    private static Map<String, Integer> colNums = newHashMap();

    static {
        colNums.put("PKTABLE_CAT", 1);
        colNums.put("PKTABLE_SCHEM", 2);
        colNums.put("PKTABLE_NAME", 3);
        colNums.put("PKCOLUMN_NAME", 4);
        colNums.put("FKTABLE_CAT", 5);
        colNums.put("FKTABLE_SCHEM", 6);
        colNums.put("FKTABLE_NAME", 7);
        colNums.put("FKCOLUMN_NAME", 8);
        colNums.put("KEY_SEQ", 9);
        colNums.put("UPDATE_RULE", 10);
        colNums.put("DELETE_RULE", 11);
        colNums.put("FK_NAME", 12);
        colNums.put("PK_NAME", 13);
        colNums.put("DEFERRABILITY", 14);
    }

    @Override
    protected int toInt(String label) {
        return colNums.get(label);
    }

    public ResultSetImportedKeys(ResultSet rs, boolean useLabel) {
        super(rs, useLabel);
    }
}