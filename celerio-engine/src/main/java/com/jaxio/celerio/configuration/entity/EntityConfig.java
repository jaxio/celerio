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

import com.jaxio.celerio.configuration.MetaAttribute;
import com.jaxio.celerio.configuration.Util;
import com.jaxio.celerio.configuration.convention.CollectionType;
import com.jaxio.celerio.convention.CommentStyle;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.InheritanceType;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Iterables.any;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.jaxio.celerio.configuration.Util.nonNull;
import static com.jaxio.celerio.configuration.entity.ColumnConfig.*;
import static java.lang.Boolean.TRUE;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.springframework.util.StringUtils.hasLength;

/*
 * Describes an entity config
 */
@Setter
@Slf4j
public class EntityConfig implements CacheConfigGetter {
    private Boolean skip;
    private String entityName;
    @Getter
    private String catalog;
    @Getter
    private String schemaName;
    private String tableName;
    private String sequenceName;
    private Boolean indexed;
    private Boolean middleTable;
    private String comment;
    private String rootPackage;
    private String subPackage;
    private String label;
    private AssociationDirection associationDirection;
    private CacheConfig cacheConfig;
    private List<String> usages = newArrayList();
    private Inheritance inheritance;
    private CollectionType collectionType;
    private ExtendsClass extendsClass;
    private List<ImplementsInterface> implementsInterfaces = newArrayList();
    private List<CustomAnnotation> customAnnotations = newArrayList();
    private List<Label> labels;
    private List<ColumnConfig> columnConfigs = newArrayList();
    private List<MetaAttribute> metaAttributes = newArrayList();
    // Ignored by JIBX thanks to src/main/config/customization.xml
    private Map<String, ColumnConfig> columnConfigByColumnName = newHashMap();

    /*
     * The 2d level cache configuration for this entity.
     */
    public CacheConfig getCacheConfig() {
        return cacheConfig;
    }

    /*
     * Free parameters. Not used internally.
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
     * Convenient for hbm2celerio to avoid dead tags.
     */
    public void forceImplementsInterfacesToNullIfEmpty() {
        if (implementsInterfaces != null && implementsInterfaces.isEmpty()) {
            implementsInterfaces = null;
        }
    }

    public Boolean getSkip() {
        return skip;
    }

    public boolean shouldSkip() {
        return skip != null && skip == TRUE;
    }

    /*
     * The JPA entity's type. <br>
     * For example, entityName="BankAccount". <br>
     * By default, the entity name is deduced from the table name. <br>
     * For example: 'bank_account' will become 'BankAccount';
     */
    public String getEntityName() {
        return entityName;
    }

    public boolean hasEntityName() {
        return hasLength(entityName);
    }

    /*
     * Allows you to specify the sequence name to use in order to generate this entity pk value. When a sequence name is provided the corresponding @SequenceGenerator
     * and @GeneratedValue annotations are added to the primary key attribute.
     */
    public String getSequenceName() {
        return sequenceName;
    }

    public boolean hasSequenceName() {
        return hasLength(sequenceName);
    }

    public boolean hasCatalog() {
        return isNotBlank(catalog);
    }

    public boolean hasSchemaName() {
        return isNotBlank(schemaName);
    }

    /*
     * The underlying table name for the entity. If not set, inheritance must be configured.
     */
    public String getTableName() {
        return tableName;
    }

    public boolean hasTableName() {
        return hasLength(tableName);
    }

    /*
     * Should this entity be annotated with hibernate search @Indexed annotation? False by default, unless if one columnConfig is marked as indexed.
     */
    public Boolean getIndexed() {
        return indexed;
    }

    public boolean hasTrueIndexed() {
        return indexed != null && indexed;
    }

    /*
     * The labels for this entity. They appear in the entity properties file located under 'src/main/resources/localization/domain'.
     */
    public List<Label> getLabels() {
        return labels;
    }

    public boolean atLeastOneColumnConfigIsIndexed() {
        for (ColumnConfig cc : columnConfigs) {
            if (cc.hasIndexedField()) {
                return true;
            }
        }
        return false;
    }

    /*
     * By convention a table is considered as a many-to-many middle table if it has two foreign keys and no other regular columns. This attribute allows you to
     * consider this table as a middle table, even if it has other regular columns. A regular column is a column that is neither a primary key nor a version
     * (i.e. optimistic lock).
     */
    public Boolean getMiddleTable() {
        return middleTable;
    }

    public boolean hasMiddleTable() {
        return middleTable != null;
    }

    /*
     * The comment that will be inserted in this entity's JavaDoc.
     */
    public String getComment() {
        return comment;
    }

    public String getCommentAsJavadoc() {
        return getCommentAsJavadoc("    ");
    }

    public String getCommentAsJavadoc(String prepend) {
        return CommentStyle.JAVADOC.decorate(getComment(), prepend);
    }

    public boolean hasComment() {
        return hasLength(comment);
    }

