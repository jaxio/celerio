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

package com.jaxio.celerio.output;

import com.jaxio.celerio.Config;
import com.jaxio.celerio.configuration.EclipseFormatter;
import com.jaxio.celerio.configuration.FormatterEnum;
import com.jaxio.celerio.configuration.eclipse.Setting;
import com.jaxio.celerio.configuration.support.EclipseProfilesLoader;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

@Service
@Slf4j
public class EclipseCodeFormatter {
    private Map<String, String> options;
    private CodeFormatter codeFormatter;
    private FormatterEnum formatterChoice;

    @Autowired
    public EclipseCodeFormatter(Config config, EclipseProfilesLoader loader) {
        EclipseFormatter eclipseFormatter = config.getCelerio().getConfiguration().getConventions().getEclipseFormatter();
        formatterChoice = eclipseFormatter.getFormatterChoice();

        switch (formatterChoice) {
            case USE_FORMATTER_FILE:
                String filePath = config.getBaseDir() + File.separatorChar + eclipseFormatter.getFormatterFile();
                try {
                    setFormatterSettings(loader.loadSettingsFromEclipseFile(new File(filePath)));
                } catch (IOException ioe) {
                    log.error("Could not create the Eclipse Code Formatter. Please check the path to your formatter filer.", ioe);
                    formatterChoice = FormatterEnum.NONE;
                }
                break;
            case USE_ECLIPSE_DEFAULT:
                setFormatterSettings(null);
                break;
            case NONE:
                break;
        }
    }

    @SuppressWarnings({"unchecked", "deprecation"})
    public void setFormatterSettings(List<Setting> settings) {

        // // change the option to wrap each enum constant on a new line
        // options.put(DefaultCodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_ENUM_CONSTANTS,
        // DefaultCodeFormatterConstants.createAlignmentValue(true,
        // DefaultCodeFormatterConstants.WRAP_ONE_PER_LINE,
        // DefaultCodeFormatterConstants.INDENT_ON_COLUMN));
        //
        if (settings != null) {
            options = newHashMap();
            for (Setting s : settings) {
                options.put(s.getId(), s.getValue());
            }
        } else {
            options = DefaultCodeFormatterConstants.getEclipseDefaultSettings();

            options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_8);
            options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_8);
            options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8);

            options.put(JavaCore.FORMATTER_LINE_SPLIT, "160");
            options.put(JavaCore.FORMATTER_TAB_CHAR, JavaCore.SPACE);
            options.put(JavaCore.FORMATTER_TAB_SIZE, "4");
        }

        // instanciate the default code formatter with the given options
        codeFormatter = ToolFactory.createCodeFormatter(options);
    }

    public String format(String source) {
        if (formatterChoice == FormatterEnum.NONE) {
            return source;
        }

        final TextEdit edit = codeFormatter.format(CodeFormatter.K_COMPILATION_UNIT, // format a compilation unit
                source, // source to format
                0, // starting position
                source.length(), // length
                0, // initial indentation
                System.getProperty("line.separator") // line separator
        );

        IDocument document = new Document(source);
        try {
            edit.apply(document);
        } catch (MalformedTreeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Error for " + source);
            e.printStackTrace();
        }

        // display the formatted string on the System out
        return document.get();
    }
}
