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
import com.jaxio.celerio.convention.WellKnownFolder;
import com.jaxio.celerio.output.EclipseCodeFormatter;
import com.jaxio.celerio.output.OutputResult;
import com.jaxio.celerio.output.SourceFile;
import com.jaxio.celerio.output.XmlCodeFormatter;
import com.jaxio.celerio.template.pack.Template;
import com.jaxio.celerio.template.pack.TemplatePack;
import com.jaxio.celerio.util.IOUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Map;

import static java.io.File.separatorChar;
import static java.util.regex.Pattern.*;
import static org.apache.commons.io.FilenameUtils.normalize;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.substringBeforeLast;
import static org.apache.velocity.util.StringUtils.normalizePath;

// -----------
// IMPLEMENTATION NOTE: 
//
// Do not get confused!
// userDomainPath | generatedDomainPath correspond for example to src/main/java | src/main/generated-java
// while userSource | generatedSource correspond to baseDir | outputDirectory
// 
// Files are generated in the path: outputDirectory/(userDomainPath or generatedDomainPath)
// But by default outputDirectory is equals to baseDir.
//
// In case outputDirectory and baseDir are different
// having generatedDomainPath different from userDomainPath is not needed as generated code is already clearly separated.
// That's why in such case we set generatedDomainPath to the same value as userDomainPath (see ProjectFactory code).
// -----------

@Service
@Slf4j
public class PreviousEngine {
    private final static String DYNA_IMPORTS_TAG = "__celerio_dyna_imports__";

    private final static String BASE_SUFFIX = "Base";
    private final static String BASE_SUFFIX_ = BASE_SUFFIX + "_";

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private EclipseCodeFormatter eclipseCodeFormatter;

    @Autowired
    private XmlCodeFormatter xmlCodeFormatter;

    @Autowired
    private IOUtil ioUtil;

    @Autowired
    private ContentWriter contentWriter;

    @Autowired
    private Config config;
    @Autowired
    private VelocityGenerator velocityGenerator;
    @Getter
    private String currentFullFilename = "";
    private String currentClass = "";
    private String currentRootClass = "";
    private String currentRootCast = "";

    @Getter
    private boolean currentEnableDynamicImport;
    private OutputResult outputResult;
    private SourceFile userSource;
    private SourceFile generatedSource;

    // ----------------------------------------------------------------------
    // Take over related (user extends the generated class + "Base")
    // ----------------------------------------------------------------------

    public String enableDynaImports() {
        currentEnableDynamicImport = true;
        return DYNA_IMPORTS_TAG;
    }

    /**
     * called within the velocity script.<br>
     * see $velocity.setJavaFilename(...)<br>
     * this allows us to generate dynamically the filename.
     *
     * @param relativePathOrPackage
     * @param filename
     * @param domain
     * @throws Exception
     */
    private String setCurrentFilename(String relativePathOrPackage, String filename, String domain) throws Exception {
        currentFullFilename = convertToFullFilename(relativePathOrPackage, filename, domain);
        return "";
    }

