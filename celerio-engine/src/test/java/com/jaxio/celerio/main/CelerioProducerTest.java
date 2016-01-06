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

package com.jaxio.celerio.main;

import com.jaxio.celerio.configuration.Celerio;
import com.jaxio.celerio.configuration.convention.ClassTypeOverride;
import com.jaxio.celerio.configuration.convention.Conventions;
import com.jaxio.celerio.configuration.convention.GeneratedPackageOverride;
import com.jaxio.celerio.configuration.convention.MethodConventionOverride;
import com.jaxio.celerio.configuration.database.Metadata;
import com.jaxio.celerio.configuration.database.support.MetadataExtractor;
import com.jaxio.celerio.configuration.entity.EntityConfig;
import com.jaxio.celerio.configuration.support.CelerioLoader;
import com.jaxio.celerio.configuration.support.MetadataLoader;
import com.jaxio.celerio.convention.ClassType;
import com.jaxio.celerio.convention.GeneratedPackage;
import com.jaxio.celerio.convention.MethodConvention;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.sql.SQLException;

@Ignore
@ContextConfiguration("classpath:applicationContext-celerio.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class CelerioProducerTest {

    private static final String H2_SQL_SCRIPT = "classpath:/sql/minimal.sql";
    private static final String CONFLICTS_H2_SQL_SCRIPT = "classpath:/sql/name-conflicts.sql";

    @Autowired
    private MetadataExtractor extractor;

    @Autowired
    private CelerioProducer celerioProducer;

    @Autowired
    private CelerioLoader celerioLoader;

    @Autowired
    private MetadataLoader loader;

    @Test
    public void basic() {
        // just to keep junit mouth shut.
    }

    @Ignore
    public void minimal() throws Exception {
        produceFolder("src/test/resources/minimal");
    }

    @Ignore
    public void joinColumn() throws Exception {
        produceFolder("src/test/resources/joincolumn");
    }

    @Ignore
    public void xmlmetadata() throws Exception {
        produceFolder("src/test/resources/xmlmetadata");
    }

    private void produceFolder(String folder) throws IOException, Exception {
        Celerio celerio = celerioLoader.load(folder + "/celerio-maven-plugin.xml");
        Metadata metadata = loader.load(folder + "/metadata.xml");
        celerioProducer.produce(celerio, metadata);
    }

    @Ignore
    public void produceTablePerSubclassStategy() throws Exception {
        Celerio celerio = celerioLoader.load("src/test/resources/vehicle/table-per-class-strategy.xml");
        Metadata metadata = getMetaDataFromH2Script("classpath:/vehicle/table-per-class-strategy.sql");
        celerioProducer.produce(celerio, metadata);
    }

    @Ignore
    public void produceConflictsH2() throws Exception {
        celerioProducer.produce("conflits", getMetaDataFromH2Script(CONFLICTS_H2_SQL_SCRIPT));
    }

    @Ignore
    public void produceH2() throws Exception {
        celerioProducer.produce("default", getMetaDataFromH2Script(H2_SQL_SCRIPT));
    }

    @Ignore
    public void produceH2AndConventions() throws Exception {
        addConventionOverrides();
        celerioProducer.produce("conventions", getMetaDataFromH2Script(H2_SQL_SCRIPT));
    }

    @Ignore
    public void produceH2AndEntityConfig() throws Exception {
        Metadata metadata = getMetaDataFromH2Script(H2_SQL_SCRIPT);
        EntityConfig entityConfig = new EntityConfig();
        entityConfig.setEntityName(metadata.getTables().get(0).getName());
        entityConfig.setTableName(metadata.getTables().get(0).getName());
        entityConfig.setSubPackage("specific.sub.folder.for.single.table");
        celerioProducer.getConfig().getCelerio().getEntityConfigs().add(entityConfig);
        celerioProducer.produce("entity.config", metadata);
    }

    private void addConventionOverrides() {
        Conventions conventions = celerioProducer.getConfig().getCelerio().getConfiguration().getConventions();
        conventions.getClassTypes().add(overrideModelClass());
        conventions.getClassTypes().add(overrideModelPk());
        conventions.getGeneratedPackages().add(overrideModelPackage());
        conventions.getMethodConventions().add(overrideHasMethod());
    }

    @Ignore
    public void produce() throws Exception {
        celerioProducer.produce(loader.load("src/test/resources/xmlmetadata/broken.xml"));
    }

    private Metadata getMetaDataFromH2Script(String sqlResource) throws ClassNotFoundException, SQLException {
        EmbeddedDatabase embeddedDatabase = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).addScript(sqlResource).build();
        return extractor.extract(embeddedDatabase.getConnection());
    }

    private MethodConventionOverride overrideHasMethod() {
        MethodConventionOverride convention = new MethodConventionOverride();
        convention.setMethodConvention(MethodConvention.HAS);
        convention.setPrefix("isConvention");
        convention.setSuffix("conventionSet");
        return convention;
    }

    private ClassTypeOverride overrideModelClass() {
        ClassTypeOverride convention = new ClassTypeOverride();
        convention.setClassType(ClassType.model);
        convention.setPrefix("ModelPrefixConventionOverride");
        convention.setSuffix("SuffixConventionOverride");
        return convention;
    }

    private ClassTypeOverride overrideModelPk() {
        ClassTypeOverride convention = new ClassTypeOverride();
        convention.setClassType(ClassType.primaryKey);
        convention.setPrefix("PKPrefixConventionOverride");
        convention.setSuffix("PKSuffixConventionOverride");
        return convention;
    }

    private GeneratedPackageOverride overrideModelPackage() {
        GeneratedPackageOverride convention = new GeneratedPackageOverride();
        convention.setGeneratedPackage(GeneratedPackage.Model);
        convention.setSubPackage("model.specific.sub.package");
        return convention;
    }
}
