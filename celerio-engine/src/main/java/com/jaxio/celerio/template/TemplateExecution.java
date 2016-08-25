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

import com.jaxio.celerio.Config;
import com.jaxio.celerio.configuration.HeaderComment;
import com.jaxio.celerio.configuration.Module;
import com.jaxio.celerio.convention.CommentStyle;
import com.jaxio.celerio.convention.GeneratedPackage;
import com.jaxio.celerio.convention.WellKnownFolder;
import com.jaxio.celerio.model.Attribute;
import com.jaxio.celerio.model.support.ClassNamer;
import com.jaxio.celerio.model.support.ClassNamer2;
import com.jaxio.celerio.model.support.EnumNamer;
import com.jaxio.celerio.model.support.PackageImport;
import com.jaxio.celerio.output.OutputResult;
import com.jaxio.celerio.support.Namer;
import com.jaxio.celerio.template.pack.Template;
import com.jaxio.celerio.template.pack.TemplatePack;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.jaxio.celerio.convention.WellKnownFolder.*;
import static java.lang.Boolean.TRUE;
import static org.apache.commons.lang.StringUtils.*;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;


/**
 * The TemplateExecution is used from the template file (<code>$output</code> var) to:
 * <ul>
 * <li>configure where to write the evaluated template</li>
 * <li>manage java imports</li>
 * <li>abort the generation if needed</li>
 * </ul>
 */
@Slf4j
@Component
@Scope(SCOPE_PROTOTYPE)
public class TemplateExecution {

    private static final String DEFAULT_BASE_FOLDER = ""; // means '.'
    private static String JAVA_FILE_EXTENSION = ".java";
    private static String XML_FILE_EXTENSION = ".xml";

    @Autowired
    @Setter
    private PreviousEngine previousEngine;
    @Autowired
    private Config config;
    @Getter
    private String module;

    private TemplatePack templatePack;
    private Template template;

    // ------------------------------------------------------------
    // public methods below are called from templates to set
    // the target folder and filename
    // ------------------------------------------------------------

    // ---------------------------------
    // JAVA
    // ---------------------------------

    public String java(GeneratedPackage generatedPackage, String javaClassName) {
        generatedPackageRestriction(generatedPackage);
        return java(generatedPackage.getPackageName(), javaClassName);
    }

    public String java(EnumNamer enumNamer) {
        return java(enumNamer.getPackageName(), enumNamer.getType());
    }

    public String java(ClassNamer classNamer) {
        classTypeRestriction(classNamer);
        return java(classNamer.getPackageName(), classNamer.getType());
    }

    private String classTypeRestriction(ClassNamer classNamer) {
        return stopFileGeneration(!config.getCelerio().getConfiguration().getRestriction().canGenerate(classNamer.getClassType()));
    }

    private String wellKnownFolderRestriction(WellKnownFolder wellKnownFolder) {
        return stopFileGeneration(!config.getCelerio().getConfiguration().getRestriction().canGenerate(wellKnownFolder));
    }

    private String generatedPackageRestriction(GeneratedPackage generatedPackage) {
        return stopFileGeneration(!config.getCelerio().getConfiguration().getRestriction().canGenerate(generatedPackage));
    }

    public String java(ClassNamer classNamer, String javaClassName) {
        classTypeRestriction(classNamer);
        return java(classNamer.getPackageName(), javaClassName);
    }

    public String java(String javaPackageOrFolder, String javaClassName) {
        String filename = javaClassName;
        if (javaClassName.indexOf('.') == -1) {
            filename = filename + JAVA_FILE_EXTENSION;
        }
        return setCurrentFilename(javaPackageOrFolder.replace('.', '/'), filename, JAVA);
    }

    public String java(ClassNamer2 classNamer2) {
        return java(classNamer2.getPackageName(), classNamer2.getType());
    }

    public String java(ClassNamer2 classNamer2, String javaClassName) {
        return java(classNamer2.getPackageName(), javaClassName);
    }

