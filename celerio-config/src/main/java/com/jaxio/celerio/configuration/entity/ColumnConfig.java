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

package com.jaxio.celerio.configuration.entity;

import com.google.common.base.Predicate;
import com.jaxio.celerio.configuration.database.JdbcType;
import com.jaxio.celerio.util.MappedType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.jaxio.celerio.configuration.Util.nonNull;
import static com.jaxio.celerio.configuration.database.support.SqlUtil.escapeSql;
import static java.lang.Boolean.TRUE;
import static org.apache.commons.lang.StringUtils.isNotBlank;

@Setter
public class ColumnConfig {
    protected List<Label> labels;
    protected List<String> usages = newArrayList();
    protected EnumConfig enumConfig = new EnumConfig();
    protected String sharedEnumName;
    protected SafeHtml safeHtml;
    protected GeneratedValue generatedValue;
    protected GenericGenerator genericGenerator;
    protected IndexedField indexedField;
    protected List<CustomAnnotation> customAnnotations = newArrayList();
    @Getter
    protected ManyToOneConfig manyToOneConfig;
    @Getter
    protected OneToManyConfig oneToManyConfig;
    @Getter
    protected OneToOneConfig oneToOneConfig;
    @Getter
    protected OneToOneConfig inverseOneToOneConfig;
    @Getter
    protected ManyToManyConfig manyToManyConfig;
    // db metadata
    protected Boolean ignore;
    protected JdbcType type; // TODO: confusing ==> rename to jdbcType
    protected MappedType mappedType;
    protected Boolean lazy;
    protected String fieldName;
    protected String tableName;
    protected String columnName;
    protected Integer size;
    protected Integer min;
    protected Integer ordinalPosition;
    protected Integer displayOrder;
    protected String typeConverter;
    protected String comment;
    protected Integer decimalDigits;
    protected String defaultValue;
    // functional
    protected Boolean businessKey;
    protected Boolean asTransient;
    protected Boolean messageKey;
    protected Boolean html;
    private String label;
    // relations
    protected Boolean inverse;
    protected AssociationDirection associationDirection;
    protected Boolean enableOneToVirtualOne;
    // front
    protected Boolean autoIncrement;
    protected Boolean nullable;
    protected Boolean formField;
    protected Boolean searchField;
    protected Boolean searchResult;
    protected Boolean selectLabel;
    protected Boolean unique;
    protected Boolean version;
    protected Boolean visible;
    protected Boolean password;
    // relation
    protected String targetTableName;
    protected String targetColumnName;

    /*
     * The labels for this attribute. They appear in the entity properties file located under 'src/main/resources/localization/domain'.
     */
    public List<Label> getLabels() {
        return labels;
    }

    /*
     * For future uses
     */
    public List<String> getUsages() {
        return usages;
    }

    public void setUsages(List<String> usages) {
        this.usages = nonNull(usages);
    }

    /*
     * Convenient for hbm2celerio to avoid dead tags.
     */
    public void forceUsagesToNullIfEmpty() {
        if (usages != null && usages.isEmpty()) {
            usages = null;
        }
    }

    /*
     * Specify the enum config to map this column to a Java enum. If this enum is used by other columns, you must declare instead a shared enum and reference it
     * using the sharedEnumName attribute.
     */
    public EnumConfig getEnumConfig() {
        return enumConfig;
    }

    public void setEnumConfig(EnumConfig e) {
        this.enumConfig = e;
    }

    public boolean hasEnum() {
        return hasEnumConfig() || hasSharedEnum();
    }

    public boolean hasSharedEnum() {
        return getSharedEnumName() != null;
    }

    public boolean hasEnumConfig() {
        return enumConfig != null && enumConfig.hasEnum();
    }

    /*
     * References a shared enum name by its name. You cannot have both an enum configuration, and a shared enum name.
     */
    public String getSharedEnumName() {
        return sharedEnumName;
    }

