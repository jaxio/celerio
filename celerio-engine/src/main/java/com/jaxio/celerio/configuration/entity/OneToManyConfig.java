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

package com.jaxio.celerio.configuration.entity;

import com.jaxio.celerio.configuration.MetaAttribute;
import lombok.Setter;

import java.util.List;

import static org.springframework.util.StringUtils.hasLength;

@Setter
public class OneToManyConfig implements CascadeGetter, CacheConfigGetter, FetchTypeGetter, OrderByGetter, AssociationActionGetter, LabelGetter {
    private String var;
    private String elementVar;
    private Boolean orphanRemoval;
    private FetchType fetch;
    private String orderBy;
    private Integer displayOrder;
    private List<Label> labels;
    private List<Cascade> cascades;
    private CacheConfig cacheConfig;
    private AssociationAction associationAction;
    private List<MetaAttribute> metaAttributes;

    /*
     * The variable name for the collection. It should be plural, for example: 'children'. Please configure also the elementVar as the singular of this var.
     */
    public String getVar() {
        return var;
    }

    public boolean hasVar() {
        return hasLength(getVar());
    }

    /*
     * The variable name for an element of the collection. For example, if the variable name for the collection is 'children', the elementVar should be child.
     * This elementVar will be used to generate convenient methods for the collection, such as an adder method addChild(YourType child).
     */
    public String getElementVar() {
        return elementVar;
    }

    public boolean hasElementVar() {
        return hasLength(elementVar);
    }

    /*
     * The JPA orphanRemoval for this association.
     */
    public Boolean getOrphanRemoval() {
        return orphanRemoval;
    }

    /*
     * The JPA fetch type for this association. Use NONE if you do not want any fetchType to be set.
     */
    public FetchType getFetch() {
        return fetch;
    }

    /*
     * Specifies the ordering of the elements. If not null, the JPA <code>@OrderBy</code> annotation is used.
     * Example: orderBy="birthdate DESC" will generate <code>@OrderBy("birthdate DESC")</code>
     */
    public String getOrderBy() {
        return orderBy;
    }


    /*
     * The order of appearance of this one to many in forms, from top to bottom or from left to right. It defaults to alphabetical order.
     */
    public Integer getDisplayOrder() {
        return displayOrder;
    }
    /*
     * The labels for this association.
     */
    public List<Label> getLabels() {
        return labels;
    }

    /*
     * The list of JPA cascade types for the this association.
     */
    public List<Cascade> getCascades() {
        return cascades;
    }

    /*
     * Convenient for hbm2celerio to avoid dead tags.
     */
    public void forceCascadesToNullIfEmpty() {
        if (cascades != null && cascades.isEmpty()) {
            cascades = null;
        }
    }

    /*
     * The 2d level cache configuration for this association.
     */
    public CacheConfig getCacheConfig() {
        return cacheConfig;
    }

    /*
     * Configure which actions should be generated for this association in the front end layer.
     */
    public AssociationAction getAssociationAction() {
        return associationAction;
    }

    /**
     * Meta attributes are free form key value pairs.
     */
    public List<MetaAttribute> getMetaAttributes() {
        return metaAttributes;
    }

    public boolean hasMetaAttributes() {
        return metaAttributes != null && !metaAttributes.isEmpty();
    }
}
