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

package com.jaxio.celerio.template;

import com.jaxio.celerio.Config;
import com.jaxio.celerio.main.CelerioProducer;
import com.jaxio.celerio.output.OutputResultFactory;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.FileReader;
import java.io.StringReader;
import java.util.Scanner;
import java.util.regex.MatchResult;

import static org.apache.commons.io.IOUtils.contentEquals;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

@ContextConfiguration("classpath:applicationContext-celerio.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class VelocityGeneratorTest {
    @Autowired
    private Config config;

    @Autowired
    private CelerioProducer celerioProducer;

    @Autowired
    private OutputResultFactory outputResultFactory;

    @Test
    public void testExtraction() {
        String message = "Object 'com.jaxio.celerio.convention.WellKnownFolder' does not contain property 'resource' at src/main/resources/spring/springmvc-parent.p.vm.xml[line "
                + "28, column 47]";
        Scanner s = new Scanner(message);
        String u = s.findInLine("\\[line (\\d+), column (\\d+)\\]");
        assertThat(u).isNotEmpty();
        MatchResult result = s.match();
        assertThat(result.groupCount()).isEqualTo(2);
        assertThat(result.group(1)).isEqualTo("28");
        assertThat(result.group(2)).isEqualTo("47");
    }
}
