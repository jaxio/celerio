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

package com.jaxio.celerio.factory.conventions;

import com.jaxio.celerio.model.Attribute;
import com.jaxio.celerio.model.Entity;
import com.jaxio.celerio.model.support.account.SavedSearchAttributes;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import static com.google.common.collect.Lists.newArrayList;

@Slf4j
public class SavedSearchConvention {

    private Iterable<String> formClassCandidates = newArrayList("formClass", "formClassname");
    private Iterable<String> nameCandidates = newArrayList("name");
    private Iterable<String> formContentCandidates = newArrayList("formContent");
    private Iterable<String> entityNames = newArrayList("savedSearch", "savedSearchForm");

    @Getter
    private final SavedSearchAttributes savedSearchAttributes;
    private final Entity e;
    @Getter
    private boolean match;

    public SavedSearchConvention(Entity e) {
        this.e = e;
        this.savedSearchAttributes = attributes(e);
        this.match = buildMatch();
    }

    private boolean buildMatch() {
        if (!match(e.getModel().getVar(), entityNames)) {
            return false;
        }
        boolean complete = savedSearchAttributes.isComplete();
        if (complete) {
            log.info(e.getName() + ": assumed by convention to be a SavedSearch table");
        }
        return complete;
    }

    private SavedSearchAttributes attributes(Entity e) {
        SavedSearchAttributes savedSearchAttributes = new SavedSearchAttributes();
        if (e.hasSimplePk() //
                && detectFormClass(e, savedSearchAttributes) //
                && detectName(e, savedSearchAttributes) //
                && detectFormContent(e, savedSearchAttributes) //
                && detectAccount(e, savedSearchAttributes)) {
            return savedSearchAttributes;
        }
        return new SavedSearchAttributes();
    }

    private boolean detectFormContent(Entity e, SavedSearchAttributes savedSearchAttributes) {
        for (Attribute a : e.getAttributes().getList()) {
            if (a.isBlob() && match(a.getVar(), formContentCandidates)) {
                savedSearchAttributes.setFormContent(a);
                log.debug("'formContent' candidate: " + a.getVar() + " found on " + e.getName());
                return true;
            }
        }
        return false;
    }

    private boolean detectName(Entity e, SavedSearchAttributes savedSearchAttributes) {
        for (Attribute a : e.getAttributes().getList()) {
            if (a.isString() && match(a.getVar(), nameCandidates)) {
                savedSearchAttributes.setName(a);
                log.debug("'name' candidate: " + a.getVar() + " found on " + e.getName());
                return true;
            }
        }
        return false;
    }

    private boolean detectFormClass(Entity e, SavedSearchAttributes savedSearchAttributes) {
        for (Attribute a : e.getAttributes().getList()) {
            if (a.isString() && match(a.getVar(), formClassCandidates)) {
                savedSearchAttributes.setFormClass(a);
                log.debug("'formClass' candidate: " + a.getVar() + " found on " + e.getName());
                return true;
            }
        }
        return false;
    }

    private boolean detectAccount(Entity e, SavedSearchAttributes savedSearchAttributes) {
        for (Attribute a : e.getAttributes().getList()) {
            if (a.isSimpleFk()) {
                if (!e.isManyToManyJoinEntity()) {
                    if (a.getXToOneRelation().getToEntity().isAccount()) {
                        savedSearchAttributes.setAccount(a);
                        log.debug("'account' candidate: " + a.getVar() + " found on " + e.getName());
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean match(String target, Iterable<String> candidates) {
        for (String candidate : candidates) {
            if (candidate.equalsIgnoreCase(target)) {
                return true;
            }
        }
        return false;
    }
}
