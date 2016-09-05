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
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.springframework.util.StringUtils.hasLength;

@Setter
public class ManyToOneConfig implements CascadeGetter, CacheConfigGetter, FetchTypeGetter, AssociationActionGetter, LabelGetter, TargetEntityNameGetter {
    private String var;
    private FetchType fetch;
    private String targetEntityName;
    private List<Label> labels;
    private List<Cascade> cascades;
    private CacheConfig cacheConfig;
    private AssociationAction associationAction;
    private List<MetaAttribute> metaAttributes;

    /*
     * The variable name for association. It should be singular, for example: 'parent'.
     */
    public String getVar() {
        return var;
    }

    public boolean hasVar() {
        return hasLength(getVar());
    }

    /*
     * The JPA fetch type for this association. Use NONE if you do not want any fetchType to be set.
     */
    public FetchType getFetch() {
        return fetch;
    }

    /*
     * If the target entity is part of a SINGLE_TABLE inheritance hierarchy, you must set name of the target entity as Celerio cannot guess it.
     */
    public String getTargetEntityName() {
        return targetEntityName;
    }

    public boolean hasTargetEntityName() {
        return isNotBlank(targetEntityName);
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
     * Configure which actions should or should not be generated for this association in the front end layer.
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