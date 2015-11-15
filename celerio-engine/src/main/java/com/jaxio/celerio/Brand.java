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

package com.jaxio.celerio;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class Brand {
    private final String logoFilename = "logo.png";
    private final String logoPath;
    private final String brandingPath;
    private String companyName = "Jaxio";
    private String companyUrl = "http://www.jaxio.com/";
    private String footer = "";
    private String rootPackage = "com.jaxio";
    private final String maintenanceEmail = "support@jaxio.com";

    public Brand() {
        brandingPath = System.getProperty("user.home") + "/.celerio/branding.properties";
        logoPath = System.getProperty("user.home") + "/.celerio/" + logoFilename;
        try {

            Properties p = new Properties();
            File f = new File(brandingPath);
            if (f.exists()) {
                p.load(new FileInputStream(f));
                companyName = p.getProperty("company_name", companyName);
                companyUrl = p.getProperty("company_url", companyUrl);
                footer = p.getProperty("footer", footer);
                rootPackage = p.getProperty("root_package", rootPackage);
            } else {
                p.setProperty("root_package", rootPackage);
                p.setProperty("company_name", companyName);
                p.setProperty("company_url", companyUrl);
                p.setProperty("footer", footer);
                if (!f.getParentFile().exists()) {
                    f.getParentFile().mkdirs();
                }
                p.store(new FileOutputStream(f), "CELERIO BRANDING");
            }

            // copy logo if not present
            File logo = new File(logoPath);
            if (!logo.exists()) {
                PathMatchingResourcePatternResolver o = new PathMatchingResourcePatternResolver();
                Resource defaultBrandLogo = o.getResource("classpath:/brand-logo.png");
                new FileOutputStream(logo).write(IOUtils.toByteArray(defaultBrandLogo.getInputStream()));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getBrandingPath() {
        return brandingPath;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getCompanyUrl() {
        return companyUrl;
    }

    public String getMaintenanceEmail() {
        return maintenanceEmail;
    }

    public String getFooter() {
        return footer;
    }

    public String getRootPackage() {
        return rootPackage;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public String getLogoFilename() {
        return logoFilename;
    }
}