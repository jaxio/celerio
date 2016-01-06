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

import com.google.common.collect.Iterables;
import com.jaxio.celerio.configuration.entity.CacheConfigGetter;
import com.jaxio.celerio.model.Attribute;
import com.jaxio.celerio.model.AttributePair;
import com.jaxio.celerio.model.Relation;
import com.jaxio.celerio.spi.support.AbstractRelationSpi;
import com.jaxio.celerio.util.AnnotationBuilder;
import com.jaxio.celerio.util.AttributeBuilder;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.jaxio.celerio.model.support.jpa.JpaConfigHelper.*;

public class JpaToOneRelation extends AbstractRelationSpi {

    @Override
    public boolean compatibleWith(Relation relation) {
        return (relation.isManyToOne() || relation.isOneToOne()) && !relation.isIntermediate();
    }

    @Override
    public String velocityVar() {
        return "jpa";
    }

    @Override
    public Object getTarget() {
        return this;
    }

    public List<String> getAnnotations() {
        if (relation.isComposite() && !relation.isManyToOne()) {
            return Arrays.asList("// TODO: composite one to one");
        }
        AnnotationBuilder ab = new AnnotationBuilder();

        if (relation.isManyToOne()) {
            ab.add(getManyToOneAnnotation());
        } else if (relation.isOneToOne()) {
            ab.add(getOneToOneAnnotation());
        } else {
            throw new IllegalStateException("Expecting a one-to-one or many-to-one association!");
        }

        ab.add(getMapsIdAnnotation());
        ab.add(getCacheAnnotation());

        if (!relation.isInverse()) {
            if (relation.isSimple()) {
                ab.add(getJoinColumnAnnotation(relation.getFromAttribute(), relation.getToAttribute()));
            } else if (relation.isComposite()) {
                ab.add(getJoinColumnsAnnotation(relation.getAttributePairs()));
            }
        }
        return ab.getAnnotations();
    }

    public String getManyToOneAnnotation() {
        addImport("javax.persistence.ManyToOne");
        return new AttributeBuilder( //
                getManyToOneCascade(), //
                getManyToOneFetch()) //
                .bindAttributesTo("@ManyToOne");
    }

    public String getOneToOneAnnotation() {
        addImport("javax.persistence.OneToOne");
        addImport("static javax.persistence.FetchType.LAZY");
        AttributeBuilder ab = new AttributeBuilder();

        if (relation.isInverse()) {
            ab.add(getInverseOneToOneCascade());
            ab.add(getInverseOneToOneFetch());
            ab.add(getOrphanRemoval());
            ab.add("mappedBy = \"" + relation.getInverse().getTo().getVar() + "\"");
        } else {
            ab.add(getOneToOneCascade());
            ab.add(getOneToOneFetch());
        }

        return ab.bindAttributesTo("@OneToOne");
    }

    private boolean hasMapsIdAnnotation() {
        return !relation.isComposite() && !relation.isInverse() && relation.getFromAttribute().isInPk();
    }

    public String getMapsIdAnnotation() {
        if (hasMapsIdAnnotation()) {
            AttributeBuilder ab = new AttributeBuilder();
            if (relation.getFromAttribute().isInCpk()) {
                ab.addString("value", relation.getFromAttribute().getVar());
            }
            addImport("javax.persistence.MapsId");
            return ab.bindAttributesTo("@MapsId");
        }
        return "";
    }

    private String getJoinColumnAnnotation(Attribute fromAttribute, Attribute toAttribute) {
//    TODO: investigate why it does not work!, it should according to doc
//          http://webdev.apl.jhu.edu/~jcs/ejava-javaee/coursedocs/605-784-site/docs/content/html/jpa-relationex-one2one.html#jpa-relationex-o2o-uni-mapsid
//
//        if (hasMapsIdAnnotation()) {
//            return ""; // not needed.
//        }
        AttributeBuilder ab = getJoinColumnAttributes(fromAttribute, toAttribute);
        addImport("javax.persistence.JoinColumn");
        return ab.bindAttributesTo("@JoinColumn");
    }

    private String getJoinColumnsAnnotation(List<AttributePair> attributePairs) {
        addImport("javax.persistence.JoinColumns");
        addImport("javax.persistence.JoinColumn");

        List<String> ab = newArrayList();
        for (AttributePair pair : attributePairs) {
            AttributeBuilder joinBuilder = getJoinColumnAttributes(pair.getFromAttribute(), pair.getToAttribute());
            joinBuilder.bindAttributesTo("@JoinColumn");
            ab.add(joinBuilder.bindAttributesTo("@JoinColumn"));
        }

        AttributeBuilder joinsBuilder = new AttributeBuilder();
        joinsBuilder.add("value", Iterables.toArray(ab, String.class));
        return joinsBuilder.bindAttributesTo("@JoinColumns");
    }

    private AttributeBuilder getJoinColumnAttributes(Attribute fromAttribute, Attribute toAttribute) {
        return new AttributeBuilder( //
                getJoinColumnName(fromAttribute), //
                getJoinReferencedColumnName(fromAttribute, toAttribute), //
                getNullable(fromAttribute), //
                getUnique(fromAttribute), //
                getJoinColumnInsertable(fromAttribute), //
                getJoinColumnUpdatable(fromAttribute));
    }

