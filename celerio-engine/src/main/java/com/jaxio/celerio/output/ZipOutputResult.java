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

import com.jaxio.celerio.template.pack.Template;
import com.jaxio.celerio.template.pack.TemplatePack;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.Date;
import java.util.SortedSet;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.google.common.collect.Sets.newTreeSet;
import static org.apache.commons.io.FilenameUtils.normalize;

@Slf4j
public class ZipOutputResult implements OutputResult {

    private String filename;

    private SortedSet<String> fileList = newTreeSet();

    private BufferedOutputStream bufferedOutputStream;

    private ZipOutputStream zipOutputStream;
    private boolean isOpen = false;
    private SourceFile userSource = null;
    private SourceFile generatedSource = null;

    public ZipOutputResult(String filename) {
        this.filename = filename;
        this.userSource = new ZipSourceFile();
        this.generatedSource = new ZipSourceFile();
    }

    @Override
    public void open() throws IOException {
        if (isOpen) {
            return;
        }
        bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(new File(filename)));
        zipOutputStream = new JarOutputStream(bufferedOutputStream);
        isOpen = true;
    }

    @Override
    public boolean hasCollision(String pathToFile) {
        return false;
    }

    @Override
    public String getCollisionName(String targetFilename) {
        throw new IllegalStateException("It should not be invoked");
    }

    @Override
    public void close() throws IOException {
        zipOutputStream.close();
        bufferedOutputStream.close();
        isOpen = false;
    }

    @Override
    public void addContent(byte[] contentBytes, String entryName, TemplatePack pack, Template template) throws IOException {
        open();
        if (fileList.contains(entryName)) {
            log.error("Ignore duplicate entry: " + entryName);
            return;
        } else {
            fileList.add(entryName);
        }

        // Add archive entry
        ZipEntry zipEntry = new ZipEntry(normalize(entryName));
        zipOutputStream.putNextEntry(zipEntry);
        zipOutputStream.write(contentBytes, 0, contentBytes.length);
    }

    @Override
    public void addContent(InputStream contentStream, String entryName, TemplatePack templatePack, Template template) throws IOException {
        open();

        if (fileList.contains(entryName)) {
            log.error("Ignore duplicate entry: " + entryName);
            return;
        } else {
            fileList.add(entryName);
        }

        // Add archive entry
        ZipEntry zipEntry = new JarEntry(normalize(entryName));
        zipEntry.setTime((new Date()).getTime());
        zipOutputStream.putNextEntry(zipEntry);

        IOUtils.copy(contentStream, zipOutputStream);
    }

    @Override
    public void addCollisionContent(byte[] contentBytes, String entryName, TemplatePack pack, Template template) throws IOException {
        throw new IllegalStateException("It should not be invoked");
    }

    @Override
    public SourceFile getUserSource() {
        return this.userSource;
    }

    @Override
    public SourceFile getGeneratedSource() {
        return this.generatedSource;
    }

    @Override
    public boolean sameDirectory() {
        return userSource.getDirectory().equals(generatedSource.getDirectory());
    }
}
