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

package com.jaxio.celerio.model.support;

import com.jaxio.celerio.model.Attribute;
import lombok.Data;

@Data
public class AuditLogAttribute {
    private Attribute author;
    private Attribute event;
    private Attribute eventDate;
    private Attribute stringAttribute1;
    private Attribute stringAttribute2;
    private Attribute stringAttribute3;

    public boolean isAuthorSet() {
        return author != null;
    }

    public boolean isEventSet() {
        return event != null;
    }

    public boolean isEventDateSet() {
        return eventDate != null;
    }

    public boolean isStringAttribute1Set() {
        return stringAttribute1 != null;
    }

    public boolean isStringAttribute2Set() {
        return stringAttribute2 != null;
    }

    public boolean isStringAttribute3Set() {
        return stringAttribute3 != null;
    }

    public boolean isComplete() {
        return isAuthorSet() && isEventSet() && isEventDateSet() && isStringAttribute1Set() && isStringAttribute2Set() && isStringAttribute3Set();
    }
}