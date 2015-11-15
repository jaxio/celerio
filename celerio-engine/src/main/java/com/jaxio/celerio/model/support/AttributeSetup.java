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

package com.jaxio.celerio.model.support;

import com.jaxio.celerio.configuration.DateMapping;
import com.jaxio.celerio.configuration.NumberMapping;
import com.jaxio.celerio.configuration.entity.ColumnConfig;
import com.jaxio.celerio.model.Attribute;
import com.jaxio.celerio.util.MappedType;
import lombok.extern.slf4j.Slf4j;

import java.sql.Types;
import java.util.List;

import static com.jaxio.celerio.model.support.SuffixPrefixPredicates.IS_BOOLEAN_PREFIX;
import static com.jaxio.celerio.util.MappedType.*;

@Slf4j
public class AttributeSetup {
    private Attribute attribute;
    /**
     * example on oracle : should Number(1,0) be treated as a boolean ?
     * <p>
     * Technically oracle maps this type to a numeric ranging from 0 to 9.
     */
    private boolean applyDecimalWithSizeOneAndNoDigitsAreBooleanConventionEnabled = true;

    public AttributeSetup(Attribute attribute) {
        this.attribute = attribute;
    }

    public MappedType getMappedType() {
        // Configuration
        MappedType mt = getMappedTypeByConfiguration();
        if (mt != null) {
            return mt;
        }

        // By global configuration
        mt = getMappedTypeByGlobalMappingConfiguration();
        if (mt != null) {
            return mt;
        }

        // Convention
        return getMappedTypeByConvention();
    }

    private MappedType getMappedTypeByConfiguration() {
        if (attribute.getColumnConfig().getMappedType() != null) {
            return attribute.getColumnConfig().getMappedType();
        }
        return null;
    }

    private MappedType getMappedTypeByGlobalMappingConfiguration() {

        switch (attribute.getJdbcType().getJdbcType()) {
            case Types.BIGINT:
            case Types.DOUBLE:
            case Types.FLOAT:
            case Types.INTEGER:
            case Types.SMALLINT:
            case Types.TINYINT:
            case Types.DECIMAL:
            case Types.NUMERIC:
            case Types.REAL:
                // a number, we can continue.
                return getNumberMappedType();

            case Types.DATE:
            case Types.TIME:
            case Types.TIMESTAMP:
                return getDateMappedType();
            default:
                // the number mapping does not apply.
                return null;
        }
    }

    private MappedType getNumberMappedType() {
        List<NumberMapping> numberMappings = attribute.getConfig().getCelerio().getConfiguration().getNumberMappings();
        for (NumberMapping nm : numberMappings) {
            if (nm.match(attribute.getColumnConfig().getSize(), attribute.getColumnConfig().getDecimalDigits())) {

                log.info("Number Mapping match for " + attribute.getFullColumnName() + " jdbcType=" + attribute.getJdbcType().getName() + "("
                        + attribute.getColumnConfig().getSize() + ", " + attribute.getColumnConfig().getDecimalDigits() + ") => " + nm.toString());

                MappedType mt = nm.getMappedType();

                // make sure we do not let PK or FK be mapped to big integer as it is not supported by JPA.
                if (mt == MappedType.M_BIGINTEGER && (attribute.isInPk() || attribute.isInFk())) {
                    return MappedType.M_LONG;
                }

                return mt;
            }
        }

        // no mapping found
        return null;
    }

    private MappedType getDateMappedType() {
        List<DateMapping> dateMappings = attribute.getConfig().getCelerio().getConfiguration().getDateMappings();
        for (DateMapping dm : dateMappings) {
            if (dm.match(attribute.getJdbcType(), attribute.getColumnName())) {
                log.info("Date Mapping match for " + attribute.getFullColumnName().toUpperCase() + " jdbcType=" + attribute.getJdbcType().getName() + " => "
                        + dm.toString());
                return dm.getMappedType();
            }
        }

        // no mapping found
        return null;
    }

    private MappedType getMappedTypeByConvention() {
        MappedType mt = getRawMappedType();

        if (applyDecimalWithSizeOneAndNoDigitsAreBooleanConventionEnabled && mt.isNumeric() && !attribute.isEnum()
                && attribute.getColumnConfig().getSize() == 1 && attribute.getColumnConfig().getDecimalDigits() == 0) {
            mt = MappedType.M_BOOLEAN;
            log.info("Applying boolean convention to '" + attribute.getFullName()
                    + "'. If needed, you can force the mapping to another type using the mappedType attribute in columnConfig.");
        }

        return mt;
    }

    private MappedType getRawMappedType() {
        switch (attribute.getJdbcType().getJdbcType()) {
            case Types.ARRAY:
                return M_ARRAY;
            case Types.BIGINT:
                return M_LONG;
            case Types.BINARY:
                return M_BYTES;
            case Types.BIT:
            case Types.BOOLEAN:
                return M_BOOLEAN;
            case Types.BLOB:
                return M_BLOB;
            case Types.CLOB:
            case Types.NCLOB:
            case Types.SQLXML:
                return M_CLOB;
            case Types.DATALINK:
                return M_URL;
            case Types.DATE:
                return M_UTILDATE;
            case Types.DISTINCT:
            case Types.JAVA_OBJECT:
                return M_OBJECT;
            case Types.DOUBLE:
            case Types.FLOAT:
                return M_DOUBLE;
            case Types.INTEGER:
            case Types.SMALLINT:
            case Types.TINYINT:
                return M_INTEGER;
            case Types.LONGVARBINARY:
                return M_BYTES;
            case Types.CHAR:
            case Types.LONGVARCHAR:
            case Types.VARCHAR:
            case Types.LONGNVARCHAR:
            case Types.NCHAR:
            case Types.NVARCHAR:
                return M_STRING;
            case Types.DECIMAL:
            case Types.NUMERIC:
                ColumnConfig column = attribute.getColumnConfig();
                if (column.getDecimalDigits() > 0) {
                    if (column.getSize() < 11 && column.getDecimalDigits() < 4) {
                        return M_DOUBLE;
                    } else {
                        return M_BIGDECIMAL;
                    }
                } else {
                    if (column.getSize() == 1 && IS_BOOLEAN_PREFIX.apply(attribute)) {
                        return M_BOOLEAN;
                    } else {
                        if (column.getSize() < 11) {
                            return M_INTEGER;
                        } else if (column.getSize() < 19 || attribute.isInPk() || attribute.isInFk()) { // BIG INTEGER IS NOT
                            // ACCEPTED BY HIBERNATE
                            // FOR PK
                            return M_LONG;
                        } else {
                            return M_BIGINTEGER;
                        }
                    }
                }
            case Types.OTHER:
                return M_OBJECT;
            case Types.REAL:
                return M_FLOAT;
            case Types.REF:
                return M_REF;
            case Types.STRUCT:
                return M_OBJECT;
            case Types.TIME:
            case Types.TIMESTAMP:
                return M_UTILDATE;
            case Types.VARBINARY:
                if (attribute.getColumnConfig().getSize() == 1) {
                    return M_BYTE;
                } else {
                    return M_BYTES;
                }
            case Types.ROWID:
                return M_LONG;
            default:
                throw new RuntimeException("Could not retrieve the type of " + this);
        }
    }
}
