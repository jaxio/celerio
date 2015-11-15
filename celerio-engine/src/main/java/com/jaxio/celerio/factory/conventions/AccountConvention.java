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
import com.jaxio.celerio.model.Relation;
import com.jaxio.celerio.model.support.account.AccountAttributes;
import com.jaxio.celerio.model.support.account.RoleAttributes;
import lombok.extern.slf4j.Slf4j;

import static com.google.common.collect.Lists.newArrayList;

/**
 * When no account entity is present, this class can be used to auto discover and setup an account entity.
 */
@Slf4j
public class AccountConvention {
    private Iterable<String> usernameCandidates = newArrayList("login", "username", "identifiant", "email", "emailAddress", "mail");
    private Iterable<String> emailCandidates = newArrayList("email", "emailAddress", "mail");
    private Iterable<String> passwordCandidates = newArrayList("password", "pwd", "passwd", "motdepasse");
    private Iterable<String> enabledCandidates = newArrayList("isenabled", "enabled");
    private Iterable<String> roleNameCandidates = newArrayList("authority", "namelocale", "rolename", "role");

    /**
     * Is this entity the 'account' entity?
     */
    public boolean setupAccount(Entity e) {
        AccountAttributes accountAttributes = new AccountAttributes();

        // 0- reject entity having a composite PK.
        if (e.hasCompositePk()) {
            return false;
        }

        // 1- detect mandatory username
        for (Attribute a : e.getAttributes().getList()) {
            if (a.isString() && match(a.getVar(), usernameCandidates) && a.isUnique()) {
                accountAttributes.setUsername(a);
                log.debug("'Username' candidate: " + a.getVar() + " found on " + e.getName());
                break;
            }
        }

        if (!accountAttributes.isUsernameSet()) {
            return false;
        }

        // 2- detect mandatory password
        for (Attribute a : e.getAttributes().getList()) {
            if (a.isString() && match(a.getVar(), passwordCandidates)) {
                accountAttributes.setPassword(a);
                log.debug("'Password' candidate: " + a.getVar() + " found on " + e.getName());
                break;
            }
        }

        if (!accountAttributes.isPasswordSet()) {
            return false;
        }

        // from here we have at least an account.
        e.setAccountAttributes(accountAttributes);

        // 3- detect optional email
        for (Attribute a : e.getAttributes().getList()) {
            if (a.isString() && match(a.getVar(), emailCandidates)) {
                accountAttributes.setEmail(a);
                log.debug("'Email' candidate: " + a.getVar() + " found on " + e.getName());
                break;
            }
        }

        // 4- detect optional enabled
        for (Attribute a : e.getAttributes().getList()) {
            if (a.isBoolean() && match(a.getVar(), enabledCandidates)) {
                accountAttributes.setEnabled(a);
                log.debug("'Enabled' candidate: " + a.getVar() + " found on " + e.getName());
                break;
            }
        }

        // 5- detect Role relation
        for (Relation relation : e.getXToMany().getList()) {
            RoleAttributes roleAttributes = getRoleEntity(relation.getToEntity());

            if (roleAttributes != null) {
                relation.getToEntity().setRoleAttributes(roleAttributes);
                accountAttributes.setRoleRelation(relation);
                log.debug("'Role' relation detected: " + relation.toString());
                break;
            }
        }

        return true;
    }

    public RoleAttributes getRoleEntity(Entity e) {
        for (Attribute a : e.getAttributes().getList()) {
            if (a.isString() && match(a.getVar(), roleNameCandidates)) {
                RoleAttributes roleAttributes = new RoleAttributes();
                roleAttributes.setRoleName(a);
                return roleAttributes;
            }
        }
        return null;
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
