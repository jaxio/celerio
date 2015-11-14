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

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static com.google.common.collect.Maps.newConcurrentMap;
import static org.apache.commons.io.FilenameUtils.normalize;
import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.apache.commons.io.IOUtils.contentEquals;
import static org.apache.commons.lang.StringUtils.isBlank;

@Slf4j
public class FolderSourceFile implements SourceFile {
    private Map<String, Boolean> fileExistCache = newConcurrentMap();
    private String directory;

    public FolderSourceFile(String directory) {
        this.directory = normalize(directory);

        if (isBlank(directory)) {
            throw new IllegalStateException("No output directory is set");
        }
        createFolder(directory);
    }

    @Override
    public String getDirectory() {
        return directory;
    }

    @Override
    public String getFullPath(String pathToFile) {
        return directory + File.separatorChar + normalize(pathToFile);
    }

    @Override
    public boolean fileExists(String pathToFile) {
        Boolean exists = fileExistCache.get(pathToFile);
        if (exists != null) {
            return exists;
        }
        exists = new File(getFullPath(pathToFile)).exists();
        fileExistCache.put(pathToFile, exists);
        return exists;
    }

    @Override
    public byte[] getContent(String pathToFile) {
        try {
            return FileUtils.readFileToByteArray(new File(getFullPath(pathToFile)));
        } catch (IOException e) {
            log.warn("could not read content: " + pathToFile, e);
            return new byte[0];
        }
    }

    @Override
    public boolean isSameContent(String pathToExistingContent, byte[] contentToCompare) throws IOException {
        InputStream existingContent = null;
        boolean sameContent = false;
        try {
            existingContent = new ByteArrayInputStream(getContent(pathToExistingContent));
            sameContent = contentEquals(new ByteArrayInputStream(contentToCompare), existingContent);
        } finally {
            closeQuietly(existingContent);
        }

        return sameContent;
    }

    private void createFolder(String folder) {
        File dir = new File(folder);
        try {
            if (!dir.exists()) {
                dir.mkdirs();
            }
        } catch (Exception e) {
            // ignore
        }
        if (!dir.isDirectory() || !dir.canWrite()) {
            throw new RuntimeException("Cannot write to: " + folder);
        }
    }
}
