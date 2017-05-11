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

package com.jaxio.celerio.util;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.lang.BooleanUtils.toBoolean;

public enum MappedType {
    // NOTE: fullType of byte[] is useless in theory but for consistency and to avoid generation errors we need
    // to set one. We set it to java.lang.Byte in order to have it ignored (as any package starting with java.lang.*).
    M_ARRAY("Array", "java.sql.Array"), //
    M_BIGDECIMAL("BigDecimal", "java.math.BigDecimal"), //
    M_BIGINTEGER("BigInteger", "java.math.BigInteger"), //
    M_BOOLEAN("Boolean", "java.lang.Boolean"), //
    M_BYTES("byte[]", "java.lang.Byte"), //
    M_CLOB("String", "java.lang.String"), //
    M_DOUBLE("Double", "java.lang.Double"), //
    M_FLOAT("Float", "java.lang.Float"), //
    M_BLOB("byte[]", "java.lang.Byte"), //
    M_INTEGER("Integer", "java.lang.Integer"), //
    M_LONG("Long", "java.lang.Long"), //
    M_REF("Ref", "java.sql.Ref"), //
    M_STRING("String", "java.lang.String"), //
    M_CHAR("Character", "java.lang.Character"), //
    M_BYTE("Byte", "java.lang.Byte"), //
    M_LOCALDATE("LocalDate", "java.time.LocalDate"), //
    M_LOCALDATETIME("LocalDateTime", "java.time.LocalDateTime"), //
    M_ZONEDDATETIME("ZonedDateTime", "java.time.ZonedDateTime"),
    M_SQLDATE("java.sql.Date", "java.sql.Date"), //
    M_UTILDATE("Date", "java.util.Date"), //
    M_INSTANT("Instant", "java.time.Instant"),
    M_TIME("java.sql.Time", "java.sql.Time"), //
    M_TIMESTAMP("java.sql.Timestamp", "java.sql.Timestamp"), //
    M_URL("java.net.URL", "java.net.URL"), //
    M_OBJECT("Object", "java.lang.Object");

    private final String javaType;

    private final String fullJavaType;

    private MappedType(String javaType, String fullJavaType) {
        this.javaType = javaType;
        this.fullJavaType = fullJavaType;
    }

    public static MappedType fromFullJavaType(String fullJavaType) {
        for (MappedType mappedType : MappedType.values()) {
            if (mappedType.getFullJavaType().equals(fullJavaType)) {
                return mappedType;
            }
        }

        return null;
    }

    public String getJavaType() {
        return javaType;
    }

    public String getFullJavaType() {
        return fullJavaType;
    }

    public boolean isSupportedType() {
        switch (this) {
            case M_ARRAY:
            case M_OBJECT:
            case M_REF:
            case M_URL:
                return false;
            default:
                return true;
        }
    }

    public boolean isNumeric() {
        switch (this) {
            case M_BIGDECIMAL:
            case M_BIGINTEGER:
            case M_DOUBLE:
            case M_FLOAT:
            case M_INTEGER:
            case M_LONG:
                return true;
            default:
                return false;
        }
    }

    public boolean isLong() {
        return this == M_LONG;
    }

    public boolean isInteger() {
        return this == M_INTEGER;
    }

    public boolean isBigInteger() {
        return this == M_BIGINTEGER;
    }

    final public boolean isFloat() {
        return this == M_FLOAT;
    }

    final public boolean isDouble() {
        return this == M_DOUBLE;
    }

    public boolean isBigDecimal() {
        return this == M_BIGDECIMAL;
    }

    /**
     * is Eligible for Version column Version numbers may be of Hibernate type long, integer, short, timestamp or calendar.
     *
     * @return true if numeric, false otherwise
     */
    public boolean isEligibleForVersion() {
        return isInteger() || isLong() || (isDate() && this != M_LOCALDATE && this != M_LOCALDATETIME && this != M_ZONEDDATETIME);
    }

    public boolean isString() {
        return this == M_STRING || this == M_CLOB;
    }

    public boolean isChar() {
        return this == M_CHAR;
    }

    public boolean isObject() {
        return this == M_OBJECT;
    }

    public boolean isBoolean() {
        return this == M_BOOLEAN;
    }

    public boolean isDate() {
        switch (this) {
            case M_SQLDATE:
            case M_UTILDATE:
            case M_TIME:
            case M_TIMESTAMP:
            case M_LOCALDATE:
            case M_LOCALDATETIME:
            case M_ZONEDDATETIME:
            case M_INSTANT:
                return true;
            default:
                return false;
        }
    }

    public boolean isJavaUtilDate() {
        return this == M_UTILDATE;
    }

    public boolean isLocalDate() {
        return this == M_LOCALDATE;
    }

    public boolean isLocalDateTime() {
        return this == M_LOCALDATETIME;
    }

    public boolean isZonedDateTime() {
        return this == M_ZONEDDATETIME;
    }

    public boolean isInstant() {
        return this == M_INSTANT;
    }

    public boolean isByte() {
        return this == M_BYTE;
    }

    public boolean isLob() {
        return isBlob() || isClob();
    }

    public boolean isBlob() {
        return this == M_BLOB || this == M_BYTES;
    }

    public boolean isClob() {
        return this == M_CLOB;
    }

