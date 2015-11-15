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

@Setter
public class AssociationAction {
    private Boolean create;
    private Boolean edit;
    private Boolean view;
    private Boolean select;
    private Boolean autoComplete;
    private Boolean remove;

    /*
     * Generate code in order to create a new target entity from the source entity main edit page.
     */
    public Boolean getCreate() {
        return create;
    }

    /*
     * Generate code in order to edit a target entity from the source entity main edit page.
     */
    public Boolean getEdit() {
        return edit;
    }

    /*
     * Generate code in order to view a target entity from the source entity main edit page.
     */
    public Boolean getView() {
        return view;
    }

    /*
     * Generate code in order to select an existing target entity from the source entity main edit page. Note: it does not apply to one-to-many association.
     */
    public Boolean getSelect() {
        return select;
    }

    /*
     * Generate code in order to select an existing target entity, using an autoComplete component, from the source entity main edit page. Note: it does not
     * apply to one-to-many association.
     */
    public Boolean getAutoComplete() {
        return autoComplete;
    }

    /*
     * Generate code in order to remove a target entity from the source entity main edit page.
     */
    public Boolean getRemove() {
        return remove;
    }
}