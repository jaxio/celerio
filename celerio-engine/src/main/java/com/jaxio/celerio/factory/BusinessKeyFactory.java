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

import com.jaxio.celerio.model.Attribute;
import com.jaxio.celerio.model.Entity;
import com.jaxio.celerio.model.Unique;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@Service
@Slf4j
public class BusinessKeyFactory {

    public void setupBusinessKey(Entity entity) {
        List<Attribute> bk = newArrayList();

        // Configuration
        if (entity.getBusinessKeyByConfiguration().getFlatUp().getSize() > 0) {
            bk = entity.getBusinessKeyByConfiguration().getFlatUp().getList();
            logInfo("setting business key by configuration", entity, bk);
        }

        // Convention 1: use first simple unique attribute
        if (bk.isEmpty()) {
            for (Unique u : entity.getSimpleUniques().getFlatUp().getList()) {
                if (u.isGoodBusinessKeyCandidate()) {
                    bk = u.getAttributes();
                    logInfo("setting business key by convention (first unique property)", entity, bk);
                    break;
                }
            }
        }

        // Convention 2: use first composite unique attributes
        if (bk.isEmpty()) {
            for (Unique u : entity.getCompositeUniques().getFlatUp().getList()) {
                if (u.isGoodBusinessKeyCandidate()) {
                    logInfo("setting usiness key by convention (first composite unique properties)", entity, bk);
                    bk = u.getAttributes();
                    break;
                }
            }
        }

        entity.setBusinessKey(bk);
        for (Attribute bka : bk) {
            bka.setInBk(true);
        }

        if (bk.isEmpty()) {
            log.info(entity.getName() + ": no business key configured for entity");
            log.debug("    and no classic business key found by convention. Will use PK as the business key.");
            log.debug("    Tip: You can use '<columnConfig ... businessKey=\"true\"/>' to set your business key.");
        }
    }

    private void logInfo(String msg, Entity entity, List<Attribute> bka) {
        StringBuilder sb = new StringBuilder();
        sb.append(entity.getName());
        if (!bka.isEmpty()) {
            sb.append("[");
            boolean first = true;
            for (Attribute bk : bka) {
                if (first) {
                    first = false;
                } else {
                    sb.append("-");
                }
                sb.append(bk.getName());
            }
            sb.append("]");
        }
        sb.append(": ").append(msg);

        log.info(sb.toString());
    }
}