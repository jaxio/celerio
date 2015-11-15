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

import com.jaxio.celerio.configuration.MetaAttribute;
import lombok.Setter;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static com.jaxio.celerio.configuration.Util.nonNull;

/*
 * Configuration of a column, the data reflect the jdbc metadata
 */
public class Column {
    @Setter
    protected String columnDef;
    @Setter
    protected int decimalDigits;
    @Setter
    protected String name;
    @Setter
    protected Boolean autoIncrement;
    @Setter
    protected boolean nullable;
    @Setter
    protected int ordinalPosition;
    @Setter
    protected String remarks;
    @Setter
    protected int size;
    @Setter
    protected JdbcType type;
    protected Set<String> enumValues = newHashSet();
    protected Set<MetaAttribute> metaAttributes = newHashSet();

    /*
     * Default value
     */
    public String getColumnDef() {
        return getDefaultValue(columnDef);
    }

    /*
     * The number of fractional digits
     */
    public int getDecimalDigits() {
        return decimalDigits;
    }

    /*
     * Column name
     */
    public String getName() {
        return name;
    }

    /*
     * Is Auto Increment?
     */
    public Boolean getAutoIncrement() {
        return autoIncrement;
    }

    /*
     * Is NULL allowed ?
     */
    public boolean isNullable() {
        return nullable;
    }

    /*
     * Index of column in table (starting at 1)
     */
    public int getOrdinalPosition() {
        return ordinalPosition;
    }

    /*
     * Comment describing the column
     */
    public String getRemarks() {
        return remarks;
    }

    /*
     * Column size. For char or date types this is the maximum number of characters, for numeric or decimal types this is precision.
     */
    public int getSize() {
        return size;
    }

    /*
     * This column jdbc type
     */
    public JdbcType getType() {
        return type;
    }

    /*
     * Enum values if the column represents an enum
     */
    public Set<String> getEnumValues() {
        return enumValues;
    }

    public void setEnumValues(Set<String> enumValues) {
        this.enumValues = nonNull(enumValues);
    }

    public void removeEnumValues() {
        this.enumValues = null;
    }

    public boolean hasEnum() {
        return enumValues != null && !enumValues.isEmpty();
    }

    public void addEnumValue(String value) {
        enumValues.add(value);
    }

    /*
     * Enum values if the column represents an enum
     */
    public Set<MetaAttribute> getMetaAttributes() {
        return metaAttributes;
    }

    public void setMetaAttributes(Set<MetaAttribute> metaAttributes) {
        this.metaAttributes = nonNull(metaAttributes);
    }

    public void removeMetaAttributes() {
        this.metaAttributes = null;
    }

    public boolean hasMetaAttribute() {
        return metaAttributes != null && !metaAttributes.isEmpty();
    }

    public void addMetaAttribute(MetaAttribute metaAttribute) {
        if (metaAttributes == null) {
            metaAttributes = newHashSet();
        }
        metaAttributes.add(metaAttribute);
    }

    // H2 RELATED

    public boolean candidateForH2Identity() {
        // we assume the caller knows whether it is a pk or not.
        return getType().isNumeric() && getDecimalDigits() == 0;
    }

    public String getH2() {
        StringBuilder builder = new StringBuilder(300);
        builder.append(name);
        builder.append(" ").append(type.name());
        if ((size > 0 || decimalDigits > 0) && !type.isTemporal()) { // DATE/TIME/TIMESTAMP should not have size and decimal digit on H2.
            builder.append("(");
            if (size > 0) {
                builder.append(size);
            }
            if (decimalDigits > 0 && supportH2Scale()) {
                if (size > 0) {
                    builder.append(",");
                }
                builder.append(decimalDigits);
            }
            builder.append(")");
        }
        if (!nullable) {
            builder.append(" not null");
        }
        if (hasSimpleDefaultValue()) {
            builder.append(" default ");
            builder.append(getQuote());
            builder.append(getColumnDef().replace("'", "''")); // escape quote
            builder.append(getQuote());
        } else if (type.isTemporal() && getColumnDef() != null && getColumnDef().toUpperCase().contains("SYSDATE")) {
            builder.append(" default SYSDATE");
        } else if (type.isTemporal() && getColumnDef() != null && getColumnDef().toUpperCase().startsWith("NEXTVAL(")) {
            // TODO
        }

        return builder.toString();
    }

    private boolean supportH2Scale() {
        if ("DOUBLE".equalsIgnoreCase(type.name())) {
            return false;
        }
        return true;
    }

    private boolean hasSimpleDefaultValue() {
        if (type.isNumeric() && getColumnDef() != null && getColumnDef().length() == 0) {
            return false;
        }
        return getColumnDef() != null && !getColumnDef().startsWith("(") && !type.isTemporal() && !getColumnDef().toLowerCase().startsWith("nextval");
    }

    private String getDefaultValue(String rawValue) {
        if (rawValue == null) {
            return null;
        }
        rawValue = rawValue.trim();
        if ("NULL".equalsIgnoreCase(rawValue)) {
            return null;
        }
        while (rawValue.length() >= 2 && rawValue.startsWith("'") && rawValue.endsWith("'")) {
            rawValue = rawValue.substring(1, rawValue.length() - 1);
        }
        return rawValue.trim();
    }

    private String getQuote() {
        return type.isNumeric() ? "" : "'";
    }
}
