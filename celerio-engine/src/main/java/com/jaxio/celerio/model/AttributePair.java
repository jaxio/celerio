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

package com.jaxio.celerio.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Introduced to support relation involving composite fk/pk.
 * A pair represent 1 link between the FK (fromAttribute) and the PK (toAttribute).
 */
@Data
@AllArgsConstructor
public class AttributePair {
    private Attribute fromAttribute;
    private Attribute toAttribute;
}