    /**
     * In your velocity template, call this method to set the path and filename of the generated file.
     * The prefix src/main/generated-java or target/.../src/main/java is added automatically.
     * If the file that already exists in the user space uses inheritance, the, it generates a XXBase class instead of XXX
     *
     * @param filePath            for example com.acme.myapp or com/acme/myapp
     * @param filename            for example for example MyClass.java or MyClassTest.java
     * @param userDomainPath      for example src/main/java or src/test/java
     * @param generatedDomainPath for example src/main/generated-java or src/test/generated-java
     * @throws IllegalArgumentException if file extension is not .java, .htm or .html
     */
    public String setCurrentFilename(String filePath, String filename, String userDomainPath, String generatedDomainPath) throws Exception {

        if (filename.indexOf(".java") > 0) {
            String comment;

            if (filename.endsWith(".java")) {
                ImportsContext.setCurrentImportsHolder(new ImportsHolder(filePath));
            }

            String userJavaFilename = convertToFullFilename(filePath, filename, userDomainPath);
            String generatedJavaFilename = convertToFullFilename(filePath, filename, generatedDomainPath);

            File userJavaFile = null;
            boolean metaModelFileAndAlreadyExistsInGenerated = false;

            if (filename.endsWith("_.java") && generatedSource.fileExists(generatedJavaFilename)) {
                metaModelFileAndAlreadyExistsInGenerated = true;
            }

            if (userSource.fileExists(userJavaFilename)) {
                userJavaFile = new File(userSource.getFullPath(userJavaFilename));
            } else if (metaModelFileAndAlreadyExistsInGenerated) {
                // Entity meta model special case. Here is how it works:
                // 1- Entity.p.vm.java handle the meta model take over if needed (creates "...class Xxx_ extends XxxBase_ .."
                // 2- EntityMeta.p.vm.java is executed, it detects the above take over and then generate the XxxBase_ ...
                // The trick is that during 1-, the file is created under the generatedSource folder, whereas generally
                // take over happens under the userSource... but we don't want that for the metamodel as it would pollute the
                // user source base and also because the take over is generated.
                userJavaFile = new File(generatedSource.getFullPath(generatedJavaFilename));
            }

            if (userJavaFile != null) {
                String userFileContent = ioUtil.fileToString(userJavaFile);
                String baseClassName = convertFileNameToBaseClassName(filename);

                if (javaFileExtendsClass(userFileContent, baseClassName)) {
                    currentClass = baseClassName;
                    currentRootClass = convertFileNameToClassName(filename);
                    currentRootCast = "(" + currentRootClass + ") ";
                    ImportsContext.setIsExtendedByUser(true);
                    comment = setCurrentFilename(filePath, currentClass + ".java", generatedDomainPath);

                    if (log.isInfoEnabled()) {
                        packInfo("TAKE OVER detected, will generate base class: " + currentClass);
                    }
                } else if (metaModelFileAndAlreadyExistsInGenerated) {
                    comment = setCurrentFilename(filePath, filename, generatedDomainPath);
                    currentClass = convertFileNameToClassName(filename);
                    currentRootClass = currentClass;
                    currentRootCast = "";
                    ImportsContext.setIsExtendedByUser(false);
                } else {
                    // HACK: place it in userDomainPath so it gets detected as a collision by processFile in case baseDir equals outputDirectory
                    comment = setCurrentFilename(filePath, filename, userDomainPath);
                    currentClass = convertFileNameToClassName(filename);
                    currentRootClass = currentClass;
                    currentRootCast = "";
                    ImportsContext.setIsExtendedByUser(false);
                }

                // Move old generated file (when present), as it breaks compilation.
                // Such move cases are not handled in the contentWriter.processFile and must be therefore handled here
                // NOTE: do not try to optimize this if/else check, it would be error prone and not understandable (thanks :-))
                boolean moveOldGeneratedFile = false;
                boolean sameSubPath = normalizePath(userDomainPath).equals(normalizePath(generatedDomainPath));

                if (ImportsContext.isExtendedByUser()) {
                    // We generate XBase.java, existing file could be X.java
                    if (outputResult.sameDirectory()) {
                        if (!sameSubPath) {
                            if (!metaModelFileAndAlreadyExistsInGenerated) {
                                // move generatedDomainPath/X.java as the user has created his own X.java in the userDomainPath
                                moveOldGeneratedFile = true;
                            }
                        } // else: we keep the file as it is the user's one!
                    } else {
                        // no risk as all is clearly separated, except for automatic metamodel takeover which occurs in the generatedSource.
                        // we clearly do not want to move the file that we automatically created.
                        if (!metaModelFileAndAlreadyExistsInGenerated) {
                            moveOldGeneratedFile = true;
                        }
                    }
                } else {
                    if (outputResult.sameDirectory()) {
                        if (!sameSubPath) {
                            if (!metaModelFileAndAlreadyExistsInGenerated) {
                                moveOldGeneratedFile = true;
                            }
                        } // else: we keep the file as it is the user's one!
                    } // else: such case is taken into account in contentWriter.processFile
                }

                if (moveOldGeneratedFile) {
                    String oldGeneratedJavaFilePath = convertToFullFilename(filePath, filename, generatedDomainPath);
                    if (generatedSource.fileExists(oldGeneratedJavaFilePath)) {
                        ioUtil.forceMove(new File(generatedSource.getFullPath(oldGeneratedJavaFilePath)),
                                new File(userSource.getFullPath(outputResult.getCollisionName(oldGeneratedJavaFilePath) + ".old")));
                    }
                }

                return comment;
            } else {
                // does not exist, we can generate it as is
                comment = setCurrentFilename(filePath, filename, generatedDomainPath);
                currentClass = convertFileNameToClassName(filename);
                currentRootClass = currentClass;
                currentRootCast = "";
                ImportsContext.setIsExtendedByUser(false);
            }

            return comment;
        } else if (generatedDomainPath.equals(WellKnownFolder.FLOWS.getGeneratedFolder())) {
            String comment = setCurrentFilename(filePath, filename, generatedDomainPath);
            return comment;
        } else {
            String comment = setCurrentFilename(filePath, filename, userDomainPath);
            return comment;
        }
    }

