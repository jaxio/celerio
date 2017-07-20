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

package com.jaxio.celerio.model.support.jpa;

import com.jaxio.celerio.configuration.TrueFalse;
import com.jaxio.celerio.configuration.database.Table;
import com.jaxio.celerio.configuration.entity.CacheConfig;
import com.jaxio.celerio.configuration.entity.CacheConfigGetter;
import com.jaxio.celerio.configuration.entity.ColumnConfig;
import com.jaxio.celerio.model.Attribute;
import com.jaxio.celerio.model.Unique;
import com.jaxio.celerio.model.unique.CompositeUnique;
import com.jaxio.celerio.spi.support.AbstractEntitySpi;
import com.jaxio.celerio.util.AnnotationBuilder;
import com.jaxio.celerio.util.AttributeBuilder;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static com.jaxio.celerio.configuration.database.support.SqlUtil.escapeSql;
import static javax.persistence.InheritanceType.JOINED;
import static javax.persistence.InheritanceType.SINGLE_TABLE;
import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * Helper to write non trivial JPA annotations that may be applied to the generated entity class.
 * An annotation is considered non trivial when generating it would require an unreasonable
 * amount of velocity code in the template.
 */
public class JpaEntity extends AbstractEntitySpi {
    @Override
    public String velocityVar() {
        return "jpa";
    }

    @Override
    public Object getTarget() {
        return this;
    }

    public List<String> getAnnotations() {
        AnnotationBuilder ab = new AnnotationBuilder();
        ab.add(getEntityAnnotation());
        ab.add(getHibernateCacheAnnotation()); // TODO: move to cache entity spi ?
        ab.add(getTableAnnotation());
        ab.add(getInheritanceAnnotations());
        ab.add(getDiscriminatorColumnAnnotation());
        ab.add(getDiscriminatorValueAnnotation());
        ab.add(getSecondaryTableAnnotations());
        ab.add(getPrimaryKeyJoinColumnAnnotation());
        ab.add(getAttributeOverrideAnnotation());
        return ab.getAnnotations();
    }

    public String getAttributeOverrideAnnotation() {
        if (entity.isRoot() || !entity.hasParent() || !entity.getParent().is(JOINED)) {
            return "";
        }

        List<String> attributesOverrides = newArrayList();
        AttributeBuilder attributeBuilder = new AttributeBuilder();
        for (Attribute attribute : entity.getAttributes().getList()) {
            for (Attribute upAttribute : entity.getAttributes().getFlatAbove().getList()) {
                if (attribute.getColumnName().equalsIgnoreCase(upAttribute.getColumnName())) {
                    addImport("javax.persistence.AttributeOverride");
                    String attributeOverride = "@AttributeOverride(name = \"" + attribute.getVar() + "\", column = @Column(name=\""
                            + attribute.getColumnNameEscaped() + "\"))";
                    attributesOverrides.add(attributeOverride);
                    attributeBuilder.add(attributeOverride);
                }
            }
        }
        if (attributesOverrides.size() == 0) {
            return "";
        } else if (attributesOverrides.size() == 1) {
            return appendComment(attributesOverrides.iterator().next());
        } else {
            addImport("javax.persistence.AttributeOverrides");
            return appendComment("@AttributeOverrides({" + attributeBuilder.getAttributes() + "})");
        }
    }

    public String getPrimaryKeyJoinColumnAnnotation() {
        if (entity.isRoot() || !entity.hasInheritance() || !entity.getParent().is(JOINED)) {
            return "";
        }

        Table rootTable = entity.getRoot().getTable();
        Table entityTable = entity.getTable();

        Assert.isTrue(rootTable.getPrimaryKeys().size() == 1, "Composite PK are not supported with JOIN inheritance strategy");
        Assert.isTrue(entityTable.getPrimaryKeys().size() == 1, "Composite PK are not supported with JOIN inheritance strategy");

        String rootPkColumnName = rootTable.getPrimaryKeys().iterator().next();
        String entityPkColumnName = entityTable.getPrimaryKeys().iterator().next();

        if (!entityPkColumnName.equalsIgnoreCase(rootPkColumnName)) {
            return appendComment(getPrimaryKeyJoinColumnAnnotation(entityPkColumnName));
        }

        return "";
    }

