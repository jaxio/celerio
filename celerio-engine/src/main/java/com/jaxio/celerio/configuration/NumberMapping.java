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

import com.jaxio.celerio.util.MappedType;
import lombok.Setter;

/*
 * Global rule to map columns whose JDBC TYPE correspond to a number to a Java type.
 */
@Setter
public class NumberMapping {
    private MappedType mappedType;
    private Integer columnSizeMin;
    private Integer columnSizeMax;
    private Integer columnDecimalDigitsMin;
    private Integer columnDecimalDigitsMax;

    /*
     * The mapped type to use when both the column size and decimal digit value fall into the specified ranges.
     */
    public MappedType getMappedType() {
        return mappedType;
    }

    /*
     * The minimum (inclusive) column size to fall into this mapping range.
     */
    public Integer getColumnSizeMin() {
        return columnSizeMin;
    }

    /*
     * The maximum (exclusive) column size to fall into this mapping range.
     */
    public Integer getColumnSizeMax() {
        return columnSizeMax;
    }

    /*
     * The minimum (inclusive) column decimal digit value to fall into this mapping range.
     */
    public Integer getColumnDecimalDigitsMin() {
        return columnDecimalDigitsMin;
    }

    /*
     * The maximum (exclusive) column decimal digit value to fall into this mapping range.
     */
    public Integer getColumnDecimalDigitsMax() {
        return columnDecimalDigitsMax;
    }

    public boolean match(Integer size, Integer decimalDigits) {
        if (size == null || decimalDigits == null) {
            return false;
        }

        return (columnSizeMin == null || size >= columnSizeMin) && (columnSizeMax == null || size < columnSizeMax)
                && (columnDecimalDigitsMin == null || decimalDigits >= columnDecimalDigitsMin)
                && (columnDecimalDigitsMax == null || decimalDigits < columnDecimalDigitsMax);
    }

    @Override
    public String toString() {
        return "NumberMapping: mappedType=" + mappedType + " size[" + columnSizeMin + ", " + columnSizeMax + "[ decimalDigits [" + columnDecimalDigitsMin + ","
                + columnDecimalDigitsMax + "[";
    }
}
