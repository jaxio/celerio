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

package com.jaxio.celerio.configuration;

import com.jaxio.celerio.configuration.database.JdbcType;
import com.jaxio.celerio.util.MappedType;
import lombok.Setter;

/*
 * Global rule to map columns whose JDBC TYPE is DATE, TIME or TIMESTAMP to a Java type.
 */
@Setter
public class DateMapping {
    private MappedType mappedType;
    private JdbcType columnJdbcType;
    private String columnNameRegExp;

    /*
     * The mapped type to use when both the jdbcType and the columnNamePattern matches what is expected.
     */
    public MappedType getMappedType() {
        return mappedType;
    }

    /*
     * Only column with this JdbcType are concerned by this mapping. Accepted JdbcType are DATE, TIME, TIMESTAMP. When set to null, we assume the column
     * JdbcType may be DATE, TIME, or TIMESTAMP.
     */
    public JdbcType getColumnJdbcType() {
        return columnJdbcType;
    }

    /*
     * An optional regular expression to restrict the mapping by column name. The matching is case insensitive.
     */
    public String getColumnNameRegExp() {
        return columnNameRegExp;
    }

    public boolean match(JdbcType jdbcType, String columnName) {
        if (jdbcType == null || columnName == null) {
            return false;
        }

        if (jdbcType != JdbcType.DATE && jdbcType != JdbcType.TIME && jdbcType != JdbcType.TIMESTAMP) {
            return false;
        }

        return (columnJdbcType == null || columnJdbcType == jdbcType) && (columnNameRegExp == null || columnName.toUpperCase().matches(columnNameRegExp));
    }

    @Override
    public String toString() {
        return "DateMapping: mappedType=" + mappedType + " columnJdbcType=" + columnJdbcType + " columnNameRegExp=" + columnNameRegExp;
    }
}
