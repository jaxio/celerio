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

package com.jaxio.celerio.configuration.pack;

import com.jaxio.celerio.configuration.EntityContextProperty;
import com.jaxio.celerio.configuration.support.CelerioPackConfigLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.sql.SQLException;

import static org.fest.assertions.Assertions.assertThat;

@ContextConfiguration("classpath:applicationContext-celerio.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class CelerioPackConfigLoaderTest {

    private static String TEMPLATE_PACK_INFO = "classpath:/celerio-pack.xml";

    @Autowired
    ApplicationContext ctx;

    @Autowired
    private CelerioPackConfigLoader loader;

    @Test
    public void load() throws ClassNotFoundException, SQLException, JAXBException, IOException {

        CelerioPack celerioPack = loader.load(ctx.getResource(TEMPLATE_PACK_INFO).getFile());
        EntityContextProperty entityContextProperty = celerioPack.getCelerioTemplateContext().getEntityContextProperties().get(0);

        assertThat(celerioPack.getPackName().getValue()).isEqualTo("toto");
        assertThat(celerioPack.getPackDescription().getValue()).isEqualTo("toto description");
        assertThat(celerioPack.getPackDescription2().getValue()).isEqualTo("toto description 2");
        assertThat(celerioPack.getPackCommand().getValue()).isEqualTo("toto cmd");
        assertThat(celerioPack.getPackCommandHelp().getValue()).isEqualTo("toto help");
        assertThat(celerioPack.getProjectLink().getValue()).isEqualTo("toto link");

        assertThat(entityContextProperty.getProperty()).isEqualTo("rest");
    }
}
