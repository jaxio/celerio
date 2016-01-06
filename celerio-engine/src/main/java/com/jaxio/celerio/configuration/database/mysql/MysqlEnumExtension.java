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

package com.jaxio.celerio.configuration.database.mysql;

import com.jaxio.celerio.configuration.database.Column;
import com.jaxio.celerio.configuration.database.Metadata;
import com.jaxio.celerio.configuration.database.Table;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

@Service
public class MysqlEnumExtension extends MysqlExtension {

    private MysqlEnumValuesExtractor mysqlEnumValuesExtractor = new MysqlEnumValuesExtractor();

    @Override
    public void apply(Connection connection, Metadata metadata) {
        JdbcTemplate jdbcTemplate = getJdbcTemplate(connection);
        for (Table table : metadata.getTables()) {
            processEnums(jdbcTemplate, metadata, table);
        }
    }

    private void processEnums(JdbcTemplate jdbcTemplate, final Metadata metadata, final Table table) {
        jdbcTemplate.query("show columns from " + table.getName() + " where type like 'enum%'", new RowMapper<Void>() {
            @Override
            public Void mapRow(ResultSet rs, int rowNum) throws SQLException {
                processEnumType(metadata, table, rs.getString("Field"), rs.getString("Type"));
                return null;
            }
        });
    }

    private void processEnumType(Metadata metadata, Table table, String columnName, String constraint) {
        Column column = table.getColumnByName(columnName);
        for (String value : mysqlEnumValuesExtractor.extract(constraint)) {
            column.addEnumValue(value);
        }
    }
}
