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

import com.jaxio.celerio.Brand;
import com.jaxio.celerio.configuration.BuildInfo;
import com.jaxio.celerio.configuration.Configuration;
import com.jaxio.celerio.configuration.Pack;
import com.jaxio.celerio.configuration.database.JdbcConnectivity;
import com.jaxio.celerio.configuration.database.Metadata;
import com.jaxio.celerio.main.CelerioProducer;
import com.jaxio.celerio.output.OutputResult;
import com.jaxio.celerio.output.OutputResultFactory;
import com.jaxio.celerio.template.pack.ClasspathTemplatePackInfoLoader;
import com.jaxio.celerio.template.pack.TemplatePackInfo;
import com.jaxio.celerio.util.XsdHelper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.*;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.jaxio.celerio.util.PackageUtil.isPackageNameValid;
import static org.apache.commons.io.FileUtils.writeStringToFile;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.substringAfter;

/**
 * This plugin creates a default project folder layout following Maven conventions.
 * <p>
 * A Maven <div class="filename">pom.xml</div> is generated as well as default files to help you start from scratch a project.
 * <p>
 * These files represent the minimum required files to produce a project using Celerio.
 *
 * @since 3.0.0
 */
@Mojo(name = "bootstrap", defaultPhase = LifecyclePhase.INITIALIZE, requiresProject = false)
public class BootstrapMojo extends AbstractMojo {

    /**
     * Whether the bootstrap ask interactively for appName, rootPackage etc.?
     */
    @Parameter(property = "bootstrap-maven-plugin.interactive", defaultValue = "true")
    protected String interactive = "true";

    /**
     * The bootstrap pack to use by default if none is specified or if running in non-interactive mode.
     */
    @Parameter(property = "bootstrap-maven-plugin.defaultBootstrapPackName", defaultValue = "pack-backend-jpa")
    protected String defaultBootstrapPackName = "pack-backend-jpa";

    /**
     * The sql conf to use by default if none is specified or if running in non-interactive mode.
     */
    @Parameter(property = "bootstrap-maven-plugin.defaultSqlConfName", defaultValue = "books")
    protected String defaultSqlConfName = "books";


    private List<TemplatePackInfo> bootstrapPacksInfo;
    private Brand brand = new Brand();
    private String rootPackage;
    private String appName;
    private String bootstrapPackName;
    private String sqlConfName;
    private String packCommand = "";
    private String packCommandHelp = "";

    // ------------------------------------
    // cmd line params
    // ------------------------------------
    private String paramCelerioPack = "celerioPack";

    private String paramAppName = "appName";
    private String paramAppRootPackage = "appRootPackage";

    // driver
    private final String paramJdbcGroupId = "jdbcGroupId";
    private final String paramJdbcArtifactId = "jdbcArtifactId";
    private final String paramJdbcVersion = "jdbcVersion";
    private final String paramJdbcDriver = "jdbcDriver";

    // access
    private final String paramHibernateDialect = "hibernateDialect";
    private final String paramJdbcUser = "jdbcUser";
    private final String paramJdbcPassword = "jdbcPassword";
    private final String paramJdbcUrl = "jdbcPassword";
    private final String paramJdbcCatalog = "jdbcCatalog";
    private final String paramJdbcSchema = "jdbcSchema";

    private final String[] params = new String[]{
            paramCelerioPack, paramAppName, paramAppRootPackage,
            paramJdbcGroupId, paramJdbcArtifactId, paramJdbcVersion, paramJdbcDriver,
            paramHibernateDialect, paramJdbcUser, paramJdbcPassword,
            paramJdbcUrl, paramJdbcCatalog, paramJdbcSchema};


    // ------------------------------
    // CELERIO GLUE
    // ------------------------------
    private ApplicationContext context;

    protected boolean isInteractive() {
        return interactive.equals("true");
    }

    protected String getDefaultBootstrapPackName() {
        return defaultBootstrapPackName;
    }

