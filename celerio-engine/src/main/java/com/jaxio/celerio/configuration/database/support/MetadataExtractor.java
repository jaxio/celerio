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

package com.jaxio.celerio.configuration.database.support;

import com.google.common.base.Joiner;
import com.jaxio.celerio.configuration.database.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static com.jaxio.celerio.util.EncodingUtil.fixIso;

@Service
@Slf4j
public class MetadataExtractor {
    boolean isOracle;
    boolean isDB2;
    boolean useLabel;
    @Autowired
    private List<Extension> extensions;

    public Metadata extract(JdbcConnectivity configuration) throws ClassNotFoundException, SQLException {
        Class<?> jdbcDriverClass = Class.forName(configuration.getDriver());

        isOracle = isOracle(jdbcDriverClass);
        isDB2 = isDB2(jdbcDriverClass);
        useLabel = !isDB2; // on z/os column label is not supported!

        // patch the configuration in order to work with oracle/db2 when schema is null
        if ((isOracle || isDB2) && configuration.getSchemaName() == null) {
            configuration.setSchemaName(configuration.getUser().toUpperCase());
            if (log.isInfoEnabled()) {
                log.info("    Schema is null, we force it to :\"" + configuration.getSchemaName() + "\"");
            }
        }

        Connection connection = getDatabaseConnection(configuration);

        try {
            return extract(configuration, connection);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    public Metadata extract(Connection connection) throws ClassNotFoundException, SQLException {
        return extract(new JdbcConnectivity(TableType.TABLE), connection);
    }

    public Metadata extract(JdbcConnectivity jdbcConnectivity, Connection connection) throws ClassNotFoundException, SQLException {
        DatabaseMetaData databaseMetadata = connection.getMetaData();
        Metadata metadata = new Metadata();
        metadata.setDatabaseInfo(extractDatabaseInfo(databaseMetadata));
        loadTables(jdbcConnectivity, databaseMetadata, metadata);

        for (Table table : metadata.getTables()) {
            loadColumns(databaseMetadata, table);
            loadPrimaryKeys(databaseMetadata, table);
            loadImportedKeys(databaseMetadata, table, metadata);
            loadIndexes(jdbcConnectivity, databaseMetadata, table);
        }

        metadata.setJdbcConnectivity(jdbcConnectivity);
        loadEnums(connection, metadata);
        return metadata;
    }

    private Connection getDatabaseConnection(JdbcConnectivity configuration) throws ClassNotFoundException, SQLException {

        if (log.isInfoEnabled()) {
            log.info("Connecting to Database jdbcUrl=" + configuration.getUrl());
        }

        Properties info = new Properties();
        info.put("user", configuration.getUser());
        info.put("password", configuration.getPassword());

        if (isOracle && configuration.isOracleRetrieveSynonyms()) {
            info.put("includeSynonyms", "true");
            if (log.isInfoEnabled()) {
                log.info("    Requesting oracle synonyms");
            }
        }

        if (isOracle && configuration.isOracleRetrieveRemarks()) {
            // it is important to pass the property only on oracle connection
            // For example passing it on postgresql trigger a NPE!
            info.put("remarksReporting", "true");

            if (log.isInfoEnabled()) {
                log.info("    Requesting oracle remarks/comments");
            }
        }

        Connection dbConnection = DriverManager.getConnection(configuration.getUrl(), info);
        dbConnection.setReadOnly(true);

        if (log.isInfoEnabled()) {
            log.info("Connected OK");
        }
        return dbConnection;
    }

    private boolean isOracle(Class<?> jdbcDriverClass) throws ClassNotFoundException {
        return jdbcDriverClass.getName().toLowerCase().contains("oracle");
    }

    private boolean isDB2(Class<?> jdbcDriverClass) throws ClassNotFoundException {
        return jdbcDriverClass.getName().toLowerCase().contains("db2");
    }

    private void loadEnums(Connection connection, Metadata metadata) {
        for (Extension extension : extensions) {
            if (extension.applyable(metadata)) {
                extension.apply(connection, metadata);
            }
        }
    }

    private void loadColumns(DatabaseMetaData databaseMetaData, Table table) throws SQLException {
        log.info("Extracting columns for table: " + table.getName());
        ResultSet resultSet = databaseMetaData.getColumns(table.getCatalog(), table.getSchemaName(), table.getName(), "%");
        ResultSetWrapper rsw = new ResultSetColumns(resultSet, useLabel);

        while (resultSet.next()) {
            Column c = new Column();

            // fill it
            c.setName(getString(rsw, "COLUMN_NAME"));
            c.setType(JdbcType.fromJdbcType(rsw.getInt("DATA_TYPE")));
            c.setSize(rsw.getInt("COLUMN_SIZE"));
            c.setDecimalDigits(rsw.getInt("DECIMAL_DIGITS"));
            c.setNullable(isNullable(rsw.getInt("NULLABLE")));
            String remarks = getString(rsw, "REMARKS");
            if (notEmpty(remarks)) {
                c.setRemarks(remarks);
            }
            String columnDef = getString(rsw, "COLUMN_DEF");
            if (notEmpty(columnDef)) {
                c.setColumnDef(columnDef);
            }
            c.setOrdinalPosition(rsw.getInt("ORDINAL_POSITION"));

            try {
                // not all driver may support it, it was added post jdk 1.4.2
                String autoIncrement = getString(rsw, "IS_AUTOINCREMENT");
                if ("YES".equalsIgnoreCase(autoIncrement)) {
                    c.setAutoIncrement(Boolean.TRUE);
                } else if ("NO".equalsIgnoreCase(autoIncrement)) {
                    c.setAutoIncrement(Boolean.FALSE);
                } else {
                    c.setAutoIncrement(null);
                }
            } catch (SQLException sqle) {
                c.setAutoIncrement(null);
            }

            // add it
            table.addColumn(c);
        }

        resultSet.close();
    }

    private void loadImportedKeys(DatabaseMetaData databaseMetaData, Table table, Metadata metaData) throws SQLException {
        log.info("Extracting imported keys for table: " + table.getName());

        ResultSet resultSet = databaseMetaData.getImportedKeys(table.getCatalog(), table.getSchemaName(), table.getName());
        ResultSetWrapper rsw = new ResultSetImportedKeys(resultSet, useLabel);

        while (resultSet.next()) {
            ImportedKey importedKey = new ImportedKey();

            // fill it
            importedKey.setPkTableCatalog(getString(rsw, "PKTABLE_CAT"));
            importedKey.setPkTableSchema(getString(rsw, "PKTABLE_SCHEM"));
            importedKey.setPkTableName(getString(rsw, "PKTABLE_NAME"));
            importedKey.setPkColumnName(getString(rsw, "PKCOLUMN_NAME"));

            importedKey.setFkName(getString(rsw, "FK_NAME"));
            importedKey.setFkColumnName(getString(rsw, "FKCOLUMN_NAME"));

            // With DB2 we observed some duplicate in FK due to presence of table alias. Here is an example:
            // <importedKey fkColumnName="ADMRDEPT" fkName="ROD" pkColumnName="DEPTNO" pkTableName="DEPARTMENT"/>
            // <importedKey fkColumnName="ADMRDEPT" fkName="ROD" pkColumnName="DEPTNO" pkTableName="DEPT"/>
            // DEPT is in fact a table alias!
            // to circumvent the issue, we make sure the imported key points to a table reversed.

            if (metaData.getTableBySchemaAndName(importedKey.getPkTableSchema(), importedKey.getPkTableName()) != null) {
                // add it
                table.addImportedKey(importedKey);
            } else {
                log.warn("Ignoring imported key whose 'pkTableName' cannot be found: " + importedKey);
            }
        }

        resultSet.close();
    }

    private void loadIndexes(JdbcConnectivity configuration, DatabaseMetaData databaseMetaData, Table table) throws SQLException {
        if (!configuration.shouldReverseIndexes()) {
            log.warn("Skipping reverse for indexes of table " + table.getName());
            return;
        }

        boolean retreiveOnlyUniques = configuration.shouldReverseOnlyUniqueIndexes();
        if (retreiveOnlyUniques) {
            log.info("Extracting only unique indexes for table: " + table.getName());
        } else {
            log.info("Extracting all indexes for table: " + table.getName());
        }

        // index reverse can be pretty slow, so we need to warn the user in such cases.
        long thresholdAlertMillisec = 5 * 1000l; // 5 secs 

        long t0 = System.currentTimeMillis();

        // unique or not
        boolean useApproximation = true; // / when true, result is allowed to reflect approximate or out of data values; when false, results are requested to be
        // accurate
        ResultSet resultSet = databaseMetaData.getIndexInfo(table.getCatalog(), table.getSchemaName(), table.getName(), retreiveOnlyUniques,
                useApproximation);
        ResultSetWrapper rsw = new ResultSetIndexInfo(resultSet, useLabel);

        while (resultSet.next()) {
            Index index = new Index();
            index.setIndexName(getString(rsw, "INDEX_NAME"));
            index.setColumnName(getString(rsw, "COLUMN_NAME"));
            index.setNonUnique(rsw.getBoolean("NON_UNIQUE"));

            table.addIndex(index);
        }

        resultSet.close();

        long t1 = System.currentTimeMillis();

        if ((t1 - t0) >= thresholdAlertMillisec) {
            if (retreiveOnlyUniques) {
                log.warn("Table " + table.getName() + ": Index reverse seems long, you can disable it by setting <jdbc.reverseIndexes>false</jdbc.reverseIndexes> maven property.");
            } else {
                log.warn("Table " + table.getName() + ": Index reverse seems long, you may want to restrict it by setting <jdbc.reverseOnlyUniqueIndexes>true</jdbc.reverseOnlyUniqueIndexes> maven property");
            }
        }
    }

    private void loadPrimaryKeys(DatabaseMetaData databaseMetaData, Table table) throws SQLException {
        log.info("Extracting primary key for table: " + table.getName());

        ResultSet resultSet = databaseMetaData.getPrimaryKeys(table.getCatalog(), table.getSchemaName(), table.getName());
        ResultSetWrapper rsw = new ResultSetPrimaryKeys(resultSet, useLabel);

        while (resultSet.next()) {
            table.addPrimaryKey(getString(rsw, "COLUMN_NAME"));
        }

        resultSet.close();
    }

    private DatabaseInfo extractDatabaseInfo(DatabaseMetaData databaseMetaData) {
        DatabaseInfo database = new DatabaseInfo();

        // database
        try {
            database.setDatabaseProductName(databaseMetaData.getDatabaseProductName());
        } catch (Exception e) { /* ignore */
        }

        try {
            database.setDatabaseProductVersion(databaseMetaData.getDatabaseProductVersion());
        } catch (Exception e) { /* ignore */
        }

        try {
            database.setDatabaseMajorVersion(databaseMetaData.getDatabaseMajorVersion());
        } catch (Exception e) { /* ignore */
        }

        try {
            database.setDatabaseMinorVersion(databaseMetaData.getDatabaseMinorVersion());
        } catch (Exception e) { /* ignore */
        }

        // driver
        try {
            database.setDriverName(databaseMetaData.getDriverName());
        } catch (Exception e) { /* ignore */
        }

        try {
            database.setDriverVersion(databaseMetaData.getDriverVersion());
        } catch (Exception e) { /* ignore */
        }

        try {
            database.setDriverMajorVersion(databaseMetaData.getDriverMajorVersion());
        } catch (Exception e) { /* ignore */
        }

        try {
            database.setDriverMinorVersion(databaseMetaData.getDriverMinorVersion());
        } catch (Exception e) { /* ignore */
        }

        return database;
    }

    private void loadTables(JdbcConnectivity configuration, DatabaseMetaData databaseMetaData, Metadata metaData) throws SQLException {

        Set<String> tableNamePatterns = new HashSet<String>();

        if (configuration.getTableNamePatterns() == null || configuration.getTableNamePatterns().isEmpty()) {
            log.info("No table name pattern defined, using '%'");
            tableNamePatterns.add("%");
        } else {
            log.info("Custom table pattern defined");
            tableNamePatterns.addAll(configuration.getTableNamePatterns());
        }

        log.info("Working with " + tableNamePatterns.size() +  " table name pattern(s)");

        for (String tableNamePattern : tableNamePatterns) {

            if (log.isInfoEnabled()) {
                log.info("Loading with catalog=[" + configuration.getCatalog() + "] schemaPattern=[" + configuration.getSchemaName() + "] tableNamePattern=["
                    + tableNamePattern + "] types=[" + Joiner.on(",").join(configuration.getTableTypes()) + "]");
            }
            ResultSet resultSet = databaseMetaData.getTables(configuration.getCatalog(), configuration.getSchemaName(),tableNamePattern, getTableTypesAsStringArray(configuration.getTableTypes()));
            ResultSetWrapper rsw = new ResultSetTables(resultSet, useLabel);

            while (resultSet.next()) {
                Table table = new Table();

                table.setCatalog(getString(rsw, "TABLE_CAT"));
                table.setSchemaName(getString(rsw, "TABLE_SCHEM"));
                table.setName(getString(rsw, "TABLE_NAME"));
                table.setType(TableType.valueOf(getString(rsw, "TABLE_TYPE")));
                String remarks = getString(rsw, "REMARKS");
                if (notEmpty(remarks)) {
                    table.setRemarks(remarks);
                }

                if (!skipTable(table)) {
                    if (log.isInfoEnabled()) {
                        log.info("Table reversed: " + table.getName());
                    }
                    metaData.add(table);
                } else {
                    if (log.isInfoEnabled()) {
                        log.info("Table ignored : " + table.getName());
                    }
                }
            }
        }
    }

    private String[] getTableTypesAsStringArray(List<TableType> tableTypes) {
        List<String> ret = newArrayList();
        for (TableType tableType : tableTypes) {
            ret.add(tableType.name());
        }
        return ret.toArray(new String[0]);
    }

    private boolean notEmpty(String s) {
        return s != null && !"".equals(s);
    }

    private boolean isNullable(int nullable) {
        switch (nullable) {
            case DatabaseMetaData.columnNoNulls:
                return false;
            case DatabaseMetaData.columnNullableUnknown:
            case DatabaseMetaData.columnNullable:
            default:
                return true;
        }
    }

    private boolean skipTable(Table table) {
        String tableNameUpper = table.getName().toUpperCase();

        if ("HIBERNATE_UNIQUE_KEY".equals(tableNameUpper)) {
            log.warn("    Table " + table.getName() + " found but it is a hibernate specific table, skipping");
            return true;
        }

        if (tableNameUpper.startsWith("BIN$") ||
            tableNameUpper.startsWith("DR$") ||
            tableNameUpper.startsWith("RUPD$") ||
            tableNameUpper.startsWith("MLOG$")) {
            log.warn("    Table " + table.getName() + " found but is a special table, skipping");
            return true;
        }

        return false;
    }

    private String getString(ResultSetWrapper rsw, String key) throws SQLException {
        String rawData = rsw.getString(key);
        if (rawData == null) {
            return null;
        }
        String utf8 = fixIso(rawData);
        if (!rawData.equals(utf8)) {
            log.warn("    Converted " + rawData + " to " + utf8 + " please specify the charset encoding in your jdbc url");

        }
        return utf8;
    }
}
