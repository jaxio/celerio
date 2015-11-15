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

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A simple wrapper introduced to cope with DB2 on Z/OS. In this conf, apparently the column 'label' are not supported. To prevent regression, we continue to
 * use label for other systems.
 * <p>
 * Once we know it works without label (it should), we can remove this code and use col number directly.
 */
public abstract class ResultSetWrapper {
    private ResultSet rs;
    private boolean useLabel;

    public ResultSetWrapper(ResultSet rs, boolean useLabel) {
        this.rs = rs;
        this.useLabel = useLabel;
    }

    abstract protected int toInt(String label);

    final public String getString(String label) throws SQLException {
        if (useLabel) {
            return rs.getString(label);
        } else {
            return rs.getString(toInt(label));
        }
    }

    final public int getInt(String label) throws SQLException {
        if (useLabel) {
            return rs.getInt(label);
        } else {
            return rs.getInt(toInt(label));
        }
    }

    final public boolean getBoolean(String label) throws SQLException {
        if (useLabel) {
            return rs.getBoolean(label);
        } else {
            return rs.getBoolean(toInt(label));
        }
    }
}
