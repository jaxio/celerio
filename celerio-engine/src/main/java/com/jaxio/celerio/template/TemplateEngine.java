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

package com.jaxio.celerio.template;

import com.google.common.base.Predicate;
import com.jaxio.celerio.Brand;
import com.jaxio.celerio.Config;
import com.jaxio.celerio.configuration.Module;
import com.jaxio.celerio.configuration.Pack;
import com.jaxio.celerio.configuration.Pattern;
import com.jaxio.celerio.convention.ClassType;
import com.jaxio.celerio.convention.GeneratedPackage;
import com.jaxio.celerio.convention.MethodConvention;
import com.jaxio.celerio.convention.WellKnownFolder;
import com.jaxio.celerio.model.Attribute;
import com.jaxio.celerio.model.Entity;
import com.jaxio.celerio.model.EnumType;
import com.jaxio.celerio.model.Project;
import com.jaxio.celerio.template.pack.PackLoader;
import com.jaxio.celerio.template.pack.Template;
import com.jaxio.celerio.template.pack.TemplatePack;
import com.jaxio.celerio.util.BrandUtil;
import com.jaxio.celerio.util.IdentifiableProperty;
import com.jaxio.celerio.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.reverse;
import static com.google.common.collect.Maps.newHashMap;

@Service
@Slf4j
public class TemplateEngine {
    private static final String DASH_LINE = "-----------------------------------------------------------------------";

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Config config;

    @Autowired
    private ContentWriter contentWriter;

    @Autowired
    private PackLoader packLoader;

    private Map<String, Object> getGlobalContext() {
        Map<String, Object> context = newHashMap();
        // static values
        context.put("dollar", "$");
        context.put("d", "$");
        context.put("pound", "#");
        context.put("p", "#");
        context.put("serialVersionUID", "    static final private long serialVersionUID = " + 1 + "L;");
        // global context values
        context.put("brand", new Brand());
        context.put("brandUtil", new BrandUtil(config.getOutputResult()));
        context.put("config", config);
        context.put("configuration", config.getCelerio().getConfiguration());
        context.put("generation", config.getCelerio().getConfiguration().getGeneration());
        context.put("databaseInfo", config.getMetadata().getDatabaseInfo());
        context.put("jdbcConnectivity", config.getMetadata().getJdbcConnectivity());
        context.put("project", config.getProject());
        context.put("metadata", config.getOriginalMetadata());
        context.put("StringUtil", StringUtil.class);
        // enums
        ContextUtil.addEnumValues(context, ClassType.values());
        ContextUtil.addEnumValues(context, WellKnownFolder.values());
        ContextUtil.addEnumValues(context, GeneratedPackage.values());
        ContextUtil.addEnumValues(context, Module.values());
        ContextUtil.addEnumValues(context, MethodConvention.values());

        // identifiable
        context.put("identifiableProperty", new IdentifiableProperty(config.getCelerio().getConfiguration().getConventions().getIdentifiableProperty()));

        return context;
    }

    private String s(Collection<?> s) {
        return s.size() <= 1 ? "" : "s";
    }