    /**
     * Java files (except test file) generated from velocity must use this method to get their class name. When the user has extended the generated class,
     * "Base" is appended to the regular class name. Otherwise, the regular class name is returned. The regular class name is deduced from the java file name.
     */
    public String getCurrentClass() {
        return currentClass;
    }

    public String getCurrentRootClass() {
        return currentRootClass;
    }

    public String getCurrentRootCast(String... generics) {
        if (isBlank(currentRootCast)) {
            return "";
        } else if (generics == null || generics.length == 0) {
            return currentRootCast;
        } else {
            String types = "";
            int i = 0;
            for (String generic : generics) {
                types += generic + (i == generics.length ? "" : ", ");
                i++;
            }
            return currentRootCast + "<" + types + ">";
        }
    }

    public String getCurrentClassWithout_() {
        if (currentClass.endsWith("_")) {
            return substringBeforeLast(currentClass, "_");
        }
        throw new IllegalStateException("Must be invoked only if you know currentClass ends with '_'");
    }

    // -------------------------------------------------------------------------
    // Template processing
    // -------------------------------------------------------------------------

    /**
     * Returns "abstract" if the current class is extended by the user, an empty string otherwise.
     */
    public String getCurrentAbstract() {
        return ImportsContext.isExtendedByUser() ? "abstract" : "";
    }

    /**
     * add Base to a given java filename it is used for transparently subclassing generated classes
     */
    private String convertFileNameToBaseClassName(String filename) {
        if (filename.endsWith("_.java")) {
            return substringBeforeLast(filename, "_.java") + BASE_SUFFIX_;
        } else {
            return substringBeforeLast(filename, ".java") + BASE_SUFFIX;
        }
    }

    // --------------------------------------------
    // private Utils
    // --------------------------------------------

    protected String convertToFullFilename(String relpath_or_package, String filename, String domain) throws Exception {

        if (relpath_or_package != null && ".".equals(relpath_or_package.trim())) {
            log.warn("******** BUG IN FilenameUtils ==============> " + normalize(relpath_or_package.replace('.', separatorChar) + separatorChar + filename));

            throw new Exception("not a good practice, please clean up your template relative path, you may set it to empty string... :" + relpath_or_package
                    + ", " + filename);
        }

        String ret;

        if (hasLength(relpath_or_package)) {
            ret = normalize(relpath_or_package.replace('.', separatorChar) + separatorChar + filename);
        } else {
            ret = filename;
        }

        if (hasLength(domain)) {
            return normalize(domain + File.separator + ret);
        } else {
            return ret;
        }
    }

