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

package com.jaxio.celerio.maven.plugin.bootstrap;

import static org.apache.commons.io.FileUtils.writeStringToFile;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;

import com.jaxio.celerio.template.pack.TemplatePackInfo;

/**
 * This plugin creates several project folder layout following Maven conventions.
 * <p>
 * A Maven <div class="filename">pom.xml</div> is generated as well as default files to help you start from scratch a project.
 * <p>
 * These files represent the minimum required files to produce a project using Celerio.
 * 
 * @goal bootstrap-it
 * @phase initialize
 * @requiresProject false
 * @since 3.0.0
 */
public class BootstrapForITMojo extends BootstrapMojo {
    private String currentAppName;
    private String currentBootstrapPack;
    private String currentSqlConfName;
    private String currentRootPackage;

    /**
     * The current folder
     * 
     * @parameter property="basedir"
     */
    protected String baseDir;

    @Override
    protected String getDefaultBootstrapPackName() {
        return currentBootstrapPack;
    }

    @Override
    protected String getDefaultSqlConfName() {
        return currentSqlConfName;
    }

    @Override
    protected boolean isInteractive() {
        return false;
    }

    @Override
    protected String getDefaultRootPackage() {
        return currentRootPackage;
    }

    @Override
    protected String getDefaultAppName() {
        return currentAppName;
    }

    @Override
    public void execute() throws MojoExecutionException {
        long timestamp = System.currentTimeMillis();
        int counter = 1;
        for (SqlConfInfo sqlConfInfo : getSqlConfInfos()) {
            for (TemplatePackInfo tpi : getBootstrapPacksInfo()) {

                currentBootstrapPack = tpi.getName();
                currentRootPackage = "integration.ts" + timestamp + "." + tpi.getName().replace("-", "_");
                currentSqlConfName = sqlConfInfo.getName();
                currentAppName = "app_" + appNo(counter++) + "_" + currentBootstrapPack.replace("pack-", "").replace("-", "_") + "_"
                        + currentSqlConfName.replace("-", "_");

                getLog().info("------------------------------------------------");
                getLog().info("Bootstrapping project using " + currentBootstrapPack);
                getLog().info("  rootPackage: " + currentRootPackage);
                getLog().info("  sqlConfName: " + currentSqlConfName);
                getLog().info("------------------------------------------------");

                try {
                    StringBuffer toWrite = new StringBuffer("invoker.profiles=" + tpi.getProfilesCSV() + "\n");
                    toWrite.append("invoker.goals=verify\n");
                    toWrite.append("invoker.buildResult=success\n");
                    writeStringToFile(new File(currentAppName, "invoker.properties"), toWrite.toString());
                } catch (IOException ioe) {
                    throw new MojoExecutionException("Could not create invoker.properties", ioe);
                }
                super.execute();
            }
        }
    }

    private String appNo(int no) {
        if (no < 10) {
            return "00" + no;
        }

        if (no >= 10 && no < 100) {
            return "0" + no;
        }

        return "" + no;
    }
}
