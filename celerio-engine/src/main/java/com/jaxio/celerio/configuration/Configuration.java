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

package com.jaxio.celerio.configuration;

import com.jaxio.celerio.configuration.convention.Conventions;
import com.jaxio.celerio.configuration.entity.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.jaxio.celerio.configuration.Module.COPYABLE;
import static com.jaxio.celerio.configuration.Module.SPRING_MVC_3;
import static com.jaxio.celerio.configuration.Pattern.hasPattern;
import static com.jaxio.celerio.configuration.Util.firstNonNull;
import static com.jaxio.celerio.configuration.Util.nonNull;
import static com.jaxio.celerio.configuration.entity.AssociationDirection.UNIDIRECTIONAL;
import static org.apache.commons.lang.WordUtils.capitalize;

public class Configuration {
    protected CelerioTemplateContext celerioTemplateContext = new CelerioTemplateContext();
    protected List<Pack> packs = newArrayList();
    protected List<Module> modules = newArrayList();
    protected List<String> customModules = newArrayList();
    protected List<Pattern> filenames = newArrayList();
    protected List<Pattern> templates = newArrayList();
    protected List<Pattern> tables = newArrayList();
    protected List<SequencePattern> sequences = newArrayList();
    protected List<NumberMapping> numberMappings = newArrayList();
    protected List<DateMapping> dateMappings = newArrayList();
    protected CacheConfig defaultEntityCacheConfig;
    protected ManyToOneConfig defaultManyToOneConfig;
    protected OneToManyConfig defaultOneToManyConfig;
    protected OneToOneConfig defaultOneToOneConfig;
    protected OneToOneConfig defaultInverseOneToOneConfig;
    protected ManyToManyConfig defaultManyToManyConfig;
    protected ManyToManyConfig defaultInverseManyToManyConfig;
    protected Conventions conventions = new Conventions();
    protected Generation generation = new Generation();
    protected Restriction restriction = new Restriction();
    protected AssociationDirection associationDirection = UNIDIRECTIONAL;
    private Boolean enableOneToVirtualOne = false;

    @Getter
    @Setter
    private TrueFalse jpaUseCatalog = TrueFalse.FALSE;
    @Getter
    @Setter
    private TrueFalse jpaUseSchema = TrueFalse.FALSE;

    protected String applicationName = "application";
    @Setter
    protected String rootPackage;
    protected List<MetaAttribute> metaAttributes = newArrayList();
    protected HeaderComment headerComment = new HeaderComment();

    public void setCelerioTemplateContext(CelerioTemplateContext velocityContext) {
        this.celerioTemplateContext = velocityContext;
    }

    /*
     * Entry point to extend Celerio engine's Velocity context. Only needed if you develop new Celerio templates.
     */
    public CelerioTemplateContext getCelerioTemplateContext() {
        return celerioTemplateContext;
    }

    /*
     * List of template packs to execute during the generation. Defaults to the template packs found in the classpath.
     */
    public List<Pack> getPacks() {
        return packs;
    }

    public void setPacks(List<Pack> packs) {
        this.packs = nonNull(packs);
    }

    public void addPack(Pack pack) {
        packs.add(pack);
    }

    public boolean isPackEnabled(String packName) {
        for (Pack pack : packs) {
            if (pack.nameMatch(packName)) {
                return pack.isEnable();
            }
        }
        return true;
    }

    public Pack getPackByName(String name) {
        for (Pack pack : packs) {
            if (pack.nameMatch(name)) {
                return pack;
            }
        }
        return null;
    }

    public boolean hasPack(String name) {
        return getPackByName(name) != null;
    }

    /*
     * List of modules enabled during the generation. Modules are cross cutting functionalities that span across packs.
     */
    public List<Module> getModules() {
        return modules;
    }

    public void setModules(List<Module> modules) {
        this.modules = nonNull(modules);
    }

    public boolean hasModule(Module module) {
        if (module == COPYABLE && hasModule(SPRING_MVC_3)) {
            return true;
        } else {
            return modules.contains(module) || customModules.contains(module.name());
        }
    }

    public boolean hasModule(String moduleName) {
        return hasModule(Module.valueOf(moduleName));
    }

    /*
     * List of custom modules enabled during the generation. Modules are cross cutting functionalities that span across packs.
     */
    public List<String> getCustomModules() {
        return customModules;
    }

    public void setCustomModules(List<String> customModules) {
        this.customModules = nonNull(customModules);
    }

    /*
     * Control the generation output by filtering the generated files based on their filename.
     */
    public List<Pattern> getFilenames() {
        return filenames;
    }

