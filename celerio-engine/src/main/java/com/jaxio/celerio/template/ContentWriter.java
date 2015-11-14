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

import com.jaxio.celerio.output.OutputResult;
import com.jaxio.celerio.output.SourceFile;
import com.jaxio.celerio.template.pack.Template;
import com.jaxio.celerio.template.pack.TemplatePack;
import com.jaxio.celerio.util.IOUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

import static com.jaxio.celerio.template.ContentWriter.Action.*;
import static org.apache.commons.io.FilenameUtils.normalize;

@Slf4j
@Service
public class ContentWriter {
    @Autowired
    private IOUtil ioUtil;

    public void processFile(OutputResult outputResult, TemplatePack templatePack, Template template, byte[] contentToPublish, String targetFilename)
            throws Exception {
        SourceFile userSource = outputResult.getUserSource();
        SourceFile generatedSource = outputResult.getGeneratedSource();

        targetFilename = normalize(targetFilename);

        if ((!outputResult.sameDirectory()) && userSource.fileExists(targetFilename)) {
            // First check in user folder, but only if the globally configured outputDirectory
            // is different from the regular maven baseDir.

            // clean up potential previously generated file (when userSource was not yet containing the file)
            if (generatedSource.fileExists(targetFilename)) {
                ioUtil.forceMove(new File(generatedSource.getFullPath(targetFilename)),
                        new File(userSource.getFullPath(outputResult.getCollisionName(targetFilename) + ".old")));
            }

            // determines if regeneration is required
            if (userSource.isSameContent(targetFilename, contentToPublish)) {
                if (log.isDebugEnabled()) {
                    packDebug(templatePack, IDENTICAL_EXISTS, targetFilename);
                }
            } else {
                // Since outputDirectory is different, this is considered by definition as a collision
                targetFilename = outputResult.getCollisionName(targetFilename);
                if (log.isInfoEnabled()) {
                    packInfo(templatePack, GENERATE_IN_COLLISIONS_FOLDER, targetFilename);
                }

                // generate
                outputResult.addCollisionContent(contentToPublish, targetFilename, templatePack, template);
            }
        } else if (generatedSource.fileExists(targetFilename)) {
            // Checks in generated folder. Beware, it could be the same as user folder.

            if (generatedSource.isSameContent(targetFilename, contentToPublish)) {
                if (log.isDebugEnabled()) {
                    packDebug(templatePack, IDENTICAL_EXISTS, targetFilename);
                }
            } else {
                if (outputResult.hasCollision(targetFilename)) {
                    // NOTE: the method above is contextual it depends on outputDirectory...
                    targetFilename = outputResult.getCollisionName(targetFilename);
                    if (log.isInfoEnabled()) {
                        packInfo(templatePack, GENERATE_IN_COLLISIONS_FOLDER, targetFilename);
                    }
                    outputResult.addCollisionContent(contentToPublish, targetFilename, templatePack, template);
                } else {
                    if (log.isInfoEnabled()) {
                        packInfo(templatePack, GENERATE_AND_REPLACE, getFullPathForLog(outputResult, targetFilename));
                    }
                    // generate
                    outputResult.addContent(contentToPublish, targetFilename, templatePack, template);
                }
            }
        } else {
            if (log.isInfoEnabled()) {
                packInfo(templatePack, GENERATE, getFullPathForLog(outputResult, targetFilename));
            }

            // generate
            outputResult.addContent(contentToPublish, targetFilename, templatePack, template);
        }
    }

    /**
     * Use it only to display proper path in log
     *
     * @param targetFilename
     */
    private String getFullPathForLog(OutputResult or, String targetFilename) {
        if (or.sameDirectory()) {
            return targetFilename;
        } else {
            return or.getGeneratedSource().getFullPath(targetFilename);
        }
    }

    private void packDebug(TemplatePack templatePack, Action action, String file) {
        log.debug("[" + templatePack.getName() + "][" + action.value + "] " + file);
    }

    private void packInfo(TemplatePack templatePack, Action action, String file) {
        log.info("[" + templatePack.getName() + "][" + action.value + "] " + file);
    }

    public enum Action {
        GENERATE("generate"), //
        GENERATE_AND_REPLACE("generate and replace"), //
        IDENTICAL_EXISTS("identical exists"), //
        GENERATE_IN_COLLISIONS_FOLDER("generate in collision folder");
        String value;

        Action(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

    }
}