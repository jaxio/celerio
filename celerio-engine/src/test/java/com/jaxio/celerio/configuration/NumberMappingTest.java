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

package com.jaxio.celerio.configuration;

import org.junit.Assert;
import org.junit.Test;

public class NumberMappingTest {

    @Test
    public void matchOk() {
        NumberMapping nm = new NumberMapping();

        nm.setColumnSizeMin(1);
        nm.setColumnSizeMax(11);

        nm.setColumnDecimalDigitsMin(1);
        nm.setColumnDecimalDigitsMax(4);

        Assert.assertTrue(nm.match(1, 1));
        Assert.assertTrue(nm.match(10, 3));

        Assert.assertFalse(nm.match(1, 4));
        Assert.assertFalse(nm.match(11, 1));
        Assert.assertFalse(nm.match(1, null));
        Assert.assertFalse(nm.match(1, null));
        Assert.assertFalse(nm.match(null, null));
    }

    @Test
    public void matchOkWhenMaxIsNull() {
        NumberMapping nm = new NumberMapping();

        nm.setColumnSizeMin(11);
        nm.setColumnSizeMax(null);

        nm.setColumnDecimalDigitsMin(4);
        nm.setColumnDecimalDigitsMax(null);

        Assert.assertTrue(nm.match(11, 4));
        Assert.assertTrue(nm.match(22, 22));

        Assert.assertFalse(nm.match(10, 4));
        Assert.assertFalse(nm.match(11, 3));

        Assert.assertFalse(nm.match(1, null));
        Assert.assertFalse(nm.match(1, null));
        Assert.assertFalse(nm.match(null, null));
    }

}
