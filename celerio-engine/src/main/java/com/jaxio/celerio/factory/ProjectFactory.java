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

package com.jaxio.celerio.factory;

import com.jaxio.celerio.Config;
import com.jaxio.celerio.ConfigurationCheck;
import com.jaxio.celerio.aspects.ForbiddenWhenBuildingAspect;
import com.jaxio.celerio.configuration.*;
import com.jaxio.celerio.configuration.convention.*;
import com.jaxio.celerio.configuration.database.Table;
import com.jaxio.celerio.configuration.entity.EntityConfig;
import com.jaxio.celerio.configuration.entity.EnumConfig;
import com.jaxio.celerio.configuration.entity.EnumType;
import com.jaxio.celerio.configuration.entity.EnumValue;
import com.jaxio.celerio.convention.GeneratedPackage;
import com.jaxio.celerio.convention.WellKnownFolder;
import com.jaxio.celerio.factory.conventions.AccountConvention;
import com.jaxio.celerio.model.Attribute;
import com.jaxio.celerio.model.Entity;
import com.jaxio.celerio.model.Project;
import com.jaxio.celerio.model.Relation;
import com.jaxio.celerio.model.relation.AbstractRelation;
import com.jaxio.celerio.model.support.ClassNamer2;
import com.jaxio.celerio.model.support.ValuesAttribute;
import com.jaxio.celerio.model.support.custom.CustomAttribute;
import com.jaxio.celerio.model.support.custom.CustomEntity;
import com.jaxio.celerio.model.support.formatter.FormatterAttribute;
import com.jaxio.celerio.model.support.h2.H2Attribute;
import com.jaxio.celerio.model.support.jpa.*;
import com.jaxio.celerio.model.support.jquery.JQueryAttribute;
import com.jaxio.celerio.model.support.jsf.JsfAttribute;
import com.jaxio.celerio.model.support.search.SearchAttribute;
import com.jaxio.celerio.model.support.search.SearchEntity;
import com.jaxio.celerio.model.support.springmvc.SpringMVCAttribute;
import com.jaxio.celerio.model.support.springmvc.SpringMVCEntity;
import com.jaxio.celerio.model.support.validation.ValidationAttribute;
import com.jaxio.celerio.model.support.validation.ValidationRelation;
import com.jaxio.celerio.spi.AttributeSpi;
import com.jaxio.celerio.spi.EntitySpi;
import com.jaxio.celerio.spi.ProjectSpi;
import com.jaxio.celerio.spi.RelationSpi;
import com.jaxio.celerio.template.pack.PackLoader;
import com.jaxio.celerio.template.pack.TemplatePack;
import com.jaxio.celerio.util.FallBackUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static com.jaxio.celerio.util.FallBackUtil.fallBack;
import static com.jaxio.celerio.util.StringUtil.getFirstCharacterUppered;

@Service
@Slf4j
public class ProjectFactory {
    public static final String DEFAULT_ENTITY_ROOTPACKAGE = "com.jaxio.demo";

    @Autowired
    private Config config;

    @Autowired
    private EntityFactory entityFactory;

    @Autowired
    private RelationFactory relationFactory;

    @Autowired
    private EntityConfigFactory entityConfigFactory;

    @Autowired
    private PrimaryKeyFactory primaryKeyFactory;

    @Autowired
    private UniqueFactory uniqueFactory;

    @Autowired
    private BusinessKeyFactory businessKeyFactory;

    @Autowired
    private ForeignKeyHintsFactory foreignKeyHintsFactory;

    @Autowired
    private InheritanceFactory inheritanceFactory;

    @Autowired
    private ConfigurationCheck configurationCheck;

    @Autowired
    private ForbiddenWhenBuildingAspect forbiddenWhenBuildingAspect;

    @Autowired
    private BuildInfo buildInfo;

