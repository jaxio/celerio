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

import com.jaxio.celerio.convention.WellKnownFolder;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FolderOutputResultTest {

    @Test
    public void isImportantFile() {
        FolderOutputResult pe = new FolderOutputResult(new ZipSourceFile(), new ZipSourceFile());
        assertTrue(pe.isImportantFile("path/pom.xml"));
        assertTrue(pe.isImportantFile("path/celerio-maven-plugin.xml"));
        assertTrue(pe.isImportantFile("path/01-create.sql"));
    }

    @Test
    public void isInJavaUserOnlyFolder() {
        FolderOutputResult pe = new FolderOutputResult(new ZipSourceFile(), new ZipSourceFile());
        // main
        assertTrue(pe.isInJavaUserOnlyFolder("src/main/java/Toto"));
        assertTrue(pe.isInJavaUserOnlyFolder("src\\main\\java\\Toto"));
        assertTrue(pe.isInJavaUserOnlyFolder("src\\main/java/toto"));

        assertFalse(pe.isInJavaUserOnlyFolder("src/main/generated-java/Toto"));
        assertFalse(pe.isInJavaUserOnlyFolder("src\\main\\generated-java\\Toto"));
        assertFalse(pe.isInJavaUserOnlyFolder("src\\main/generated-java/toto"));

        // test
        assertTrue(pe.isInJavaUserOnlyFolder("src/test/java/Toto"));
        assertTrue(pe.isInJavaUserOnlyFolder("src\\test\\java\\Toto"));
        assertTrue(pe.isInJavaUserOnlyFolder("src\\test/java/toto"));

        assertFalse(pe.isInJavaUserOnlyFolder("src/test/generated-java/Toto"));
        assertFalse(pe.isInJavaUserOnlyFolder("src\\test\\generated-java\\Toto"));
        assertFalse(pe.isInJavaUserOnlyFolder("src\\test/generated-java/toto"));
    }

    @Test
    public void isInJavaUserOnlyFolderWithJavaSameAsGeneratedJava() {
        FolderOutputResult pe = new FolderOutputResult(new ZipSourceFile(), new ZipSourceFile());
        String oldJavaGenerated = WellKnownFolder.JAVA.getGeneratedFolder();
        String oldJavaTestGenerated = WellKnownFolder.JAVA_TEST.getGeneratedFolder();

        WellKnownFolder.JAVA.setGeneratedFolder("src/main/java");
        WellKnownFolder.JAVA_TEST.setGeneratedFolder("src/test/java");

        // main
        assertTrue(pe.isInJavaUserOnlyFolder("src/main/java/Toto"));
        assertTrue(pe.isInJavaUserOnlyFolder("src\\main\\java\\Toto"));
        assertTrue(pe.isInJavaUserOnlyFolder("src\\main/java/toto"));

        // test
        assertTrue(pe.isInJavaUserOnlyFolder("src/test/java/Toto"));
        assertTrue(pe.isInJavaUserOnlyFolder("src\\test\\java\\Toto"));
        assertTrue(pe.isInJavaUserOnlyFolder("src\\test/java/toto"));

        // Having to do this is insane... very dangerous to be able to change enums!
        WellKnownFolder.JAVA.setGeneratedFolder(oldJavaGenerated);
        WellKnownFolder.JAVA_TEST.setGeneratedFolder(oldJavaTestGenerated);
    }
}
