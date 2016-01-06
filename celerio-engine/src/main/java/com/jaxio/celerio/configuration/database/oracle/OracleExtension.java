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

package com.jaxio.celerio.configuration.database.oracle;

import com.jaxio.celerio.configuration.database.Metadata;
import com.jaxio.celerio.configuration.database.h2.H2Extension;
import com.jaxio.celerio.configuration.database.support.Extension;

import java.sql.Connection;

public class OracleExtension implements Extension {
    private static final String ORACLE_DATABASE = "oracle";

    /**
     * @see H2Extension
     */
    @Override
    public void apply(Connection connection, Metadata metadata) {

    }

    @Override
    public boolean applyable(Metadata metadata) {
        return ORACLE_DATABASE.equalsIgnoreCase(metadata.getDatabaseInfo().getDatabaseProductName());
    }
}
