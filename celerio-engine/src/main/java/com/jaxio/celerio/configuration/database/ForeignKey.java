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

import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.Assert;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.jaxio.celerio.configuration.Util.nonNull;

@NoArgsConstructor
public class ForeignKey {
    private List<ImportedKey> importedKeys = newArrayList();
    @Setter
    private String name;
    @Setter
    private String pkTableName;
    @Setter
    private String fkTableName;

    public ForeignKey(String name, String pkTableName, String fkTableName) {
        this.name = name;
        this.pkTableName = pkTableName;
        this.fkTableName = fkTableName;
    }

    public void setImportedKeys(List<ImportedKey> importedKeys) {
        this.importedKeys = nonNull(importedKeys);
    }

    public List<ImportedKey> getImportedKeys() {
        return importedKeys;
    }

    public String getName() {
        return name;
    }

    public String getPkTableName() {
        return pkTableName;
    }

    public String getFkTableName() {
        return fkTableName;
    }

    public void addImportedKeys(List<ImportedKey> ikeys) {
        Assert.isTrue(ikeys.size() > 0, "there must be at least one imported key");

        for (ImportedKey ikey : ikeys) {
            addImportedKey(ikey);
        }
    }

    public void addImportedKey(ImportedKey key) {
        if (importedKeys.isEmpty()) {
            name = key.getFkName();
        } else {
            Assert.isTrue(name.equalsIgnoreCase(key.getFkName()), "FkName must be the same");
        }

        importedKeys.add(key);
    }

    public ImportedKey getImportedKey() {
        return getImportedKeys().iterator().next();
    }

    public boolean isSimple() {
        return getSize() == 1;
    }

    public boolean isComposite() {
        return getSize() > 1;
    }

    public int getSize() {
        return importedKeys.size();
    }

    @Override
    public String toString() {
        return "name=" + name + ", pkTableName=" + pkTableName + ", fkTableName=" + fkTableName;
    }
}
