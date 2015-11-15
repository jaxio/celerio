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

package com.jaxio.celerio.support;


/**
 * Namer give you access to some commonly used vars / methods.
 */
public interface Namer {

    /**
     * The java Type name, without any package information. Ex: BankAccount.
     */
    String getType();

    /**
     * The java Type name in lower case, without any package information.
     * Used in general to construct sub package.  Ex: bankaccount.
     */
    String getTypeLow();

    /**
     * The path corresponding to the package name. For example <code>"com/company/project/domain"</code> if the package name is <code>com.company.project.domain</code>.
     */
    String getPath();

    /**
     * The full package name. Ex: com.company
     */
    String getPackageName();

    /**
     * The full Java type, with package information. Ex: com.company.project.domain.BankAccount.
     */
    String getFullType();

    /**
     * The var for the current type. Starts with a lower case. Ex: parentBankAccount.
     */
    String getVar();

    /**
     * Same as {@link #getVar} but starts with an upper case. Ex: ParentBankAccount.
     * Used to construct derived method.
     */
    String getVarUp();

    /**
     * The var for a current type collection. Starts with a lower case. Ex: parentBankAccounts.
     */
    String getVars();

    /**
     * Same as {@link #getVars} but starts with an upper case. Ex: ParentBankAccounts.
     */
    String getVarsUp();

    /**
     * Fluent method name. Ex: withParentBankAccount.
     */
    String getWith();

    /**
     * Name of the adder method to use to add element in the var returned by {@link #getVars}.
     * Ex: addParentBankAccount
     */
    String getAdder();

    /**
     * Name of the adder method to use to add multiple elements in the var returned by {@link #getVars}.
     * Ex: addParentBankAccounts
     */
    String getAdders();

    /**
     * Name of the 'contains' method to use to check whether the collection var returned by {@link #getVars}
     * contains an element. Ex: containsParentBankAccount
     */
    String getContains();

    /**
     * Name of the getter method to use when getting the var returned by {@link #getVar}.
     * Ex: getParentBankAccount
     */
    String getGetter();

    /**
     * Name of the getter method to use when getting the var returned by {@link #getVars}.
     * Ex: getParentBankAccounts
     */
    String getGetters();

    /**
     * Name of the remover method to use when removing an element from the collection var returned by {@link #getVars}.
     * Ex: removeParentBankAccount
     */
    String getRemover();

    /**
     * Name of the remover method to use when removing multiple elements from the collection var returned by {@link #getVars}.
     * Ex: removeParentBankAccounts
     */
    String getRemovers();

    /**
     * Name to the setter method to use when setting the var returned by {@link #getVar}.
     * Ex: setParentBankAccount
     */
    String getSetter();

    /**
     * Name to the setter method to use when setting the var returned by {@link #getVars}.
     * Ex: setParentBankAccounts
     */
    String getSetters();

    /**
     * Name to the editer method to use when editing the var returned by {@link #getVar}.
     * Ex: editParentBankAccount
     */
    String getEditer();

    String getHibernateFilterName();
}
