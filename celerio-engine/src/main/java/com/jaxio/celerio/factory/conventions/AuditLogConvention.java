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
import com.jaxio.celerio.model.support.AuditLogAttribute;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

import static com.google.common.collect.Lists.newArrayList;

@Slf4j
public class AuditLogConvention {
    private Iterable<String> authorCandidates = newArrayList("author", "auteur");
    private Iterable<String> operationCandidates = newArrayList("event");
    private Iterable<String> operationDateCandidates = newArrayList("eventDate");
    private Iterable<String> string1Candidates = newArrayList("stringAttribute1", "attribute1", "string1");
    private Iterable<String> string2Candidates = newArrayList("stringAttribute2", "attribute2", "string2");
    private Iterable<String> string3Candidates = newArrayList("stringAttribute3", "attribute3", "string3");
    private ArrayList<String> entityNames = newArrayList("auditLog", "auditTrail", "audit");
    @Getter
    private final AuditLogAttribute auditLogAttribute;
    private final Entity e;
    @Getter
    private final boolean match;

    public AuditLogConvention(Entity e) {
        this.e = e;
        auditLogAttribute = attributes(e);
        this.match = buildMatch();
    }

    private boolean buildMatch() {
        if (!match(e.getModel().getVar(), entityNames)) {
            return false;
        }
        boolean complete = auditLogAttribute.isComplete();
        if (complete) {
            log.info(e.getName() + ": assumed by convention to be an AuditLog table");
        }
        return complete;
    }

    /**
     * Is this entity the 'audit log' entity?
     */
    private AuditLogAttribute attributes(Entity e) {
        AuditLogAttribute auditLogAttributes = new AuditLogAttribute();
        matchAuthor(e, auditLogAttributes);
        matchEvent(e, auditLogAttributes);
        matchEventDate(e, auditLogAttributes);
        matchStringAttribute1(e, auditLogAttributes);
        matchStringAttribute2(e, auditLogAttributes);
        matchStringAttribute3(e, auditLogAttributes);
        return auditLogAttributes;
    }

    private void matchStringAttribute1(Entity e, AuditLogAttribute auditLogAttributes) {
        for (Attribute a : e.getAttributes().getList()) {
            if (a.isString() && match(a.getVar(), string1Candidates)) {
                auditLogAttributes.setStringAttribute1(a);
                log.debug("'stringAttribute1' candidate: " + a.getVar() + " found on " + e.getName());
                return;
            }
        }
    }

    private void matchStringAttribute2(Entity e, AuditLogAttribute auditLogAttributes) {
        for (Attribute a : e.getAttributes().getList()) {
            if (a.isString() && match(a.getVar(), string2Candidates)) {
                auditLogAttributes.setStringAttribute2(a);
                log.debug("'stringAttribute2' candidate: " + a.getVar() + " found on " + e.getName());
                return;
            }
        }
    }

    private void matchStringAttribute3(Entity e, AuditLogAttribute auditLogAttributes) {
        for (Attribute a : e.getAttributes().getList()) {
            if (a.isString() && match(a.getVar(), string3Candidates)) {
                auditLogAttributes.setStringAttribute3(a);
                log.debug("'stringAttribute3' candidate: " + a.getVar() + " found on " + e.getName());
                return;
            }
        }
    }

    private void matchEventDate(Entity e, AuditLogAttribute auditLogAttributes) {
        for (Attribute a : e.getDateAttributes().getList()) {
            if (a.isDate() && match(a.getVar(), operationDateCandidates)) {
                auditLogAttributes.setEventDate(a);
                log.debug("'eventDate' candidate: " + a.getVar() + " found on " + e.getName());
                return;
            }
        }
    }

    private void matchEvent(Entity e, AuditLogAttribute auditLogAttributes) {
        for (Attribute a : e.getSimpleStringAttributes().getList()) {
            if (a.isString() && match(a.getVar(), operationCandidates)) {
                auditLogAttributes.setEvent(a);
                log.debug("'event' candidate: " + a.getVar() + " found on " + e.getName());
                return;
            }
        }
    }

    private void matchAuthor(Entity e, AuditLogAttribute auditLogAttributes) {
        for (Attribute a : e.getSimpleStringAttributes().getList()) {
            if (a.isString() && match(a.getVar(), authorCandidates)) {
                auditLogAttributes.setAuthor(a);
                log.debug("'author' candidate: " + a.getVar() + " found on " + e.getName());
                return;
            }
        }
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
