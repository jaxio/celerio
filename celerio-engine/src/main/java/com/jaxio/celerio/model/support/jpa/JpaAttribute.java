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

package com.jaxio.celerio.model.support.jpa;

import com.jaxio.celerio.configuration.MetaAttribute;
import com.jaxio.celerio.configuration.SequencePattern;
import com.jaxio.celerio.configuration.entity.EnumConfig;
import com.jaxio.celerio.configuration.entity.GeneratedValue;
import com.jaxio.celerio.configuration.entity.GenericGenerator;
import com.jaxio.celerio.model.Attribute;
import com.jaxio.celerio.spi.support.AbstractAttributeSpi;
import com.jaxio.celerio.util.AnnotationBuilder;
import com.jaxio.celerio.util.AttributeBuilder;
import org.springframework.util.Assert;

import java.util.Iterator;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.apache.commons.lang.StringUtils.isBlank;

public class JpaAttribute extends AbstractAttributeSpi {

    public JpaAttribute(Attribute attribute) {
        init(attribute);
    }

    @Override
    public String velocityVar() {
        return "jpa--NOT-YET-USED"; // TODO: use it
    }

    @Override
    public Object getTarget() {
        return this;
    }

    public List<String> getAnnotations() {
        AnnotationBuilder ab = new AnnotationBuilder();
        ab.add(
                getTransientAnnotation(), //
                getIdAnnotation(), //
                getLobAnnotation(), //
                getEnumAnnotation(), //
                getBasicAnnotation(), //
                getColumnAnnotation(), //
                getVersionAnnotation(), //
                getTemporalAnnotation(), //
                getHibernateTypeAnnotation(), //
                getGeneratedValueAnnotation(), //
                getSequenceGeneratorAnnotation(), //
                getGenericGeneratorAnnotation());
        return ab.getAnnotations();
    }

    public String getTransientAnnotation() {
        if (attribute.getColumnConfig() == null //
                || attribute.getColumnConfig().getAsTransient() == null //
                || attribute.getColumnConfig().getAsTransient() == FALSE) {
            return null;
        }
        addImport("javax.persistence.Transient");
        return "@Transient";
    }

    public String getIdAnnotation() {
        if (attribute.isSimplePk()) {
            addImport("javax.persistence.Id");
            return "@Id";
        } else {
            return null;
        }
    }

    public String getHibernateTypeAnnotation() {
        if (attribute.getColumnConfig() == null) {
            return null;
        }
        if (attribute.getColumnConfig().getTypeConverter() != null) {
            StringBuffer annotation = new StringBuffer("");
            addImport("org.hibernate.annotations.Type");
            annotation.append("@Type(type = \"").append(attribute.getColumnConfig().getTypeConverter()).append("\")");
            return annotation.toString();
        } else if (attribute.getColumnConfig().hasEnum() && attribute.getColumnConfig().getEnumConfig().isCustomType()) {
            StringBuffer annotation = new StringBuffer("");

            EnumConfig enumConfig = attribute.getColumnConfig().getEnumConfig();


            if (enumConfig.hasUserType()) {
                addImport("org.hibernate.annotations.Type");
                addImport("org.hibernate.annotations.Parameter");
                addImport(enumConfig.getUserType());
                annotation.append("@Type(type = " + enumConfig.getUserType() + ", parameters = { @Parameter(name = \"class\", value = \""
                        + attribute.getEntity().getModel().getFullType() + "\"), @Parameter(name = \"attribute\", value = \"" + attribute.getVar() + "\") })");
            } else {
                addImport("javax.persistence.Convert");
                addImport("" + attribute.getFullType() + "Converter");
                annotation.append("@Convert(converter=" + attribute.getType() + "Converter.class" + ")");
            }
            return annotation.toString();
/*
        // NOT NEEDED WITH HIBERNATE 5.
        } else if (attribute.getMappedType() == MappedType.M_LOCALDATE) {
            addImport("org.hibernate.annotations.Type");
            String hibernateType = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate";
            if (attribute.getConfig().getCelerio().getConfiguration().hasJodaTime()) {
                hibernateType = attribute.getConfig().getCelerio().getConfiguration().getJodaTime().getLocalDateHibernateType();
            }
            return "@Type(type = \"" + hibernateType + "\")";
        } else if (attribute.getMappedType() == MappedType.M_LOCALDATETIME) {
            addImport("org.hibernate.annotations.Type");
            String hibernateType = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime";
            if (attribute.getConfig().getCelerio().getConfiguration().hasJodaTime()) {
                hibernateType = attribute.getConfig().getCelerio().getConfiguration().getJodaTime().getLocalDateTimeHibernateType();
            }
            return "@Type(type = \"" + hibernateType + "\")";
*/
        } else {
            return null;
        }
    }

