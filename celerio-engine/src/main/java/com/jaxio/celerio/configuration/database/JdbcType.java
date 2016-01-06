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

package com.jaxio.celerio.configuration.database;

import lombok.extern.slf4j.Slf4j;

import java.sql.Types;

@Slf4j
public enum JdbcType {
    /**
     * Not supported
     */
    ARRAY(Types.ARRAY), //
    BIGINT(Types.BIGINT), //
    BINARY(Types.BINARY), //
    BIT(Types.BIT), //
    BLOB(Types.BLOB), //
    BOOLEAN(Types.BOOLEAN), //
    CHAR(Types.CHAR), //
    CLOB(Types.CLOB),
    /**
     * Not supported
     */
    DATALINK(Types.DATALINK), //
    DATE(Types.DATE), //
    DECIMAL(Types.DECIMAL),
    /**
     * Not supported
     */
    DISTINCT(Types.DISTINCT), //
    DOUBLE(Types.DOUBLE), //
    FLOAT(Types.FLOAT), //
    INTEGER(Types.INTEGER), //
    JAVA_OBJECT(Types.JAVA_OBJECT), //
    LONGVARBINARY(Types.LONGVARBINARY), //
    LONGVARCHAR(Types.LONGVARCHAR), //
    NUMERIC(Types.NUMERIC),
    /**
     * Not supported
     */
    OTHER(Types.OTHER), //
    REAL(Types.REAL), //
    REF(Types.REF), //
    SMALLINT(Types.SMALLINT),
    /**
     * Not supported
     */
    STRUCT(Types.STRUCT), //
    TIME(Types.TIME), //
    TIMESTAMP(Types.TIMESTAMP), //
    TINYINT(Types.TINYINT), //
    VARBINARY(Types.VARBINARY), //
    VARCHAR(Types.VARCHAR), //
    ROW_ID(Types.ROWID), //
    LONGNVARCHAR(Types.LONGNVARCHAR), //
    NCHAR(Types.NCHAR), //
    NCLOB(Types.NCLOB), //
    NVARCHAR(Types.NVARCHAR),
    /**
     * Not supported
     */
    NULL(Types.NULL), //
    SQLXML(Types.SQLXML);

    private final int jdbcType;

    JdbcType(int jdbcType) {
        this.jdbcType = jdbcType;
    }

    public int getJdbcType() {
        return jdbcType;
    }

    public String getName() {
        return name();
    }

    public String getFullName() {
        return "Types." + getName();
    }

    public static JdbcType fromJdbcType(int jdbcType) {
        for (JdbcType type : values()) {
            if (type.getJdbcType() == jdbcType) {
                return type;
            }
        }

        log.error("Could not resolve the Jdbc Type: " + jdbcType);
        return null; // so reverse can take place and metadata be sent to our server. We do post validation to better investigate the invalid db schema
    }

    public static JdbcType fromJdbcName(String name) {
        for (JdbcType type : values()) {
            if (name.equals(type.getName()) || name.equals(type.getFullName())) {
                return type;
            }
        }

        log.error("Could not resolve the Jdbc Type: " + name);
        return null;
    }

    public boolean isString() {
        switch (jdbcType) {
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
            case Types.CLOB:
            case Types.LONGNVARCHAR:
            case Types.NCHAR:
            case Types.NCLOB:
            case Types.NVARCHAR:
            case Types.SQLXML:
                return true;
            default:
                return false;
        }
    }

    // added in 3.0.40 to determine if the enum is by default STRING or ORDINAL
    // I prefer a separated method to do not introduce regression (I could have changed isString...)
    public boolean isStringOrChar() {
        return isString() || isChar();
    }

    private boolean isChar() {
        switch (jdbcType) {
            case Types.CHAR:
                return true;
            default:
                return false;
        }
    }

    public boolean isNumeric() {
        switch (jdbcType) {
            case Types.BIGINT:
            case Types.DECIMAL:
            case Types.DOUBLE:
            case Types.FLOAT:
            case Types.INTEGER:
            case Types.NUMERIC:
            case Types.REAL:
            case Types.SMALLINT:
            case Types.TINYINT:
            case Types.ROWID:
                return true;
            default:
                return false;
        }
    }

    public boolean isTemporal() {
        switch (jdbcType) {
            case Types.DATE:
            case Types.TIMESTAMP:
            case Types.TIME:
                return true;
            default:
                return false;
        }
    }
}
