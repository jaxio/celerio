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

package com.jaxio.celerio.util;

import java.util.List;

/**
 * A class may implements this interface to expose its hierarchical structure (ex: Entity in JPA).
 */
public interface Hierarchical<T> {

    String getName();

    /**
     * Returns true if the current node is the root of the hierarchy.
     */
    boolean isRoot();

    /**
     * Returns the root node of the hierarchy or the current node if it is the root.
     */
    T getRoot();

    /**
     * Returns the parent's current node or null if the current node is the root.
     */
    T getParent();

    /**
     * Returns the current node's ancestor or an empty list if this element is the root.
     * The first element in the list is the root.
     */
    List<T> getParents();

    /**
     * Returns the current node's children.
     */
    List<T> getChildren();
}