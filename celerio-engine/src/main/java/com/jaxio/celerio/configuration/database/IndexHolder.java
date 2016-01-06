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

package com.jaxio.celerio.configuration.database;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.jaxio.celerio.configuration.Util.nonNull;

@Getter
@NoArgsConstructor
public class IndexHolder {
    protected List<Index> indexes = newArrayList();
    private String name;
    private String tableName;
    private boolean isNonUnique;

    public IndexHolder(String tableName) {
        this.tableName = tableName;
    }

    public void setIndexes(List<Index> indexes) {
        this.indexes = nonNull(indexes);
    }

    public void addIndexes(List<Index> indexes) {
        Assert.isTrue(indexes.size() > 0, "there must be at least one index");
        for (Index index : indexes) {
            addIndex(index);
        }
    }

    public void addIndex(Index index) {
        if (indexes.isEmpty()) {
            name = index.getIndexName();
            isNonUnique = index.isNonUnique();
        } else {
            Assert.isTrue(name.equalsIgnoreCase(index.getIndexName()), "The indexName must be the same");
            Assert.isTrue(!(isNonUnique ^ index.isNonUnique()), "Indexes must have same non unique value");
        }

        indexes.add(index);
    }

    public boolean isSimple() {
        return getSize() == 1;
    }

    public Index getIndex() {
        Assert.isTrue(isSimple(), "Can be invoked only on simple index");
        return getIndexes().iterator().next();
    }

    public boolean isComposite() {
        return getSize() > 1;
    }

    public int getSize() {
        return indexes.size();
    }

    public boolean isUnique() {
        return !isNonUnique;
    }
}
