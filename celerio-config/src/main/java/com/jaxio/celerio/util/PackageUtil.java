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

package com.jaxio.celerio.util;

import org.apache.commons.lang.Validate;

import javax.lang.model.SourceVersion;

import static org.apache.commons.lang.StringUtils.stripToNull;

public class PackageUtil {

    static public String assemblePackage(String... packageChunks) {
        StringBuilder sb = new StringBuilder();

        for (String packageChunk : packageChunks) {
            if (stripToNull(packageChunk) != null) {
                if (sb.length() > 0) {
                    sb.append(".");
                }
                sb.append(packageChunk.trim());
            }
        }

        Validate.isTrue(sb.length() > 0, "An assembled package is null or empty!");
        return sb.toString();
    }

    public static boolean isPackageNameValid(String packageName) {
        if (!SourceVersion.isName(packageName)) {
            return false;
        }

        if (packageName.startsWith("java")) {
            return false;
        }

        return true;
    }
}