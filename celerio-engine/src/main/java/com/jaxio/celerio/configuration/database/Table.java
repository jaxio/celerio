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

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.jaxio.celerio.configuration.Util.nonNull;
import static com.jaxio.celerio.configuration.database.support.SqlUtil.escapeSql;
import static org.springframework.util.StringUtils.hasLength;

/*
 * Describes all the metadata for a given table
 */
@Slf4j
public class Table {

    // columns
    private List<Column> columns = newArrayList();
    private Map<String, Column> columnsByName = newHashMap();

    // indexes
    private List<Index> indexes = newArrayList();
    private Map<String, IndexHolder> indexHoldersByName = newHashMap();

    // imported keys
    private List<ImportedKey> importedKeys = newArrayList();
    private Map<String, ForeignKey> foreignKeysByName = newHashMap();

    // primary keys
    private List<String> primaryKeys = newArrayList();

    // misc
    @Setter
    protected String name;
    @Setter
    protected String remarks;
    @Setter
    protected TableType type;

    @Getter
    @Setter
    protected String schemaName;
    @Getter
    @Setter
    protected String catalog;

    public Map<String, Column> getColumnsByName() {
        return columnsByName;
    }

    public void setColumnsByName(Map<String, Column> columnsByName) {
        this.columnsByName = columnsByName;
    }

    public Map<String, IndexHolder> getIndexHoldersByName() {
        return indexHoldersByName;
    }

    public void setIndexHoldersByName(Map<String, IndexHolder> indexHoldersByName) {
        this.indexHoldersByName = indexHoldersByName;
    }

    public Map<String, ForeignKey> getForeignKeysByName() {
        return foreignKeysByName;
    }

    public void setForeignKeysByName(Map<String, ForeignKey> foreignKeysByName) {
        this.foreignKeysByName = foreignKeysByName;
    }

    /*
     * This table name<br>
     * Example: USER
     */
    public String getName() {
        return name;
    }

    public String getNameEscaped() {
        return escapeSql(getName());
    }

    /*
     * Documentation for this table<br>
     * Example: Table containing all the user related information
     */
    public String getRemarks() {
        return remarks;
    }

    /*
     * Type of the table
     */
    public TableType getType() {
        return type;
    }

    // --------------------------------
    // Columns
    // --------------------------------

    /*
     * Describes all the columns metadata for this table
     */
    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = nonNull(columns);
        columnsByName = newHashMap();

