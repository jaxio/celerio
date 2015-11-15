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

package com.jaxio.celerio.model.primarykey;

import com.jaxio.celerio.model.Attribute;
import com.jaxio.celerio.model.Entity;
import com.jaxio.celerio.model.PrimaryKey;
import lombok.Getter;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

@Getter
public class NoPrimaryKey implements PrimaryKey {
    private final Entity entity;
    private final List<Attribute> attributes = unmodifiableList(new ArrayList<Attribute>());

    public NoPrimaryKey(Entity entity) {
        Assert.notNull(entity);
        this.entity = entity;
    }

    @Override
    public boolean isNoPk() {
        return true;
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public boolean isComposite() {
        return false;
    }

    @Override
    final public boolean isJavaBaseClass() {
        return false;
    }

    @Override
    public boolean isImported() {
        return false;
    }

    @Override
    public Attribute getAttribute() {
        throw new IllegalStateException("You cannot invoke this method on entity " + entity.getName() + " since it has no primary key!!!");
    }

    @Override
    final public boolean isDate() {
        return false;
    }

    @Override
    final public boolean isEnum() {
        return false;
    }

    @Override
    public String getVar() {
        return throwException();
    }

    @Override
    public String getType() {
        return throwException();
    }

    @Override
    public String getTypeLow() {
        return throwException();
    }

    @Override
    public String getPath() {
        return throwException();
    }

    @Override
    public String getPackageName() {
        return throwException();
    }

    @Override
    public String getFullType() {
        return throwException();
    }

    @Override
    public String getVarUp() {
        return throwException();
    }

    @Override
    public String getVars() {
        return throwException();
    }

    @Override
    public String getVarsUp() {
        return throwException();
    }

    @Override
    public String getAdder() {
        return throwException();
    }

    @Override
    public String getAdders() {
        return throwException();
    }

    @Override
    public String getContains() {
        return throwException();
    }

    @Override
    public String getGetter() {
        return throwException();
    }

    @Override
    public String getGetters() {
        return throwException();
    }

    @Override
    public String getWith() {
        return throwException();
    }

    @Override
    public String getRemover() {
        return throwException();
    }

    @Override
    public String getRemovers() {
        return throwException();
    }

    @Override
    public String getSetter() {
        return throwException();
    }

    @Override
    public String getSetters() {
        return throwException();
    }

    @Override
    public String getEditer() {
        return throwException();
    }

    @Override
    public String getHibernateFilterName() {
        return throwException();
    }

    @Override
    public String getToStringMethod() {
        return throwException();
    }

    private String throwException() {
        throw new IllegalStateException("[" + entity.getName()
                + "] You probably have entity.primaryKey.var in your code, you should use entity.root.primaryKey.var");
    }
}
