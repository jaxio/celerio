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

package com.jaxio.celerio.util;

import com.jaxio.celerio.Brand;
import com.jaxio.celerio.output.OutputResult;
import com.jaxio.celerio.template.pack.Template;
import com.jaxio.celerio.template.pack.TemplatePack;
import com.jaxio.celerio.template.pack.TemplatePackInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.io.IOUtils.toByteArray;

public class BrandUtil {
    private Brand brand = new Brand();
    private OutputResult outputResult;

    public BrandUtil(OutputResult outputResult) {
        this.outputResult = outputResult;
    }

    public boolean isJaxio() {
        return "Jaxio".equalsIgnoreCase(brand.getCompanyName());
    }

    public boolean useRepository() {
        return true;
    }

    public void copyLogoToFolder(String folderRelativePath) {
        try {
            File logo = new File(brand.getLogoPath());
            String filename = folderRelativePath + "/logo.png";

            TemplatePack fakePack = new TemplatePack() {
                @Override
                public String getName() {
                    return "brand";
                }

                @Override
                public TemplatePackInfo getTemplatePackInfo() {
                    throw new RuntimeException("not implemented");
                }

                @Override
                public List<String> getTemplateNames() {
                    return newArrayList();
                }

                @Override
                public Template getTemplateByName(String name) throws IOException {
                    throw new IOException("not implemented");
                }
            };

            outputResult.addContent(toByteArray(new FileInputStream(logo)), filename, fakePack, new Template("logo.png", null, null));
        } catch (IOException e) {
            throw new RuntimeException("Could not load the uncrypted template packs", e);
        }
    }
}