    public String getBasicAnnotation() {
        if (attribute.isInPk()) {
            return null;
        }

        AttributeBuilder builder = new AttributeBuilder(getBasicFetch());
        if (builder.isEmpty()) {
            return null;
        }

        // we set it only if LAZY is active and if different from default
        builder.add(getBasicOptional());
        if (builder.isEmpty()) {
            return null;
        }
        addImport("javax.persistence.Basic");
        return "@Basic(" + builder.getAttributes() + ")";
    }

    public String getEnumAnnotation() {
        if (!attribute.isEnum()) {
            return null;
        }
        if (attribute.getColumnConfig().getEnumConfig().isCustomType()) {
            return null;
        }
        addImport("javax.persistence.Enumerated");
        if (attribute.getColumnConfig().getEnumConfig().isString()) {
            addImport("static javax.persistence.EnumType.STRING");
            return "@Enumerated(STRING)";
        } else {
            addImport("static javax.persistence.EnumType.ORDINAL");
            return "@Enumerated(ORDINAL)";
        }
    }

    private String getBasicOptional() {
        if (attribute.isNullable()) {
            return null;
        }
        return "optional = false";
    }

    private String getBasicFetch() {
        if (!attribute.isLazyLoaded()) {
            return null;
        }
        addImport("static javax.persistence.FetchType.LAZY");
        return "fetch = LAZY";
    }

    public String getLobAnnotation() {
        if (!attribute.isLob()) {
            return null;
        }
        addImport("javax.persistence.Lob");
        return "@Lob";
    }

    public String getTemporalAnnotation() {
        if (attribute.isJavaUtilOnlyDate() /* TODO: handle mapping to Calendar */) {
            addImport("javax.persistence.Temporal");
            addImport("static javax.persistence.TemporalType.DATE");
            return "@Temporal(DATE)";
        }

        if (attribute.isJavaUtilDateAndTime()) {
            addImport("javax.persistence.Temporal");
            addImport("static javax.persistence.TemporalType.TIMESTAMP");
            return "@Temporal(TIMESTAMP)";
        }

        if (attribute.isJavaUtilOnlyTime()) {
            addImport("javax.persistence.Temporal");
            addImport("static javax.persistence.TemporalType.TIME");
            return "@Temporal(TIME)";
        }

        return "";
    }

    public String getColumnAnnotation() {
        AttributeBuilder attributes = getColumnAnnotationAttributes();
        if (attributes.isEmpty()) {
            return "";
        } else {
            addImport("javax.persistence.Column");
            return "@Column(" + attributes.getAttributes() + ")";
        }
    }

    private AttributeBuilder getColumnAnnotationAttributes() {
        return new AttributeBuilder(getTable(), //
                getName(), //
                getNullable(), //
                getUnique(), //
                getLength(), //
                getPrecision(), //
                getScale(), //
                getColumnInsertable(), //
                getColumnUpdatable());
    }

    private String getColumnUpdatable() {
        if (isColumnUpdatable()) {
            return null;
        }
        return "updatable = false";
    }

