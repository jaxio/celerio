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

/**
 * Information about the database where celerio extracted the metadata
 */
public class DatabaseInfo {
    @Setter
    private int databaseMajorVersion;
    @Setter
    private int databaseMinorVersion;
    @Setter
    private String databaseProductName = "";
    @Setter
    private String databaseProductVersion = "";
    @Setter
    private int driverMajorVersion;
    @Setter
    private int driverMinorVersion;
    @Setter
    private String driverName = "";
    @Setter
    private String driverVersion = "";
    @Setter
    private String extraInfo = "";

    public int getDatabaseMajorVersion() {
        return databaseMajorVersion;
    }

    public int getDatabaseMinorVersion() {
        return databaseMinorVersion;
    }

    public String getDatabaseProductName() {
        return databaseProductName;
    }

    public String getDatabaseProductVersion() {
        return databaseProductVersion;
    }

    public int getDriverMajorVersion() {
        return driverMajorVersion;
    }

    public int getDriverMinorVersion() {
        return driverMinorVersion;
    }

    public String getDriverName() {
        return driverName;
    }

    public String getDriverVersion() {
        return driverVersion;
    }

    public String getExtraInfo() {
        return extraInfo;
    }
}
