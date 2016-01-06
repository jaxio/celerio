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

import com.jaxio.celerio.configuration.pack.CelerioPack;
import com.jaxio.celerio.configuration.support.CelerioPackConfigLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.jaxio.celerio.configuration.Util.CLASSPATH_CELERIO_PACK;

@Service
public class ClasspathResourceUncryptedPackLoader {

    @Autowired
    private CelerioPackConfigLoader celerioPackConfigLoader;


    public List<TemplatePack> getPacks() {
        List<TemplatePack> packs = newArrayList();
        try {
            PathMatchingResourcePatternResolver o = new PathMatchingResourcePatternResolver();
            Resource infos[] = o.getResources(CLASSPATH_CELERIO_PACK);

            for (Resource info : infos) {
                CelerioPack celerioPack = celerioPackConfigLoader.load(info.getInputStream());
                TemplatePackInfo templatePackInfo = new TemplatePackInfo(celerioPack);
                Resource templatesAsResources[] = o.getResources("classpath*:/celerio/" + templatePackInfo.getName() + "/**/*");
                packs.add(new ClasspathResourceUncryptedPack(templatePackInfo, templatesAsResources));
            }

            return packs;
        } catch (IOException e) {
            throw new RuntimeException("Could not load the template packs", e);
        }
    }
}
