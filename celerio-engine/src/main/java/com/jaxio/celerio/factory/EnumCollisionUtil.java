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

package com.jaxio.celerio.factory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

/**
 * Accumulate enum names. If one name is taken, append a counter value...
 */
@Service
@Slf4j
public class EnumCollisionUtil {

    private Set<String> enumNames = newHashSet();

    private void addName(String enumName) {
        enumNames.add(clashKey(enumName));
    }

    public String getClashSafeName(String enumName) {
        if (alreadyUsed(enumName)) {
            // already used...
            int i = 2;
            while (true) {
                String candidate = enumName + (i++);
                if (!alreadyUsed(candidate)) {
                    log.warn("Var name clash resolution for " + enumName + " => " + candidate
                            + ". You should define your enum globally using sharedEnumConfigs");
                    enumName = candidate;
                    break;
                }
            }
        }
        addName(enumName);
        return enumName;
    }

    private boolean alreadyUsed(String enumName) {
        return enumNames.contains(clashKey(enumName));
    }

    private String clashKey(String enumName) {
        return enumName.toUpperCase();
    }
}