    /*
     * When this element is present, the SafeHtml annotation is added on this field.
     */
    public SafeHtml getSafeHtml() {
        return safeHtml;
    }

    public boolean hasSafeHtml() {
        return safeHtml != null;
    }

    /*
     * The base label for this column. You may either set it here or in a nested labels/label.
     */
    public String getLabel() {
        return label;
    }

    /*
     * When the column represents a single primary key, you can configure the GeneratedValue JPA annotation here.
     */
    public GeneratedValue getGeneratedValue() {
        return generatedValue;
    }

    public boolean hasGeneratedValue() {
        return generatedValue != null;
    }

    /*
     * When the column represents a single primary key, you can configure the GenericGenerator JPA annotation here.
     */
    public GenericGenerator getGenericGenerator() {
        return genericGenerator;
    }

    public boolean hasGenericGenerator() {
        return genericGenerator != null;
    }

    /*
     * Configure the Hibernate search Field annotation. If present, the field is annotated, if absent it is not.
     */
    public IndexedField getIndexedField() {
        return indexedField;
    }

    public boolean hasIndexedField() {
        return indexedField != null;
    }

    /*
     * List of custom annotations to apply on this property.
     */
    public List<CustomAnnotation> getCustomAnnotations() {
        return customAnnotations;
    }

    public void setCustomAnnotations(List<CustomAnnotation> customAnnotations) {
        this.customAnnotations = nonNull(customAnnotations);
    }

    /*
     * Convenient for hbm2celerio to avoid dead tags.
     */
    public void forceCustomAnnotationsToNullIfEmpty() {
        if (customAnnotations != null && customAnnotations.isEmpty()) {
            customAnnotations = null;
        }
    }

    /*
     * If set to true, the column will be ignored. Make sure you do not ignore not null columns.
     */
    public Boolean getIgnore() {
        return ignore;
    }

    public boolean hasTrueIgnore() {
        return getIgnore() == Boolean.TRUE;
    }

    /*
     * Override the default JdbcType.
     */
    public JdbcType getType() {
        return type;
    }

    /*
     * Force the Java mapped type for this column instead of relying on Celerio's conventions.
     */
    public MappedType getMappedType() {
        return mappedType;
    }

    /*
     * Should the mapped property be lazy loaded ? If yes, the annotation <code>@Basic(fetch = FetchType.LAZY)</code> is used. Defaults to 'true' for CLOB, BLOB
     * or BYTES mapped types.
     */
    public Boolean getLazy() {
        return lazy;
    }

    public boolean hasLazy() {
        return lazy != null;
    }

    /*
     * The corresponding variable name in the Java world. By default, the field name is deduced from the column name.<br>
     * For primary key the field name is always forced to Example: 'first_name' will become 'firstName';
     */
    public String getFieldName() {
        return fieldName;
    }

    /*
     * Allows you to use JPA secondary table if you set a table name that is different from the entity table name. Default to the entity table name.
     */
    public String getTableName() {
        return tableName;
    }

    public boolean hasTableName() {
        return isNotBlank(tableName);
    }

    public String getTableNameEscaped() {
        return escapeSql(getTableName());
    }

    /*
     * The mandatory column name.
     */
    public String getColumnName() {
        return columnName;
    }

    public boolean hasColumnName() {
        return isNotBlank(columnName);
    }

    /*
     * Override the column size defined in the Metadata. Defaults to the size found when reversing the database schema.
     */
    public Integer getSize() {
        return size;
    }

    public boolean hasMin() {
        return getMin() != null;
    }

    /*
     * Minimum length for String. When present it is used in the @Size validation annotation. No default value.
     */
    public Integer getMin() {
        return min;
    }

    /*
     * Override the column ordinal position defined in the Metadata. Defaults to the ordinal position found when reversing the database schema.
     */
    public Integer getOrdinalPosition() {
        return ordinalPosition;
    }