        for (Column column : this.columns) {
            putColumnByName(column);
        }
    }

    public void addColumn(Column column) {
        String colName = column.getName();

        if (colName != null) {
            if (getColumnByName(colName) == null) {
                // avoid this kind of issue: http://stackoverflow.com/questions/1601203/jdbc-databasemetadata-getcolumns-returns-duplicate-columns
                columns.add(column);
                putColumnByName(column);
            } else {
                log.warn("Skip already added column: " + colName);
            }
        } else {
            log.warn("Skip column with null name! (should not happen)");
        }
    }

    public Column getColumnByName(String name) {
        if (name == null) {
            return null;
        }
        return columnsByName.get(name.toUpperCase());
    }

    private void putColumnByName(Column column) {
        // TODO: assert column's name not null
        if (column.getName() != null) {
            columnsByName.put(column.getName().toUpperCase(), column);
        }
    }

    // --------------------------------
    // Index
    // --------------------------------
    /*
     * Describes all the indexes for this table
     */
    public List<Index> getIndexes() {
        return indexes;
    }

    public void setIndexes(List<Index> indexes) {
        this.indexes = nonNull(indexes);
        indexHoldersByName = newHashMap();

        for (Index index : this.indexes) {
            putIndexInIndexHoldersByName(index);
        }
    }

    public void addIndex(Index index) {
        if (!hasLength(index.getIndexName())) {
            log.warn("Found an index with no name on table: " + getName());
            return;
        }

        indexes.add(index);
        putIndexInIndexHoldersByName(index);
    }

    public void putIndexInIndexHoldersByName(Index i) {
        if (!hasLength(i.getIndexName())) {
            log.warn("Found an index with no name on table: " + getName());
            return;
        }

        IndexHolder ih = indexHoldersByName.get(i.getIndexName().toUpperCase());
        if (ih == null) {
            ih = new IndexHolder(getName());
            indexHoldersByName.put(i.getIndexName().toUpperCase(), ih);
        }
        ih.addIndex(i);
    }

    /*
     * Returns the Index List that represent unique constraints.
     */
    public List<IndexHolder> getUniqueIndexHolders() {
        List<IndexHolder> result = newArrayList();

        for (IndexHolder ih : indexHoldersByName.values()) {
            if (ih.isUnique()) {
                result.add(ih);
            }
        }

        return result;
    }

    public IndexHolder getIndexHolderByName(String indexName) {
        return indexHoldersByName.get(indexName.toUpperCase());
    }

    /*
     * Determine if the passed column is part of a unique index that has only 1 element, the passed column.
     */
    public boolean isUnique(String columnName) {
        if (columnName == null) {
            return false;
        }

        for (IndexHolder ih : indexHoldersByName.values()) {
            if (ih.isUnique() && ih.getSize() == 1) {
                if (ih.getIndexes().iterator().next().getColumnName().equalsIgnoreCase(columnName)) {
                    return true;
                }
            }
        }

        return false;
    }

    // --------------------------------
    // Imported keys
    // --------------------------------
    /*
     * Describes all the imported keys for this table
     */
    public List<ImportedKey> getImportedKeys() {
        return importedKeys;
    }

    public void setImportedKeys(List<ImportedKey> importedKeys) {
        this.importedKeys = nonNull(importedKeys);
        foreignKeysByName = newHashMap();

        for (ImportedKey importedKey : this.importedKeys) {
            putImportedKeyInForeignKeyByName(importedKey);
        }
    }

    /*
     * Add real constraints, coming from the database.
     */
    public void addImportedKey(ImportedKey importedKey) {
        importedKeys.add(importedKey);
        putImportedKeyInForeignKeyByName(importedKey);
    }

    /*
     * Check if the passed importedKey is already present or not in a existing ForeignKey of size 1.
     */
    public boolean alreadyPresent(ImportedKey importedKey) {
        if (foreignKeysByName == null) {
            return false;
        }

        Collection<ForeignKey> fks = foreignKeysByName.values();
        if (fks == null) {
            return false;
        }

        for (ForeignKey fk : fks) {
            if (fk.getSize() == 1) {
                ImportedKey other = fk.getImportedKey();
                // Note: we just need to compare the FkColumnName, if we have a match, we consider
                // that this is an error in the conf, and we skip. No need to compare pkTable and pkCol
                boolean same = importedKey.getFkColumnName().equalsIgnoreCase(other.getFkColumnName());

                if (same) {
                    return true;
                }
            }
        }
        return false;
    }

    private void putImportedKeyInForeignKeyByName(ImportedKey key) {
        ForeignKey fk = foreignKeysByName.get(key.getFkName().toUpperCase());
        if (fk == null) {
            fk = new ForeignKey(key.getFkName(), key.getPkTableName(), getName());
            foreignKeysByName.put(key.getFkName().toUpperCase(), fk);
        }
        fk.addImportedKey(key);
    }

    public ForeignKey getForeignKeyByName(String name) {
        return foreignKeysByName.get(name.toUpperCase());
    }

    public Collection<ForeignKey> getForeignKeys() {
        return foreignKeysByName.values();
    }

    // --------------------------------
    // Primary keys
    // --------------------------------

    /*
     * Describes all the primary keys for this table
     */
    public List<String> getPrimaryKeys() {
        return primaryKeys;
    }

    public void setPrimaryKeys(List<String> primaryKeys) {
        this.primaryKeys = nonNull(primaryKeys);
    }

    public void addPrimaryKey(String columnName) {
        primaryKeys.add(columnName);
    }

    /*
     * Should the PK be created with IDENTITY?
     */
    public boolean hasH2IdentityPk() {
        return hasSimplePrimaryKey() && getColumnByName(getPrimaryKey()).candidateForH2Identity();
    }

    public boolean hasPk() {
        return getPrimaryKeys().size() > 0;
    }

    public boolean hasSimplePrimaryKey() {
        return getPrimaryKeys().size() == 1;
    }

    public boolean hasCompositePrimaryKey() {
        return getPrimaryKeys().size() > 1;
    }

    public String getPrimaryKey() {
        Assert.isTrue(hasSimplePrimaryKey(), "Not a simple primary key. You cannot invoke this method");
        return getPrimaryKeys().iterator().next();
    }

    public void cleanup() {
        removeEmptyEnumValues();
        removeEmptyMetaAttributes();
    }

    private void removeEmptyEnumValues() {
        for (Column column : getColumns()) {
            if (column != null && !column.hasEnum()) {
                column.removeEnumValues();
            }
        }
    }

    private void removeEmptyMetaAttributes() {
        for (Column column : getColumns()) {
            if (column != null && !column.hasMetaAttribute()) {
                column.removeMetaAttributes();
            }
        }
    }

    public static String keyForMap(String schemaName, String tableName) {
        if (StringUtils.isNotBlank(schemaName)) {
            return schemaName.toUpperCase() + "." + tableName.toUpperCase();
        }
        return tableName.toUpperCase();
    }

    public String asKeyForMap() {
        return keyForMap(schemaName, name);
    }
}
