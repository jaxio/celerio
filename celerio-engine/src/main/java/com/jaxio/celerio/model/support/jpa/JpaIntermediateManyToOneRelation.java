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

import com.jaxio.celerio.configuration.entity.CacheConfigGetter;
import com.jaxio.celerio.configuration.entity.ManyToOneConfig;
import com.jaxio.celerio.model.PackageImportAdder;
import com.jaxio.celerio.model.Relation;
import com.jaxio.celerio.spi.RelationSpi;
import com.jaxio.celerio.template.ImportsContext;
import com.jaxio.celerio.util.AnnotationBuilder;
import com.jaxio.celerio.util.AttributeBuilder;

import java.util.List;

import static com.jaxio.celerio.model.support.jpa.JpaConfigHelper.jpaCascade;
import static com.jaxio.celerio.model.support.jpa.JpaConfigHelper.jpaFetch;

public class JpaIntermediateManyToOneRelation implements RelationSpi, PackageImportAdder {
    private Relation relation;

    @Override
    public boolean compatibleWith(Relation relation) {
        return relation.isIntermediate() && relation.isManyToOne();
    }

    @Override
    public String velocityVar() {
        return "jpa";
    }

    @Override
    public Object getTarget() {
        return this;
    }

    @Override
    public void init(Relation relation) {
        this.relation = relation;
    }


    public List<String> getAnnotations() {
        return new AnnotationBuilder( //
                getManyToOneAnnotation(), //
                getJoinTableAnnotation(), //
                getCacheAnnotation()) // TODO ? getSpecificJoinFetchAnnotation(), as for many to many ?
                .getAnnotations();
    }

    private String getManyToOneAnnotation() {
        addImport("javax.persistence.ManyToOne");
        AttributeBuilder ab = new AttributeBuilder();
        ManyToOneConfig manyToOneDefaultConf = relation.getMiddleEntity().getConfig().getCelerio().getConfiguration().getDefaultManyToOneConfig();
        ab.add(jpaCascade(this, relation.getCascadeGetter(), manyToOneDefaultConf));
        ab.add(jpaFetch(this, relation.getFetchTypeGetter(), manyToOneDefaultConf));
        return ab.bindAttributesTo("@ManyToOne");
    }

    private String getJoinTableAnnotation() {
        if (relation.isInverse()) {
            return null;
        }
        addImport("javax.persistence.JoinTable");
        String fromJoinColumn = getJoinColumn(relation.getMiddleToLeft());
        String toJoinColumn = getJoinColumn(relation.getMiddleToRight());
        return "@JoinTable(name = \"" + relation.getMiddleEntity().getTableNameEscaped() + "\", joinColumns = " + fromJoinColumn + ", inverseJoinColumns = "
                + toJoinColumn + ")";
    }

    private String getJoinColumn(Relation relation) {
        AttributeBuilder builder = new AttributeBuilder();
        if (relation.isSimple()) {
            builder.add(relation.getFromAttribute().getColumnNameEscaped());
        } else if (relation.isComposite()) {
            for (Relation subRelation : relation.getRelations()) {
                builder.add(subRelation.getFromAttribute().getColumnNameEscaped());
            }
        } else {
            throw new IllegalStateException("What is this relation ?");
        }
        addImport("javax.persistence.JoinColumn");
        return "@JoinColumn(name = \"" + builder.getAttributes() + "\")";
    }

    public String getCacheAnnotation() {
        CacheConfigGetter localConf = relation.getMiddleToRight().getFromAttribute().getColumnConfig().getManyToOneConfig();
        CacheConfigGetter globalConf = relation.getMiddleEntity().getConfig().getCelerio().getConfiguration().getDefaultManyToOneConfig();
        return JpaConfigHelper.getCacheAnnotation(this, localConf, globalConf);
    }

    @Override
    public void addImport(String fullType) {
        ImportsContext.getCurrentImportsHolder().add(fullType);
    }
}
