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

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class ImportsHolderTest {

    @Test
    public void empty() {
        ImportsHolder holder = new ImportsHolder("");
        assertThat(holder.toJavaImportString()).isEmpty();
    }

    @Test
    public void staticImports() {
        ImportsHolder oneImport = new ImportsHolder("");
        oneImport.add("static org.A");
        assertThat(oneImport.toJavaImportString()).isEqualTo("import static org.A;\n");

        ImportsHolder twoImports = new ImportsHolder("");
        twoImports.add("static org.A");
        twoImports.add("static fr.B");
        assertThat(twoImports.toJavaImportString()).isEqualTo("" //
                + "import static fr.B;\n" //
                + "import static org.A;\n");
    }

    @Test
    public void wellKnownDomainSortedAndBeforeCustomDomain() {
        ImportsHolder holder = new ImportsHolder("");
        holder.add("java.A");
        holder.add("java.B");
        holder.add("javax.C");
        holder.add("org.D");
        holder.add("fr.C");

        assertThat(holder.toJavaImportString()).isEqualTo("" //
                + "import java.A;\n" //
                + "import java.B;\n" //
                + "\n" //
                + "import javax.C;\n" //
                + "\n" //
                + "import org.D;\n" //
                + "\n" //
                + "import fr.C;\n");
    }

    @Test
    public void complete() {
        ImportsHolder holder = new ImportsHolder("");
        holder.add("java.A");
        holder.add("java.B");
        holder.add("javax.C");
        holder.add("org.D");
        holder.add("fr.C");
        holder.add("static fr.A");
        holder.add("static org.B");

        assertThat(holder.toJavaImportString()).isEqualTo("" //
                + "import static fr.A;\n" //
                + "import static org.B;\n" //
                + "\n" //
                + "import java.A;\n" //
                + "import java.B;\n" //
                + "\n" //
                + "import javax.C;\n" //
                + "\n" //
                + "import org.D;\n" //
                + "\n" //
                + "import fr.C;\n");
    }

    @Test
    public void doNotImportClassInSamePackage() {
        ImportsHolder samePackage = new ImportsHolder("com.jaxio");
        samePackage.add("com.jaxio.DoNotImport");
        assertThat(samePackage.toJavaImportString()).isEmpty();

        ImportsHolder mixPackage = new ImportsHolder("com.jaxio");
        mixPackage.add("com.jaxio.stuff.Import");
        mixPackage.add("com.jaxio.DoNotImport");
        assertThat(mixPackage.toJavaImportString()).isEqualTo("import com.jaxio.stuff.Import;\n");
    }

    @Test
    public void doNotImportJavaLangTypes() {
        ImportsHolder holder = new ImportsHolder("com.jaxio");
        holder.add("java.lang.Integer");
        assertThat(holder.toJavaImportString()).isEmpty();
    }
}
