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

import com.jaxio.celerio.configuration.database.IndexHolder;
import com.jaxio.celerio.configuration.database.Metadata;
import com.jaxio.celerio.configuration.database.Table;
import com.jaxio.celerio.configuration.support.MetadataLoader;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.XmlMappingException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

@Ignore
@ContextConfiguration("classpath:applicationContext-celerio.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class CelerioLoaderTest {

    @Autowired
    private MetadataLoader loader;

    // TODO replace ADES test
    @Ignore
    public void loader() throws XmlMappingException, IOException {
        Metadata metadata = loader.load("src/test/resources/metadata-ades.xml");
        assertThat(metadata).isNotNull();
        Table table = metadata.getTableByName("CIPTS17B");
        assertThat(table).isNotNull();
        assertThat(table.getPrimaryKeys()).isEmpty();
        List<IndexHolder> uniqueIndexes = table.getUniqueIndexHolders();
        assertThat(uniqueIndexes).isNotEmpty();
        assertThat(uniqueIndexes.size()).isEqualTo(1);
        IndexHolder indexHolder = uniqueIndexes.get(0);
        assertThat(indexHolder).isNotNull();
        assertThat(indexHolder.isComposite()).isTrue();
    }
}
