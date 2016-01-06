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

import lombok.Setter;

import static org.springframework.util.StringUtils.hasLength;

/**
 * Configuration element for Hibernate/EhCache 2d level cache.
 */
@Setter
public class CacheConfig {
    private CacheConcurrencyStrategy usage;
    private String include;
    private String region;

    /*
     * Hibernate/EhCache CacheConcurrencyStrategy. Use NONE if you do not want any Cache annotation to be set.
     */
    public CacheConcurrencyStrategy getUsage() {
        return usage;
    }

    public String getInclude() {
        return include;
    }

    public String getRegion() {
        return region;
    }

    public boolean hasUsage() {
        return usage != null;
    }

    public boolean hasInclude() {
        return hasLength(include);
    }

    public boolean hasRegion() {
        return hasLength(region);
    }
}