    public void produce(Project project, boolean bootstrapOnly) throws Exception {

        if (config.getCelerio().getConfiguration().getModules().isEmpty()) {
            log.info("No active module");
        } else {
            log.info("Active modules");
            for (Module module : config.getCelerio().getConfiguration().getModules()) {
                log.info(" . " + module);
            }
        }

        List<TemplatePack> templatePacks = packLoader.getTemplatePacks();
        if (log.isInfoEnabled()) {
            log.info(DASH_LINE);
            log.info("Working with " + templatePacks.size() + " templates pack" + s(templatePacks) + ".");
            for (TemplatePack templatePack : templatePacks) {
                log.info(". " + templatePack.getName() + " (" + templatePack.getTemplateNames().size() + " resource" + s(templatePack.getTemplateNames()) + ")");

                Pack configPack = config.getCelerio().getConfiguration().getPackByName(templatePack.getName());
                if (configPack != null) {
                    log.info("        Configuration found");
                    for (Pattern p : configPack.getTemplates()) {
                        log.info("        - include: " + p.isInclude() + ", template pattern: " + p.getPattern());
                    }
                    for (Pattern p : configPack.getFilenames()) {
                        log.info("        - include: " + p.isInclude() + ", filename pattern: " + p.getPattern());
                    }
                }
            }
        }

        config.getOutputResult().open();

        // producing bootstrap first
        List<TemplatePack> packReversed = reverse(templatePacks);
        for (TemplatePack templatePack : packReversed) {
            if (getTemplateNames(templatePack, TemplateType.bootstrap).isEmpty()) {
                continue;
            }

            log.info(DASH_LINE);
            log.info("Template Pack (bootstrap) " + templatePack.getName());
            produceBootstrap(project, templatePack);
            break;
        }

        if (bootstrapOnly) {
            return;
        }

        for (TemplatePack templatePack : templatePacks) {
            log.info(DASH_LINE);
            log.info("Template Pack " + templatePack.getName());

            produceProject(project, templatePack);
            produceEntities(project, templatePack);
            produceCompositePk(project, templatePack);
            produceAttributes(project, templatePack);
            produceEnums(project, templatePack);
            produceStaticTemplates(templatePack);
            produceCelerioExampleTemplates(templatePack);
        }

        config.getOutputResult().close();
    }

    private void produceStaticTemplates(TemplatePack templatePack) throws Exception {
        for (String staticTemplateName : getStaticTemplateNames(templatePack)) {
            Template template = templatePack.getTemplateByName(staticTemplateName);

            // filter
            if (!config.getCelerio().getConfiguration().hasFilename(templatePack.getName(), template.getName())) {
                if (log.isDebugEnabled()) {
                    log.debug("SKIPPING:" + template.getName());
                }
                continue;
            }

            contentWriter.processFile(config.getOutputResult(), templatePack, template, template.getBytes(), template.getName());
        }
    }

    private void produceCelerioExampleTemplates(TemplatePack templatePack) throws Exception {
        for (String staticTemplateName : getCelerioExampleTemplateNames(templatePack)) {
            Template template = templatePack.getTemplateByName(staticTemplateName);

            String physicalTargetFilename = template.getName().replace("celerio-example", "celerio");
            // filter
            if (!config.getCelerio().getConfiguration().hasFilename(templatePack.getName(), physicalTargetFilename)) {
                log.debug("SKIPPING:" + physicalTargetFilename);
                continue;
            }

            contentWriter.processFile(config.getOutputResult(), templatePack, template, template.getBytes(), physicalTargetFilename);
        }
    }

    private void produceProject(Project project, TemplatePack templatePack) throws Exception, IOException {
        for (String templateName : getTemplateNames(templatePack, TemplateType.project)) {
            produce(getGlobalContext(), templatePack, templateName);
        }
    }

    private void produceBootstrap(Project project, TemplatePack templatePack) throws Exception, IOException {
        for (String templateName : getTemplateNames(templatePack, TemplateType.bootstrap)) {
            produce(getGlobalContext(), templatePack, templateName);
        }
    }

    private void produceEntities(Project project, TemplatePack templatePack) throws Exception, IOException {
        for (String templateName : getTemplateNames(templatePack, TemplateType.entity)) {
            for (Entity entity : project.getCurrentEntities()) {
                if (entity.isManyToManyJoinEntity() || entity.isSkip()) {
                    continue;
                }
                Map<String, Object> context = getGlobalContext();
                context.put("entity", entity);
                context.put("primaryKey", entity.getPrimaryKey());
                produce(context, templatePack, templateName);
            }
        }
    }

    private void produceCompositePk(Project project, TemplatePack templatePack) throws Exception, IOException {
        for (String templateName : getTemplateNames(templatePack, TemplateType.compositePrimaryKey)) {
            for (Entity entity : project.getRootEntities().getList()) {
                if (entity.hasCompositePk() && !entity.isManyToManyJoinEntity() && !entity.isSkip()) {
                    Map<String, Object> context = getGlobalContext();
                    context.put("entity", entity);
                    context.put("primaryKey", entity.getPrimaryKey());
                    produce(context, templatePack, templateName);
                }
            }
        }
    }

