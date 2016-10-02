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

package com.jaxio.celerio.model.relation;

import com.google.common.base.Preconditions;
import com.jaxio.celerio.configuration.entity.CascadeGetter;
import com.jaxio.celerio.configuration.entity.FetchTypeGetter;
import com.jaxio.celerio.configuration.entity.OrderByGetter;
import com.jaxio.celerio.model.Attribute;
import com.jaxio.celerio.model.AttributePair;
import com.jaxio.celerio.model.Entity;
import com.jaxio.celerio.model.Relation;
import com.jaxio.celerio.util.Labels;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.NotImplementedException;

import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

public abstract class AbstractRelation implements Relation, Map<String, Object> {
    @Getter
    private Relation inverse;

    private boolean isInverse;

    @Override
    public String getName() {
        return getKind();
    }

    protected void setInverse(Relation inverse) {
        this.inverse = inverse;
    }

    protected AbstractRelation buildInverse() {
        throw new IllegalStateException("Implement it if really needed");
    }

    @Override
    final public Relation createInverse() {
        if (isInverse()) {
            throw new IllegalStateException("You cannot create an inverse side from an inverse side");
        }

        AbstractRelation inverse = buildInverse();
        inverse.isInverse = true;
        setInverse(inverse);
        inverse.setInverse(this);
        return inverse;
    }

    @Override
    public boolean hasInverse() {
        return getInverse() != null;
    }

    @Override
    public boolean isInverse() {
        return isInverse;
    }

    @Override
    public boolean isUnique() {
        if (isSimple()) {
            return getFromAttribute().isUnique();
        }
        // TODO : implement
        throw new NotImplementedException("to be developped using entity uniques");
    }

    /**
     * Note: When x to one has an inverse relation, we never mark it as @NotNull as it would break webflow navigation (indeed, the mandatory value is set
     * transparently once the entity is added to the collection) However, in test (XxxGenerator), we must take mandatory into account...
     */
    @Override
    public boolean isMandatory() {
        if (isSimple()) {
            return (isManyToOne() || isOneToOne()) && getFromAttribute().isNotNullable();
        }

        if (isComposite()) {
            if (isManyToOne() || isOneToOne()) {
                for (Attribute fa : getFromAttributes()) {
                    if (fa.isNotNullable()) {
                        return true; // if one of them is required, we assume the composite relation is required
                    }
                }
            }
        }

        return false;
    }

    @Override
    public boolean isOneToOne() {
        return false;
    }

    @Override
    public boolean isOneToVirtualOne() {
        return false;
    }

    @Override
    public boolean isOneToMany() {
        return false;
    }

    @Override
    public boolean isManyToMany() {
        return false;
    }

