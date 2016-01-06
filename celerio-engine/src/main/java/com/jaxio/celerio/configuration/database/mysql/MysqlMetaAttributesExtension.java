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

import com.jaxio.celerio.configuration.MetaAttribute;
import com.jaxio.celerio.configuration.database.Column;
import com.jaxio.celerio.configuration.database.Metadata;
import com.jaxio.celerio.configuration.database.Table;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.StringUtils.trim;

@Service
public class MysqlMetaAttributesExtension extends MysqlExtension {

    @Override
    public void apply(Connection connection, Metadata metadata) {
        JdbcTemplate jdbcTemplate = getJdbcTemplate(connection);
        for (Table table : metadata.getTables()) {
            processYearTypes(jdbcTemplate, metadata, table);
        }
    }

    private void processYearTypes(JdbcTemplate jdbcTemplate, final Metadata metadata, final Table table) {
        jdbcTemplate.query("show columns from " + table.getName(), new RowMapper<Void>() {
            @Override
            public Void mapRow(ResultSet rs, int rowNum) throws SQLException {
                Column column = table.getColumnByName(rs.getString("Field"));
                column.addMetaAttribute(new MetaAttribute("type", rs.getString("Type")));
                String extra = rs.getString("Extra");
                if (isNotBlank(extra)) {
                    column.addMetaAttribute(new MetaAttribute("extra", trim(extra)));
                }
                return null;
            }
        });
    }
}