    private boolean isColumnUpdatable() {
        return attribute.isInPk() || !attribute.isInFk();
    }

    private String getColumnInsertable() {
        return isColumnInsertable() ? null : "insertable = false";
    }

    private boolean isColumnInsertable() {
        return isColumnUpdatable();
    }

    private String getName() {
        return "name = \"" + attribute.getColumnNameEscaped() + "\"";
    }

    private String getTable() {
        if (!attribute.getColumnConfig().getTableName().equalsIgnoreCase(attribute.getEntity().getEntityConfig().getTableName())) {
            String secondaryTableName = attribute.getColumnConfig().getTableNameEscaped();
            return "table = \"" + secondaryTableName + "\"";
        }

        return null;
    }

    private String getNullable() {
        return attribute.isSimplePk() || attribute.isNullable() ? null : "nullable = false";
    }

    private String getUnique() {
        return attribute.isSimplePk() || !attribute.isUnique() ? null : "unique = true";
    }

    private String getLength() {
        if (!attribute.isNumeric() && !attribute.isLob() && attribute.getColumnConfig().getSize() != 255) {
            return "length = " + attribute.getColumnConfig().getSize();
        } else {
            return "";
        }
    }

    private String getPrecision() {
        if (attribute.isNumeric() && attribute.getColumnConfig().getSize() != 0) {
            return "precision = " + attribute.getColumnConfig().getSize() + "";
        } else {
            return "";
        }
    }

    private String getScale() {
        if (attribute.isNumeric() && attribute.getColumnConfig().getDecimalDigits() != 0) {
            return "scale = " + attribute.getColumnConfig().getDecimalDigits() + "";
        } else {
            return "";
        }
    }

    public String getVersionAnnotation() {
        if (!attribute.isVersion()) {
            return "";
        }

        if (attribute.getMappedType().isEligibleForVersion()) {
            addImport("javax.persistence.Version");
            return "@Version";
        } else {
            throw new IllegalStateException("The column " + attribute.getFullColumnName() + " type cannot be used with @Version");
        }
    }

    private String getGenericGeneratorStrategy() {
        if (attribute.isSimplePk() && attribute.isString()) {
            if (attribute.getColumnConfig().getSize() == 32) {
                return "uuid";
            } else if (attribute.getColumnConfig().getSize() == 36) {
                // assuming Hibernate 3.6.9 at least
                // avoid this warning:
                // [jpa2222] - 2012-01-20 16:08:58,432 - - Using org.hibernate.id.UUIDHexGenerator which does not generate IETF RFC 4122 compliant UUID values;
                // consider using org.hibernate.id.UUIDGenerator instead
                return "uuid2";
            } else {
                return "assigned";
            }
        }
        return "";
    }

    private String getGeneratedValueGenerator() {
        return "strategy-" + getGenericGeneratorStrategy();
    }

    //-----------------------------------------
    // @GeneratedValue
    //-----------------------------------------

    public String getGeneratedValueAnnotation() {
        if (useSequenceNameShortcut()) {
            return getGeneratedValueForSequenceNameByConfiguration();
        }

        if (attribute.getColumnConfig().useConfigForIdGenerator()) {
            // TODO: explain why we do not directly check attribute.getColumnConfig().hasGeneratedValue() in the if statement above.
            return getGeneratedValueAnnotationByConfiguration();
        } else if (attribute.getAutoIncrement() == TRUE && !attribute.isSimpleFk()) {
            // the jdbc driver supports IS_AUTOINCREMENT metadata, great!
            // if it is an fk, we do not want @GeneratedValue because we use instead @MapsId on the association...
            addImport("javax.persistence.GeneratedValue");
            addImport("static javax.persistence.GenerationType.IDENTITY");
            return "@GeneratedValue(strategy = IDENTITY)";
        } else if (attribute.getAutoIncrement() == FALSE && /* 32 length string are special for us */!attribute.isString()) {
            // the jdbc driver supports IS_AUTOINCREMENT metadata, great!
            return "";
        } else {
            // the jdbc driver does not support IS_AUTOINCREMENT
            // fall back to convention
            return getGeneratedValueAnnotationByConvention();
        }
    }

