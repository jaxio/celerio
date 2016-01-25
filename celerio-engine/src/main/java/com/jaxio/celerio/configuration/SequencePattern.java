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

import com.jaxio.celerio.util.StringUtil;
import lombok.Setter;
import org.springframework.util.AntPathMatcher;

import static com.jaxio.celerio.util.StringUtil.hasLength;

/*
 * A pattern is a structure to help handling inclusion and exclusion of resources
 */
public class SequencePattern {
    @Setter
    private String tableNamePattern;
    @Setter
    private String sequenceName;
    @Setter
    private String catalog;
    @Setter
    private String schema;
    @Setter
    private Integer initialValue;
    @Setter
    private Integer allocationSize;
    private transient AntPathMatcher antPathMatcher = new AntPathMatcher();

    public SequencePattern() {
    }

    public SequencePattern(String tableNamePattern, String sequenceName) {
        this.tableNamePattern = tableNamePattern;
        this.sequenceName = sequenceName;
    }

    /*
     * The table pattern. If the pattern contains '?', '*' the matching will be done using an ant matcher, otherwise it will do a equalsIgnoreCase
     * <ul>
     * <li>? matches one character</li>
     * <li>* matches zero or more characters</li>
     * </ul>
     * Some examples:
     * <ul>
     * <li>US?R - matches USER but also USOR or USAR etc.</li>
     * <li>TBL_* - matches all tables whose name starts with TBL_, for example TBL_USER</li>
     * </ul>
     */
    public String getTableNamePattern() {
        return tableNamePattern;
    }

    /*
     * The sequence name to use for the entity that is associated with the matching table. You can use the magic '{TABLE_NAME}' token which is replaced with the
     * corresponding table name. For example: SEQ_{TABLE_NAME}.
     */
    public String getSequenceName() {
        return sequenceName;
    }



    /**
     * (Optional) The catalog of the sequence generator.
     */
    public String getCatalog() {
        return catalog;
    }

    public boolean hasCatalog() {
        return hasLength(catalog);
    }

    /**
     *  (Optional) The schema of the sequence generator.
     */
    public String getSchema() {
        return schema;
    }

    public boolean hasSchema() {
        return hasLength(schema);
    }

    /*
     * (Optional) The value from which the sequence object
     * is to start generating. Defaults to 1.
     */
    public Integer getInitialValue() {
        return initialValue;
    }

    public boolean hasNonDefaultInitialValue() {
        return initialValue != null && initialValue != 1;
    }

    /*
     * (Optional) The amount to increment by when allocating
     * sequence numbers from the sequence. Defaults to 50.
     */
    public Integer getAllocationSize() {
        return allocationSize;
    }

    public boolean hasNonDefaultAllocationSize() {
        return allocationSize != null && allocationSize != 50;
    }

    public AntPathMatcher getAntPathMatcher() {
        return antPathMatcher;
    }

    public boolean match(String value) {
        if (value == null) {
            return false;
        }

        if (antPathMatcher.isPattern(tableNamePattern)) {
            return antPathMatcher.match(tableNamePattern.toLowerCase(), value.toLowerCase());
        } else {
            return value.toLowerCase().equalsIgnoreCase(tableNamePattern.toLowerCase());
        }
    }
}