    // ---------------------------------
    // JAVA TEST
    // ---------------------------------

    public String javaTest(GeneratedPackage generatedPackage, String javaClassName) {
        generatedPackageRestriction(generatedPackage);
        return javaTest(generatedPackage.getPackageName(), javaClassName);
    }

    public String javaTest(ClassNamer classNamer) {
        classTypeRestriction(classNamer);
        return javaTest(classNamer.getPackageName(), classNamer.getTestType());
    }

    public String javaInTest(ClassNamer classNamer) {
        classTypeRestriction(classNamer);
        return javaTest(classNamer.getPackageName(), classNamer.getType());
    }

    public String javaTest(ClassNamer classNamer, String javaClassName) {
        classTypeRestriction(classNamer);
        return javaTest(classNamer.getPackageName(), javaClassName);
    }

    public String javaTest(String javaPackageOrFolder, String javaClassName) {
        return setCurrentFilename(javaPackageOrFolder.replace('.', '/'), javaClassName + JAVA_FILE_EXTENSION, JAVA_TEST);
    }

    // Support for ClassNamer2 (defined in configuration)
    public void javaTest(ClassNamer2 classNamer2) {
        javaTest(classNamer2.getPackageName(), classNamer2.getType());
    }

    // ---------------------------------
    // Others
    // ---------------------------------

    public String config(String subFolder, String fileName) {
        return setCurrentFilename(subFolder, fileName, CONFIG);
    }

    public String resource(String resourceName) {
        return setCurrentFilename(resourceName, RESOURCES);
    }

    public String resource(String subFolder, String resourceName) {
        return setCurrentFilename(subFolder, resourceName, RESOURCES);
    }

    public String resource(GeneratedPackage generatedPackage, String resourceName) {
        return resource(generatedPackage.getPackagePath(), resourceName);
    }

    public String localization(String resourceName) {
        return setCurrentFilename(DEFAULT_BASE_FOLDER, resourceName, LOCALIZATION);
    }

    public String localization(String subFolder, String resourceName) {
        return setCurrentFilename(subFolder, resourceName, LOCALIZATION);
    }

    public String domainLocalization(String resourceName) {
        return setCurrentFilename(DEFAULT_BASE_FOLDER, resourceName, DOMAIN_LOCALIZATION);
    }

    public String domainLocalization(String subFolder, String resourceName) {
        return setCurrentFilename(subFolder, resourceName, DOMAIN_LOCALIZATION);
    }

    public String resourceTest(String resourceName) {
        return setCurrentFilename("", resourceName, RESOURCES_TEST);
    }

    public String resourceTest(String subFolder, String resourceName) {
        return setCurrentFilename(subFolder, resourceName, RESOURCES_TEST);
    }

    public String resourceTest(GeneratedPackage generatedPackage, String resourceName) {
        return resourceTest(generatedPackage.getPackagePath(), resourceName);
    }

    public String sql(String sqlFilename) {
        return setCurrentFilename(sqlFilename, SQL);
    }

    public String sql(String subFolder, String sqlFilename) {
        return setCurrentFilename(subFolder, sqlFilename, SQL);
    }

    public String spring(String springFilename) {
        return setCurrentFilename(springFilename, SPRING);
    }

    public String spring(String subFolder, String springFilename) {
        return setCurrentFilename(subFolder, springFilename, SPRING);
    }

    public String springTest(String springFilename) {
        return setCurrentFilename(springFilename, SPRING_TEST);
    }

    public String webapp(String filename) {
        return setCurrentFilename(filename, WEBAPP);
    }

    public String webapp(String subFolder, String filename) {
        return setCurrentFilename(subFolder, filename, WEBAPP);
    }

    public String flow(String subFolder, String filename) {
        return setCurrentFilename(subFolder, filename, FLOWS);
    }

