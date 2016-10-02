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

package com.jaxio.celerio.model;

import com.jaxio.celerio.configuration.entity.CascadeGetter;
import com.jaxio.celerio.configuration.entity.FetchTypeGetter;
import com.jaxio.celerio.configuration.entity.OrderByGetter;
import com.jaxio.celerio.support.Namer;
import com.jaxio.celerio.util.Labels;
import com.jaxio.celerio.util.Named;

import java.util.List;

/**
 * JPA Relation meta-information.
 */
public interface Relation extends Named {
    String getKind();

    /**
     * In most cases, a foreign key attribute.
     */
    Attribute getFromAttribute();

    /**
     * The target attribute, often a primary key attribute.
     */
    Attribute getToAttribute();

    /**
     * In case of a composite relation, the foreign key attributes.
     */
    List<Attribute> getFromAttributes();

    /**
     * In case of a composite relation, the target attributes, often a composite pk.
     */
    List<Attribute> getToAttributes();

    /**
     * AttributePairs that form the relation. In case of composite relation, there are more than 1 {@link AttributePair}.
     */
    List<AttributePair> getAttributePairs();

    Entity getFromEntity();


    Entity getToEntity();

    Namer getFrom();

    Namer getTo();

    boolean hasInverse();

    boolean isInverse();

    Relation getInverse();

    Relation createInverse();

    boolean isMandatory();

    boolean isUnique();

    /**
     * A simple relation is neither composite nor intermediate.
     *
     * @return
     */
    boolean isSimple();

    /**
     * A composite relation involves more than 1 foreign key.
     */
    boolean isComposite();

    /**
     * An intermediate relation is a relation involving an intermediate table.
     */
    boolean isIntermediate();

    List<Relation> getRelations();

    /**
     * For intermediate relation only. The intermediate entity corresponding to the intermediate table.
     */
    Entity getMiddleEntity();

    /**
     * For intermediate relation only. An helper 'invisible' relation.
     */
    Relation getMiddleToLeft();

    /**
     * For intermediate relation only. An helper 'invisible' relation.
     */
    Relation getMiddleToRight();

    boolean isOneToOne();

    boolean isOneToVirtualOne();

    boolean isOneToMany();

    boolean isManyToMany();

    boolean isManyToOne();

    boolean isCollection();

    String getLabelName();

    Labels getLabels();

    String getDisplayOrderAsString();

    String getOneLineComment();

    CascadeGetter getCascadeGetter();

    FetchTypeGetter getFetchTypeGetter();

    OrderByGetter getOrderByGetter();

    boolean isGenCreate();

    void setGenCreate(boolean value);

    boolean isGenEdit();

    void setGenEdit(boolean value);

    boolean isGenView();

    void setGenView(boolean value);

    boolean isGenSelect();

    void setGenSelect(boolean value);

    boolean isGenAutoComplete();

    void setGenAutoComplete(boolean value);

    boolean isGenRemove();

    void setGenRemove(boolean value);
}
