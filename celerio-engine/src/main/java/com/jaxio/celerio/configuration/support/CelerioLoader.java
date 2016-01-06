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

package com.jaxio.celerio.configuration.support;

import com.jaxio.celerio.configuration.Celerio;
import com.jaxio.celerio.configuration.Include;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.oxm.XmlMappingException;
import org.springframework.oxm.jibx.JibxMarshaller;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@Service
@Slf4j
public class CelerioLoader extends AbstractJibxLoader<Celerio> {

    @Autowired
    @Qualifier("celerioMarshaller")
    private JibxMarshaller marshaller;

    public JibxMarshaller getMarshaller() {
        return marshaller;
    }

    public Celerio load(String filename) throws XmlMappingException, IOException {
        return load(new File(filename));

    }

    public Celerio load(File file) throws XmlMappingException, IOException {
        Celerio celerio = load(new FileInputStream(file));
        loadIncludes(file.getParentFile(), celerio);
        return celerio;
    }

    private void loadIncludes(File parent, Celerio primaryCelerio) {
        if (primaryCelerio.getIncludes() == null) {
            return;
        }

        for (Include include : primaryCelerio.getIncludes()) {
            File secondaryFile = new File(parent, include.getFilename());

            if (!secondaryFile.exists()) {
                throw new IllegalStateException("The file " + secondaryFile.getAbsolutePath()
                        + " included in the main Celerio configuration could not be found!");
            }

            try {
                log.info("Loading secondary configuration file: " + secondaryFile.getName());
                Celerio secondaryCelerio = load(secondaryFile);
                // we add only the entity configs
                primaryCelerio.addSecondaryEntityConfigs(secondaryCelerio.getEntityConfigs());
            } catch (FileNotFoundException e) {
                log.error("The file " + secondaryFile.getAbsolutePath() + " included in the main celerio configuration file does not exist");
                continue;
            } catch (IOException e) {
                log.error("io exception when loading" + secondaryFile.getAbsolutePath() + " included in the main celerio configuration file.");
                throw new RuntimeException("error", e);
            }
        }
    }
}