    protected String getDefaultSqlConfName() {
        return defaultSqlConfName;
    }

    protected String getDefaultRootPackage() {
        return brand.getRootPackage();
    }

    protected String getDefaultAppName() {
        if (bootstrapPackName != null && sqlConfName != null) {
            return substringAfter(bootstrapPackName, "pack-").replace('-', '_') + "_" + sqlConfName.replace('-', '_');
        }

        return "appli";
    }

    // ------------------------------------
    // MOJO & WIZARDS
    // ------------------------------------

    /**
     * Mojo entry point.
     */
    @Override
    public void execute() throws MojoExecutionException {

        try {
            if (getBootstrapPacksInfo().isEmpty()) {
                getLog().error("Could not find any Celerio Template Pack having a META-INF/celerio.txt file on the classpath!");
                return;
            }
            celerioWelcomeBanner();

            JdbcConnectivity jdbcConnectivity = null;

            if (useCommandLineParameters()) {
                jdbcConnectivity = setUpParamValuesAndReturnJdbcConnectivity();

                TemplatePackInfo tpi = getBootstrapPackInfoByName(bootstrapPackName);
                packCommand = tpi.getCommand();
                packCommandHelp = tpi.getCommandHelp();
            } else {
                if (isInteractive()) {
                    startInteractiveConfWizard();
                } else {
                    useDefaultConf();
                }
            }

            runCelerioInBootstrapMode(jdbcConnectivity);

            copySqlConf();

            copyCelerioXsd();

            printInstructionsOnceBootstrapIsReady();

        } catch (Exception e) {
            getLog().error(e.getMessage());
            throw new MojoExecutionException(e.getLocalizedMessage(), e);
        }
    }

    private boolean useCommandLineParameters() {
        // light check
        return checkParamPresence(paramAppName) && checkParamPresence(paramJdbcDriver) && checkParamPresence(paramJdbcUser);
    }

    private boolean checkParamPresence(String paramName) {
        String v = System.getProperty(paramName);
        return v != null && !v.isEmpty();
    }

    private JdbcConnectivity setUpParamValuesAndReturnJdbcConnectivity() {

        HashMap<String, String> map = new HashMap();
        for (String param : params) {
            map.put(param, System.getProperty(param));
        }

        rootPackage = map.get(paramAppRootPackage);
        appName = map.get(paramAppName);
        bootstrapPackName = map.get(paramCelerioPack);
        sqlConfName = "empty";

        JdbcConnectivity jdbc = new JdbcConnectivity();
        jdbc.setDriver(map.get(paramJdbcDriver));
        jdbc.setDriverGroupId(map.get(paramJdbcGroupId));
        jdbc.setDriverArtifactId(map.get(paramJdbcArtifactId));
        jdbc.setDriverArtifactIdVersion(map.get(paramJdbcVersion));

        jdbc.setHibernateDialect(map.get(paramHibernateDialect));
        jdbc.setUser(map.get(paramJdbcUser));
        jdbc.setPassword(map.get(paramJdbcPassword));
        jdbc.setSchemaName(map.get(paramJdbcSchema));
        jdbc.setCatalog(map.get(paramJdbcCatalog));
        jdbc.setUrl(map.get(paramJdbcUrl));

        if (jdbc.getDriver().toLowerCase().contains("oracle") || jdbc.getDriver().toLowerCase().contains("derby")) {
            jdbc.setSqlDelimiter("/");
        } else {
            jdbc.setSqlDelimiter(";");
        }

        return jdbc;
    }

