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

package com.jaxio.celerio.template;

import java.util.Map;

class ContextUtil {

    public static void addEnumValues(Map<String, Object> context, Enum<?>[] enumValues) {
        for (Enum<?> enumValue : enumValues) {
            String key = enumValue.name();
            if (context.containsKey(key)) {
                throwEnumConflictException(context, enumValue, key);
            }
            context.put(key, enumValue);
        }
    }

    @SuppressWarnings("rawtypes")
    public static void throwEnumConflictException(Map<String, Object> context, Enum enumValue, String key) {
        Object existingObject = context.get(key);
        if (existingObject instanceof Enum) {
            Enum existingEnum = (Enum) existingObject;
            throw new IllegalStateException("Ambiguity found in enums, " + enumValue.getClass().getName() + "." + enumValue.name() + " vs "
                    + existingEnum.getClass().getName() + "." + existingEnum);
        } else {
            throw new IllegalStateException("Collision between enum " + enumValue.getClass().getName() + "." + enumValue.name() + " and "
                    + existingObject.getClass());
        }
    }
}
