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

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import static org.fest.assertions.Assertions.assertThat;

public class MySqlEnumExtractorTest {
    private JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(new SingleConnectionDataSource("jdbc:mysql://localhost/sakila", "root", "root", true));
    }

    @Test
    @Ignore
    public void film() {
        SqlRowSet queryForRowSet = jdbcTemplate().queryForRowSet("show columns from film where type like 'enum%'");
        while (queryForRowSet.next()) {
            String field = queryForRowSet.getString("Field");
            String type = queryForRowSet.getString("Type");
            MysqlEnumValuesExtractor mysqlEnumValuesExtractor = new MysqlEnumValuesExtractor();
            System.out.println("Extracting values from " + field + "." + type);
            for (String a : mysqlEnumValuesExtractor.extract(type)) {
                System.out.println(a);
            }
        }
    }

    @Test
    public void nothing() {
        assertThat(new MysqlEnumValuesExtractor().extract("not an enum")) //
                .isEmpty();
    }

    @Test
    public void strings() {
        assertThat(new MysqlEnumValuesExtractor().extract("enum('G', 'PG','PG-13', 'R', 'NC-17')"))//
                .hasSize(5) //
                .containsExactly("G", "PG", "PG-13", "R", "NC-17");//
        assertThat(new MysqlEnumValuesExtractor().extract("enum('a', 'b', 'c')")) //
                .hasSize(3) //
                .containsExactly("a", "b", "c");
        assertThat(new MysqlEnumValuesExtractor().extract("eNum('a', 'b', 'c')")) //
                .hasSize(3) //
                .containsExactly("a", "b", "c");
    }

    @Test
    public void numerics() {
        assertThat(new MysqlEnumValuesExtractor().extract("enum(1, 2, 3)")) //
                .hasSize(3) //
                .containsExactly("1", "2", "3");
    }
}
