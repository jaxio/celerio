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

import com.jaxio.celerio.configuration.database.Column;
import com.jaxio.celerio.configuration.database.Table;
import com.jaxio.celerio.configuration.entity.ColumnConfig;
import com.jaxio.celerio.model.Attribute;
import com.jaxio.celerio.model.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
/**
 * this is a four steps process
 * <ul>
 * <li>get data from column config</li>
 * <li>if no data config, create one and store it in config</li>
 * <li>get unset column config data from metadata</li>
 * <li>get unset column config data from default</li>
 * </ul>
 */
public class AttributeFactory {
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ColumnConfigFactory columnConfigFactory;

    public Attribute build(Entity entity, Table table, Column column) {
        ColumnConfig columnConfig = columnConfigFactory.buildColumnConfig(entity, table, column);
        return build(entity, columnConfig);
    }

    public Attribute build(Entity entity, ColumnConfig columnConfig) {
        Attribute attribute = applicationContext.getBean(Attribute.class);
        attribute.setEntity(entity);
        attribute.setColumnConfig(columnConfig);
        return attribute;
    }

    public void setup(Attribute attribute) {
        // columnConfig.setIsFormField(fallBack(columnConfig.isFormField(), AttributePredicates.VERSION.apply(attribute)));
    }
}