    public void setFilenames(List<Pattern> filenames) {
        this.filenames = nonNull(filenames);
    }

    public void addFilename(Pattern filenamePattern) {
        filenames.add(filenamePattern);
    }

    public boolean hasFilename(String value) {
        return hasPattern(getFilenames(), value);
    }

    public boolean hasFilename(String packName, String value) {
        if (!getFilenames().isEmpty()) {
            return hasFilename(value);
        } else if (isPackEnabled(packName) && hasPack(packName)) {
            Pack pack = getPackByName(packName);
            return pack.hasFilename(value);
        } else {
            return true;
        }
    }

    /*
     * Control the generation output by filtering the execution of the generation templates based on their filename.
     */
    public List<Pattern> getTemplates() {
        return templates;
    }

    public void setTemplates(List<Pattern> templates) {
        this.templates = nonNull(templates);
    }

    public void addTemplate(Pattern templatePattern) {
        templates.add(templatePattern);
    }

    private boolean hasTemplate(String value) {
        if (value.startsWith("/")) {
            value = "/" + value;
        }
        return hasPattern(getTemplates(), value);
    }

    public boolean hasTemplate(String packName, String templateName) {
        if (hasPack(packName) && isPackEnabled(packName)) {
            Pack pack = getPackByName(packName);
            if (pack.getTemplates().size() > 0) {
                return pack.hasTemplate(templateName);
            }
        }

        if (getTemplates().size() > 0) {
            return hasTemplate(templateName);
        } else {
            return true;
        }
    }

    /*
     * Filter the tables you want to be generated
     */
    public List<Pattern> getTables() {
        return tables;
    }

    public void setTables(List<Pattern> tables) {
        this.tables = nonNull(tables);
    }

    public boolean hasTable(String value) {
        return hasPattern(getTables(), value);
    }

    /*
     * Defines sequence names based on table name
     */
    public List<SequencePattern> getSequences() {
        return sequences;
    }

    public void setSequences(List<SequencePattern> sequences) {
        this.sequences = nonNull(sequences);
    }

    /*
     * The list of number mappings. The first match is used. If no match is found, convention applies.
     */
    public List<NumberMapping> getNumberMappings() {
        return numberMappings;
    }

    public void setNumberMappings(List<NumberMapping> numberMappings) {
        this.numberMappings = nonNull(numberMappings);
    }

    /*
     * The list of date mappings. The first match is used. If no match is found, convention applies.
     */
    public List<DateMapping> getDateMappings() {
        return dateMappings;
    }

    public void setDateMappings(List<DateMapping> dateMappings) {
        this.dateMappings = nonNull(dateMappings);
    }

    // ---------------------------------
    // Default 2d level cache settings
    // ---------------------------------

    // entity
    public void setDefaultEntityCacheConfig(CacheConfig defaultEntityCacheConfig) {
        this.defaultEntityCacheConfig = defaultEntityCacheConfig;
    }

    /*
     * Default Entity 2d level cache configuration. Uses ehcache. To disable default cache annotation generation for entity having no CacheConfig element,
     * simply remove this element.
     */
    public CacheConfig getDefaultEntityCacheConfig() {
        return defaultEntityCacheConfig;
    }

    // many to one

    public void setDefaultManyToOneConfig(ManyToOneConfig defaultManyToOneConfig) {
        this.defaultManyToOneConfig = defaultManyToOneConfig;
    }

    /*
     * Default many-to-one configuration allowing you to configure FetchType, Cascade and Cache globally.
     */
    public ManyToOneConfig getDefaultManyToOneConfig() {
        return defaultManyToOneConfig;
    }

    // one to many

    public void setDefaultOneToManyConfig(OneToManyConfig defaultOneToManyConfig) {
        this.defaultOneToManyConfig = defaultOneToManyConfig;
    }

    /*
     * Default one-to-many configuration allowing you to configure FetchType, Cascade and Cache globally.
     */
    public OneToManyConfig getDefaultOneToManyConfig() {
        return defaultOneToManyConfig;
    }

    // one to one

    public void setDefaultOneToOneConfig(OneToOneConfig defaultOneToOneConfig) {
        this.defaultOneToOneConfig = defaultOneToOneConfig;
    }

    /*
     * Default one-to-one configuration allowing you to configure FetchType, Cascade and Cache globally.
     */
    public OneToOneConfig getDefaultOneToOneConfig() {
        return defaultOneToOneConfig;
    }

    // inverse one to one

