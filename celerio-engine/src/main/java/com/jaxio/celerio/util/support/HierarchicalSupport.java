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

public class HierarchicalSupport<H extends Hierarchical<H>> {
    private H h;

    public HierarchicalSupport(H h) {
        this.h = h;
    }

    public H getRoot() {
        if (h.getParent() == null) {
            return h;
        }
        H parent = h;
        H current = h;
        do {
            parent = current;
            current = current.getParent();
        } while (current != null);
        return parent;
    }

    public int getHierarchyLevel() {
        H current = h;
        int count = 0;
        do {
            count++;
            current = current.getParent();
        } while (current != null);
        return count;
    }

    public boolean isRoot() {
        return getHierarchyLevel() == 1;
    }
}