    /**
     * Ask the user all the info we need.
     */
    private void startInteractiveConfWizard() throws IOException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("");
            chooseBootstrapPack(br);
            chooseSampleSqlAndConf(br);
            enterAppName(br);
            enterPackageName(br);
        } finally {
            br.close();
        }
    }

    // ----------------------------------------
    // TEMPLATE PACKS
    // ----------------------------------------

    private void useDefaultConf() {
        appName = getDefaultAppName();
        rootPackage = getDefaultRootPackage() + "." + getDefaultAppName();
        bootstrapPackName = getDefaultBootstrapPackName();
        sqlConfName = getDefaultSqlConfName();
    }

    /**
     * Interactively ask the user which pack should be used.
     */
    private void chooseBootstrapPack(BufferedReader br) throws IOException {
        while (true) {
            printInstruction("Choose the type of application you want to generate:");
            for (int i = 0; i < getBootstrapPacksInfo().size(); i++) {
                TemplatePackInfo templatePackInfo = getBootstrapPacksInfo().get(i);
                System.out.println(" " + (i + 1) + ") " + templatePackInfo.getName());
                System.out.println("    " + templatePackInfo.getProjectLink());
                System.out.println("    " + templatePackInfo.getDescription());

                if (templatePackInfo.getDescription2() != null) {
                    System.out.println("    " + templatePackInfo.getDescription2());
                }
                System.out.println("");
            }

            String choice = br.readLine();
            if (isBlank(choice)) {
                continue;
            } else {
                try {
                    TemplatePackInfo chosenTemplatePackInfo = getBootstrapPacksInfo().get(Integer.parseInt(choice) - 1);
                    bootstrapPackName = chosenTemplatePackInfo.getName();
                    System.out.println("OK, using: " + chosenTemplatePackInfo.getName());
                    packCommand = chosenTemplatePackInfo.getCommand();
                    packCommandHelp = chosenTemplatePackInfo.getCommandHelp();
                } catch (Exception e) {
                    System.out.println("");
                    continue;
                }
            }
            break;
        }
    }

    // ----------------------------------------
    // SQL CONF
    // ----------------------------------------

    /**
     * Return the celerio template packs found on the classpath.
     */
    protected List<TemplatePackInfo> getBootstrapPacksInfo() {
        if (bootstrapPacksInfo == null) {
            bootstrapPacksInfo = getCelerioApplicationContext().getBean(ClasspathTemplatePackInfoLoader.class).resolveTopLevelPacks();
        }
        return bootstrapPacksInfo;
    }

    private TemplatePackInfo getBootstrapPackInfoByName(String name) {
        for (TemplatePackInfo tpi : getBootstrapPacksInfo()) {
            if (tpi.getName().equals(name)) {
                return tpi;
            }
        }
        return null;
    }


    /**
     * Interactively ask the user which sql conf should be used.
     */
    private void chooseSampleSqlAndConf(BufferedReader br) throws IOException {
        while (true) {
            printInstruction("Which sample SQL schema would you like to use?");
            for (int i = 0; i < getSqlConfInfos().size(); i++) {
                System.out.println(" " + (i + 1) + ") " + getSqlConfInfos().get(i).getName());
                System.out.println("    " + getSqlConfInfos().get(i).getDescription());
                if (getSqlConfInfos().get(i).getDescription2() != null) {
                    System.out.println("    " + getSqlConfInfos().get(i).getDescription2());
                }
                System.out.println("");
            }

            String choice = br.readLine();
            if (isBlank(choice)) {
                continue;
            } else {
                try {
                    sqlConfName = getSqlConfInfos().get(Integer.parseInt(choice) - 1).getName();
                    System.out.println("OK, using: " + sqlConfName);
                } catch (Exception e) {
                    System.out.println("");
                    continue;
                }
            }
            break;
        }
    }

    /**
     * Scan the classpath for SQL configurations.
     */
    protected List<SqlConfInfo> getSqlConfInfos() {
        List<SqlConfInfo> packInfos = newArrayList();
        PathMatchingResourcePatternResolver o = new PathMatchingResourcePatternResolver();

        try {
            Resource packInfosAsResource[] = o.getResources("classpath*:sqlconf/*/00-info.txt");
            for (Resource r : packInfosAsResource) {
                packInfos.add(new SqlConfInfo(r));
            }

            Collections.sort(packInfos);
            return packInfos;
        } catch (IOException ioe) {
            throw new RuntimeException("Error while searching for SQL CONF having a sqlconf/*/00-info.txt file!", ioe);
        }
    }

    private void copySqlConf() throws IOException {
        PathMatchingResourcePatternResolver o = new PathMatchingResourcePatternResolver();

        File sqlDir = new File(appName + "/src/main/sql/h2");
        sqlDir.mkdirs();

        // copy sql
        copySingleSqlFile(o, sqlDir, "01-drop.sql");
        copySingleSqlFile(o, sqlDir, "02-create.sql");
        copySingleSqlFile(o, sqlDir, "03-import.sql");

        // copy conf.
        Resource xmlResource[] = o.getResources("classpath*:sqlconf/" + sqlConfName + "/celerio-maven-plugin.xml");
        StringWriter xmlWriter = new StringWriter();
        IOUtils.copy(xmlResource[0].getInputStream(), xmlWriter, "UTF-8");
        String xmlConf = xmlWriter.toString();
        xmlConf = xmlConf.replace("${groupId}", rootPackage); // same as for archetype
        xmlConf = xmlConf.replace("${artifactId}", appName); // same as for archetype
        File xmlDir = new File(appName + "/src/main/config/celerio-maven-plugin");
        xmlDir.mkdirs();
        File xmlFile = new File(xmlDir, "celerio-maven-plugin.xml");
        getLog().info("Copy configuration file from '" + sqlConfName + "' resources: " + xmlFile.getName());
        writeStringToFile(xmlFile, xmlConf, "UTF-8");
    }

    private void copySingleSqlFile(PathMatchingResourcePatternResolver o, File sqlDir, String filename) throws IOException {
        Resource sqlResource[] = o.getResources("classpath*:sqlconf/" + sqlConfName + "/" + filename);
        File sqlFile = new File(sqlDir, filename);
        getLog().info("Copy SQL file from '" + sqlConfName + "' resources: " + sqlFile.getName());
        IOUtils.copy(sqlResource[0].getInputStream(), new FileOutputStream(sqlFile));

    }

    // ----------------------------------------
    // APP NAME AND PACKAGE NAME
    // ----------------------------------------

    private void copyCelerioXsd() throws IOException {
        File xsdDir = new File(appName + "/src/main/config/celerio-maven-plugin");
        xsdDir.mkdirs();
        File celerioXsdFile = new File(xsdDir, "celerio.xsd");
        File nonamespaceXsdFile = new File(xsdDir, "nonamespace.xsd");

        getLog().info("Copy Celerio configuration xsd files to " + appName + "/src/main/config/celerio-maven-plugin");
        writeStringToFile(celerioXsdFile, XsdHelper.getCelerioXsdAsString(), "UTF-8");
        writeStringToFile(nonamespaceXsdFile, XsdHelper.getNonamespaceXsdAsString(), "UTF-8");
    }

    /**
     * Ask the user to enter the package name.
     */
    private void enterPackageName(BufferedReader br) throws IOException {
        String suggestedRootPackage = getDefaultRootPackage() + "." + appName;
        while (true) {
            printInstruction("Enter the Java root package of your application: [" + suggestedRootPackage + "]");

            String packageNameCandidate = br.readLine();
            if (isBlank(packageNameCandidate)) {
                rootPackage = suggestedRootPackage;
                break;
            } else {
                if (isPackageNameValid(packageNameCandidate)) {
                    rootPackage = packageNameCandidate;
                    break;
                } else {
                    System.out.println("Oops! invalid Java package name.");
                    System.out.println("");
                    continue;
                }
            }
        }
    }

    /**
     * Ask the user to enter the application name.
     */
    private void enterAppName(BufferedReader br) throws IOException {
        while (true) {
            printInstruction("Enter your application name: [" + getDefaultAppName() + "]");

            String appNameCandidate = br.readLine();
            if (isBlank(appNameCandidate)) {
                appName = getDefaultAppName();
                break;
            } else {
                if (isPackageNameValid(getDefaultRootPackage() + "." + appNameCandidate)) {
                    appName = appNameCandidate;
                    break;
                } else {
                    System.out.println("Oops! invalid application name. Keep it simple, no '-', etc...");
                    System.out.println("");
                    continue;
                }
            }
        }
    }

    /**
     * Returns the Celerio engine spring application context.
     */
    private ApplicationContext getCelerioApplicationContext() {
        if (context == null) {
            context = new ClassPathXmlApplicationContext("classpath:applicationContext-celerio.xml");
        }
        return context;
    }

    private BuildInfo getCelerioBuildInfo() {
        return getCelerioApplicationContext().getBean(BuildInfo.class);
    }

    private void runCelerioInBootstrapMode(JdbcConnectivity jdbcConnectivity) throws Exception {
        getLog().info("Please wait a moment...");
        CelerioProducer producer = getCelerioApplicationContext().getBean(CelerioProducer.class);

        Configuration configuration = new Configuration();
        configuration.setRootPackage(rootPackage);
        configuration.setApplicationName(appName);
        configuration.setPacks(newArrayList(new Pack(bootstrapPackName)));

        producer.getConfig().getCelerio().setConfiguration(configuration);
        if (jdbcConnectivity != null) {
            Metadata metadata = new Metadata();
            metadata.setJdbcConnectivity(jdbcConnectivity);
            producer.getConfig().setMetadata(metadata);
        }

        producer.getConfig().setOutputResult(getOutputResult(getCelerioApplicationContext()));
        producer.bootstrap();
    }

    private OutputResult getOutputResult(ApplicationContext context) {
        return context.getBean(OutputResultFactory.class).getOutputResult(appName, appName);
    }

    // ----------------------------------------
    // COSMETIC PRINT
    // ----------------------------------------

    private void celerioWelcomeBanner() {
        // http://ascii.mastervb.net/
        // font : varsity.ftl
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        getLog().info("");
        getLog().info("        ______         __                  _          ");
        getLog().info("      .' ___  |       [  |                (_)         ");
        getLog().info("     / .'   \\_| .---.  | | .---.  _ .--.  __   .--.   ");
        getLog().info("     | |       / /__\\\\ | |/ /__\\\\[ `/'`\\][  |/ .'`\\ \\ ");
        getLog().info("     \\ `.___.'\\| \\__., | || \\__., | |     | || \\__. | ");
        getLog().info("      `.____ .' '.__.'[___]'.__.'[___]   [___]'.__.'   v" + getCelerioBuildInfo().getPomVersion());
        getLog().info("");
        getLog().info("       Branding file: " + brand.getBrandingPath());
        getLog().info("       Documentation: http://www.jaxio.com/documentation/celerio/");
        getLog().info("       (c) 2005-" + currentYear + " Jaxio, http://www.jaxio.com");
        getLog().info("");
    }

    private void printInstructionsOnceBootstrapIsReady() {
        getLog().info("");
        getLog().info("============================================");
        getLog().info("");
        System.out.println("");
        System.out.println("FINAL STEPS:");
        System.out.println("");
        System.out.println("1/ Optional: you can edit the generated pom.xml file to use your own database");
        System.out.println("");
        System.out.println("2/ Execute the following commands:");
        System.out.println("");
        System.out.println("cd " + appName);
        System.out.println("" + packCommand);
        System.out.println("");
        if (packCommandHelp != null && !packCommandHelp.isEmpty()) {
            System.out.println("3/ " + packCommandHelp);
            System.out.println("");
        }
        getLog().info("");
        getLog().info("============================================");
    }

    /**
     * Print the passed instruction in the console.
     */
    private void printInstruction(String instruction) {
        System.out.println(StringUtils.repeat("-", instruction.length()));
        System.out.println(instruction);
        System.out.println(StringUtils.repeat("-", instruction.length()));
    }
}