    @Autowired
    private PackLoader packLoader;
    // -----------------------------------------------
    // SPI - Service Provider Interface
    // -----------------------------------------------
    private List<Class<? extends ProjectSpi>> projectSpis = newArrayList();
    private List<Class<? extends EntitySpi>> entitySpis = newArrayList();
    private List<Class<? extends AttributeSpi>> attributeSpis = newArrayList();
    private List<Class<? extends RelationSpi>> relationSpis = newArrayList();
    private List<ProjectSpi> defaultProjectSpis = newArrayList();
    private List<EntitySpi> defaultEntitySpis = newArrayList( //
            new JpaEntity(), //
            new CustomEntity(), //
            new SearchEntity(), //
            new SpringMVCEntity() //
    );
    private List<AttributeSpi> defaultAttributeSpis = newArrayList( //
            new CustomAttribute(), //
            new FormatterAttribute(), //
            new SearchAttribute(), //
            new ValidationAttribute(), //
            new SpringMVCAttribute(), //
            new JsfAttribute(), //
            new H2Attribute(), //
            new JQueryAttribute(), //
            new ValuesAttribute()
    );
    private List<RelationSpi> defaultRelationSpis = newArrayList( //
            new ValidationRelation(), //
            new JpaToOneRelation(), //
            new JpaOneToManyRelation(), //
            new JpaManyToManyRelation(), //
            new JpaIntermediateManyToOneRelation(), //
            new JpaIntermediateOneToManyRelation() //
    );

    public void init() {
        Assert.notNull(config.getOutputResult(), "Output result is not defined");

        Project project = config.getProject();
        Celerio celerio = config.getCelerio();

        if (config.isSpringfuseMode()) {
            handleSpringfuseSpecificities();
        }

        setCelerioVersion(celerio, buildInfo);
        updateNamingConventions(celerio.getConfiguration());

        celerio.setEntityConfigs(entityConfigFactory.filterEntityConfigs(celerio.getEntityConfigs()));

        log.info("Checking configuration consistency...");
        entityConfigFactory.assertEntityConfigListIsConsistent(celerio.getEntityConfigs());

        log.info("Processing enums...");
        buildSharedEnums(celerio);

        log.info("Processing entities...");
        buildEntities(project, celerio.getEntityConfigs());

        log.info("Processing inheritance...");
        inheritanceFactory.wireEntityHierarchies();

        log.info("Processing uniques/pk/fk...");
        setupEntities();

        forbiddenWhenBuildingAspect.buildingDone();

        log.info("Processing relations...");
        setupRelations();

        log.info("Processing business keys...");
        setupBusinessKeys();

        log.info("Processing global validation...");
        globalValidation();

        log.info("Applying conventions...");
        conventions(project, config.getCelerio().getConfiguration());

        log.info("Loading Celerio SPIs...");
        loadAndApplySpis();
    }

    /**
     * @Todo allow packs to say which module they require Update: not a good idea as pack may be simple folder on disk... => no
     */
    private void handleSpringfuseSpecificities() {
        if (config.isSpringfuseMode()) {
            // FOLDER FOR JAVA GENERATED CODE
            WellKnownFolderOverride javaFolder = new WellKnownFolderOverride();
            javaFolder.setWellKnownFolder(WellKnownFolder.JAVA);
            javaFolder.setGeneratedFolder("src/main/java");

            WellKnownFolderOverride javaTestFolder = new WellKnownFolderOverride();
            javaTestFolder.setWellKnownFolder(WellKnownFolder.JAVA_TEST);
            javaTestFolder.setGeneratedFolder("src/test/java");

            List<WellKnownFolderOverride> wellKnownFolders = newArrayList(javaFolder, javaTestFolder);
            config.getCelerio().getConfiguration().getConventions().setWellKnownFolders(wellKnownFolders);

            // MISC
            config.getCelerio().getConfiguration().getGeneration().setUseMavenCelerioPlugin(false); // todo: a bit redundant!

            // OVERRIDE COMMENTS with default ones
            config.getCelerio().getConfiguration().setHeaderComment(new HeaderComment());
        }
    }

    private void setCelerioVersion(Celerio celerio, BuildInfo buildInfo) {
        celerio.getConfiguration().getGeneration().setVersion(buildInfo.getPomVersion());
    }