    public String webinf(String filename) {
        return setCurrentFilename(filename, WEBINF);
    }

    public String webinf(String filePath, String filename) {
        return setCurrentFilename(filePath, filename, WEBINF);
    }

    public String view(String filename) {
        return setCurrentFilename(filename, VIEWS);
    }

    public String view(String filePath, String filename) {
        return setCurrentFilename(filePath, filename, VIEWS);
    }

    public String file(String filename) {
        return setCurrentFilename(DEFAULT_BASE_FOLDER, filename, DEFAULT_BASE_FOLDER, DEFAULT_BASE_FOLDER);
    }

    public String file(String filePath, String filename) {
        return setCurrentFilename(filePath, filename, DEFAULT_BASE_FOLDER, DEFAULT_BASE_FOLDER);
    }

    public String site(String filename) {
        return setCurrentFilename(filename, WellKnownFolder.SITE);
    }

    public String site(String filePath, String filename) {
        return setCurrentFilename(filePath, filename, WellKnownFolder.SITE);
    }

    private String setCurrentFilename(String filename, WellKnownFolder output) {
        wellKnownFolderRestriction(output);
        return setCurrentFilename(DEFAULT_BASE_FOLDER, filename, output.getFolder(), output.getGeneratedFolder());
    }

    private String setCurrentFilename(String folder, String filename, WellKnownFolder output) {
        wellKnownFolderRestriction(output);
        return setCurrentFilename(folder, filename, output.getFolder(), output.getGeneratedFolder());
    }

    private String setCurrentFilename(String folder, String filename, String userSpaceFolder, String generatedFolder) {
        try {
            previousEngine.setCurrentFilename(folder, filename, userSpaceFolder, generatedFolder);
        } catch (Exception e) {
            throw new RuntimeException("ouch", e);
        }
        if (filename.endsWith(JAVA_FILE_EXTENSION)) {
            return "" //
                    + getHeaderComment() + "package " + folder.replace("/", ".") + ";\n" //
                    + enableDynaImports();
        }
        if (filename.endsWith(XML_FILE_EXTENSION)) {
            return "" //
                    + "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" //
                    + chop(getHeaderComment() + "\n");
        }
        return getHeaderComment();
    }

    public String getHeaderComment() {
        return getHeaderComment("");
    }

    public String getHeaderComment(String prepend) {
        String currentFilename = previousEngine.getCurrentFullFilename();
        if (!StringUtils.hasLength(currentFilename)) {
            throw new IllegalStateException("You cannot set a header before setting the output filename");
        }
        HeaderComment headerComment = config.getCelerio().getConfiguration().getHeaderComment();
        if (!headerComment.getInclude()) {
            return "";
        }
        CommentStyle commentStyle = CommentStyle.fromFilename(currentFilename);
        if (commentStyle == null) {
            return "";
        }
        List<String> comments = newArrayList(headerComment.getComments());
        if (TRUE == headerComment.getShowTemplateName() || config.getCelerio().getConfiguration().getRootPackage().startsWith("integration.test.")) {
            comments.add("Template " + templatePack.getName() + ":" + template.getName().replace('\\', '/'));
            if (template.hasProjectLink()) {
                comments.add("Template is part of Open Source Project: " + template.getTemplatePackInfo().getProjectLink());
            }
        }
        return commentStyle.decorate(comments, prepend);
    }

    // ----------------------------------------------------
    // Module support
    // ----------------------------------------------------

    public void module(String module) {
        this.module = module;
    }

    // ---------------------------------------------------------------
    // Called from template, the filename is supposed be already set.
    // ---------------------------------------------------------------

    public String getCurrentClass() {
        return previousEngine.getCurrentClass();
    }

    public String getCurrentRootClass() {
        return previousEngine.getCurrentRootClass();
    }

    public String getCurrentRootCast() {
        return previousEngine.getCurrentRootCast();
    }

