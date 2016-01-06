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
import com.jaxio.celerio.configuration.entity.ManyToManyConfig;
import com.jaxio.celerio.model.PackageImportAdder;
import com.jaxio.celerio.model.Relation;
import com.jaxio.celerio.spi.RelationSpi;
import com.jaxio.celerio.template.ImportsContext;
import com.jaxio.celerio.util.AnnotationBuilder;
import com.jaxio.celerio.util.AttributeBuilder;

import java.util.List;

import static com.jaxio.celerio.model.support.jpa.JpaConfigHelper.*;

public class JpaManyToManyRelation implements RelationSpi, PackageImportAdder {
    private Relation relation;

    @Override
    public boolean compatibleWith(Relation relation) {
        return relation.isManyToMany();
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
                getManyToManyAnnotation(), //
                getJoinTableAnnotation(), //
                getOrderByAnnotation(), //
                getCacheAnnotation(), getSpecificJoinFetchAnnotation()) //
                .getAnnotations();
    }

    private String getManyToManyAnnotation() {
        addImport("javax.persistence.ManyToMany");
        AttributeBuilder ab = new AttributeBuilder();

        if (relation.isInverse()) {
            ManyToManyConfig inverseManyToManyDefaultConf = relation.getMiddleEntity().getConfig().getCelerio().getConfiguration()
                    .getDefaultInverseManyToManyConfig();

            ab.add("mappedBy = \"" + relation.getFrom().getVars() + "\"");
            ab.add(jpaCascade(this, relation.getCascadeGetter(), inverseManyToManyDefaultConf));
            ab.add(jpaFetch(this, relation.getFetchTypeGetter(), inverseManyToManyDefaultConf));
        } else {
            ManyToManyConfig manyToManyDefaultConf = relation.getMiddleEntity().getConfig().getCelerio().getConfiguration().getDefaultManyToManyConfig();
            ab.add(jpaCascade(this, relation.getCascadeGetter(), manyToManyDefaultConf));
            ab.add(jpaFetch(this, relation.getFetchTypeGetter(), manyToManyDefaultConf));
        }

        return ab.bindAttributesTo("@ManyToMany");
    }

    private String getOrderByAnnotation() {
        ManyToManyConfig defaultConf;
        if (relation.isInverse()) {
            defaultConf = relation.getMiddleEntity().getConfig().getCelerio().getConfiguration().getDefaultInverseManyToManyConfig();
        } else {
            defaultConf = relation.getMiddleEntity().getConfig().getCelerio().getConfiguration().getDefaultManyToManyConfig();
        }
        return orderByAnnotation(this, relation.getOrderByGetter(), defaultConf);
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
        CacheConfigGetter localConf = null;
        CacheConfigGetter globalConf = null;

        if (!relation.isInverse()) {
            localConf = relation.getMiddleToRight().getFromAttribute().getColumnConfig().getManyToManyConfig();
            globalConf = relation.getMiddleEntity().getConfig().getCelerio().getConfiguration().getDefaultManyToManyConfig();
        } else {
            localConf = relation.getMiddleToLeft().getFromAttribute().getColumnConfig().getManyToManyConfig();
            globalConf = relation.getMiddleEntity().getConfig().getCelerio().getConfiguration().getDefaultInverseManyToManyConfig();
        }

        return JpaConfigHelper.getCacheAnnotation(this, localConf, globalConf);
    }

    /**
     * When you have multiple eager bags you need to specify how you want to make them eager.
     */
    private String getSpecificJoinFetchAnnotation() {
        List<Relation> manyToManyRelations = relation.getToEntity().getManyToMany().getList();
        // we have only on many-to-many ? No problem, move on
        if (manyToManyRelations.size() <= 1) {
            return null;
        }
        // we have many many to many relations ? and one requires eager, then we need to set the fetchmode
        for (Relation relation : manyToManyRelations) {
            if (relation.getFetchTypeGetter() != null && relation.getFetchTypeGetter().getFetch() != null && relation.getFetchTypeGetter().getFetch().isEager()) {
                addImport("org.hibernate.annotations.Fetch");
                addImport("org.hibernate.annotations.FetchMode");
                return "@Fetch(value = FetchMode.SUBSELECT)";
            }
        }
        // no eager requested, move on
        return null;
    }

    @Override
    public void addImport(String fullType) {
        ImportsContext.getCurrentImportsHolder().add(fullType);
    }
}