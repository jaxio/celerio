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

package com.jaxio.celerio.configuration.eclipse;

import com.jaxio.celerio.configuration.support.EclipseProfilesLoader;
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
public class EclipseProfilesExtractorTest {

    private static String CODE_FORMATTER_ECLIPSE = "classpath:/code-formatter-eclipse.xml";

    @Autowired
    ApplicationContext ctx;

    @Autowired
    private EclipseProfilesLoader loader;

    @Test
    public void load() throws ClassNotFoundException, SQLException, JAXBException, IOException {
        // top level
        Profiles profiles = loader.load(ctx.getResource(CODE_FORMATTER_ECLIPSE).getFile());
        assertThat(profiles.getVersion()).isEqualTo("11");

        // first profile
        Profile profile = profiles.getProfiles().iterator().next();
        assertThat(profile.getKind()).isEqualTo("CodeFormatterProfile");
        assertThat(profile.getName()).isEqualTo("Jaxio");
        assertThat(profile.getVersion()).isEqualTo("11");

        // first 2 settings
        Setting setting = profile.getSettings().get(0);
        assertThat(setting.getId()).isEqualTo("org.eclipse.jdt.core.formatter.comment.insert_new_line_before_root_tags");
        assertThat(setting.getValue()).isEqualTo("insert");

        setting = profile.getSettings().get(1);
        assertThat(setting.getId()).isEqualTo("org.eclipse.jdt.core.formatter.insert_space_after_comma_in_annotation");
        assertThat(setting.getValue()).isEqualTo("insert");
    }
}