    // velocity does not work with varargs
    public String getCurrentRootCast(String generic1) {
        return previousEngine.getCurrentRootCast(generic1);
    }

    public String getCurrentRootCast(String generic1, String generic2) {
        return previousEngine.getCurrentRootCast(generic1, generic2);
    }

    public String getCurrentRootCast(String generic1, String generic2, String generic3) {
        return previousEngine.getCurrentRootCast(generic1, generic2, generic3);
    }

    public String getCurrentClassWithout_() {
        return previousEngine.getCurrentClassWithout_();
    }


    /**
     * Whether the current class is extended by the user using take over feature ?
     */
    public boolean isAbstract() {
        return ImportsContext.isExtendedByUser();
    }

    public String getAbstractSpace() {
        return isAbstract() ? "abstract " : "";
    }

    // ----------------------------------------------------
    // Dynamic package import manager
    // ----------------------------------------------------
    public String enableDynaImports() {
        return previousEngine.enableDynaImports();
    }

    public boolean isDynamicImportEnabled() {
        return previousEngine.isCurrentEnableDynamicImport();
    }

    /**
     * Import the passed fullType.
     */
    public void require(String fullType) {
        requireFirstTime(fullType);
    }

    /**
     * Import the passed namer's fullType.
     */
    public void require(Namer namer) {
        require(namer.getFullType());
    }

    /**
     * Import the passed namer's fullType corresponding metamodel (i.e. appends '_');
     */
    public void requireMetamodel(Namer namer) {
        require(namer.getFullType() + "_");
    }

    /**
     * Import the passed simpleClassName present in the passed generatedPackage.
     */
    public void require(GeneratedPackage generatedPackage, String simpleClassName) {
        requireFirstTime(generatedPackage.getPackageName() + "." + simpleClassName);
    }

    /**
     * Import the passed classNamer's type present in the passed packageNamer's package name.
     */
    public void require(Namer packageNamer, Namer classNamer) {
        requireFirstTime(packageNamer.getPackageName() + "." + classNamer.getType());
    }

    /**
     * Import all the packageImports.
     */
    public void require(List<PackageImport> packageImports) {
        ImportsContext.getCurrentImportsHolder().addImports(packageImports);
    }

    /**
     * Import statically the passed fullType.
     */
    public void requireStatic(String fullType) {
        requireFirstTime("static " + fullType);
    }

    /**
     * Import statically the passed simpleClassName present in the passed generatedPackage.
     */
    public void requireStatic(GeneratedPackage generatedPackage, String simpleClassName) {
        requireStatic(generatedPackage.getPackageName() + "." + simpleClassName);
    }

    /**
     * Import the passed fullType.
     *
     * @return true if the passed fullType is imported for the first time.
     */
    public boolean requireFirstTime(String fullType) {
        return ImportsContext.getCurrentImportsHolder().add(fullType);
    }

    /**
     * Import the passed namer's fullType.
     *
     * @return true if the passed namer's fullType is imported for the first time.
     */
    public boolean requireFirstTime(Namer namer) {
        return requireFirstTime(namer.getFullType());
    }

    /**
     * Import the passed attribute type.
     */
    public void require(Attribute attribute) {
        requireFirstTime(attribute);
    }

    /**
     * Import the passed attribute type.
     *
     * @return true if the passed attribute type is imported for the first time.
     */
    public boolean requireFirstTime(Attribute attribute) {
        if (!attribute.isJavaBaseClass()) {
            return ImportsContext.getCurrentImportsHolder().add(attribute.getFullType());
        }
        return false;
    }

    /**
     * Import the passed fullType and returns the corresponding annotation.
     * For example passing <code>com.comp.MyAnno</code> returns <code>@MyAno</code>
     * and imports <code>com.comp.MyAnno</code>.
     */
    public String dynamicAnnotation(String fullType) {
        return dynamicAnnotationSupport(fullType, true, false);
    }

