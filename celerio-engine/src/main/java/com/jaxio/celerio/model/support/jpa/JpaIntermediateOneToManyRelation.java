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
import com.jaxio.celerio.configuration.entity.OneToManyConfig;
import com.jaxio.celerio.model.PackageImportAdder;
import com.jaxio.celerio.model.Relation;
import com.jaxio.celerio.spi.RelationSpi;
import com.jaxio.celerio.template.ImportsContext;
import com.jaxio.celerio.util.AnnotationBuilder;
import com.jaxio.celerio.util.AttributeBuilder;

import java.util.List;

import static com.jaxio.celerio.model.support.jpa.JpaConfigHelper.*;

public class JpaIntermediateOneToManyRelation implements RelationSpi, PackageImportAdder {
    private Relation relation;
    private OneToManyConfig oneToManyDefaultConf;

    @Override
    public boolean compatibleWith(Relation relation) {
        return relation.isIntermediate() && relation.isOneToMany();
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
        oneToManyDefaultConf = relation.getMiddleEntity().getConfig().getCelerio().getConfiguration().getDefaultOneToManyConfig();
    }

    public List<String> getAnnotations() {
        return new AnnotationBuilder( //
                getOneToManyAnnotation(), //
                getOrderByAnnotation(), //
                getCacheAnnotation()) // , getSpecificJoinFetchAnnotation()) //
                .getAnnotations();
    }

    private String getOneToManyAnnotation() {
        addImport("javax.persistence.OneToMany");
        AttributeBuilder ab = new AttributeBuilder();

        ab.add("mappedBy = \"" + relation.getFrom().getVar() + "\"");
        ab.add(getOrphanRemoval());
        ab.add(jpaCascade(this, relation.getCascadeGetter(), oneToManyDefaultConf));
        ab.add(jpaFetch(this, relation.getFetchTypeGetter(), oneToManyDefaultConf));
        return ab.bindAttributesTo("@OneToMany");
    }

    private String getOrphanRemoval() {
        return orphanRemoval( //
                relation.getMiddleToLeft().getFromAttribute().getColumnConfig().getOneToManyConfig(), //
                oneToManyDefaultConf);
    }

    private String getOrderByAnnotation() {
        return orderByAnnotation(this, relation.getMiddleToLeft().getFromAttribute().getColumnConfig().getOneToManyConfig(), //
                oneToManyDefaultConf);
    }

    public String getCacheAnnotation() {
        CacheConfigGetter localConf = relation.getMiddleToLeft().getFromAttribute().getColumnConfig().getOneToManyConfig();
        return JpaConfigHelper.getCacheAnnotation(this, localConf, oneToManyDefaultConf);
    }

    @Override
    public void addImport(String fullType) {
        ImportsContext.getCurrentImportsHolder().add(fullType);
    }
}