    public void setDefaultInverseOneToOneConfig(OneToOneConfig defaultInverseOneToOneConfig) {
        this.defaultInverseOneToOneConfig = defaultInverseOneToOneConfig;
    }

    /*
     * Default inverse one-to-one configuration allowing you to configure FetchType, Cascade and Cache globally.
     */
    public OneToOneConfig getDefaultInverseOneToOneConfig() {
        return defaultInverseOneToOneConfig;
    }

    // many to many

    public void setDefaultManyToManyConfig(ManyToManyConfig defaultManyToManyConfig) {
        this.defaultManyToManyConfig = defaultManyToManyConfig;
    }

    /*
     * Default many-to-many configuration allowing you to configure FetchType, Cascade and Cache globally.
     */
    public ManyToManyConfig getDefaultManyToManyConfig() {
        return defaultManyToManyConfig;
    }

    // inverse many to many

    public void setDefaultInverseManyToManyConfig(ManyToManyConfig defaultInverseManyToManyConfig) {
        this.defaultInverseManyToManyConfig = defaultInverseManyToManyConfig;
    }

    /*
     * Default inverse many-to-many configuration allowing you to configure FetchType, Cascade and Cache globally.
     */
    public ManyToManyConfig getDefaultInverseManyToManyConfig() {
        return defaultInverseManyToManyConfig;
    }

    /*
     * Configure the java convention such as classnames, packages, methods
     */
    public Conventions getConventions() {
        return conventions;
    }

    public void setConventions(Conventions conventions) {
        this.conventions = firstNonNull(conventions, this.conventions);
    }

    /*
     * For future use
     */
    public List<MetaAttribute> getMetaAttributes() {
        return metaAttributes;
    }

    public void setMetaAttributes(List<MetaAttribute> metaAttributes) {
        this.metaAttributes = nonNull(metaAttributes);
    }

    /*
     * Miscellaneous generation configuration
     */
    public Generation getGeneration() {
        return generation;
    }

    public void setGeneration(Generation generation) {
        this.generation = firstNonNull(generation, this.generation);
    }

    /*
     * Choose the default association direction
     */
    public AssociationDirection getAssociationDirection() {
        return associationDirection;
    }

    public void setAssociationDirection(AssociationDirection associationDirection) {
        this.associationDirection = firstNonNull(associationDirection, this.associationDirection);
    }

    /*
     * Enable one to virtual one, which is a one to one that uses a Collection. This method allows for performance enhancement in hibernate using lazy loading.
     */
    public Boolean getEnableOneToVirtualOne() {
        return enableOneToVirtualOne;
    }

    /*
     * Specify the default application name that is used in the generated pom.xml. It should be one word, no space.<br>
     * Example: casino
     */
    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        if (StringUtils.hasLength(applicationName)) {
            this.applicationName = applicationName;
        }
    }

    /*
     * Specify the default root package for all the generated java code<br>
     * Example: com.mycompany
     */
    public String getRootPackage() {
        return rootPackage;
    }

    public boolean has(Module module) {
        return hasModule(module);
    }

    /*
     * The JDBC settings enabling Celerio to retrieve your database meta data.
     */
    public HeaderComment getHeaderComment() {
        return headerComment;
    }

    public void setHeaderComment(HeaderComment headerComment) {
        this.headerComment = firstNonNull(headerComment, this.headerComment);
    }

    /*
     * Restrict the generation to the given elements
     */
    public Restriction getRestriction() {
        return restriction;
    }

    public void setRestriction(Restriction restriction) {
        this.restriction = firstNonNull(restriction, this.restriction);
    }

    // ------------------------------------------------------------
    // Utils for Multi module conf
    // ------------------------------------------------------------

    public String toInitialVersion() {
        return "0.0.1-SNAPSHOT";
    }

    public String toApplicationNameUp() {
        return applicationName.toUpperCase();
    }

    public String toName(String shortName) {
        return applicationName.toUpperCase() + " - " + capitalize(shortName);
    }

    public String toModule(String moduleName) {
        return applicationName.toLowerCase() + "-" + moduleName.toLowerCase();
    }

    public String toModulePath(String module1) {
        return toModule(module1);
    }

    public String toModulePath(String module1, String module2) {
        return toModule(module1) + "/" + toModule(module2);
    }

    public String toModulePath(String module1, String module2, String module3) {
        return toModule(module1) + "/" + toModule(module2) + "/" + toModule(module3);
    }

    public String toArtifactId(String moduleShortName) {
        return toModule(moduleShortName);
    }
}