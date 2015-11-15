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

import com.jaxio.celerio.Brand;
import com.jaxio.celerio.Config;
import com.jaxio.celerio.configuration.Celerio;
import com.jaxio.celerio.configuration.database.Metadata;
import com.jaxio.celerio.factory.ProjectFactory;
import com.jaxio.celerio.template.TemplateEngine;
import com.jaxio.celerio.template.pack.PackLoader;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Calendar;

@Service
@Data
@Slf4j
public class CelerioProducer {
    @Autowired
    private ProjectFactory projectFactory;
    @Autowired
    private Config config;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private PackLoader packLoader;
    @Value("${pom.version:unknown}")
    private String version;
    @Value("${release.info:unknown}")
    private String releaseInfo;

    /**
     * Run celerio in bootstrap mode. Only 'bootstrap' templates are evaluated.
     */
    public void bootstrap() throws Exception {
        produce(true);
    }

    public void produce() throws Exception {
        produce(false);
    }

    public void produce(boolean bootstrapOnly) throws Exception {
        Assert.notNull(config.getOutputResult(), "Output result is not defined");
        config.getOutputResult().open();
        projectFactory.init();
        templateEngine.produce(config.getProject(), bootstrapOnly);
        welcome();
        config.getOutputResult().close();
    }

    public void produce(String rootPackage, Metadata metadata) throws Exception {
        config.getCelerio().getConfiguration().setRootPackage(rootPackage);
        config.setMetadata(metadata);
        produce();
    }

    public void produce(Metadata metadata) throws Exception {
        config.setMetadata(metadata);
        produce();
    }

    public void produce(Celerio celerio, Metadata metadata) throws Exception {
        config.setCelerio(celerio);
        config.setMetadata(metadata);
        produce();
    }

    private void welcome() {
        // http://ascii.mastervb.net/
        // font : varsity.ftl
        Brand brand = new Brand();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        log.info("-----------------------------------------------------------------------");
        log.info("        ______         __                  _          ");
        log.info("      .' ___  |       [  |                (_)         ");
        log.info("     / .'   \\_| .---.  | | .---.  _ .--.  __   .--.   ");
        log.info("     | |       / /__\\\\ | |/ /__\\\\[ `/'`\\][  |/ .'`\\ \\ ");
        log.info("     \\ `.___.'\\| \\__., | || \\__., | |     | || \\__. | ");
        log.info("      `.____ .' '.__.'[___]'.__.'[___]   [___]'.__.'  v" + config.getCelerio().getConfiguration().getGeneration().getVersion());
        log.info("");
        log.info("       Branding file: " + brand.getBrandingPath());
        log.info("       Documentation: http://www.jaxio.com/documentation/celerio/");
        log.info("       (c) 2005-" + currentYear + " Jaxio, http://www.jaxio.com");
        if (version.contains("SNAPSHOT")) {
            log.info("       " + releaseInfo);
        }
        log.info("-----------------------------------------------------------------------");
    }
}