    /*
     * This method creates a file and generates the class given a template and the type of template @param templateName the velocity template @param
     * templateType the type of template (schema/table/column)
     */
    public void processDynamicFile(Map<String, Object> context, TemplatePack templatePack, Template template) throws Exception {
        try {
            if (!(template.getName().indexOf(".vm.") >= 0 || template.getName().endsWith(".vm"))) {
                throw new IllegalStateException("not a velocity template!: " + template.getName());
            }

            String evaluatedTemplate = null;

            try {
                evaluatedTemplate = velocityGenerator.evaluate(context, templatePack, template);
            } catch (StopFileReachedException e) {
                return;
            }

            if (currentFullFilename.endsWith(".donotgenerate")) {
                return;
            }
            if (isBlank(currentFullFilename)) {
                log.error("In " + templatePack.getName() + ":" + template.getName() + "  target filename is missing");
                return;
            }
            if (!config.getCelerio().getConfiguration().hasFilename(templatePack.getName(), currentFullFilename)) {
                packDebug(templatePack, "SKIPPING:" + currentFullFilename);
                return;
            }

            if (currentEnableDynamicImport) {
                if (ImportsContext.getCurrentImportsHolder().hasImports()) {
                    evaluatedTemplate = evaluatedTemplate.replace(DYNA_IMPORTS_TAG, "\n" + ImportsContext.getCurrentImportsHolder().toJavaImportString());
                } else {
                    evaluatedTemplate = evaluatedTemplate.replace(DYNA_IMPORTS_TAG, "");
                }
            }

            if (currentFullFilename.endsWith(".java")) {
                evaluatedTemplate = eclipseCodeFormatter.format(evaluatedTemplate);
            } else if (currentFullFilename.endsWith(".xml") || currentFullFilename.endsWith(".xhtml")) {
                evaluatedTemplate = xmlCodeFormatter.format(evaluatedTemplate);
            }

            // generated content is in the string writer
            if (log.isDebugEnabled()) {
                packDebug(templatePack, "processing template " + template.getName() + " (" + currentFullFilename + ")");
            }
            try {
                contentWriter.processFile(outputResult, templatePack, template, evaluatedTemplate.getBytes("UTF-8"), currentFullFilename);
            } catch (Exception e) {
                log.error("In " + templatePack.getName() + ":" + template.getName() + " template, got exception " + e.getMessage(), e);
            }

        } finally {
            clearDynamicFileContext();
        }
    }

    private void clearDynamicFileContext() {
        currentFullFilename = "";
        currentClass = "";
        ImportsContext.setIsExtendedByUser(false);
        ImportsContext.setCurrentImportsHolder(null);
        currentEnableDynamicImport = false;
    }

    // --------------------------------------------
    // Logging methods
    // --------------------------------------------

    /**
     * return the class name given a java file.
     */
    private String convertFileNameToClassName(String filename) {
        return StringUtils.substringBefore(filename, ".java");
    }

    private boolean javaFileExtendsClass(String javaFileContent, String extendedClassName) {
        return match(".*\\s+extends\\s+" + extendedClassName + "\\s*.*", javaFileContent);
    }

    private boolean match(String pattern, String content) {
        if (content == null) {
            return false;
        }

        return compile(pattern, MULTILINE | DOTALL | UNIX_LINES).matcher(content).matches();
    }

    protected void packDebug(TemplatePack templatePack, String message) {
        log.debug("[" + templatePack.getName() + "][" + message + "]");
    }

    // -------------------------------------------------
    // Engine Configuration
    // -------------------------------------------------

    protected void packInfo(TemplatePack templatePack, String message) {
        log.info("[" + templatePack.getName() + "][" + message + "]");
    }

    protected void packInfo(String message) {
        log.info("[" + message + "]");
    }

    protected void packWarn(TemplatePack templatePack, String message) {
        log.warn("[" + templatePack.getName() + "][" + message + "]");
    }

    public OutputResult getOutputResult() {
        return outputResult;
    }

    public void setOutputResult(OutputResult outputResult) {
        this.outputResult = outputResult;
        this.userSource = outputResult.getUserSource();
        this.generatedSource = outputResult.getGeneratedSource();
    }

    private boolean hasLength(String s) {
        return s != null && s.length() > 0;
    }

    public void stopFileGeneration() {
        throw new StopFileReachedException();
    }

    public static class StopFileReachedException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }
}