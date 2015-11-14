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

@Getter
public enum WellKnownDatabase {
    H2("h2", "com.h2database", "h2", "1.3.172", "org.hibernate.dialect.H2Dialect", ";"), //
    Postgresql("postgresql", "org.postgresql", "postgresql", "8.2-504.jdbc3", "org.hibernate.dialect.PostgreSQLDialect", ";"), //
    Oracle("oracle", "com.oracle", "ojdbc14", "10.2.0.3", "org.hibernate.dialect.Oracle10gDialect", "/"), //
    Mysql("mysql", "mysql", "mysql-connector-java", "5.1.25", "org.hibernate.dialect.MySQLInnoDBDialect", ";"), //
    Hsql("hsqldb", "org.hsqldb", "hsqldb", "2.2.9", "org.hibernate.dialect.HSQLDialect", ";"), //
    derby("derby", "org.apache.derby", "derby", "10.10.1.1", "org.hibernate.dialect.DerbyDialect", "/");

    private final String type;
    private final String patternInJdbcUrl;
    private final String groupId;
    private final String artifactId;
    private final String version;
    private final String hibernateDialect;
    private final String sqlDelimiter;

    WellKnownDatabase(String type, String groupId, String artifactId, String version, String hibernateDialect, String sqlDelimiter) {
        this.type = type;
        this.patternInJdbcUrl = type;
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.hibernateDialect = hibernateDialect;
        this.sqlDelimiter = sqlDelimiter;
    }

    public boolean matchJdbcUrl(String jdbcUrl) {
        return jdbcUrl != null && jdbcUrl.contains(":" + getPatternInJdbcUrl());
    }

    public static WellKnownDatabase fromJdbcUrl(String jdbcUrl) {
        for (WellKnownDatabase db : values()) {
            if (db.matchJdbcUrl(jdbcUrl)) {
                return db;
            }
        }
        return null;
    }
}
