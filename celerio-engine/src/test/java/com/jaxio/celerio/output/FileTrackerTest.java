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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static com.google.common.collect.Maps.newHashMap;
import static org.fest.assertions.Assertions.assertThat;

@ContextConfiguration("classpath*:applicationContext-celerio.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class FileTrackerTest {

    @Autowired
    FileTracker fileTracker;
    @Autowired
    IOUtil ioUtil;

    @Test
    public void deleteGeneratedFiles() throws IOException {
        File projectDir = new File("target");

        HashMap<String, FileMetaData> generatedFiles = newHashMap();

        File f1 = createFileWithContent(projectDir, "f1.txt", "my content");
        File f2 = createFileWithContent(projectDir, "f2.txt", "my content2");

        assertThat(f1.exists()).isTrue();
        ;
        assertThat(f2.exists()).isTrue();

        generatedFiles.put("f1.txt", new FileMetaData(null, null, "f1.txt", f1));
        generatedFiles.put("f2.txt", new FileMetaData(null, null, "f2.txt", f2));
        fileTracker.saveToProjectDir(generatedFiles, projectDir);

        HashMap<String, FileMetaData> loadedGeneratedFiles = fileTracker.loadFromProjectDir(projectDir);
        assertThat(loadedGeneratedFiles.values()).contains(new FileMetaData(null, null, "f1.txt", f1));
        assertThat(loadedGeneratedFiles.values()).contains(new FileMetaData(null, null, "f2.txt", f2));

        fileTracker.deleteGeneratedFileIfIdentical(projectDir, new ArrayList<String>());
        HashMap<String, FileMetaData> loadedGeneratedFilesAfterDelete = fileTracker.loadFromProjectDir(projectDir);
        assertThat(loadedGeneratedFilesAfterDelete.values()).isEmpty();
    }

    @Test
    public void leaveGeneratedFilesUntouched() throws IOException {
        File projectDir = new File("target");

        HashMap<String, FileMetaData> generatedFiles = newHashMap();

        // simulate a generation round
        File f1 = createFileWithContent(projectDir, "leaveGeneratedFilesUntouched-f1.txt", "my content");
        File f2 = createFileWithContent(projectDir, "leaveGeneratedFilesUntouched-f2.txt", "my content2");
        generatedFiles.put("leaveGeneratedFilesUntouched-f1.txt", new FileMetaData(null, null, "leaveGeneratedFilesUntouched-f1.txt", f1));
        generatedFiles.put("leaveGeneratedFilesUntouched-f2.txt", new FileMetaData(null, null, "leaveGeneratedFilesUntouched-f2.txt", f2));
        assertThat(generatedFiles).hasSize(2);
        fileTracker.saveToProjectDir(generatedFiles, projectDir);

        // simulate user changing 1 file content
        f2 = createFileWithContent(projectDir, "leaveGeneratedFilesUntouched-f2.txt", "my content2 has changed");

        // make sure we can detect the file change
        HashMap<String, FileMetaData> loadedGeneratedFiles = fileTracker.loadFromProjectDir(projectDir);
        assertThat(loadedGeneratedFiles.values()).contains(new FileMetaData(null, null, "leaveGeneratedFilesUntouched-f1.txt", f1));
        assertThat(loadedGeneratedFiles.values()).excludes(new FileMetaData(null, null, "leaveGeneratedFilesUntouched-f2.txt", f2));

        FileMetaData willBeDeleted = new FileMetaData(null, null, "leaveGeneratedFilesUntouched-f1.txt", f1);
        HashSet<FileMetaData> deletedFiles = fileTracker.deleteGeneratedFileIfIdentical(projectDir, new ArrayList<String>());
        assertThat(deletedFiles).hasSize(1);
        assertThat(deletedFiles).contains(willBeDeleted);
        assertThat(deletedFiles).excludes(new FileMetaData(null, null, "leaveGeneratedFilesUntouched-f2.txt", f2));
    }

    private File createFileWithContent(File projectDir, String filePath, String content) throws IOException {
        File file = new File(projectDir, filePath);
        File parent = file.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
        ioUtil.stringToFile(content, file);
        return file;
    }
}