    public String getSecondaryTableAnnotations() {
        Set<Table> secondaryTables = getSecondaryTables();
        if (secondaryTables.size() == 0) {
            return "";
        } else if (secondaryTables.size() == 1) {
            return appendComment(getSecondaryTableAnnotation(secondaryTables.iterator().next()));
        } else {
            addImport("javax.persistence.SecondaryTables");
            StringBuilder sb = new StringBuilder("@SecondaryTables({");
            boolean first = true;
            for (Table secondaryTable : secondaryTables) {
                if (first) {
                    first = false;
                } else {
                    sb.append(", ");
                }
                sb.append(getSecondaryTableAnnotation(secondaryTable));
            }
            sb.append("})");
            return appendComment(sb.toString());
        }
    }

    public String getDiscriminatorValueAnnotation() {
        if (!(entity.hasInheritance() && entity.getInheritance().hasDiscriminatorValue() && entity.is(SINGLE_TABLE))) {
            return "";
        }
        addImport("javax.persistence.DiscriminatorValue");
        return appendComment("@DiscriminatorValue(\"" + entity.getInheritance().getDiscriminatorValue() + "\")");
    }

    public String getInheritanceAnnotations() {
        if (!(entity.hasInheritance() && entity.isRoot())) {
            return "";
        }

        addImport("javax.persistence.Inheritance");
        if (entity.is(SINGLE_TABLE)) {
            return appendComment("@Inheritance");
        } else {
            addImport("javax.persistence.InheritanceType");
            return appendComment("@Inheritance(strategy = InheritanceType." + entity.getInheritance().getStrategy() + ")");
        }
    }

    public String getDiscriminatorColumnAnnotation() {
        if (!(entity.hasInheritance() && entity.isRoot())) {
            return "";
        }

        if (entity.getInheritance().hasDiscriminatorColumn()) {
            addImport("javax.persistence.DiscriminatorColumn");
            return appendComment("@DiscriminatorColumn(name = \"" + entity.getInheritance().getDiscriminatorColumn() + "\")");
        }

        return "";
    }

    private Set<Table> getSecondaryTables() {
        Set<Table> result = newHashSet();
        for (ColumnConfig cc : entity.getEntityConfig().getColumnConfigs()) {
            // skip ignored attributes
            if (cc.hasTrueIgnore()) {
                continue;
            }

            // skip same table
            if (entity.getTableName().equalsIgnoreCase(cc.getTableName())) {
                continue;
            }

            // keep different ones
            Table secondaryTable = entity.getConfig().getMetadata().getTableByName(cc.getTableName());
            result.add(secondaryTable);
        }

        return result;
    }

    private String getSecondaryTableAnnotation(Table secondaryTable) {
        Table table = entity.getTable();

        Assert.isTrue(table.getPrimaryKeys().size() == 1, "The table '" + table.getName() + "' is expected to have a one-column primary key. "
                + "It has instead " + table.getPrimaryKeys().size());

        Assert.isTrue(secondaryTable.getPrimaryKeys().size() == 1, "The secondary table '" + secondaryTable.getName()
                + "' is expected to have a one-column primary key. " + "It has instead " + secondaryTable.getPrimaryKeys().size());

        String pkColumnName = table.getPrimaryKeys().iterator().next();
        String secondaryPkColumnName = secondaryTable.getPrimaryKeys().iterator().next();

        addImport("javax.persistence.SecondaryTable");
        if (pkColumnName.equalsIgnoreCase(secondaryPkColumnName)) {
            return "@SecondaryTable(name = \"" + secondaryTable.getNameEscaped() + "\")";
        } else {
            return "@SecondaryTable(name = \"" + secondaryTable.getNameEscaped() + "\", " + "pkJoinColumns = "
                    + getPrimaryKeyJoinColumnAnnotation(secondaryPkColumnName) + ")";
        }
    }

