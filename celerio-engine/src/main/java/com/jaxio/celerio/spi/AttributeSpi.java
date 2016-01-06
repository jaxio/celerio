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

import com.jaxio.celerio.model.Attribute;

import java.util.ServiceLoader;

/**
 * The AttributeSpi interface allows Celerio template developers to plug new {@link Attribute} helpers.
 * <p>
 * The implementations are loaded by the java's {@link ServiceLoader}.
 */
public interface AttributeSpi {

    /**
     * Invoked by Celerio, when this AttributeSpi is binded on an attribute instance.
     */
    void init(Attribute attribute);

    /**
     * The var name under which the target is bound during template evaluation.<br>
     * For example if <code>velocityVar()</code> methods returns <code>myextension</code>,
     * the corresponding target is given by <code>$attribute.myextension</code>.
     */
    String velocityVar();

    /**
     * The bound target.
     */
    Object getTarget();
}
