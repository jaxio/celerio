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

import com.jaxio.celerio.model.Relation;
import com.jaxio.celerio.spi.support.AbstractRelationSpi;
import com.jaxio.celerio.util.AnnotationBuilder;
import com.jaxio.celerio.util.AttributeBuilder;

import java.util.List;

import static com.jaxio.celerio.model.support.jpa.JpaConfigHelper.*;

public class JpaOneToManyRelation extends AbstractRelationSpi {

    @Override
    public boolean compatibleWith(Relation relation) {
        return (relation.isOneToMany() || relation.isOneToVirtualOne()) && !relation.isIntermediate();
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
        if (!(relation.isOneToMany() || relation.isOneToVirtualOne())) {
            throw new IllegalArgumentException("Expecting a oneToMany or oneToVirtualOne relation");
        }
        super.init(relation);
    }

    public List<String> getAnnotations() {
        return new AnnotationBuilder( //
                getOneToManyAnnotation(), //
                getOrderByAnnotation(), //
                getCacheAnnotation()) //
                .getAnnotations();
    }

    public String getOneToManyAnnotation() {
        addImport("javax.persistence.OneToMany");
        AttributeBuilder ab = new AttributeBuilder();
        ab.add("mappedBy = \"" + relation.getInverse().getTo().getVar() + "\"");
        ab.add(getOrphanRemoval());
        ab.add(getOneToManyFetch());
        ab.add(getOneToManyCascade());
        return ab.bindAttributesTo("@OneToMany");
    }

    public String getOrderByAnnotation() {
        return orderByAnnotation(this, //
                relation.getInverse().getFromAttribute().getColumnConfig().getOneToManyConfig(), //
                relation.getFromEntity().getConfig().getCelerio().getConfiguration().getDefaultOneToManyConfig());
    }

    public String getCacheAnnotation() {
        return JpaConfigHelper.getCacheAnnotation(this, //
                relation.getFromAttribute().getColumnConfig().getOneToManyConfig(), relation.getFromEntity().getConfig().getCelerio().getConfiguration()
                        .getDefaultOneToManyConfig());
    }

    public boolean getHasTrueOrphanRemoval() {
        return hasTrueOrphanRemoval( //
                relation.getInverse().getFromAttribute().getColumnConfig().getOneToManyConfig(), //
                relation.getFromEntity().getConfig().getCelerio().getConfiguration().getDefaultOneToManyConfig());
    }

    private String getOrphanRemoval() {
        return orphanRemoval( //
                relation.getInverse().getFromAttribute().getColumnConfig().getOneToManyConfig(), //
                relation.getFromEntity().getConfig().getCelerio().getConfiguration().getDefaultOneToManyConfig());
    }

    private String getOneToManyFetch() {
        return jpaFetch(this, //
                relation.getInverse().getFromAttribute().getColumnConfig().getOneToManyConfig(), //
                relation.getFromEntity().getConfig().getCelerio().getConfiguration().getDefaultOneToManyConfig());
    }

    private String getOneToManyCascade() {
        return jpaCascade(this, relation.getInverse().getFromAttribute().getColumnConfig().getOneToManyConfig(), //
                relation.getFromEntity().getConfig().getCelerio().getConfiguration().getDefaultOneToManyConfig());
    }
}