    protected String dynamicAnnotationSupport(String fullType, boolean invokeRequire, boolean appendEol) {
        String eol = appendEol ? "\n" : "";
        if (fullType.contains("(")) {
            String fullTypeToImport = substringBefore(fullType, "(");
            String body = "(" + substringAfter(fullType, "(");
            if (invokeRequire) {
                require(fullTypeToImport);
            }
            if (fullTypeToImport.contains(".")) {
                return "@" + substringAfterLast(fullTypeToImport, ".") + body + eol;
            } else {
                return "@" + fullTypeToImport + body + eol;
            }
        } else {
            if (invokeRequire) {
                require(fullType);
            }
            if (fullType.contains(".")) {
                return "@" + substringAfterLast(fullType, ".") + eol;
            } else {
                return "@" + fullType + eol;
            }
        }
    }

    /**
     * Import the passed fullTypes and returns the corresponding annotations (one per line).
     * In case the current class is taken over by the user, the fullTypes are not imported
     * and each annotation line returned is commented out. A comment informs the java developer
     * that he should use these commented annotations in his subclass.
     */
    public String dynamicAnnotationTakeOver(String... fullTypes) {
        StringBuilder sb = new StringBuilder();
        if (!isAbstract()) {
            for (String fullType : fullTypes) {
                sb.append(dynamicAnnotationSupport(fullType, true, true));
            }
        } else {
            if (fullTypes.length > 1) {
                sb.append("// Make sure you use these " + fullTypes.length + " annotations in your subclass").append("\n");
            } else {
                sb.append("// Make sure you use this annotation in your subclass").append("\n");
            }

            for (String fullType : fullTypes) {
                sb.append("// ").append("@" + fullType).append("\n");
            }
        }
        return sb.toString();
    }


    // ----------------------------------------------------
    // Control generation
    // ----------------------------------------------------

    public String generateIf(Module module) {
        return generateIf(config.getCelerio().getConfiguration().has(module));
    }

    public String generateIf(boolean value) {
        return stopFileGeneration(!value);
    }

    public String generateIf(boolean value1, boolean value2) {
        return stopFileGeneration(!(value1 && value2));
    }

    public String skipIf(Module module) {
        return stopFileGeneration(config.getCelerio().getConfiguration().has(module));
    }

    public String skipIf(boolean value) {
        return stopFileGeneration(value);
    }

    public String stopFileGeneration(boolean stop) {
        if (stop) {
            stopFileGeneration();
        }
        return "";
    }

    public String stopFileGeneration() {
        previousEngine.stopFileGeneration();
        return "";
    }

    protected void write(OutputResult outputResult, Map<String, Object> context, TemplatePack templatePack, Template template) throws Exception {
        this.templatePack = templatePack;
        this.template = template;
        previousEngine.setOutputResult(outputResult);
        previousEngine.processDynamicFile(context, templatePack, template);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public void createMetaModelTakeOver(ClassNamer entityModel, String content) {
        try {
            // we create automatically the take over only if the user has not himself for whatever reason 
            // taken over the meta model. BDF reported it as EVOSOCLE-283
            String potentialUserCreatedMetaModelPath = JAVA.getFolder() + "/" + entityModel.getPath() + "/" + entityModel.getType() + "_.java";

            if (new File(previousEngine.getOutputResult().getUserSource().getFullPath(potentialUserCreatedMetaModelPath)).exists()) {
                log.info("Skip automatic take over for metamodel " + entityModel.getType() + " since it was done by the developer");
                return;
            }

            String filePath = JAVA.getGeneratedFolder() + "/" + entityModel.getPath() + "/" + entityModel.getType() + "_.java";
            filePath = filePath.replace("/", "" + File.separatorChar);
            previousEngine.getOutputResult().addContent(content.getBytes("UTF-8"), filePath, null, null);
            log.info("Automatic take over for metamodel " + entityModel.getType() + " to be in sync with entity taken over.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
