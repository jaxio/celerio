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

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@Service
public class ClasspathTemplatePackInfoLoader {

    public List<TemplatePackInfo> resolveTopLevelPacks() {
        List<TemplatePackInfo> packInfos = newArrayList();
        PathMatchingResourcePatternResolver o = new PathMatchingResourcePatternResolver();

        try {
            Resource packInfosAsResource[] = o.getResources("classpath*:META-INF/celerio.txt");
            for (Resource r : packInfosAsResource) {
                packInfos.add(new TemplatePackInfo(r));
            }

            return packInfos;
        } catch (IOException ioe) {
            throw new RuntimeException("Error while searching for Celerio template pack having a META-INF/celerio.txt file!", ioe);
        }
    }
}