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

import com.jaxio.celerio.util.IOUtil;
import com.thoughtworks.xstream.XStream;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static org.apache.commons.io.IOUtils.closeQuietly;

@Service
@Slf4j
public class FileTracker {
    @Setter
    private String generatedFileLocation = ".celerio";

    @Autowired
    private IOUtil ioUtil;

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    private String getFilename() {
        return generatedFileLocation + File.separatorChar + "generated.xml";
    }

    public SCMStatus getSCMStatus(File projectDir) {
        if (GITStatusCrawler.isProjectUnderGit(projectDir)) {
            return GITStatusCrawler.doStatus(projectDir);
        } else if (SVNStatusCrawler.isProjectUnderSvn(projectDir)) {
            return SVNStatusCrawler.doStatus(projectDir);
        } else {
            return new SCMStatus(null);
        }
    }

    @SuppressWarnings("unchecked")
    public HashMap<String, FileMetaData> loadFromProjectDir(File projectDir) throws IOException {
        File xmlFileMetaDatas = new File(projectDir, getFilename());

        if (!xmlFileMetaDatas.exists()) {
            info("File Tracker Metadata not found: " + xmlFileMetaDatas.getAbsolutePath());
            return newHashMap();
        }

        XStream xstream = new XStream();
        FileInputStream in = new FileInputStream(xmlFileMetaDatas);
        HashMap<String, FileMetaData> fileMetaDatas = (HashMap<String, FileMetaData>) xstream.fromXML(in);
        closeQuietly(in);
        return fileMetaDatas;
    }

    public void saveToProjectDir(HashMap<String, FileMetaData> fileMetaDatas, File projectDir) throws IOException {
        File xmlFileMetaDatas = new File(projectDir, getFilename());
        File parent = xmlFileMetaDatas.getParentFile();

        if (parent != null) {
            parent.mkdirs();
        } // else child was for example "."

        XStream xstream = new XStream();
        FileOutputStream out = new FileOutputStream(xmlFileMetaDatas);
        xstream.toXML(fileMetaDatas, out);
        closeQuietly(out);
    }

    public HashSet<FileMetaData> deleteGeneratedFileIfIdentical(File projectDir, List<String> excludedPatterns) throws IOException {
        SCMStatus scmStatus = getSCMStatus(projectDir);

        HashSet<FileMetaData> scmFiles = newHashSet();
        HashSet<FileMetaData> deletedFiles = newHashSet();
        HashSet<FileMetaData> notFoundFiles = newHashSet();

        File xmlFileMetaDatas = new File(projectDir, getFilename());

        if (!xmlFileMetaDatas.exists()) {
            info("CANNOT FIND FILE TRACKER METADATA " + xmlFileMetaDatas.getAbsolutePath());
            return deletedFiles;
        }

        HashMap<String, FileMetaData> filesToScan = loadFromProjectDir(projectDir);

        for (Entry<String, FileMetaData> entry : filesToScan.entrySet()) {
            FileMetaData oldFmd = entry.getValue();
            String relativePath = entry.getKey();
            File oldFile = new File(projectDir, relativePath);

            if (oldFile.exists()) {
                if (scmStatus.isUnderSCM(relativePath)) {
                    info("skip delete (file now under SCM): " + entry.getKey());
                    scmFiles.add(oldFmd);
                    continue;
                }

                if (oldFmd.isBootstrapFile()) {
                    info("skip file generated during bootstrap: " + entry.getKey());
                    continue;
                }

                FileMetaData recentFmd = new FileMetaData(null, null, entry.getKey(), oldFile);
                // info("recent: " +recentFmd.toString());
                // info("old: " + oldFmd.toString());

                if (oldFmd.equals(recentFmd)) {
                    if (mustExcludeFile(relativePath, excludedPatterns)) {
                        info("skip delete (present in excludedFiles): " + entry.getKey());
                    } else {
                        info("delete: " + entry.getKey());
                        oldFile.delete();
                        ioUtil.pruneEmptyDirs(oldFile);
                        deletedFiles.add(recentFmd);
                    }
                } else {
                    info("skip delete (potential user modifications): " + entry.getKey());
                }
            } else {
                info("skip delete (no longer exists): " + entry.getKey());
                notFoundFiles.add(oldFmd);
            }
        }

        for (FileMetaData fmd : scmFiles) {
            filesToScan.remove(fmd.getFileRelativePath());
        }
        for (FileMetaData fmd : deletedFiles) {
            filesToScan.remove(fmd.getFileRelativePath());
        }
        for (FileMetaData fmd : notFoundFiles) {
            filesToScan.remove(fmd.getFileRelativePath());
        }
        info("rewrite file tracker metadata: " + getFilename());
        saveToProjectDir(filesToScan, projectDir);
        return deletedFiles;
    }

    private void info(String msg) {
        log.info("[" + msg + "]");
    }

    private boolean mustExcludeFile(String path, List<String> excludedPatterns) {
        if (excludedPatterns == null || excludedPatterns.isEmpty()) {
            return false;
        }

        for (String pattern : excludedPatterns) {
            if (pattern != null && antPathMatcher.match(pattern, path)) {
                return true;
            }
        }

        return false;
    }
}
