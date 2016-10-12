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

public enum CascadeType {

    /**
     * Do not set any Cascade, let JPA default applies
     */
    NONE,

    /**
     * Cascade all operations
     */
    ALL,

    /**
     * Cascade persist operation
     */
    PERSIST,

    /**
     * Cascade merge operation
     */
    MERGE,

    /**
     * Cascade remove operation
     */
    REMOVE,

    /**
     * Cascade refresh operation
     */
    REFRESH,


    /**
     * Cascade detach operation
     *
     * @since Java Persistence 2.0
     *
     */
    DETACH;

    public boolean isJpaType() {
        return this != NONE;
    }

    public javax.persistence.CascadeType asJpaType() {
        switch (this) {
            case ALL:
                return javax.persistence.CascadeType.ALL;
            case PERSIST:
                return javax.persistence.CascadeType.PERSIST;
            case MERGE:
                return javax.persistence.CascadeType.MERGE;
            case REMOVE:
                return javax.persistence.CascadeType.REMOVE;
            case REFRESH:
                return javax.persistence.CascadeType.REFRESH;
            case DETACH:
                return javax.persistence.CascadeType.DETACH;
            default:
                throw new IllegalStateException("There is no JPA equivalent");
        }
    }
}
