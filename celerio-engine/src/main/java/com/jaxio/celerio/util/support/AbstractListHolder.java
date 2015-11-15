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
import com.jaxio.celerio.util.ListHolder;
import com.jaxio.celerio.util.Named;
import org.springframework.beans.support.MutableSortDefinition;
import org.springframework.beans.support.PropertyComparator;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.unmodifiableList;

/**
 * A list holder whose list comes from Iterable. The list may be forked thanks to the except() to exclude some elements based on their name. 'except' feature is
 * convenient for template developers that do not have access to Celerio code. They can do for example: searchAttributes.except('aName').list instead of writing
 * #if statement.
 */
public abstract class AbstractListHolder<T extends Named> implements ListHolder<T> {
    private List<T> cachedResult;
    private boolean isCacheEnabled = true;
    private String sortProperty;
    private Map<String, SimpleListHolder<T>> cache = newHashMap();

    /**
     * The source to use to construct the List.
     */
    protected abstract Iterable<T> getIterable();

    /**
     * Turns sort mode on using the passed sort property. The target property must be a String.
     */
    protected void setSortProperty(String sortProperty) {
        this.sortProperty = sortProperty;
    }

    protected String getSortProperty() {
        return sortProperty;
    }

    /**
     * Turns the cache on.
     */
    protected void enableCache() {
        isCacheEnabled = true;
    }

    /**
     * Turns the cache off and clear it.
     */
    protected void disableCache() {
        isCacheEnabled = false;
        cachedResult = null;
    }

    /**
     * Returns the list that this ListHolder backs.
     */
    @Override
    public List<T> getList() {
        if (isCacheEnabled) {
            if (cachedResult == null) {
                cachedResult = makeUnmodifiableUniqueList();
            }
            return cachedResult;
        } else {
            return makeUnmodifiableUniqueList();
        }
    }

    // remove duplication if any
    private List<T> makeUnmodifiableUniqueList() {
        Set<T> tmp = newHashSet();
        List<T> uniques = newArrayList();
        for (T t : getIterable()) {
            if (tmp.add(t)) {
                uniques.add(t);
            }
        }

        sort(uniques);
        return unmodifiableList(uniques);
    }

    public T getFirst() {
        return getList().iterator().next();
    }


    @Override
    public boolean isEmpty() {
        return getList().isEmpty();
    }

    @Override
    public boolean isNotEmpty() {
        return !isEmpty();
    }

    @Override
    public int getSize() {
        return getList().size();
    }

    /**
     * Return the size of the list returned by getSubList(maxSubListSize)
     */
    public int getSubListSize(int maxSubListSize) {
        return Math.min(getList().size(), maxSubListSize);
    }

    /**
     * Returns at most the first maxSubListSize elements of this list.
     */
    public List<T> getSubList(int maxSubListSize) {
        List<T> subList = newArrayList();
        int counter = 0;
        for (T t : getList()) {
            if (counter++ < maxSubListSize) {
                subList.add(t);
            } else {
                break;
            }
        }
        return subList;
    }

    public List<List<T>> getAsTwoLists() {
        List<List<T>> metaList = newArrayList();
        List<T> list1 = newArrayList();
        List<T> list2 = newArrayList();
        int counter = 0;
        int halfSize = getSize() / 2;

        for (T t : getList()) {
            if (counter++ < halfSize) {
                list1.add(t);
            } else {
                list2.add(t);
            }
        }

        metaList.add(list1);
        metaList.add(list2);

        return metaList;
    }

    /**
     * Fork this list with extra predicates.
     *
     * @param namesToExclude the name of the elements to exclude from the list.
     */
    public SimpleListHolder<T> except(String... namesToExclude) {
        String key = "current" + getCacheKey(namesToExclude);
        SimpleListHolder<T> result = cache.get(key);
        if (result == null) {
            result = new SimpleListHolder<T>(getIterable(), asNameNotEqualsToPredicates(namesToExclude), getSortProperty());
            cache.put(key, result);
        }
        return result;
    }

    private Predicate<T> asNameNotEqualsToPredicates(String... namesToExclude) {
        if (namesToExclude.length == 1) {
            return new NameNotEqualsToPredicate<T>(namesToExclude[0]);
        }
        List<Predicate<T>> result = newArrayList();
        for (String name : namesToExclude) {
            result.add(new NameNotEqualsToPredicate<T>(name));
        }
        return Predicates.<T>and(result);// using static import breaks compilation
    }

    private String getCacheKey(String... namesToExclude) {
        String key = "-";
        for (String name : namesToExclude) {
            key += name + "-";
        }
        return key;
    }

    protected void sort(List<T> listToSort) {
        if (sortProperty != null && !sortProperty.isEmpty()) {
            // watch out: the target property must be a String.
            PropertyComparator.sort(listToSort, new MutableSortDefinition(sortProperty, false, true));
        }
    }
}