    /*
     * Allows you to override the default root package.<br>
     * Example: com.yourcompany
     */
    public String getRootPackage() {
        return rootPackage;
    }

    /*
     * When you define a sub-package, the resulting entity's package becomes <code>rootPackage.domain.subPackage</code>
     * instead of <code>rootPackage.domain</code>. There is no sub-package by default.
     */
    public String getSubPackage() {
        return subPackage;
    }

    /*
     * The base label for this entity. You may either set it here or in a nested labels/label.
     */
    public String getLabel() {
        return label;
    }

    /*
     * It is pertinent only if this entity's table plays the role of a middle table in a many-to-many association.
     * In that case you can use this parameter to set the many-to-many association direction.
     */
    public AssociationDirection getAssociationDirection() {
        return associationDirection;
    }

    /*
     * Inheritance configuration.
     */
    public Inheritance getInheritance() {
        return inheritance;
    }

    public boolean hasInheritance() {
        return inheritance != null;
    }

    public boolean hasParentEntityName() {
        return inheritance != null && inheritance.hasParentEntityName();
    }

    public String getParentEntityName() {
        return inheritance.getParentEntityName();
    }

    /*
     * You can override the default collection type for this entity
     */
    public CollectionType getCollectionType() {
        return collectionType;
    }

    public boolean hasCollectionType() {
        return getCollectionType() != null;
    }

    /*
     * Specify the base class that this entity should extends. Only for root entity.
     */
    public ExtendsClass getExtendsClass() {
        return extendsClass;
    }

    public boolean hasExtendsClass() {
        return getExtendsClass() != null && getExtendsClass().hasFullType();
    }

    /*
     * Specify the extra interfaces that this entity should implement.
     */
    public List<ImplementsInterface> getImplementsInterfaces() {
        return implementsInterfaces;
    }

    public boolean hasImplementsInterfaces() {
        return getImplementsInterfaces() != null && !getImplementsInterfaces().isEmpty();
    }

    /*
     * Meta attributes are free form key value pairs.
     */
    public List<MetaAttribute> getMetaAttributes() {
        return metaAttributes;
    }

    /*
     * This entity's columnConfigs. Note that for entities without inheritance or for entities with a JOIN inheritance strategy, if a column is present in the
     * table's meta data but has no corresponding entityConfig in this list, then an entityConfig is created by default and added automatically to this list.
     */
    public List<ColumnConfig> getColumnConfigs() {
        return columnConfigs;
    }

    public void setColumnConfigs(List<ColumnConfig> columnConfigs) {
        this.columnConfigs = nonNull(columnConfigs);

        // reset our index
        columnConfigByColumnName = newHashMap();

        // reindex
        for (ColumnConfig columnConfig : this.columnConfigs) {
            putColumnConfigByColumnName(columnConfig);
        }
    }

    public boolean hasColumnConfigs() {
        return Util.hasSize(columnConfigs);
    }

    public void addColumnConfig(ColumnConfig columnConfig) {
        if (columnConfigs.contains(columnConfig)) {
            log.warn("received twice the same columnConfig!", new Exception("Need investigation as it should not happen"));
        } else {
            columnConfigs.add(columnConfig);
            putColumnConfigByColumnName(columnConfig);
        }
    }

    public ColumnConfig getColumnConfigByColumnName(String columnName) {
        return columnConfigByColumnName.get(columnName.toUpperCase());
    }

    public ColumnConfig getColumnConfigByFieldName(String fieldName) {
        for (ColumnConfig columnConfig : columnConfigs) {
            if (columnConfig.getFieldName().equalsIgnoreCase(fieldName)) {
                return columnConfig;
            }
        }
        return null;
    }

    private void putColumnConfigByColumnName(ColumnConfig columnConfig) {
        // TODO: assert not null...
        if (columnConfig.getColumnName() != null) {
            columnConfigByColumnName.put(columnConfig.getColumnName().toUpperCase(), columnConfig);
        }
    }

    /*
     * List of custom annotations to apply on this entity.
     */
    public List<CustomAnnotation> getCustomAnnotations() {
        return customAnnotations;
    }

    public void setCustomAnnotations(List<CustomAnnotation> customAnnotations) {
        this.customAnnotations = nonNull(customAnnotations);
    }

    public Map<String, ColumnConfig> getColumnConfigByColumnName() {
        return columnConfigByColumnName;
    }

    public boolean is(InheritanceType strategy) {
        return hasInheritance() && getInheritance().is(strategy);
    }

    public boolean is(AssociationDirection association) {
        return association == getAssociationDirection();
    }

    public boolean useSearchResultConfig() {
        return any(getColumnConfigs(), SEARCH_RESULT);
    }

    public boolean useSearchFieldConfig() {
        return any(getColumnConfigs(), SEARCH_FIELD);
    }

    public boolean useFormFieldConfig() {
        return any(getColumnConfigs(), FORM_FIELD);
    }

    @Override
    public String toString() {
        return "[entityName=" + getEntityName() + ", tableName=" + getTableName() + "]";
    }
}