    private String getPrimaryKeyJoinColumnAnnotation(String pkColumnName) {
        addImport("javax.persistence.PrimaryKeyJoinColumn");
        return "@PrimaryKeyJoinColumn(name = \"" + escapeSql(pkColumnName) + "\")";
    }

    public String getSelectAllNamedQuery() {
        return entity.getModel().getType() + ".selectAll";
    }

    public String getSelectAllNativeNamedQuery() {
        return entity.getModel().getType() + ".selectAll.native";
    }

    public String getHibernateCacheAnnotation() {
        if (!entity.isRoot()) {
            return "";
        }

        String cacheAnnotation = JpaConfigHelper.getCacheAnnotation(this, //
                entity.getEntityConfig(), //
                new CacheConfigGetter() {
                    @Override
                    public CacheConfig getCacheConfig() {
                        return entity.getConfig().getCelerio().getConfiguration().getDefaultEntityCacheConfig();
                    }
                });
        return appendComment(cacheAnnotation);
    }

    public String getEntityAnnotation() {
        addImport("javax.persistence.Entity");
        return appendComment("@Entity");
    }

    public String getTableAnnotation() {
        if (entity.isRoot() || (entity.hasInheritance() && entity.getParent().is(JOINED))) {
            StringBuffer annotation = new StringBuffer("");
            appendAttribute(annotation, getCatalog());
            appendAttribute(annotation, getSchema());
            appendAttribute(annotation, getName());
            appendAttribute(annotation, getUniqueConstraints());
            addImport("javax.persistence.Table");
            return appendComment("@Table(" + annotation + ")");
        }
        return "";
    }

    private String getCatalog() {
        if (entity.getConfig().getCelerio().getConfiguration().getJpaUseCatalog() == TrueFalse.TRUE) {
            String catalog = entity.getEntityConfig().getCatalog();
            return isNotBlank(catalog) ? "catalog = \"" + catalog + "\"" : "";
        }
        return "";
    }

    private String getSchema() {
        if (entity.getConfig().getCelerio().getConfiguration().getJpaUseSchema() == TrueFalse.TRUE) {
            String schema = entity.getEntityConfig().getSchemaName();
            return isNotBlank(schema) && !schema.contains("%") ? "schema = \"" + schema + "\"" : "";
        }
        return "";
    }

    private String getName() {
        return "name = \"" + escapeSql(entity.getTableName()) + "\"";
    }

    private String getUniqueConstraints() {
        List<String> constraints = getTableUniqueConstraints();

        if (constraints.isEmpty()) {
            return "";
        } else {
            String constraintsAsString = "uniqueConstraints = {";
            boolean first = true;
            for (String constraint : constraints) {
                if (!first) {
                    constraintsAsString += ", ";
                } else {
                    first = false;
                }
                constraintsAsString += constraint;
            }
            constraintsAsString += "}";
            return constraintsAsString;
        }
    }

    private List<String> getTableUniqueConstraints() {
        List<String> result = newArrayList();
        for (Unique unique : entity.getCompositeUniques().getList()) {
            result.add(buildConstraint((CompositeUnique) unique));
        }
        return result;
    }

    private String buildConstraint(CompositeUnique compositeUnique) {
        addImport("javax.persistence.UniqueConstraint");
        String ret = "@UniqueConstraint(";
        if (isNotBlank(compositeUnique.getName())) {
            ret += "name=\"" + compositeUnique.getName() + "\", ";
        }
        ret += "columnNames = {";
        boolean first = true;
        for (Attribute uniqueAttribute : compositeUnique.getAttributes()) {
            if (!first) {
                ret += ", ";
            } else {
                first = false;
            }
            ret += "\"" + escapeSql(uniqueAttribute.getColumnName()) + "\"";
        }
        ret += "})";
        return ret;
    }

    private void appendAttribute(StringBuffer ret, String attribute) {
        if (isNotBlank(attribute)) {
            if (ret.length() > 0) {
                ret.append(", ");
            }
            ret.append(attribute);
        }
    }
}
