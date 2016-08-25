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
package com.jaxio.celerio.maven.plugin.dbmetadata;

import com.jaxio.celerio.configuration.database.JdbcConnectivity;
import com.jaxio.celerio.configuration.database.Metadata;
import com.jaxio.celerio.configuration.database.TableType;
import com.jaxio.celerio.configuration.database.support.MetadataExtractor;
import com.jaxio.celerio.configuration.support.MetadataLoader;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.oxm.XmlMappingException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.io.FilenameUtils.normalize;

/**
 * This plugin connects to a relational database using JDBC and reverses the database schema meta data. The reverse engineering consists in serializing the
 * information returned by the JDBC driver into an XML file (see the documentation of the <a
 * href="http://java.sun.com/j2se/1.4.2/docs/api/java/sql/DatabaseMetaData.html">java.sql.DatabaseMetaData</a> for more information).
 * <p>
 * The <code>metadata.xml</code> file produced by this plugin is used by Celerio's generate goal. Please refer to the celerio.generate.
 *
 * @since 3.0.0
 */
@Mojo(name = "extract-metadata", defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresProject = false)
public class DbMetadataMojo extends AbstractMojo {

    /**
     * Maven project, this is by default the current Maven project.
     */
    @Parameter(property = "project", readonly = true)
    protected MavenProject project;

    /**
     * Should the database meta data extraction be skipped ?
     * <p>
     * This is a common pattern in Maven, where you can skip plugins using profiles to fully adapt your build.
     */
    @Parameter(property = "celerio-maven-plugin.skip", defaultValue = "false")
    protected boolean skip;

    /**
     * Specify the JDBC driver class.
     * <p>
     * Example: <code>org.postgresql.Driver</code>
     */
    @Parameter(property = "jdbc.driver")
    protected String jdbcDriver;

    /**
     * Specify the JDBC url to connect to your database. Make sure that you connect with enough privileges to access the meta data information.
     * <p>
     * Example: <code>jdbc:h2:~/.h2/sampledatabase</code>
     */
    @Parameter(property = "jdbc.url")
    protected String jdbcUrl;

    /**
     * Specify the JDBC user, this user needs to have the privilege to access the database metadata.
     */
    @Parameter(property = "jdbc.user")
    protected String jdbcUser;

    /**
     * Specify the JDBC password.
     */
    @Parameter(property = "jdbc.password")
    protected String jdbcPassword;

    /**
     * Specify the JDBC catalog.
     */
    @Parameter(property = "jdbc.catalog")
    protected String jdbcCatalog;

    /**
     * Should the Oracle remarks be retrieved ? Please note that this will impact the speed of the reverse engineering of your database.
     */
    @Parameter(property = "jdbc.oracleRetrieveRemarks", defaultValue = "false")
    protected boolean jdbcOracleRetrieveRemarks;

    /**
     * Should the synonyms be retrieved ?
     */
    @Parameter(property = "jdbc.oracleRetrieveSynonyms", defaultValue = "true")
    protected boolean jdbcOracleRetrieveSynonyms;

    /**
     * When false, disable completely reverse of indexes.
     * Can be useful when reversing large database full of data as reversing indexes can be slow.
     */
    @Parameter(property = "jdbc.reverseIndexes", defaultValue = "true")
    protected boolean reverseIndexes;

    /**
     * When true, reverse only indexes for unique values; when false, reverse indexes regardless of whether unique or not.
     * Can be useful when reversing large database full of data as reversing indexes can be slow.
     */
    @Parameter(property = "jdbc.reverseOnlyUniqueIndexes", defaultValue = "true")
    protected boolean reverseOnlyUniqueIndexes;

    /**
     * Should we also reverse VIEWS?
     */
    @Parameter(property = "jdbc.reverseViews", defaultValue = "false")
    protected boolean jdbcReverseViews;

    /**
     * Specify the JDBC schema.
     */
    @Parameter(property = "jdbc.schema")
    protected String jdbcSchema;

    /**
     * Specify the tableNamePattern passed to java.sql.DatabaseMetaData#getTables
     */
    @Parameter(property = "jdbcTableNamePatterns")
    protected List<String> jdbcTableNamePatterns = new ArrayList<String>();

    /**
     * The fully qualified name of the XML file created by this plugin.
     */
    @Parameter(property = "maven-metadata-plugin.targetFilename", defaultValue = "${basedir}/src/main/config/celerio-maven-plugin/metadata.xml")
    protected String targetFilename;