    private void buildEntities(Project project, List<EntityConfig> entityConfigs) {
        Set<String> tablesProcessed = new HashSet<String>();
        addEntitiesDefinedInUserConfiguration(project, entityConfigs, tablesProcessed);
        addRemainingEntities(project, tablesProcessed);
    }

    private void addRemainingEntities(Project project, Set<String> tableProcessed) {
        for (Table table : config.getMetadata().getTables()) {
            // Make sure we do not process tables twice
            if (tableProcessed.contains(table.getName().toUpperCase())) {
                // skip default entity creation
                continue;
            }

            if (!config.getCelerio().getConfiguration().hasTable(table.getName())) {
                log.info("Skipping table '" + table.getName() + "'. Reason: excluded in configuration");
                continue;
            }

            EntityConfig entityConfig = entityConfigFactory.buildEntityConfig(table);

            // Skip conflict cases
            if (project.hasEntityByName(entityConfig.getEntityName())) {
                log.warn("Skip default entity creation for table name=" + table.getName() + " entity name=" + entityConfig.getEntityName()
                        + " as it was already registered!");
                continue;
            }

            Entity entity = entityFactory.buildEntity(entityConfig);
            log.info("Adding entity " + entity.getName());
            project.addEntity(entity);
            project.putEntity(entity);
            tableProcessed.add(table.getName().toUpperCase()); // not really needed but to be consistent...
        }
    }

    private void addEntitiesDefinedInUserConfiguration(Project project, List<EntityConfig> entityConfigs, Set<String> tableProcessed) {
        for (EntityConfig entityConfig : entityConfigs) {
            if (entityConfig.hasTableName() && !config.getCelerio().getConfiguration().hasTable(entityConfig.getTableName())) {
                log.info("Skipping table '" + entityConfig.getTableName() + "'. Reason: excluded in configuration");
                continue;
            }

            entityConfigFactory.applyFallBacks(entityConfig);
            Entity entity = entityFactory.buildEntity(entityConfig);
            log.info("Adding entity " + entity.getName());
            project.addEntity(entity);

            if (!entity.hasInheritance()) {
                project.putEntity(entity);
            }

            // note: we may add a table twice, this is OK, this code is just here to
            // avoid processing tables not present in the conf
            // please check addRemainingEntities
            tableProcessed.add(entity.getTableName().toUpperCase());
        }
    }

    private void setupEntities() {
        for (Entity entity : config.getProject().getCurrentEntities()) {
            entityFactory.setupAttributes(entity);
        }

        for (Entity entity : config.getProject().getCurrentEntities()) {
            uniqueFactory.setupUniques(entity);
        }

        for (Entity entity : config.getProject().getCurrentEntities()) {
            primaryKeyFactory.setupPrimaryKey(entity);
        }

        // Attention: FK hints must be processed after PK
        for (Entity entity : config.getProject().getCurrentEntities()) {
            foreignKeyHintsFactory.setupForeignKeyHints(entity);
        }
    }

    private void setupRelations() {
        for (Entity entity : config.getProject().getCurrentEntities()) {
            relationFactory.setupRelations(entity);
        }
    }

    private void setupBusinessKeys() {
        for (Entity entity : config.getProject().getCurrentEntities()) {
            businessKeyFactory.setupBusinessKey(entity);
        }
    }

    private void globalValidation() {
        if (!configurationCheck.check(config)) {
            throw new IllegalStateException("There are some errors, generation aborted.");
        }
    }

    private void updateNamingConventions(Configuration configuration) {
        configuration.setRootPackage(fallBack(configuration.getRootPackage(), DEFAULT_ENTITY_ROOTPACKAGE));
        if ("".equals(configuration.getRootPackage())) {
            configuration.setRootPackage(DEFAULT_ENTITY_ROOTPACKAGE);
        }

        Conventions conventions = configuration.getConventions();
        updateClassTypeConventions(conventions);
        updateMethodConventions(conventions);
        updateWellKnownFolderConventions(conventions);
        updateGeneratedPackageConventions(configuration, conventions);
    }

