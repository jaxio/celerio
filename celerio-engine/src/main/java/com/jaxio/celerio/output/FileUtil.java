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

package com.jaxio.celerio.output;

import java.io.File;

public class FileUtil {

    public static String getPathRelativeToBase(File targetFile, String basePath) {
        String absoluteBasePath = new File(basePath).getAbsolutePath();
        String absolutePath = targetFile.getAbsolutePath();
        if (absolutePath.startsWith(absoluteBasePath)) {
            String result = absolutePath.substring(absoluteBasePath.length());
            if (!result.isEmpty() && result.charAt(0) == File.separatorChar) {
                result = result.substring(1);
            }
            return result;
        }
        throw new IllegalStateException("File is not under absoluteBasePath path!: " + absolutePath + " absoluteBasePath: " + absoluteBasePath);
    }
}