    private String getGeneratedValueForSequenceNameByConfiguration() {
        addImport("javax.persistence.GeneratedValue");
        addImport("static javax.persistence.GenerationType.SEQUENCE");
        return "@GeneratedValue(strategy = SEQUENCE, " //
                + "generator = \"" + attribute.getEntity().getEntityConfig().getSequenceName() + "\")";
    }

    private String getGeneratedValueAnnotationByConfiguration() {
        if (attribute.getColumnConfig().hasGeneratedValue()) {
            GeneratedValue gv = attribute.getColumnConfig().getGeneratedValue();
            addImport("javax.persistence.GeneratedValue");
            if (gv.hasStrategy() && gv.hasGenerator()) {
                addImport("static javax.persistence.GenerationType." + gv.getStrategy());
                return "@GeneratedValue(strategy = " + gv.getStrategy() + ", generator = \"" + gv.getGenerator() + "\")";
            } else if (gv.hasStrategy()) {
                addImport("static javax.persistence.GenerationType." + gv.getStrategy());
                return "@GeneratedValue(strategy = " + gv.getStrategy() + ")";
            } else {
                return "@GeneratedValue(generator = \"" + gv.getGenerator() + "\")";
            }
        } else {
            return "";
        }
    }

    private String getGeneratedValueAnnotationByConvention() {
        if (hasIdGeneratorByConvention()) {
            String strategy = getGenericGeneratorStrategy();
            addImport("javax.persistence.GeneratedValue");
            if (isBlank(strategy)) {
                return "@GeneratedValue";
            } else {
                return "@GeneratedValue(generator = \"" + getGeneratedValueGenerator() + "\")";
            }
        } else {
            return "";
        }
    }

    public boolean isManuallyAssigned() {
        Assert.isTrue(attribute.isSimplePk(), "isManuallyAssigned can only be requested on simple primary keys");
        return isColumnInsertable() && !uglyHackHasGeneratedValueAnno();
    }

    /**
     * The negation of is isManuallyAssigned().
     */
    public boolean isAutomaticallyAssigned() {
        return !isManuallyAssigned();
    }

    /**
     * mirror the getGeneratedValueAnnotation logic and return true if
     * a GeneratedValue annotation applies.
     * TODO: code review.
     */
    private boolean uglyHackHasGeneratedValueAnno() {
        if (useSequenceNameShortcut()) {
            return true;
        }

        if (attribute.getColumnConfig().useConfigForIdGenerator()) {
            return attribute.getColumnConfig().hasGeneratedValue();
        } else if (attribute.getAutoIncrement() == TRUE) {
            return true;
        } else if (attribute.getAutoIncrement() == FALSE && /* 32 length string are special for us */!attribute.isString()) {
            // the jdbc driver supports IS_AUTOINCREMENT metadata, great!
            return false;
        } else {
            // the jdbc driver does not support IS_AUTOINCREMENT
            // fall back to convention
            return hasIdGeneratorByConvention();
        }
    }


    //-----------------------------------------
    // @SequenceGenerator
    //-----------------------------------------

    public String getSequenceGeneratorAnnotation() {
        if (useSequenceNameShortcut()) {
            return getSequenceGeneratorAnnotationByConfiguration();
        }
        return "";
    }

    private boolean useSequenceNameShortcut() {
        return attribute.isSimplePk()  //
                && attribute.getEntity().isRoot() //
                && attribute.isNumeric() //
                && !attribute.getColumnConfig().useConfigForIdGenerator() //
                && attribute.getEntity().getEntityConfig().hasSequenceName()
                && !attribute.isSimpleFk(); // In that case we use @MapsId on one-to-one.
    }

