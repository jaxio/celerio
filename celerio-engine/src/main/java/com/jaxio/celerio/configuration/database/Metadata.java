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

import lombok.Setter;
import org.apache.commons.lang.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.jaxio.celerio.configuration.Util.nonNull;
import static com.jaxio.celerio.configuration.database.Table.keyForMap;

public class Metadata {
    @Setter
    private JdbcConnectivity jdbcConnectivity = new JdbcConnectivity();
    @Setter
    private DatabaseInfo databaseInfo = new DatabaseInfo();
    private List<Table> tables = newArrayList();
    // Ignored by JIBX thanks to src/main/config/customization.xml
    private Set<String> tablesInMultipleSchema = new HashSet<String>();
    private Map<String, Table> tablesByName = newHashMap();
    private Map<String, Table> tablesBySchemaAndName = newHashMap();

    public void setTables(List<Table> tables) {
        this.tables = nonNull(tables);
        tablesByName = newHashMap();

        for (Table table : this.tables) {
            putTable(table);
        }
    }

    public JdbcConnectivity getJdbcConnectivity() {
        return jdbcConnectivity;
    }

    public DatabaseInfo getDatabaseInfo() {
        return databaseInfo;
    }

    public List<Table> getTables() {
        return tables;
    }

    public Map<String, Table> getTablesByName() {
        return tablesByName;
    }

    public void add(Table table) {
        tables.add(table);
        putTable(table);
    }

    public Table getTableByName(String tableName) {
        if (tableName == null) {
            return null;
        }

        if (tablesInMultipleSchema.contains(tableName.toUpperCase())) {
            return null;
        }

        return tablesByName.get(tableName.toUpperCase());
    }

    public Table getTableBySchemaAndName(String schemaName, String tableName) {
        if (tableName == null) {
            return null;
        }

        if (StringUtils.isBlank(schemaName)) {
            return getTableByName(tableName);
        }

        return tablesBySchemaAndName.get(keyForMap(schemaName, tableName));
    }

    private void putTable(Table table) {
        if (tablesByName.containsKey(table.getName().toUpperCase())) {
            tablesInMultipleSchema.add(table.getName().toUpperCase());
        } else {
            tablesByName.put(table.getName().toUpperCase(), table);
        }

        tablesBySchemaAndName.put(keyForMap(table.schemaName, table.name), table);
    }

    public void cleanMetadata() {
        for (Table table : getTables()) {
            if (table != null) {
                table.cleanup();
            }
        }
    }
}
