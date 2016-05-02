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

import com.jaxio.celerio.configuration.entity.EnumConfig;
import com.jaxio.celerio.convention.GeneratedPackage;
import com.jaxio.celerio.model.Attribute;
import com.jaxio.celerio.spi.AttributeSpi;
import com.jaxio.celerio.template.ImportsContext;
import com.jaxio.celerio.util.MappedType;

// TODO: all this class is a big hack as ValueGenerator is hardcoded
public class ValuesAttribute implements AttributeSpi {
    private Attribute attribute;

    @Override
    public void init(Attribute attribute) {
        this.attribute = attribute;
    }

    /**
     * @return "values"
     */
    @Override
    public String velocityVar() {
        return "values";
    }

    @Override
    public Object getTarget() {
        return this;
    }

    public String getDummy() {
        if (attribute.isInPk() || attribute.isUnique()) {
            return getUnique();
        } else if (attribute.isEnum()) {
            return getDefaultEnum();
        } else if (attribute.isBoolean()) {
            return "true";
        } else if (attribute.isLocalDateOrTime() || attribute.isZonedDateTime()) {
            return attribute.getMappedType().getFullJavaType() + ".now()"; // TODO: nice import
        } else if (attribute.getMappedType() == MappedType.M_UTILDATE) {
            return "new " + attribute.getMappedType().getJavaType() + "()";
        } else if (attribute.getMappedType() == MappedType.M_TIMESTAMP) {
            return "new java.sql.Timestamp(new Date().getTime())";
        } else if (attribute.isDate()) {
            return "new Date()";
        } else if (attribute.isEmail()) {
            return "\"dummy@dummy.com\"";
        } else if (attribute.isContentType()) {
            return "\"application/text\"";
        } else if (attribute.isFilename()) {
            return "\"dummy.txt\"";
        } else if (attribute.isBlob()) {
            return "\"dummy\".getBytes()";
        } else if (attribute.isInteger()) {
            return "1";
        } else if (attribute.isLong()) {
            return "1l";
        } else if (attribute.isFloat()) {
            return "1f";
        } else if (attribute.isDouble()) {
            return "1d";
        } else if (attribute.isBigInteger()) {
            return "BigInteger.ONE";
        } else if (attribute.isBigDecimal()) {
            return "BigDecimal.ONE";
        } else if (attribute.getMappedType().isChar()) {
            return "'a'";
        } else if (attribute.getMappedType().isByte()) {
            return "(byte)1";
        } else if (attribute.isUrl()) {
            return "\"http://www.jaxio.com\"";
        } else if (attribute.getMappedType().isString() && attribute.isFixedSize()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < attribute.getSize(); i++) {
                sb.append("d");
            }
            return "\"" + sb.toString() + "\"";
        } else if (attribute.getMappedType().isString() && attribute.getColumnConfig().getMin() != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < attribute.getColumnConfig().getMin(); i++) {
                sb.append("a");
            }
            return "\"" + sb.toString() + "\"";
        } else {

            return "\"a\"";
        }
    }

    public String getNullDummy() {
        if (attribute.isNumeric()) {
            return "(" + attribute.getType() + ")null";
        } else {
            return "null";
        }
    }

    public String getUnique() {
        // limit to avoid memory errors
        int size = Math.min(attribute.getColumnConfig().getSize(), 255);

        if (attribute.isEnum()) {
            return getDefaultEnum();
        } else if (attribute.isBoolean()) {
            // can be used if we create unique key on several columns
            return "true";
        }

        // hack
        ImportsContext.addImport(GeneratedPackage.Util.getPackageName() + ".ValueGenerator");

        if (attribute.isDate()) {
            return "ValueGenerator.getUniqueDate()";
        } else if (attribute.isEmail()) {
            return "ValueGenerator.getUniqueEmail()";
        } else if (attribute.isBlob()) {
            return "ValueGenerator.getUniqueBytes(" + size + ")";
        } else if (attribute.isInteger()) {
            return "ValueGenerator.getUniqueInteger()";
        } else if (attribute.isLong()) {
            return "ValueGenerator.getUniqueLong()";
        } else if (attribute.isFloat()) {
            return "ValueGenerator.getUniqueFloat()";
        } else if (attribute.isDouble()) {
            return "ValueGenerator.getUniqueDouble()";
        } else if (attribute.isBigInteger()) {
            return "ValueGenerator.getUniqueBigInteger()";
        } else if (attribute.isBigDecimal()) {
            return "ValueGenerator.getUniqueBigDecimal()";
        } else if (attribute.getMappedType().isChar()) {
            return "ValueGenerator.getUniqueChar()";
        } else if (attribute.getMappedType().isByte()) {
            return "ValueGenerator.getUniqueByte()";
        } else {
            return "ValueGenerator.getUniqueString(" + size + ")";
        }
    }

    private String getDefaultEnum() {
        EnumConfig enumConfig = attribute.getEnumType().getConfig();
        ImportsContext.addImport(attribute.getEnumType().getModel().getFullType());
        return enumConfig.getName() + "." + enumConfig.getEnumValues().iterator().next().getName();
    }
}