    private String getJoinColumnName(Attribute fromAttribute) {
        return "name = \"" + fromAttribute.getColumnNameEscaped() + "\"";
    }

    private String getJoinReferencedColumnName(Attribute from, Attribute toAttribute) {
        if (relation.isSimple()) {
            if (toAttribute.isInPk()) {
                return null; // convention, not needed
            } else {
                return "referencedColumnName = \"" + toAttribute.getColumnNameEscaped() + "\"";
            }
        }

        // composite => set it in all cases
        return "referencedColumnName = \"" + toAttribute.getColumnNameEscaped() + "\"";
    }

    private String getNullable(Attribute attribute) {
        return attribute.isNullable() ? null : "nullable = false";
    }

    private String getUnique(Attribute attribute) {
        return attribute.isUnique() ? "unique = true" : null;
    }

    private String getJoinColumnUpdatable(Attribute attribute) {
        if (hasMapsIdAnnotation()) {
            return null;
        } else {
            return attribute.isInPk() ? "updatable = false" : null;
        }
    }

    private String getJoinColumnInsertable(Attribute attribute) {
        if (hasMapsIdAnnotation()) {
            return null;
        } else {
            return attribute.isInPk() ? "insertable = false" : null;
        }
    }

    public String getCacheAnnotation() {
        CacheConfigGetter localConf = null;
        CacheConfigGetter globalConf = null;

        if (relation.isManyToOne()) {
            localConf = relation.getFromAttribute().getColumnConfig().getManyToOneConfig();
            globalConf = relation.getFromEntity().getConfig().getCelerio().getConfiguration().getDefaultManyToOneConfig();
        } else if (relation.isOneToOne()) {
            if (!relation.isInverse()) {
                localConf = relation.getFromAttribute().getColumnConfig().getOneToOneConfig();
                globalConf = relation.getFromEntity().getConfig().getCelerio().getConfiguration().getDefaultOneToOneConfig();
            } else {
                localConf = relation.getFromAttribute().getColumnConfig().getInverseOneToOneConfig();
                globalConf = relation.getFromEntity().getConfig().getCelerio().getConfiguration().getDefaultInverseOneToOneConfig();
            }
        }

        return JpaConfigHelper.getCacheAnnotation(this, localConf, globalConf);
    }

    private String getOrphanRemoval() {
        return orphanRemoval( //
                relation.getInverse().getFromAttribute().getColumnConfig().getInverseOneToOneConfig(), //
                relation.getFromEntity().getConfig().getCelerio().getConfiguration().getDefaultInverseOneToOneConfig());
    }

    // ----------------------------------------
    // CASCADE
    // ----------------------------------------

    private String getManyToOneCascade() {
        Assert.isTrue(relation.isManyToOne());
        return jpaCascade(this, //
                relation.getFromAttribute().getColumnConfig().getManyToOneConfig(), //
                relation.getFromEntity().getConfig().getCelerio().getConfiguration().getDefaultManyToOneConfig());
    }

    private String getOneToOneCascade() {
        Assert.isTrue(relation.isOneToOne());
        Assert.isTrue(!relation.isInverse());
        return jpaCascade(this, //
                relation.getFromAttribute().getColumnConfig().getOneToOneConfig(), //
                relation.getFromEntity().getConfig().getCelerio().getConfiguration().getDefaultOneToOneConfig());
    }

    private String getInverseOneToOneCascade() {
        Assert.isTrue(relation.isOneToOne());
        Assert.isTrue(relation.isInverse());
        return jpaCascade(this, //
                relation.getInverse().getFromAttribute().getColumnConfig().getInverseOneToOneConfig(), //
                relation.getFromEntity().getConfig().getCelerio().getConfiguration().getDefaultInverseOneToOneConfig());
    }

    // ----------------------------------------
    // FETCH
    // ----------------------------------------

    private String getManyToOneFetch() {
        Assert.isTrue(relation.isManyToOne());
        return jpaFetch(this, relation.getFromAttribute().getColumnConfig().getManyToOneConfig(), //
                relation.getFromEntity().getConfig().getCelerio().getConfiguration().getDefaultManyToOneConfig());
    }

    private String getOneToOneFetch() {
        Assert.isTrue(relation.isOneToOne());
        Assert.isTrue(!relation.isInverse());
        return jpaFetch(this, //
                relation.getFromAttribute().getColumnConfig().getOneToOneConfig(), //
                relation.getFromEntity().getConfig().getCelerio().getConfiguration().getDefaultOneToOneConfig());
    }

    private String getInverseOneToOneFetch() {
        Assert.isTrue(relation.isOneToOne());
        Assert.isTrue(relation.isInverse());
        return jpaFetch(this, //
                relation.getFromAttribute().getColumnConfig().getInverseOneToOneConfig(), //
                relation.getFromEntity().getConfig().getCelerio().getConfiguration().getDefaultInverseOneToOneConfig());
    }
}