    public boolean isComparable() {
        switch (this) {
            case M_BIGDECIMAL:
            case M_BIGINTEGER:
            case M_BOOLEAN:
            case M_CLOB:
            case M_DOUBLE:
            case M_FLOAT:
            case M_INTEGER:
            case M_LONG:
            case M_STRING:
            case M_CHAR:
            case M_BYTE:
            case M_SQLDATE:
            case M_UTILDATE:
            case M_INSTANT:
                return true;
            case M_TIME:
            case M_REF:
            case M_BLOB:
            case M_BYTES:
            case M_ARRAY:
            case M_TIMESTAMP:
            case M_URL:
            case M_OBJECT:
            default:
                return false;
        }
    }

    public String getJavaValue(String value) {
        if (value == null) {
            return null;
        }

        switch (this) {
            case M_STRING:
                return "\"" + value.replace("\"", "\\\"") + "\"";
            case M_BOOLEAN:
                return toBoolean(value) ? "true" : "false";
            case M_BIGINTEGER:
                return "BigInteger.valueOf(" + value + ")";
            case M_INTEGER:
                try {
                    return (new Integer(value)).toString();
                } catch (Exception e) {
                    break;
                }
            case M_LONG:
                try {
                    return (new Long(value)).toString() + "L";
                } catch (Exception e) {
                    break;
                }
            case M_DOUBLE:
                try {
                    return (new Double(value)).toString() + "d";
                } catch (Exception e) {
                    break;
                }
            case M_SQLDATE:
            case M_UTILDATE:
            case M_TIME:
            case M_TIMESTAMP:
                value = value.toUpperCase();
                if (isCurrentDate(value)) {
                    return "new Date()";
                } else {
                    break;
                }
            case M_LOCALDATE:
                value = value.toUpperCase();
                if (isCurrentDate(value)) {
                    return "LocalDate.now()";
                } else {
                    break;
                }
            case M_LOCALDATETIME:
                value = value.toUpperCase();
                if (isCurrentDate(value)) {
                    return "LocalDateTime.now()";
                } else {
                    break;
                }
            case M_ZONEDDATETIME:
                value = value.toUpperCase();
                if (isCurrentDate(value)) {
                    return "ZonedDateTime.now()";
                } else {
                    break;
                }
            case M_INSTANT:
                value = value.toUpperCase();
                if (isCurrentDate(value)) {
                    return "Instant.now()";
                } else {
                    break;
                }
            case M_ARRAY:
            case M_BIGDECIMAL:
            case M_BYTES:
            case M_CLOB:
            case M_FLOAT:
            case M_BLOB:
            case M_REF:
            case M_CHAR:
            case M_BYTE:
            case M_URL:
            case M_OBJECT:
            default:
                return null;
        }
        return null;
    }

    private List<String> isNow = newArrayList("now()", "sysdate", "current_time");

    private boolean isCurrentDate(String value) {
        return value == null ? false : isNow.contains(value.toLowerCase());
    }

    public String getJavaDefinition(String value) {
        switch (this) {
            case M_STRING:
                return value;
            case M_BOOLEAN:
                return "Boolean.valueOf(" + value + ")";
            case M_BIGINTEGER:
                return "BigInteger.valueOf(" + value + ")";
            case M_INTEGER:
            case M_LONG:
            case M_FLOAT:
            case M_DOUBLE:
            case M_CLOB:
            case M_CHAR:
            case M_BYTE:
            case M_BIGDECIMAL:
                return "new " + this.getJavaType() + "(" + value + ")";
            case M_BLOB:
            case M_BYTES:
                return value + ".getBytes()";
            case M_SQLDATE:
            case M_UTILDATE:
            case M_TIME:
            case M_TIMESTAMP:
                return "new " + this.getFullJavaType() + "(Long.parseLong(" + value + "))";
            case M_LOCALDATETIME:
            case M_LOCALDATE:
            case M_ZONEDDATETIME:
            case M_INSTANT:
                return this.getFullJavaType() + ".parse(" + value + ")";
            case M_ARRAY:
            case M_REF:
            case M_URL:
            case M_OBJECT:
            default:
                throw new IllegalArgumentException("getJavaDefinition for " + this.name() + " is not implemented yet");
        }
    }

    public String getToStringMethod() {
        if (this == M_STRING) {
            return "";
        }
        return ".toString()";
    }

    public boolean isEnumEligible() {
        return !isByte() && !isBlob() && !isDate() && !isObject();
    }

    public String getPackageName() {
        int lastDot = getFullJavaType().lastIndexOf('.');
        return (lastDot > 0) ? getFullJavaType().substring(0, lastDot) : "";
    }

    public String getHibernateType() {
        if (isString()) {
            return "org.hibernate.type.StringType";
        } else if (isInteger()) {
            return "org.hibernate.type.IntegerType";
        } else if (isLong()) {
            return "org.hibernate.type.LongType";
        } else {
            throw new RuntimeException(this + " is not support for hibernate types");
        }
    }

    public boolean isJavaBaseClass() {
        String fullType = getFullJavaType();
        if (fullType.startsWith("java.lang")) {
            return true;
        }
        String[] nativeTypes = new String[]{"boolean", "byte", "char", "short", "int", "float", "double", "void"};
        for (String nativeType : nativeTypes) {
            if (fullType.startsWith(nativeType)) {
                return true;
            }
        }
        return false;
    }
}
