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

import com.jaxio.celerio.configuration.database.Metadata;
import com.jaxio.celerio.configuration.database.support.Extension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import javax.sql.DataSource;
import java.sql.Connection;

public abstract class MysqlExtension implements Extension {
    private static final String MYSQL_DATABASE = "mysql";

    @Override
    public boolean applyable(Metadata metadata) {
        return MYSQL_DATABASE.equalsIgnoreCase(metadata.getDatabaseInfo().getDatabaseProductName());
    }

    protected JdbcTemplate getJdbcTemplate(Connection connection) {
        DataSource dataSource = new SingleConnectionDataSource(connection, true);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate;
    }

}
