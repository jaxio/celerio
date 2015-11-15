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

package com.jaxio.celerio.factory;

import com.jaxio.celerio.configuration.entity.OneToManyConfig;
import com.jaxio.celerio.support.AccessorNamer;
import com.jaxio.celerio.support.Namer;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class RelationCollisionUtilTest {

    // ----------------------------------
    // X TO ONE
    // ----------------------------------

    @Test
    public void xToOneNamerByConventionId() {
        RelationCollisionUtil relationUtil = new RelationCollisionUtil();
        String fromEntityName = "Account";
        String fromAttributeVar = "myAddressId";
        Namer targetEntityNamer = getNamer("address", "Address");

        Namer result = relationUtil.getXToOneNamerByConvention(null, fromEntityName, fromAttributeVar, targetEntityNamer);

        assertThat(result.getVar()).isEqualTo("myAddress");
        assertThat(result.getType()).isEqualTo("Address");
    }

    @Test
    public void xToOneNamerByConventionRef() {
        RelationCollisionUtil relationUtil = new RelationCollisionUtil();
        String fromEntityName = "Account";
        String fromAttributeVar = "address";
        Namer targetEntityNamer = getNamer("address", "Address");

        Namer result = relationUtil.getXToOneNamerByConvention(null, fromEntityName, fromAttributeVar, targetEntityNamer);

        assertThat(result.getVar()).isEqualTo("address");
        assertThat(result.getType()).isEqualTo("Address");
    }

    @Test
    public void xToOneNamerByConventionAny() {
        RelationCollisionUtil relationUtil = new RelationCollisionUtil();
        String fromEntityName = "Account";
        String fromAttributeVar = "anythingElse";
        Namer targetEntityNamer = getNamer("address", "Address");

        Namer result = relationUtil.getXToOneNamerByConvention(null, fromEntityName, fromAttributeVar, targetEntityNamer);

        assertThat(result.getVar()).isEqualTo("anythingElse");
        assertThat(result.getType()).isEqualTo("Address");
    }

    @Test
    public void xToOneNamerByConventionWithClash() {
        RelationCollisionUtil relationUtil = new RelationCollisionUtil();
        String fromEntityName = "Account";
        String fromAttributeVar = "anythingElse";
        Namer targetEntityNamer = getNamer("address", "Address");

        Namer result = relationUtil.getXToOneNamerByConvention(null, fromEntityName, fromAttributeVar, targetEntityNamer);

        assertThat(result.getVar()).isEqualTo("anythingElse");
        assertThat(result.getType()).isEqualTo("Address");

        result = relationUtil.getXToOneNamerByConvention(null, fromEntityName, fromAttributeVar, targetEntityNamer);
        assertThat(result.getVar()).isEqualTo("anythingElse2");
        assertThat(result.getType()).isEqualTo("Address");
    }

    // ----------------------------------
    // ONE TO MANY
    // ----------------------------------

    @Test
    public void oneToManyNamerFromConfComplete() {
        RelationCollisionUtil relationUtil = new RelationCollisionUtil();
        Namer fromEntityNamer = getNamer("account", "Account");

        Namer result = relationUtil.getOneToManyNamerFromConf(getOneToManyConfig("unAccount", "lesAccounts"), fromEntityNamer);
        assertThat(result.getVar()).isEqualTo("unAccount");
        assertThat(result.getVars()).isEqualTo("lesAccounts");
        assertThat(result.getType()).isEqualTo("Account");
    }

    @Test
    public void oneToManyNamerFromConfPluralOnly() {
        RelationCollisionUtil relationUtil = new RelationCollisionUtil();
        Namer fromEntityNamer = getNamer("account", "Account");

        Namer result = relationUtil.getOneToManyNamerFromConf(getOneToManyConfig(null, "lesAccounts"), fromEntityNamer);
        assertThat(result.getVar()).isEqualTo("account");
        assertThat(result.getVars()).isEqualTo("lesAccounts");
        assertThat(result.getType()).isEqualTo("Account");
    }

    @Test
    public void oneToManyNamerFromConfSingularOnly() {
        RelationCollisionUtil relationUtil = new RelationCollisionUtil();
        Namer fromEntityNamer = getNamer("account", "Account");

        Namer result = relationUtil.getOneToManyNamerFromConf(getOneToManyConfig("unAccount", null), fromEntityNamer);
        assertThat(result.getVar()).isEqualTo("unAccount");
        assertThat(result.getVars()).isEqualTo("unAccounts");
        assertThat(result.getType()).isEqualTo("Account");
    }

    @Test
    public void oneToManyNamerFromConfEmpty() {
        RelationCollisionUtil relationUtil = new RelationCollisionUtil();
        Namer fromEntityNamer = getNamer("account", "Account");

        Namer result = relationUtil.getOneToManyNamerFromConf(getOneToManyConfig(null, null), fromEntityNamer);
        assertThat(result).isNull();
    }

    @Test
    public void oneToManyNamerFromConfNull() {
        RelationCollisionUtil relationUtil = new RelationCollisionUtil();
        Namer fromEntityNamer = getNamer("account", "Account");

        Namer result = relationUtil.getOneToManyNamerFromConf(null, fromEntityNamer);
        assertThat(result).isNull();
    }

    // ----------------------------------
    // MANY TO MANY
    // ----------------------------------

    private OneToManyConfig getOneToManyConfig(String elementVar, String var) {
        OneToManyConfig o2m = new OneToManyConfig();
        o2m.setElementVar(elementVar);
        o2m.setVar(var);
        return o2m;
    }

    private Namer getNamer(String var, String type) {
        return new AccessorNamer(var, type, "com.mycomp");
    }
}