    private void updateWellKnownFolderConventions(Conventions conventions) {
        for (WellKnownFolderOverride convention : conventions.getWellKnownFolders()) {
            convention.apply();
        }

        if (!config.getOutputResult().sameDirectory()) {
            for (WellKnownFolder wkf : WellKnownFolder.values()) {
                wkf.setGeneratedFolder(wkf.getFolder());
            }
        }
    }

    private void updateMethodConventions(Conventions conventions) {
        for (MethodConventionOverride convention : conventions.getMethodConventions()) {
            convention.apply();
        }
    }

    private void updateClassTypeConventions(Conventions conventions) {
        for (ClassTypeOverride convention : conventions.getClassTypes()) {
            convention.apply();
        }
    }

    private void updateGeneratedPackageConventions(Configuration configuration, Conventions conventions) {
        // set the values from the config
        for (GeneratedPackageOverride convention : conventions.getGeneratedPackages()) {
            convention.apply();
        }
        // set default values for the root package
        for (GeneratedPackage generatedPackage : GeneratedPackage.values()) {
            if (generatedPackage.getRootPackage() == null) {
                generatedPackage.setRootPackage(configuration.getRootPackage());
            }
        }
    }

    private void conventions(Project project, Configuration configuration) {
        AccountConvention accountConvention = new AccountConvention();

        // try to detect an account entity
        if (!project.isAccountEntityPresent()) {
            for (Entity entity : project.getCurrentEntities()) {
                if (accountConvention.setupAccount(entity)) {
                    return;
                }
            }
        }
    }

    private void buildSharedEnums(Celerio celerio) {
        for (EnumConfig sharedEnum : celerio.getSharedEnumConfigs()) {
            sharedEnum.setRootPackage(FallBackUtil.fallBack(sharedEnum.getRootPackage(), config.getCelerio().getConfiguration().getRootPackage()));
            sharedEnum.setType(fallBack(sharedEnum.getType(), EnumType.ORDINAL));
            sharedEnum.setName(getFirstCharacterUppered(sharedEnum.getName()));

            for (EnumValue ev : sharedEnum.getEnumValues()) {
                Assert.isTrue(ev.hasValue(), "The 'value' attribute is mandatory for the sharedEnumConfig " + sharedEnum.getName());
                ev.setName(fallBack(ev.getName(), ev.getValue()));

                if (!sharedEnum.isCustomType()) {
                    ev.setValue(null);
                }

                // fix for evdev
                if (NumberUtils.isNumber(ev.getName().substring(0, 1))) {
                    ev.setName("TODO_" + ev.getName());
                    log.warn("Please review your enum " + sharedEnum.getName() + " configuration. Some constant are numeric!");
                }
            }
        }
    }

    private void loadAndApplySpis() {
        // 1- project SPI
        loadProjectSpis(defaultProjectSpis.iterator());
        loadProjectSpis(ServiceLoader.load(ProjectSpi.class).iterator());

        // 2- entity SPI
        loadEntitySpis(defaultEntitySpis.iterator());
        loadEntitySpis(ServiceLoader.load(EntitySpi.class).iterator());

        // 3- attribute SPI
        loadAttributeSpis(defaultAttributeSpis.iterator());
        loadAttributeSpis(ServiceLoader.load(AttributeSpi.class).iterator());

        // 4- relation SPI
        loadRelationSpis(defaultRelationSpis.iterator());
        loadRelationSpis(ServiceLoader.load(RelationSpi.class).iterator());

        // 5- real binding on project, entities, attributes, relations.
        bindAllSpis();

        // 6- now bind all namers found in various pack config
        // Note: namers found in main config are loaded/binded by the entityFactory.
        for (TemplatePack templatePack : packLoader.getTemplatePacks()) {

            List<EntityContextProperty> entityContextPropertyList = templatePack.getTemplatePackInfo().getEntityContextPropertyList();
            if (entityContextPropertyList == null || entityContextPropertyList.size() == 0) {
                continue;
            }

            for (Entity entity : config.getProject().getCurrentEntities()) {
                for (EntityContextProperty ecp : entityContextPropertyList) {
                    entity.put(ecp.getProperty(), new ClassNamer2(entity, ecp.getRootPackage(), ecp.getSubPackage(), ecp.getPrefix(), ecp.getSuffix()));
                }
            }
        }
    }

