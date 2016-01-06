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

package com.jaxio.celerio.spi;

import com.jaxio.celerio.model.Entity;

import java.util.ServiceLoader;

/**
 * The EntitySpi interface allows Celerio template developers to plug new {@link Entity} helpers.
 * <p>
 * The implementations are loaded by the java's {@link ServiceLoader}.
 */
public interface EntitySpi {

    /**
     * Invoked by Celerio, when the EntitySpi implementation is binded on an entity instance.
     */
    void init(Entity entity);

    /**
     * The var name under which the target is bound during template evaluation.<br>
     * For example if <code>velocityVar()</code> methods returns <code>myextension</code>,
     * the target is given by <code>$entity.myextension</code>.
     */
    String velocityVar();

    /**
     * The bound target.
     */
    Object getTarget();
}