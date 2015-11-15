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

import com.jaxio.celerio.util.Hierarchical;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class HierarchyUtil {

    public static <T, H extends Hierarchical<H>> Iterable<T> flattenDownToLeaves(H node, ListGetter<T, H> listGetter) {
        List<T> ret = newArrayList();
        recursiveFlattenDownToLeaves(node, listGetter, ret);
        return ret;
    }

    public static <T, H extends Hierarchical<H>> void recursiveFlattenDownToLeaves(H node, ListGetter<T, H> listGetter, List<T> ret) {
        ret.addAll(listGetter.getList(node));
        for (H child : node.getChildren()) {
            recursiveFlattenDownToLeaves(child, listGetter, ret);
        }
    }

    public static <T, H extends Hierarchical<H>> Iterable<T> flattenUpToRoot(H node, ListGetter<T, H> listGetter) {
        List<T> ret = newArrayList();
        H hierarchy = node;
        do {
            ret.addAll(listGetter.getList(hierarchy));
            hierarchy = hierarchy.getParent();
        } while (hierarchy != null);

        return ret;
    }

    public static <T, H extends Hierarchical<H>> Iterable<T> flattenAbove(H node, ListGetter<T, H> listGetter) {
        List<T> ret = newArrayList();
        H hierarchy = node;
        do {
            hierarchy = hierarchy.getParent();
            if (hierarchy != null) {
                ret.addAll(listGetter.getList(hierarchy));
            }
        } while (hierarchy != null);
        return ret;
    }
}
