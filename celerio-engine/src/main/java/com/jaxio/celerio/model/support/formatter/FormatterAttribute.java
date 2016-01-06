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

package com.jaxio.celerio.model.support.formatter;

import com.jaxio.celerio.convention.GeneratedPackage;
import com.jaxio.celerio.spi.support.AbstractAttributeSpi;
import com.jaxio.celerio.util.AnnotationBuilder;

import java.util.List;

import static com.jaxio.celerio.configuration.Module.SPRING_MVC_3;

public class FormatterAttribute extends AbstractAttributeSpi {

    /**
     * return "formatter"
     */
    @Override
    public String velocityVar() {
        return "formatter";
    }

    @Override
    public Object getTarget() {
        return this;
    }

    public List<String> getAnnotations() {
        AnnotationBuilder ab = new AnnotationBuilder();
        ab.add( //
                getDateTimeFormatterAnnotation(), //
                getFileSizeFormatterAnnotation());
        return ab.getAnnotations();
    }

    private String getDateTimeFormatterAnnotation() {
        if (!attribute.isDate() || !attribute.getConfig().getCelerio().getConfiguration().has(SPRING_MVC_3)) {
            return "";
        }
        addImport("org.springframework.format.annotation.DateTimeFormat");
        addImport("static " + GeneratedPackage.Util.getPackageName() + ".ResourcesUtil.DATE_FORMAT_PATTERN");
        return appendComment("@DateTimeFormat(pattern = DATE_FORMAT_PATTERN)");
    }

    public String getFileSizeFormatterAnnotation() {
        if (!attribute.isFileSize() || !attribute.getConfig().getCelerio().getConfiguration().has(SPRING_MVC_3)) {
            return "";
        }
        addImport(GeneratedPackage.Formatter.getPackageName() + ".FileSizeFormatter");
        return appendComment("@FileSizeFormatter");
    }
}
