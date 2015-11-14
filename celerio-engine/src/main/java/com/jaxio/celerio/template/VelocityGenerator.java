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

import com.jaxio.celerio.template.PreviousEngine.StopFileReachedException;
import com.jaxio.celerio.template.pack.Template;
import com.jaxio.celerio.template.pack.TemplatePack;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.VelocityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.MatchResult;

import static java.lang.Integer.parseInt;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.apache.commons.io.FilenameUtils.getName;
import static org.apache.commons.io.IOUtils.closeQuietly;

@Service
@Slf4j
public class VelocityGenerator {

    @Autowired
    private VelocityEngine engine;

    public String evaluate(Map<String, Object> context, TemplatePack templatePack, Template template) throws IOException {
        StringWriter sw = new StringWriter();
        try {
            engine.evaluate(new VelocityContext(context), sw, template.getName(), template.getTemplate());
            return sw.toString();
        } catch (ParseErrorException parseException) {
            handleStopFileGeneration(parseException);
            log.error("In " + templatePack.getName() + ":" + template.getName() + " template, parse exception " + parseException.getMessage(),
                    parseException.getCause());
            displayLinesInError(parseException, templatePack, template);
            throw new IllegalStateException();
        } catch (MethodInvocationException mie) {
            handleStopFileGeneration(mie);
            log.error("In " + templatePack.getName() + ":" + mie.getTemplateName() + " method [" + mie.getMethodName() + "] has not been set", mie.getCause());
            displayLinesInError(mie, templatePack, template);
            throw mie;
        } finally {
            closeQuietly(sw);
        }
    }

    public void handleStopFileGeneration(VelocityException exception) {
        if (exception.getCause() instanceof StopFileReachedException) {
            throw new StopFileReachedException();
        }
    }

    private void displayLinesInError(VelocityException exception, TemplatePack templatePack, Template template) {
        try {
            Scanner scanner = new Scanner(exception.getMessage());
            String match = scanner.findInLine("\\[line (\\d+), column (\\d+)\\]");
            if (match == null) {
                return;
            }
            MatchResult result = scanner.match();
            int lineInError = parseInt(result.group(1));
            int column = parseInt(result.group(2));

            String[] lines = template.getTemplate().split("\\n");

            int linesBeforeToDisplay = 2;
            int linesAfterToDisplay = 2;

            for (int i = max(0, lineInError - linesBeforeToDisplay); i < lineInError; i++) {
                System.err.println(prefix(templatePack, template, i + 1) + lines[i]);
            }
            String prefix = prefix(templatePack, template, lineInError);
            System.err.print(prefix);
            for (int i = 0; i < column - 1; i++) {
                System.err.print(" ");
            }
            System.err.println("^");
            for (int i = lineInError; i < min(lines.length - 1, lineInError + linesAfterToDisplay); i++) {
                System.err.println(prefix(templatePack, template, i + 1) + lines[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String prefix(TemplatePack templatePack, Template template, int line) {
        return prefix(templatePack, template, "" + line);
    }

    private String prefix(TemplatePack templatePack, Template template, String message) {
        return "[" + templatePack.getName() + ":" + getName(template.getName()) + ":" + message + "]";
    }
}