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

package com.jaxio.celerio.model.support.validation;

import com.jaxio.celerio.spi.support.AbstractAttributeSpi;
import com.jaxio.celerio.util.AnnotationBuilder;
import com.jaxio.celerio.util.AttributeBuilder;
import repackaged.org.hibernate.validator.constraints.SafeHtml.WhiteListType;

import java.util.List;

import static com.jaxio.celerio.configuration.Module.CHAR_PADDING;
import static com.jaxio.celerio.convention.GeneratedPackage.Validation;

/**
 * Default SPI for the "validation" velocity var. Developer may override it.<br>
 * Provides convenient helper to generate validation annotation that apply to an attribute.
 */
public class ValidationAttribute extends AbstractAttributeSpi {

    @Override
    public String velocityVar() {
        return "validation";
    }

    @Override
    public Object getTarget() {
        return this;
    }

    // --------------------------------------------
    // Validation Annotations, used from templates
    // --------------------------------------------

    /**
     * Returns all the validation annotations for the attribute. Imports are processed automatically.
     */
    public List<String> getAnnotations() {
        AnnotationBuilder result = new AnnotationBuilder();
        result.add( //
                getFixedLengthAnnotation(), //
                getLengthAnnotation(), //
                getNotNullAnnotation(), //
                getNotEmptyAnnotation(), //
                getCharPaddingAnnotation(), //
                getEmailAnnotation(), //
                getDigitsAnnotation(), //
                getSafeHtmlAnnotation(), //
                getUrlAnnotation());
        return result.getAnnotations();
    }

    public String getSafeHtmlAnnotation() {
        if (skip() || !attribute.isSafeHtml()) {
            return "";
        }

        addImport("org.hibernate.validator.constraints.SafeHtml");

        AttributeBuilder ab = new AttributeBuilder(isCommentMode());
        WhiteListType wlt = attribute.getColumnConfig().getSafeHtml().getWhitelistType();

        if (wlt != null && wlt != WhiteListType.RELAXED) {
            addImport("org.hibernate.validator.constraints.SafeHtml.WhiteListType");
            ab.add("whitelistType = WhiteListType." + wlt.name());
        }

        return ab.bindAttributesTo("@SafeHtml");
    }

    public String getUrlAnnotation() {
        if (skip() || !attribute.isUrl()) {
            return null;
        }
        addImport("org.hibernate.validator.constraints.URL");
        return appendComment("@URL");
    }

    public String getCharPaddingAnnotation() {
        if (skip() || !attribute.isString() || attribute.isEnum() || attribute.isInFk()
                || !attribute.getConfig().getCelerio().getConfiguration().has(CHAR_PADDING)) {
            return "";
        }
        addImport(Validation.getPackageName() + ".Padding");
        return appendComment("@Padding");
    }

    public String getNotNullAnnotation() {
        if (skip() || attribute.isNullable()) {
            return "";
        }
        if (!(attribute.isString() && !attribute.isEnum())) {
            addImport("javax.validation.constraints.NotNull");
            return appendComment("@NotNull");
        }
        return "";
    }

    public String getNotEmptyAnnotation() {
        if (skip() || attribute.isNullable()) {
            return "";
        }
        if (attribute.isString() && !attribute.isEnum()) {
            addImport("org.hibernate.validator.constraints.NotEmpty");
            return appendComment("@NotEmpty");
        }
        return "";
    }

    public String getFixedLengthAnnotation() {
        if (skip() || !attribute.isFixedSize() || !attribute.isString() || attribute.isEnum()) {
            return "";
        }
        addImport(Validation.getPackageName() + ".FixedLength");
        AttributeBuilder ab = new AttributeBuilder(isCommentMode());
        ab.add("length = " + attribute.getColumnConfig().getSize());
        if (!attribute.isNullable()) {
            ab.add("nullable = false");

        }
        return ab.bindAttributesTo("@FixedLength");
    }

    public String getLengthAnnotation() {
        if (skip() || attribute.isFixedSize() || attribute.isFileSize() || !attribute.isString() || attribute.isEnum()) {
            return "";
        }
        addImport("javax.validation.constraints.Size");

        AttributeBuilder ab = new AttributeBuilder(isCommentMode());
        if (attribute.getColumnConfig().getMin() != null) {
            ab.addInt("min", attribute.getColumnConfig().getMin());
        }
        ab.addInt("max", attribute.getColumnConfig().getSize());
        return ab.bindAttributesTo("@Size");
    }

    public String getEmailAnnotation() {
        if (skip() || !attribute.isEmail() || attribute.isEnum()) {
            return "";
        }
        addImport("org.hibernate.validator.constraints.Email");
        return appendComment("@Email");
    }

    public String getDigitsAnnotation() {
        if (skip() || !attribute.hasDigits()) {
            return "";
        }

        addImport("javax.validation.constraints.Digits");
        AttributeBuilder ab = new AttributeBuilder(isCommentMode());
        ab.addInt("integer", attribute.getColumnConfig().getSize() - attribute.getColumnConfig().getDecimalDigits());
        ab.addInt("fraction", attribute.getColumnConfig().getDecimalDigits());
        return ab.bindAttributesTo("@Digits");
    }

    // --------------------------------------------
    // Helper, can be overiden by SPI developers
    // --------------------------------------------

    /**
     * Tells if the annotations should not be applied to the attribute.<br>
     * By default, annotation are not applied if one of the following case is true:
     * <p>
     * <ul>
     * <li>The attribute is a simple PK and is automatically assigned
     * <li>The attribute is a part of an FK
     * </ul>
     * The attribute belongs to the AuditEntity
     */
    protected boolean skip() {
        return (attribute.isSimplePk() && attribute.getJpa().isAutomaticallyAssigned()) //
                || attribute.isInFk() //
                || attribute.isAuditEntityAttribute();
    }
}