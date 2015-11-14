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
 * Exposes friendly velocity methods around a List.
 */
public interface ListHolder<T> {

    /**
     * The List this ListHolder holds.
     */
    List<T> getList();

    /**
     * Is the List held by this ListHolder empty?
     */
    boolean isEmpty();

    /**
     * Is the List held by this ListHolder not empty?
     */
    boolean isNotEmpty();

    /**
     * The size of the List held by this ListHolder.
     */
    int getSize();
}