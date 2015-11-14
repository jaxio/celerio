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

package com.jaxio.celerio.template.pack;

import com.jaxio.celerio.Config;
import com.jaxio.celerio.configuration.Pack;
import com.jaxio.celerio.convention.WellKnownFolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@Service
@Slf4j
public class PackLoader {
    @Autowired
    private Config config;

    @Autowired(required = false)
    private List<TemplatePack> localPacks = newArrayList();
    @Autowired
    private ClasspathResourceUncryptedPackLoader uncryptedPackLoader;

    public List<TemplatePack> getTemplatePacks() {
        // TODO: pack ordering
        List<TemplatePack> packs = newArrayList();
        addTemplatePacksDefinedInXML(packs);
        if (config.getCelerio().getConfiguration().getPacks().isEmpty()) {
            addLocalProject(packs);
            addPacksFoundInClassPath(packs);
        }

        return packs;
    }

    public boolean hasPack() {
        return !getTemplatePacks().isEmpty();
    }

    private void addPacksFoundInClassPath(List<TemplatePack> packs) {
        for (TemplatePack resourcePack : getAllTemplatePacksFromClasspath()) {
            if (!config.getCelerio().getConfiguration().isPackEnabled(resourcePack.getName())) {
                log.warn("The pack " + resourcePack.getName() + " has been disabled in config");
            } else {
                packs.add(resourcePack);
            }
        }
    }

    private void addTemplatePacksDefinedInXML(List<TemplatePack> packs) {
        List<Pack> packsInConfig = config.getCelerio().getConfiguration().getPacks();
        for (Pack packInConfig : packsInConfig) {
            if (!packInConfig.isEnable()) {
                log.warn("The pack " + packInConfig.getName() + " has been disabled");
                continue;
            }

            if (packInConfig.hasPath() && packInConfig.hasName()) {
                if (new File(config.getBaseDir() + File.separatorChar + packInConfig.getPath()).exists()) {
                    packs.add(new LocalResourcePackFile(packInConfig.getName(), config.getBaseDir() + File.separatorChar + packInConfig.getPath()));
                } else {
                    log.warn("The packPath " + packInConfig.getPath() + " for the pack " + packInConfig.getName() + " does not exist!");
                }
            } else if (!packInConfig.hasPath() && packInConfig.hasName()) {
                TemplatePack tp = null;
                try {
                    tp = getPackByName(packInConfig.getName());
                    packs.add(tp);
                } catch (TemplatePackNotFoundException tpnfe) {
                    // when working with multi maven projets with a single conf, the packs
                    // are filtered out by simply not providing them on the classpath.
                    // It is therefore ok to skip not found packs.
                    log.warn(tpnfe.getMessage());
                }
            } else {
                log.warn("Found an invalid pack declaration");
            }
        }
        packs.addAll(localPacks);
    }

    // ---------------------------------------------------
    // PACK LOADING (classpath)
    // ---------------------------------------------------

    private void addLocalProject(List<TemplatePack> packs) {
        String localCelerioTemplate = WellKnownFolder.CELERIO_LOCAL_TEMPLATE.getFolder();
        if (!new File(config.getBaseDir() + File.separatorChar + localCelerioTemplate).exists()) {
            return;
        }
        packs.add(new LocalResourcePackFile("celerioLocal", config.getBaseDir() + File.separatorChar + localCelerioTemplate));
    }

    private List<TemplatePack> getAllTemplatePacksFromClasspath() {
        List<TemplatePack> result = newArrayList();
        result.addAll(uncryptedPackLoader.getPacks());
        return result;
    }

    private TemplatePack getPackByName(String packName) {
        for (TemplatePack p : getAllTemplatePacksFromClasspath()) {
            if (p.getName().equals(packName)) {
                return p;
            }
        }
        throw new TemplatePackNotFoundException("Could not load the template pack " + packName);
    }
}