    private String getSequenceGeneratorAnnotationByConfiguration() {
        addImport("javax.persistence.SequenceGenerator");

        // The sequence name is known, we are looking for extra info such as initialValue and allocationSize.
        // It may seems clumsy but our promise is to keep it simple for the user, we do not want to introduce
        // a SequenceGenerator config under columnConfig as it would have no added value compare to working 
        // directly in Java. Here it is a bit transparent and the configuration is rather simple for the user.

        SequencePattern sequencePattern = null;
        List<SequencePattern> sequences = attribute.getConfig().getCelerio().getConfiguration().getSequences();
        if (sequences != null) {
            for (SequencePattern sequence : sequences) {
                if (sequence.match(attribute.getEntity().getTableName())) {
                    sequencePattern = sequence;
                    break;
                }
            }
        }

        AttributeBuilder ab = new AttributeBuilder();
        // Attention: the sequencePattern does not contain the sequenceName, it contains a pattern...
        ab.addString("name", attribute.getEntity().getEntityConfig().getSequenceName());
        ab.addString("sequenceName", attribute.getEntity().getEntityConfig().getSequenceName());

        if (sequencePattern != null) {
            if (sequencePattern.hasCatalog()) {
                ab.addString("catalog", sequencePattern.getCatalog());
            }

            if (sequencePattern.hasSchema()) {
                ab.addString("schema", sequencePattern.getSchema());
            }

            if (sequencePattern.hasNonDefaultInitialValue()) {
                ab.addInt("initialValue", sequencePattern.getInitialValue());
            }

            if (sequencePattern.hasNonDefaultAllocationSize()) {
                ab.addInt("allocationSize", sequencePattern.getAllocationSize());
            }
        }

        return ab.bindAttributesTo("@SequenceGenerator");
    }

    //-----------------------------------------
    // @GenericGenerator
    //-----------------------------------------


    public String getGenericGeneratorAnnotation() {
        if (useSequenceNameShortcut()) {
            return "";
        } else if (attribute.getColumnConfig().useConfigForIdGenerator()) {
            return getGenericGeneratorAnnotationByConfiguration();
        } else {
            return getGenericGeneratorAnnotationByConvention();
        }
    }

    private String getGenericGeneratorAnnotationByConfiguration() {
        if (attribute.getColumnConfig().hasGenericGenerator()) {
            GenericGenerator gg = attribute.getColumnConfig().getGenericGenerator();
            addImport("org.hibernate.annotations.GenericGenerator");

            AttributeBuilder ggab = new AttributeBuilder();
            ggab.addString("name", gg.getName());
            ggab.addString("strategy", gg.getStrategy());

            if (gg.getParameters().size() > 0) {
                addImport("org.hibernate.annotations.Parameter");

                String[] parameters = new String[gg.getParameters().size()];
                Iterator<MetaAttribute> iter = gg.getParameters().iterator();

                for (int i = 0; i < parameters.length; i++) {
                    MetaAttribute metaAttribute = iter.next();
                    AttributeBuilder pab = new AttributeBuilder();
                    pab.addString("name", metaAttribute.getName());
                    pab.addString("value", metaAttribute.getValue());
                    parameters[i] = pab.bindAttributesTo("@Parameter");
                }
                ggab.add("parameters", parameters);
            }

            return ggab.bindAttributesTo("@GenericGenerator");
        } else {
            return "";
        }
    }

    private String getGenericGeneratorAnnotationByConvention() {
        if (!hasIdGeneratorByConvention()) {
            return null;
        }
        String strategy = getGenericGeneratorStrategy();
        if (isBlank(strategy)) {
            return null;
        }

        addImport("org.hibernate.annotations.GenericGenerator");
        return "@GenericGenerator(name = \"" + getGeneratedValueGenerator() + "\", strategy = \"" + strategy + "\")";
    }

    public boolean hasIdGeneratorByConvention() {
        return attribute.isSimplePk() && !attribute.isDate() && !"assigned".equals(getGenericGeneratorStrategy());
    }
}
