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

package com.jaxio.celerio.configuration.database.support;

import com.jaxio.celerio.configuration.database.Column;
import com.jaxio.celerio.configuration.database.Metadata;
import com.jaxio.celerio.configuration.database.Table;
import com.jaxio.celerio.configuration.support.MetadataLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.oxm.XmlMappingException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import static org.fest.assertions.Assertions.assertThat;

@ContextConfiguration("classpath:applicationContext-celerio.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class MetaDataExtractorTest {

    private static String MINIMAL_SCRIPT = "classpath:/minimal.sql";
    private static String ALL_RELATIONS_SCRIPT = "classpath:/all-relations.sql";

    @Autowired
    private MetadataLoader loader;

    @Autowired
    private MetadataExtractor extractor;

    @Test
    public void noTable() throws ClassNotFoundException, SQLException, JAXBException, IOException {
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
        EmbeddedDatabase embeddedDatabase = builder.setType(EmbeddedDatabaseType.H2).build();

        Metadata meta = extractor.extract(embeddedDatabase.getConnection());

        assertThat(countTables(meta)).isZero();
        assertThat(countColumns(meta)).isZero();
        assertThat(countPrimaryKeys(meta)).isZero();
        assertThat(countImportedKeys(meta)).isZero();
        assertThat(countIndexes(meta)).isZero();
        assertThat(countEnums(meta)).isZero();

        embeddedDatabase.shutdown();
    }

    @Test
    public void minimal() throws ClassNotFoundException, SQLException, JAXBException, IOException {
        EmbeddedDatabase embeddedDatabase = createMinimalEmbeddedDatabase(MINIMAL_SCRIPT);
        Metadata meta = extractor.extract(embeddedDatabase.getConnection());

        assertThat(countTables(meta)).isEqualTo(6);
        assertThat(countColumns(meta)).isEqualTo(29);
        assertThat(countPrimaryKeys(meta)).isEqualTo(9);
        assertThat(countImportedKeys(meta)).isEqualTo(4);
        assertThat(countIndexes(meta)).isEqualTo(13);
        assertThat(countEnums(meta)).isZero();

        embeddedDatabase.shutdown();
    }

    @Test
    public void outputIsSameAsInput() throws ClassNotFoundException, SQLException, XmlMappingException, IOException {
        EmbeddedDatabase embeddedDatabase = createMinimalEmbeddedDatabase(ALL_RELATIONS_SCRIPT);

        Metadata meta = extractor.extract(embeddedDatabase.getConnection());


        assertThat(countTables(meta)).isEqualTo(18);
        assertThat(countColumns(meta)).isEqualTo(49);
        assertThat(countPrimaryKeys(meta)).isEqualTo(18);
        assertThat(countIndexes(meta)).isEqualTo(21);
        assertThat(countEnums(meta)).isEqualTo(3);

        File tempFile = File.createTempFile(getClass().getName(), ".xml");
        tempFile.deleteOnExit();
        loader.write(meta, tempFile);

        Metadata loadedMeta = loader.load(tempFile);
        assertThat(countTables(meta)).isEqualTo(countTables(loadedMeta));
        assertThat(countColumns(meta)).isEqualTo(countColumns(loadedMeta));
        assertThat(countPrimaryKeys(meta)).isEqualTo(countPrimaryKeys(loadedMeta));
        assertThat(countIndexes(meta)).isEqualTo(countIndexes(loadedMeta));
        assertThat(countEnums(meta)).isEqualTo(countEnums(loadedMeta));

        embeddedDatabase.shutdown();
    }

    private int countImportedKeys(Metadata meta) {
        int count = 0;
        for (Table table : meta.getTables()) {
            count += table.getImportedKeys().size();
        }
        return count;
    }

    private int countTables(Metadata meta) {
        return meta.getTables().size();
    }

    private int countIndexes(Metadata meta) {
        int count = 0;
        for (Table table : meta.getTables()) {
            count += table.getIndexes().size();
        }
        return count;
    }

    private int countColumns(Metadata meta) {
        int count = 0;
        for (Table table : meta.getTables()) {
            count += table.getColumns().size();
        }

        return count;
    }

    private int countEnums(Metadata meta) {
        int count = 0;
        for (Table table : meta.getTables()) {
            for (Column column : table.getColumns()) {
                if (column.hasEnum()) {
                    count += column.getEnumValues().size();
                }
            }
        }

        return count;
    }

    private int countPrimaryKeys(Metadata meta) {
        int count = 0;
        for (Table table : meta.getTables()) {
            count += table.getPrimaryKeys().size();
        }

        return count;
    }

    private EmbeddedDatabase createMinimalEmbeddedDatabase(String sqlScript) {
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
        return builder.setType(EmbeddedDatabaseType.H2).addScript(sqlScript).build();
    }
}
