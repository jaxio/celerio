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

package com.jaxio.celerio.main;

import com.jaxio.celerio.Config;
import com.jaxio.celerio.configuration.Celerio;
import com.jaxio.celerio.configuration.database.Metadata;
import com.jaxio.celerio.configuration.support.CelerioLoader;
import com.jaxio.celerio.configuration.support.MetadataLoader;
import com.jaxio.celerio.factory.ProjectFactory;
import com.jaxio.celerio.model.Entity;
import com.jaxio.celerio.output.OutputResultFactory;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.fest.assertions.Assertions.assertThat;

@ContextConfiguration("classpath:applicationContext-celerio.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Ignore
public class NoPkOnlyIndexesTest {

    @Autowired
    private CelerioLoader celerioLoader;

    @Autowired
    private MetadataLoader loader;

    @Autowired
    private OutputResultFactory outputResultFactory;

    @Autowired
    private ProjectFactory projectFactory;

    @Autowired
    private Config config;

    @Test
    public void basic() throws Exception {
        config.setOutputResult(outputResultFactory.getOutputResult(".", "target/generated-output/no-pk"));

        String folder = "src/test/resources/no-pk-only-indexes";
        Celerio celerio = celerioLoader.load(folder + "/celerio-maven-plugin.xml");
        Metadata metadata = loader.load(folder + "/metadata-ades.xml");
        config.setCelerio(celerio);
        config.setMetadata(metadata);
        config.getOutputResult().open();
        projectFactory.init();
        Entity entity = config.getProject().getEntityByName("CIPAO02B");
        assertThat(entity).isNotNull();
        assertThat(entity.getPrimaryKey().isComposite()).isTrue();
    }
}
