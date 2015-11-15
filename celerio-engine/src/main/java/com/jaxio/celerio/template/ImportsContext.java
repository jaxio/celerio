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

package com.jaxio.celerio.template;

/**
 * Holds the {@link ImportsHolder} and the <code>isExtendedByUser</code> boolean that are used
 * during a Java template evaluation.
 */
public class ImportsContext {

    private static final InheritableThreadLocal<ImportsHolder> currentImportsHolder = new InheritableThreadLocal<ImportsHolder>();
    private static final InheritableThreadLocal<Boolean> isExtendedByUser = new InheritableThreadLocal<Boolean>();

    static {
        isExtendedByUser.set(false);
    }

    /**
     * Returns the {@link ImportsHolder} that is bound to the current template evaluation.
     */
    public static ImportsHolder getCurrentImportsHolder() {
        return currentImportsHolder.get();
    }

    /**
     * Bind the passed {@link ImportsHolder} to the current template evaluation.
     */
    public static void setCurrentImportsHolder(ImportsHolder importsHolder) {
        currentImportsHolder.set(importsHolder);
    }

    public static void setIsExtendedByUser(boolean value) {
        isExtendedByUser.set(value);
    }

    /**
     * @see ImportsHolder#add(String)
     */
    public static void addImport(String fullType) {
        getCurrentImportsHolder().add(fullType);
    }


    /**
     * Whether the current generated java class is taken over by the end developer.
     */
    public static boolean isExtendedByUser() {
        return isExtendedByUser.get();
    }
}