    @Override
    public boolean isManyToOne() {
        return false;
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public boolean isIntermediate() {
        return false;
    }

    @Override
    public boolean isComposite() {
        return false;
    }

    @Override
    public List<Relation> getRelations() {
        throw new IllegalStateException("Only composite Relation provide this method.");
    }

    @Override
    public Entity getMiddleEntity() {
        throw new IllegalStateException("Only intermediate Relation provide this method.");
    }

    @Override
    public Relation getMiddleToLeft() {
        throw new IllegalStateException("Only intermediate Relation provide this method.");
    }

    @Override
    public Relation getMiddleToRight() {
        throw new IllegalStateException("Only intermediate Relation provide this method.");
    }

    @Override
    public boolean isCollection() {
        return false;
    }

    @Override
    public String getOneLineComment() {
        if (isManyToOne() || (isOneToOne() && !isInverse())) {
            if (isSimple()) {
                if (getFromAttribute().getOneLineComment().length() > 0) {
                    return getFromAttribute().getOneLineComment() + " (" + getFromAttribute().getVar() + ")";
                } else {
                    return " // (" + getFromAttribute().getVar() + ")";
                }
            } else if (isComposite()) {
                // Composite
                StringBuilder sb = new StringBuilder();
                sb.append(" // (");
                for (Attribute fromAttribute : getFromAttributes()) {
                    sb.append(fromAttribute.getVar()).append(", ");
                }
                sb.append(")");
                return sb.toString().replaceAll(", \\)", ")");
            }
        }

        if (isOneToOne() && isInverse()) {
            return " // inverse side";
        }

        return "";
    }

    @Override
    final public String getKind() {
        if (isManyToOne()) {
            return "many-to-one";
        }
        if (isOneToMany()) {
            return "one-to-many";
        }
        if (isManyToMany()) {
            return "many-to-many";
        }
        if (isOneToOne()) {
            return "one-to-one";
        }
        if (isOneToVirtualOne()) {
            return "one-to-virtual-one";
        }

        throw new IllegalStateException("Which kind of relation is this?");
    }

    private CascadeGetter cascadeGetter;
    private FetchTypeGetter fetchTypeGetter;

    @Getter
    @Setter
    private OrderByGetter orderByGetter;

    @Override
    public CascadeGetter getCascadeGetter() {
        return cascadeGetter;
    }

    public void setCascadeGetter(CascadeGetter cascadeGetter) {
        this.cascadeGetter = cascadeGetter;
    }

    @Override
    public FetchTypeGetter getFetchTypeGetter() {
        return fetchTypeGetter;
    }

    public void setFetchTypeGetter(FetchTypeGetter fetchTypeGetter) {
        this.fetchTypeGetter = fetchTypeGetter;
    }

    @Override
    public List<Attribute> getFromAttributes() {
        return newArrayList(getFromAttribute());
    }

    @Override
    public List<Attribute> getToAttributes() {
        return newArrayList(getToAttribute());
    }

    @Override
    public List<AttributePair> getAttributePairs() {
        List<Attribute> from = getFromAttributes();
        List<Attribute> to = getToAttributes();
        List<AttributePair> result = new ArrayList<AttributePair>();
        for (int i = 0; i < from.size(); i++) {
            result.add(new AttributePair(from.get(i), to.get(i)));
        }
        return result;
    }

    // ---------------------

    @Getter
    @Setter
    private Labels labels;

    @Getter
    @Setter
    private String displayOrderAsString;

    // ---------------------
    /**
     * Whether source code to create a new target entity should be generated.
     */
    @Setter
    @Getter
    private boolean genCreate = true;

    /**
     * Whether source code to edit an existing target entity should be generated.
     */
    @Setter
    @Getter
    private boolean genEdit = true;

    /**
     * Whether source code to view an existing target entity should be generated.
     */
    @Setter
    @Getter
    private boolean genView = true;

    /**
     * Whether source code to select an existing target entity should be generated. Includes multiple selection. Once selected the target entity can be either
     * set (x-to-one) or added (x-to-many).
     */
    @Setter
    @Getter
    private boolean genSelect = true;

    /**
     * Whether source code to select (using autoComplete) an existing target entity should be generated.
     */
    @Setter
    @Getter
    private boolean genAutoComplete = true;

    /**
     * Whether source code to remove/delete an existing target entity should be generated.
     */
    @Setter
    @Getter
    private boolean genRemove = true;


    // ------------------------------------
    // SPI are put in a Map so we can access
    // from velocity templates as if we had getter.
    // ------------------------------------

    private Map<String, Object> spis = newHashMap();

    @Override
    public void clear() {
        spis.clear();
    }

    @Override
    public boolean containsKey(Object arg0) {
        return spis.containsKey(arg0);
    }

    @Override
    public boolean containsValue(Object arg0) {
        return spis.containsValue(arg0);
    }

    @Override
    public Set<java.util.Map.Entry<String, Object>> entrySet() {
        return spis.entrySet();
    }

    @Override
    public Object get(Object arg0) {
        Object o = spis.get(arg0);
        Preconditions.checkNotNull(o, "No SPI having its var=" + arg0 + " was found. Tip: in your template for predicate method, use always ref.isSomething() instead of xxx.something");
        return o;
    }

    @Override
    public boolean isEmpty() {
        return spis.isEmpty();
    }

    @Override
    public Set<String> keySet() {
        return spis.keySet();
    }

    @Override
    public Object put(String arg0, Object arg1) {
        return spis.put(arg0, arg1);
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> arg0) {
        spis.putAll(arg0);
    }

    @Override
    public Object remove(Object arg0) {
        return spis.remove(arg0);
    }

    @Override
    public int size() {
        return spis.size();
    }

    @Override
    public Collection<Object> values() {
        return spis.values();
    }
}
