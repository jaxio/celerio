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
import com.jaxio.celerio.configuration.pack.CelerioPack;
import com.jaxio.celerio.configuration.support.CelerioPackConfigLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.jaxio.celerio.configuration.Util.LOCAL_CELERIO_PACK;

@Service
@Slf4j
public class PackLoader {
    @Autowired
    private Config config;

    @Autowired
    private CelerioPackConfigLoader celerioPackConfigLoader;

    @Autowired
    private ClasspathResourceUncryptedPackLoader uncryptedPackLoader;

    public List<TemplatePack> getTemplatePacks() {
        // TODO: pack ordering
        List<TemplatePack> packs = newArrayList();
        addTemplatePacksDefinedInXML(packs);

        if (config.getCelerio().getConfiguration().getPacks().isEmpty()) {
//            addLocalProject(packs);
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

            // pack in local folder
            if (packInConfig.hasPath() && packInConfig.hasName()) {
                // the root is the folder that contains both "celerio" folder and "META-INF" folder
                File packRoot = new File(config.getBaseDir() + File.separatorChar + packInConfig.getPath());
                if (packRoot.exists()) {
                    try {
                        File celerioPackXml = new File(packRoot, LOCAL_CELERIO_PACK);
                        if (celerioPackXml.exists()) {
                            CelerioPack celerioPack = celerioPackConfigLoader.load(celerioPackXml);
                            TemplatePackInfo templatePackInfo = new TemplatePackInfo(celerioPack);
                            templatePackInfo.overrideProperties(packInConfig.getProperties());
                            packs.add(new LocalResourcePackFile(templatePackInfo, new File(packRoot, "celerio" + File.separatorChar + celerioPack.getPackName().getValue())));
                        } else {
                            log.error("Skipping pack " + packInConfig + " the file " + celerioPackXml.getAbsolutePath() + " is missing");
                        }
                    } catch (IOException ioe) {
                        log.error("Could not load the pack " + packInConfig, ioe);
                    }
                } else {
                    log.warn("The packPath " + packInConfig.getPath() + " for the pack " + packInConfig.getName() + " does not exist!");
                }
                continue;
            }

            // pack in jar on classpath
            if (!packInConfig.hasPath() && packInConfig.hasName()) {
                TemplatePack tp = null;
                try {
                    tp = getPackFromClassPath(packInConfig);
                    packs.add(tp);
                } catch (TemplatePackNotFoundException tpnfe) {
                    // when working with multi maven projets with a single conf, the packs
                    // are filtered out by simply not providing them on the classpath.
                    // It is therefore ok to skip not found packs.
                    log.warn(tpnfe.getMessage());
                }
                continue;
            }

            log.warn("Found an invalid pack declaration: " + packInConfig);
        }
    }

    // ---------------------------------------------------
    // PACK LOADING (classpath)
    // ---------------------------------------------------
//
//    private void addLocalProject(List<TemplatePack> packs) {
//        String localCelerioTemplate = WellKnownFolder.CELERIO_LOCAL_TEMPLATE.getFolder();
//        if (!new File(config.getBaseDir() + File.separatorChar + localCelerioTemplate).exists()) {
//            return;
//        }
//        try {
//
//            packs.add(new LocalResourcePackFile(new TemplatePackInfo("celerioLocal"), new File(config.getBaseDir() + File.separatorChar + localCelerioTemplate)));
//        } catch (IOException ioe) {
//            log.error("Could not load the default local pack", ioe);
//        }
//    }

    private List<TemplatePack> getAllTemplatePacksFromClasspath() {
        List<TemplatePack> result = newArrayList();
        result.addAll(uncryptedPackLoader.getPacks());
        return result;
    }

    private TemplatePack getPackFromClassPath(Pack packFromUserConfig) {
        for (TemplatePack p : getAllTemplatePacksFromClasspath()) {
            if (p.getName().equals(packFromUserConfig.getName())) {
                p.getTemplatePackInfo().overrideProperties(packFromUserConfig.getProperties());
                return p;
            }
        }
        throw new TemplatePackNotFoundException("Could not load the template pack " + packFromUserConfig.getName());
    }
}