    private void produceEnums(Project project, TemplatePack templatePack) throws Exception, IOException {
        for (String templateName : getTemplateNames(templatePack, TemplateType.enumeration)) {
            for (EnumType enumType : project.getEnumTypes()) {
                Map<String, Object> context = getGlobalContext();
                context.put("enum", enumType);
                produce(context, templatePack, templateName);
            }
        }
    }

    private void produceAttributes(Project project, TemplatePack templatePack) throws Exception, IOException {
        for (String templateName : getTemplateNames(templatePack, TemplateType.attribute)) {
            for (Entity entity : project.getCurrentEntities()) {
                for (Attribute attribute : entity.getCurrentAttributes()) {
                    Map<String, Object> context = getGlobalContext();
                    context.put("entity", entity);
                    context.put("attribute", attribute);
                    produce(context, templatePack, templateName);
                }
            }
        }
    }

    private void produce(Map<String, Object> context, TemplatePack templatePack, String templateName) throws Exception {
        Template template = templatePack.getTemplateByName(templateName);
        context.put("pack", templatePack.getTemplatePackInfo());
        TemplateExecution execution = applicationContext.getBean("templateExecution", TemplateExecution.class);
        context.put("output", execution);
        execution.write(config.getOutputResult(), context, templatePack, template);
    }

    private List<String> getTemplateNames(TemplatePack templatePack, final TemplateType templateType) {
        Predicate<String> matcher = and(new VelocityNameMatcher(templateType), new TemplateMatcher(templatePack));
        matcher = and(matcher, not(new CelerioTemplateExampleMatcher()));
        return newArrayList(filter(templatePack.getTemplateNames(), matcher));
    }

    private List<String> getStaticTemplateNames(TemplatePack templatePack) {
        Predicate<String> matcher = and(new TemplateMatcher(templatePack), not(new CelerioTemplateExampleMatcher()));
        for (TemplateType templateType : TemplateType.values()) {
            matcher = and(matcher, not(new VelocityNameMatcher(templateType)));
        }
        return newArrayList(filter(templatePack.getTemplateNames(), matcher));
    }

    private List<String> getCelerioExampleTemplateNames(TemplatePack templatePack) {
        Predicate<String> matcher = and(new TemplateMatcher(templatePack), new CelerioTemplateExampleMatcher());
        return newArrayList(filter(templatePack.getTemplateNames(), matcher));
    }

    public enum TemplateType {
        bootstrap("boot"), //
        project("p"), //
        entity("e"), //
        attribute("a"), //
        enumeration("enum"), //
        compositePrimaryKey("cpk");
        private String pattern;

        TemplateType(String pattern) {
            this.pattern = pattern;
        }

        public String getVelocityPattern() {
            return "." + pattern + ".vm";
        }
    }

    private class VelocityNameMatcher implements Predicate<String> {
        private TemplateType type;

        public VelocityNameMatcher(TemplateType type) {
            this.type = type;
        }

        @Override
        public boolean apply(String input) {
            return input.indexOf(type.getVelocityPattern()) != -1;
        }
    }

    private class TemplateMatcher implements Predicate<String> {
        private TemplatePack templatePack;

        public TemplateMatcher(TemplatePack templatePack) {
            this.templatePack = templatePack;
        }

        @Override
        public boolean apply(String templateName) {
            if (!config.getCelerio().getConfiguration().hasTemplate(templatePack.getName(), templateName)) {
                // log.debug("Skipping template '" + templateName + "'. Reason: requested in template pattern");
                return false;
            }
            return true;
        }
    }

    // celerio templates that should be copied as is...
    private class CelerioTemplateExampleMatcher implements Predicate<String> {
        @Override
        public boolean apply(String relativePathAndTemplateName) {
            return relativePathAndTemplateName.contains("src\\main\\celerio-example") || relativePathAndTemplateName.startsWith("src/main/celerio-example");
        }
    }
}
