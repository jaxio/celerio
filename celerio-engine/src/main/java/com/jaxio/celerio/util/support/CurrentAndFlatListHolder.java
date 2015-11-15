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

package com.jaxio.celerio.util.support;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.jaxio.celerio.util.Hierarchical;
import com.jaxio.celerio.util.ListHolder;
import com.jaxio.celerio.util.Named;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static com.jaxio.celerio.util.support.Flatten.*;
import static org.springframework.util.Assert.notNull;


/**
 * Mix {@link ListHolder} and {@link Hierarchical}.
 * <p>
 * When a node is part of a hierarchy exposing a list of elements (example: an entity exposing a list of attributes),
 * this class enable you to retrieve in one single {@link ListHolder} the elements that are scattered in the node's family tree.
 *
 * @param <T> - An element that is part of a List.
 * @param <H> - In our case, an Entity
 */
public class CurrentAndFlatListHolder<T extends Named, H extends Hierarchical<H>> extends AbstractListHolder<T> {

    private H node;
    private ListGetter<T, H> listGetter;
    private Predicate<T> predicate;

    private SimpleListHolder<T> flatDown = null;
    private SimpleListHolder<T> flatAbove = null;
    private SimpleListHolder<T> flatUp = null;
    private SimpleListHolder<T> flatFull = null;
    private SimpleListHolder<T> uniqueUp = null;
    private SimpleListHolder<T> uniqueDown = null;
    private SimpleListHolder<T> uniqueFull = null;

    // DO NOT FETCH the list from the constructor. It must be fetched lazily as we construct
    // it before the list are available...
    // Important Note: we do not extends SimpleListHolder because we want the list to be fetched Lazily.
    public CurrentAndFlatListHolder(H node, ListGetter<T, H> listGetter) {
        notNull(node, "the node must not be null");
        notNull(listGetter, "the listGetter must not be null");
        this.node = node;
        this.listGetter = listGetter;
        this.predicate = Predicates.<T>alwaysTrue();
    }

    public CurrentAndFlatListHolder(H node, ListGetter<T, H> listGetter, Predicate<T> predicate) {
        notNull(node, "the node must not be null");
        this.node = node;
        this.listGetter = listGetter;
        this.predicate = predicate;
    }

    @Override
    protected Iterable<T> getIterable() {
        return filter(listGetter.getList(node), predicate);
    }


    /**
     * Returns in a {@link ListHolder} all the T elements that are present in the
     * current node and its ancestors, up to the root node.
     */
    public SimpleListHolder<T> getFlatUp() {
        if (flatUp == null) {
            flatUp = newSimpleListHolder(up_to_root, getSortProperty());
        }
        return flatUp;
    }

    /**
     * Returns in a {@link ListHolder} all the T elements that are present in the
     * current node's ancestors, up to the root node.
     */
    public SimpleListHolder<T> getFlatAbove() {
        if (flatAbove == null) {
            flatAbove = newSimpleListHolder(above, getSortProperty());
        }
        return flatAbove;
    }

    /**
     * Returns in a {@link ListHolder} all the T elements that are present in the
     * current node and all its descendants.
     */
    public SimpleListHolder<T> getFlatDown() {
        if (flatDown == null) {
            flatDown = newSimpleListHolder(down_to_leave, getSortProperty());
        }
        return flatDown;
    }

    /**
     * Returns in a {@link ListHolder} all the T elements that are present in the
     * current node's root and all its descendants.
     */
    public SimpleListHolder<T> getFlatFull() {
        if (flatFull == null) {
            flatFull = newSimpleListHolder(all, getSortProperty());
        }
        return flatFull;
    }

    /**
     * Returns in a {@link ListHolder} all the T elements (without duplicate) that are present in the
     * current node and its ancestors, up to the root node.
     */
    public SimpleListHolder<T> getUniqueFlatUp() {
        if (uniqueUp == null) {
            uniqueUp = newUniqueSimpleListHolder(up_to_root, getSortProperty());
        }
        return uniqueUp;
    }

    /**
     * Returns in a {@link ListHolder} all the T elements (without duplicates) that are present in the
     * current node and all its descendants.
     */
    public SimpleListHolder<T> getUniqueFlatDown() {
        if (uniqueDown == null) {
            uniqueDown = newUniqueSimpleListHolder(down_to_leave, getSortProperty());
        }
        return uniqueDown;
    }


    /**
     * Returns in a {@link ListHolder} all the T elements (without duplicate) that are present in the
     * current node's root and all its descendants.
     */
    public SimpleListHolder<T> getUniqueFlatFull() {
        if (uniqueFull == null) {
            uniqueFull = newUniqueSimpleListHolder(all, getSortProperty());
        }
        return uniqueFull;
    }

    //
    // Impl details
    //

    private SimpleListHolder<T> newSimpleListHolder(Flatten flatten, String sortProperty) {
        return new SimpleListHolder<T>(getIterable(flatten), predicate, sortProperty);
    }

    private SimpleListHolder<T> newUniqueSimpleListHolder(Flatten flatten, String sortProperty) {
        return new SimpleListHolder<T>(getIterableNoDuplicate(flatten), predicate, sortProperty);
    }

    private Iterable<T> getIterableNoDuplicate(Flatten flatten) {
        List<T> result = newArrayList();
        Set<String> tracker = newHashSet();
        for (T value : getIterable(flatten)) {
            if (!tracker.contains(value.getName())) {
                result.add(value);
                tracker.add(value.getName());
            }
        }
        return result;
    }

    private Iterable<T> getIterable(Flatten flatten) {
        if (flatten == up_to_root) {
            return HierarchyUtil.flattenUpToRoot(node, listGetter);
        }

        if (flatten == above) {
            return HierarchyUtil.flattenAbove(node, listGetter);
        }

        if (flatten == down_to_leave) {
            return HierarchyUtil.flattenDownToLeaves(node, listGetter);
        }

        if (flatten == all) {
            return HierarchyUtil.flattenDownToLeaves(node.getRoot(), listGetter);
        }

        throw new RuntimeException("Flatten " + flatten + " is not handled");
    }
}