    /*
     * The order of appearance of this column in forms, from top to bottom and in search results, from left to right. It defaults to the ordinal position.
     */
    public Integer getDisplayOrder() {
        return displayOrder;
    }

    /*
     * Sets the 'type' attribute passed to the 'org.hibernate.annotations.Type' annotation. By default, no Type annotation is used.
     */
    public String getTypeConverter() {
        return typeConverter;
    }

    /*
     * Indicates if this property is part of the entity business key. You may set it on several properties at the same time if your business key involves more
     * than one column. If set to true, the property will be used in equals/hashCode methods. As soon as you declare this attribute on a property, convention no
     * longer applies for the entity.
     */
    public Boolean getBusinessKey() {
        return businessKey;
    }

    public boolean hasTrueBusinessKey() {
        return TRUE == businessKey;
    }

    /*
     * Allows you to override the getter in a sub-class that extends the base entity. <br>
     * If set to true, all the annotations for the corresponding getter will be commented and a @Transient annotation will be set.
     */
    public Boolean getAsTransient() {
        return asTransient;
    }

    public boolean isTransient() {
        return asTransient != null ? asTransient : false;
    }

    /*
     * Override the comment defined in the Metadata. The comment that will be inserted as JavaDoc in the corresponding getter method. Defaults to the comment
     * found when reversing the database schema.
     */
    public String getComment() {
        return comment;
    }

    public boolean hasComment() {
        return isNotBlank(comment);
    }

    /*
     * Override the column decimal digits defined in the Metadata. Defaults to the decimal digits found when reversing the database schema.
     */
    public Integer getDecimalDigits() {
        return decimalDigits;
    }

    /*
     * Override the default value defined in the Metadata. Defaults to the default value found when reversing the database schema.
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    public boolean hasDefaultValue() {
        return getDefaultValue() != null;
    }

    /*
     * Indicates whether the possible values held by this column are used as keys to resolve the associated localized values.
     */
    public Boolean getMessageKey() {
        return messageKey;
    }

    /*
     * Does this column contain html? When true, some special escaping on the front is performed.
     */
    public Boolean getHtml() {
        return html;
    }

    public boolean hasHtml() {
        return html != null;
    }

    /*
     * If this column represents a foreign key that points to the target of a ManyToMany association it can be set to true to change the default inverse side of
     * the ManyToMany association. By convention, the column with the highest ordinal position refers to the inverse side.
     */
    public Boolean getInverse() {
        return inverse;
    }

    /*
     * If this column represents an importedKey, should it be bidirectionnal or unidirectionnal
     */
    public AssociationDirection getAssociationDirection() {
        return associationDirection;
    }

    /*
     * If this column represents an importedKey, and the column is unique, should the one to one be handled via a collection ?
     */
    public Boolean getEnableOneToVirtualOne() {
        return enableOneToVirtualOne;
    }

    /*
     * Override the autoIncrement value defined in the Metadata. You should use it only in case your driver is unable to determine whether the primary key is
     * auto incremented or not.
     */
    public Boolean getAutoIncrement() {
        return autoIncrement;
    }

    /*
     * Override the nullable value defined in the Metadata. Defaults to the nullable value found when reversing the database schema.
     */
    public Boolean getNullable() {
        return nullable;
    }

    /*
     * Should this column appear in edit form? By default, most columns appear in the edit form. Note that this configuration is taken into account only if at
     * least one of the entity's columnConfig formField attribute (could be this one) is set to true.
     */
    public Boolean getFormField() {
        return formField;
    }

    public boolean hasTrueFormField() {
        return formField == TRUE;
    }

    /*
     * Should this column appear in search form?
     */
    public Boolean getSearchField() {
        return searchField;
    }

    public boolean hasTrueSearchField() {
        return searchField == TRUE;
    }

    /*
     * Should this column appear in search results?
     */
    public Boolean getSearchResult() {
        return searchResult;
    }

