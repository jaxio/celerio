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

package com.jaxio.celerio.model;

import com.jaxio.celerio.configuration.database.JdbcType;
import com.jaxio.celerio.configuration.entity.ColumnConfig;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class AttributeTest {

    @Test
    public void testSortableAndLazyLoaded() throws Exception {
        Attribute a = new Attribute();
        ColumnConfig cc = new ColumnConfig();
        cc.setType(JdbcType.BLOB);
        a.setColumnConfig(cc);
        assertThat(a.isLazyLoaded()).isTrue();
        assertThat(a.isSortable()).isFalse();

        a = new Attribute();
        cc = new ColumnConfig();
        cc.setType(JdbcType.CLOB);
        a.setColumnConfig(cc);
        assertThat(a.isLazyLoaded()).isTrue();
        assertThat(a.isSortable()).isFalse();

        a = new Attribute();
        cc = new ColumnConfig();
        cc.setType(JdbcType.VARCHAR);
        a.setColumnConfig(cc);
        assertThat(a.isLazyLoaded()).isFalse();
        assertThat(a.isSortable()).isTrue();
    }
}
