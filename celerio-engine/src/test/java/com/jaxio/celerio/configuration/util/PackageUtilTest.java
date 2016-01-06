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

package com.jaxio.celerio.configuration.util;

import com.jaxio.celerio.util.PackageUtil;
import org.junit.Assert;
import org.junit.Test;

import static com.jaxio.celerio.util.PackageUtil.assemblePackage;
import static org.fest.assertions.Assertions.assertThat;

public class PackageUtilTest {

    @Test(expected = IllegalArgumentException.class)
    public void nullPackage() {
        assertThat(assemblePackage((String) null)).isNull();
    }

    @Test
    public void justRootPackage() {
        assertThat(assemblePackage("com.edy")).isEqualTo("com.edy");
    }

    @Test
    public void rootAndSubPackage() {
        assertThat(assemblePackage("com.edy", "oho")).isEqualTo("com.edy.oho");
    }

    @Test
    public void nullRootAndSubPackage() {
        assertThat(assemblePackage(null, "oho")).isEqualTo("oho");
    }

    @Test
    public void emptyRootAndSubPackage() {
        assertThat(assemblePackage("", "oho")).isEqualTo("oho");
    }

    @Test
    public void rootAndNullSubPackage() {
        assertThat(assemblePackage("com.edy", null)).isEqualTo("com.edy");
    }

    @Test
    public void rootAndEmptySubPackage() {
        assertThat(assemblePackage("com.edy", " ")).isEqualTo("com.edy");
    }

    @Test
    public void esotericCase() {
        assertThat(assemblePackage("", "  ", null, " com", null, "edy ", " ", null, "mitchel")).isEqualTo("com.edy.mitchel");
    }

    @Test
    public void isPackagNameValid() {
        // valid package names
        Assert.assertTrue(PackageUtil.isPackageNameValid("com"));
        Assert.assertTrue(PackageUtil.isPackageNameValid("com.jaxio.celerio"));
        Assert.assertTrue(PackageUtil.isPackageNameValid("com.jaxio2.celerio"));
        Assert.assertTrue(PackageUtil.isPackageNameValid("_com.jaxio2.celerio"));
        Assert.assertTrue(PackageUtil.isPackageNameValid("$com.jaxio2.celerio"));

        // invalid package names
        Assert.assertFalse(PackageUtil.isPackageNameValid(""));
        Assert.assertFalse(PackageUtil.isPackageNameValid("com.2jaxio.celerio"));
        Assert.assertFalse(PackageUtil.isPackageNameValid("com. jaxio.celerio"));
        Assert.assertFalse(PackageUtil.isPackageNameValid("com.jaxio-celerio"));
        Assert.assertFalse(PackageUtil.isPackageNameValid("java.lang"));
        Assert.assertFalse(PackageUtil.isPackageNameValid("javax.lang"));
    }
}