    public boolean hasTrueSearchResult() {
        return searchResult == TRUE;
    }

    /*
     * Should this column be part of the label representation
     */
    public Boolean getSelectLabel() {
        return selectLabel;
    }

    /*
     * Override the uniqueness defined in the indexes from the metadata.
     */
    public Boolean getUnique() {
        return unique;
    }

    /*
     * Should this column be visible to the users ?
     */
    public Boolean getVisible() {
        return visible;
    }

    /*
     * Should this column be used to provide optimistic locking? If true, this column will be mapped with a @Version annotation providing the mapped type is
     * compatible with @Version expected type. Defaults to true if the fieldName is "version" and if the mapped type is compatible with @Version.
     */
    public Boolean getVersion() {
        return version;
    }

    /*
     * If you use a legacy database schema that does not declare foreign keys, you can manually set the target table name. If you do, you may also need to set
     * the targetColumnName. This attribute is not taken into account if a foreign key is already declared in your schema.
     */
    public String getTargetTableName() {
        return targetTableName;
    }

    public boolean hasTargetTableName() {
        return isNotBlank(targetTableName);
    }

    /*
     * Once you have set the targetTableName, you can adjust the targetColumnName if it is different from the primaryKey column. Defaults to the
     * targetTableName's primary key column.
     */
    public String getTargetColumnName() {
        return targetColumnName;
    }

    public boolean hasTargetColumnName() {
        return isNotBlank(targetColumnName);
    }

    public boolean useConfigForIdGenerator() {
        return hasGeneratedValue() || hasGenericGenerator();
    }

    public boolean hasTargetEntityName() {
        int confCounter = 0;

        if (getManyToOneConfig() != null) {
            confCounter++;
        }

        if (getOneToOneConfig() != null) {
            confCounter++;
        }

        if (getManyToManyConfig() != null) {
            confCounter++;
        }

        if (confCounter > 1) {
            throw new IllegalStateException("You cannot have more than one association config on columnConfig " + getColumnName());
        }

        return (getManyToOneConfig() != null && getManyToOneConfig().hasTargetEntityName()) ||
                (getOneToOneConfig() != null && getOneToOneConfig().hasTargetEntityName()) ||
                (getManyToManyConfig() != null && getManyToManyConfig().hasTargetEntityName());
    }

    public String lookupTargetEntityName() {
        if (getManyToOneConfig() != null && getOneToOneConfig() != null) {
            throw new IllegalStateException("You cannot have a manyToOneConfig and a oneToOneConfig at the same time on columnConfig " + getColumnName());
        } else if (getManyToOneConfig() != null) {
            return getManyToOneConfig().getTargetEntityName();
        } else if (getOneToOneConfig() != null) {
            return getOneToOneConfig().getTargetEntityName();
        } else if (getManyToManyConfig() != null) {
            return getManyToManyConfig().getTargetEntityName();
        } else {
            return null;
        }
    }

    /*
     * Should this column be considered as storing a password ? This will impact input types attribute on the web tier.
     */
    public Boolean getPassword() {
        return password;
    }

    public Boolean isPassword() {
        return TRUE == getPassword();
    }

    public static Predicate<ColumnConfig> SEARCH_RESULT = new Predicate<ColumnConfig>() {
        @Override
        public boolean apply(ColumnConfig columnConfig) {
            return columnConfig.hasTrueSearchResult();
        }
    };

    public static Predicate<ColumnConfig> SEARCH_FIELD = new Predicate<ColumnConfig>() {
        @Override
        public boolean apply(ColumnConfig columnConfig) {
            return columnConfig.hasTrueSearchField();
        }
    };

    public static Predicate<ColumnConfig> FORM_FIELD = new Predicate<ColumnConfig>() {
        @Override
        public boolean apply(ColumnConfig columnConfig) {
            return columnConfig.hasTrueFormField();
        }
    };
}