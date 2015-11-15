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

package com.jaxio.celerio.maven.plugin.bootstrap;

import lombok.Getter;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Holds the information found in sqlconf info file.
 */
@Getter
public class SqlConfInfo implements Comparable<SqlConfInfo> {

    private String name;
    private String description;
    private String description2;

    public SqlConfInfo(Resource sqlConfInfoAsResource) throws IOException {
        Properties sqlConfInfoAsProperties = new Properties();
        sqlConfInfoAsProperties.load(new InputStreamReader(sqlConfInfoAsResource.getInputStream(), "UTF-8"));
        this.name = sqlConfInfoAsProperties.getProperty("name");
        this.description = sqlConfInfoAsProperties.getProperty("description");
        this.description2 = sqlConfInfoAsProperties.getProperty("description2");
    }

    @Override
    public int compareTo(SqlConfInfo o) {
        return name.compareTo(o.getName());
    }
}