    protected ApplicationContext context;

    public void execute() throws MojoExecutionException {
        try {
            process();
        } catch (Exception e) {
            getLog().error(e.getMessage(), e);
            throw new MojoExecutionException(e.getLocalizedMessage(), e);
        }
    }

    private void process() throws MojoExecutionException {
        if (skip) {
            getLog().info("skipping metadata plugin");
            return;
        }
        initApplicationContext();
        JdbcConnectivity configuration = getDbMetadataConfiguration();
        if (configuration == null) {
            getLog().error("aborting");
            return;
        }
        extract(configuration, targetFilename);
    }

    private void initApplicationContext() {
        context = new ClassPathXmlApplicationContext("classpath*:applicationContext-celerio.xml");
    }

    private void extract(JdbcConnectivity configuration, String target) throws MojoExecutionException {
        MetadataExtractor extractor = getMetadataExtractor();
        MetadataLoader loader = getMetadataLoader();
        try {
            Metadata metaData = extractor.extract(configuration);
            metaData.cleanMetadata();
            loader.write(metaData, targetFilename);
            getLog().info("Write metadata.xml file to " + removeBaseDir(target));
        } catch (ClassNotFoundException e) {
            throw new MojoExecutionException("Could not load driver", e);
        } catch (SQLException e) {
            throw new MojoExecutionException("Error while talking to database", e);
        } catch (FileNotFoundException e) {
            throw new MojoExecutionException("could not write to " + removeBaseDir(target), e);
        } catch (XmlMappingException e) {
            throw new MojoExecutionException("mapping exception while writing " + removeBaseDir(target), e);
        } catch (IOException e) {
            throw new MojoExecutionException("io exception while writing " + removeBaseDir(target), e);
        }
    }

    private MetadataLoader getMetadataLoader() {
        return context.getBean(MetadataLoader.class);
    }

    private MetadataExtractor getMetadataExtractor() {
        return context.getBean(MetadataExtractor.class);
    }

    private JdbcConnectivity getDbMetadataConfiguration() {
        JdbcConnectivity jdbcConnectivity = new JdbcConnectivity();
        if (jdbcCatalog != null) {
            jdbcConnectivity.setCatalog(jdbcCatalog);
        }

        if (jdbcSchema != null) {
            jdbcConnectivity.setSchemaName(jdbcSchema);
        }
        jdbcConnectivity.setDriver(jdbcDriver);
        jdbcConnectivity.setUser(jdbcUser);
        jdbcConnectivity.setPassword(jdbcPassword);
        jdbcConnectivity.setUrl(jdbcUrl);
        jdbcConnectivity.setOracleRetrieveSynonyms(jdbcOracleRetrieveSynonyms);
        jdbcConnectivity.setOracleRetrieveRemarks(jdbcOracleRetrieveRemarks);
        jdbcConnectivity.setReverseIndexes(reverseIndexes);
        jdbcConnectivity.setReverseOnlyUniqueIndexes(reverseOnlyUniqueIndexes);
        jdbcConnectivity.add(TableType.TABLE);
        jdbcConnectivity.setTableNamePatterns(jdbcTableNamePatterns);
        if (jdbcReverseViews) {
            getLog().info("Reverse VIEWS is enabled");
            jdbcConnectivity.add(TableType.VIEW);
        }

        if (jdbcConnectivity.invalid()) {
            displayMissingMetadataConfiguration(jdbcConnectivity);
            getLog().error("Please update your jdbc configuration so we can reverse your database");
            return null;
        }
        return jdbcConnectivity;
    }

    private void displayMissingMetadataConfiguration(JdbcConnectivity configuration) {
        if (StringUtils.isBlank(configuration.getDriver())) {
            getLog().error("jdbcDriver missing");
        }
        if (StringUtils.isBlank(configuration.getUser())) {
            getLog().error("jdbcUser missing");
        }
        if (StringUtils.isBlank(configuration.getUrl())) {
            getLog().error("jdbcUrl missing");
        }
    }

    private String removeBaseDir(String filename) {
        String normalizedFilename = normalize(filename);
        return normalizedFilename.replace(getBaseDir(), "");
    }

    private String getBaseDir() {
        return normalize(project.getBasedir().getAbsoluteFile().getAbsolutePath() + File.separatorChar);
    }
}
