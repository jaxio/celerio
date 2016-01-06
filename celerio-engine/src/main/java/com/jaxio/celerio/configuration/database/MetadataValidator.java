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

import java.util.ArrayList;
import java.util.List;

public class MetadataValidator {

    private List<String> errorMessages = new ArrayList<String>();

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public boolean hasErrorMessages() {
        return errorMessages.size() > 0;
    }

    public void validate(Metadata metadata) {
        checkForJdbcType(metadata);
        checkForPkPresence(metadata);
    }

    public void checkForJdbcType(Metadata metadata) {
        for (Table t : metadata.getTables()) {
            for (Column c : t.getColumns()) {
                if (c.getType() == null) {
                    errorMessages.add("The column " + t.getName() + "." + c.getName() + " " + " jdbc type could not be resolved!");
                }
            }
        }
    }

    public void checkForPkPresence(Metadata metadata) {
        for (Table t : metadata.getTables()) {
            if (!t.hasPk()) {
                errorMessages.add("The table " + t.getName() + " does not have a Primary Key. This table is going to be ignored, you should declare a pk.");
            }
        }
    }
}