    private void loadProjectSpis(Iterator<ProjectSpi> iterator) {
        while (iterator.hasNext()) {
            ProjectSpi spi = iterator.next();
            log.info("Load ProjectSpi: " + spi.velocityVar() + " => " + spi.getClass().getName());
            projectSpis.add(spi.getClass());
        }
    }

    private void loadEntitySpis(Iterator<EntitySpi> iterator) {
        while (iterator.hasNext()) {
            EntitySpi spi = iterator.next();
            log.info("Load EntitySpi: " + spi.velocityVar() + " => " + spi.getClass().getName());
            entitySpis.add(spi.getClass());
        }
    }

    private void loadAttributeSpis(Iterator<AttributeSpi> iterator) {
        while (iterator.hasNext()) {
            AttributeSpi spi = iterator.next();
            log.info("Load AttributeSpi: " + spi.velocityVar() + " => " + spi.getClass().getName());
            attributeSpis.add(spi.getClass());
        }
    }

    private void loadRelationSpis(Iterator<RelationSpi> iterator) {
        while (iterator.hasNext()) {
            RelationSpi spi = iterator.next();
            log.info("Load RelationSpi: " + spi.velocityVar() + " => " + spi.getClass().getName());
            relationSpis.add(spi.getClass());
        }
    }

    private void bindAllSpis() {
        for (Class<? extends ProjectSpi> projectSpiClass : projectSpis) {
            bindProjectSpi(config.getProject(), projectSpiClass);
        }

        for (Entity entity : config.getProject().getCurrentEntities()) {
            for (Class<? extends EntitySpi> entitySpiClass : entitySpis) {
                bindEntitySpi(entity, entitySpiClass);
            }

            for (Attribute attribute : entity.getCurrentAttributes()) {
                for (Class<? extends AttributeSpi> attributeSpiClass : attributeSpis) {
                    bindAttributeSpi(attribute, attributeSpiClass);
                }
            }

            for (Relation relation : entity.getRelations().getList()) {
                for (Class<? extends RelationSpi> relationSpiClass : relationSpis) {
                    bindRelationSpi(relation, relationSpiClass);
                }
            }
        }
    }

    private void bindProjectSpi(Project project, Class<? extends ProjectSpi> spiClass) {
        try {
            ProjectSpi spi = spiClass.newInstance();
            spi.init(project);
            project.put(spi.velocityVar(), spi.getTarget());
        } catch (Exception e) {
            log.error("Could not instantiate ProjectSpi", e);
        }
    }

    private void bindEntitySpi(Entity entity, Class<? extends EntitySpi> spiClass) {
        try {
            EntitySpi spi = spiClass.newInstance();
            spi.init(entity);
            entity.put(spi.velocityVar(), spi.getTarget());
        } catch (Exception e) {
            log.error("Could not instantiate EntitySpi", e);
        }
    }

    private void bindAttributeSpi(Attribute attribute, Class<? extends AttributeSpi> spiClass) {
        try {
            AttributeSpi spi = spiClass.newInstance();
            spi.init(attribute);
            attribute.put(spi.velocityVar(), spi.getTarget());
        } catch (Exception e) {
            log.error("Could not instantiate AttributeSpi", e);
        }
    }

    private void bindRelationSpi(Relation relation, Class<? extends RelationSpi> spiClass) {
        try {
            RelationSpi spi = spiClass.newInstance();
            if (spi.compatibleWith(relation)) {
                spi.init(relation);
                ((AbstractRelation) relation).put(spi.velocityVar(), spi.getTarget());
            }
        } catch (Exception e) {
            log.error("Could not instantiate RelationSpi", e);
        }
    }
}