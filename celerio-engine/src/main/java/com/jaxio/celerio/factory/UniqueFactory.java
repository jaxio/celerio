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

import com.jaxio.celerio.Config;
import com.jaxio.celerio.configuration.database.Index;
import com.jaxio.celerio.configuration.database.IndexHolder;
import com.jaxio.celerio.configuration.database.Table;
import com.jaxio.celerio.model.Attribute;
import com.jaxio.celerio.model.Entity;
import com.jaxio.celerio.model.unique.CompositeUnique;
import com.jaxio.celerio.model.unique.SimpleUnique;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@Service
@Slf4j
public class UniqueFactory {

    @Autowired
    private Config config;

    public void setupUniques(Entity entity) {
        Table table = entity.getTable();
        if (table == null) {
            return;
        }

        for (IndexHolder ih : table.getUniqueIndexHolders()) {
            if (ih.isSimple()) {
                Attribute a = entity.getAttributeByTableAndColumnName(ih.getTableName(), ih.getIndex().getColumnName());
                if (a != null) {
                    entity.addUnique(new SimpleUnique(ih.getName(), a));
                }
            } else if (ih.isComposite()) {
                List<Attribute> attributes = newArrayList();

                for (Index index : ih.getIndexes()) {
                    Attribute a = entity.getAttributeByTableAndColumnName(ih.getTableName(), index.getColumnName());
                    if (a != null) {
                        attributes.add(a);
                    }
                }

                if (attributes.size() == ih.getSize()) {
                    entity.addUnique(new CompositeUnique(ih.getName(), attributes));
                } else if (attributes.size() > 0) {
                    log.warn("Ignoring unique composite index spread accross entity hierarchy:: " + ih.getName());
                    // TODO: spread accross hierarchy, is it acceptable ?
                }
            } else {
                throw new IllegalStateException("Should not happen!");
            }
        }
    }
}