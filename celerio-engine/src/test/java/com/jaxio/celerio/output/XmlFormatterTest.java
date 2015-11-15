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

package com.jaxio.celerio.output;

import com.jaxio.celerio.Config;
import org.junit.Test;

import java.io.IOException;

import static org.fest.assertions.Assertions.assertThat;

public class XmlFormatterTest {

    @Test
    public void format() throws IOException {
        Config config = new Config();
        config.getCelerio().getConfiguration().getConventions().getXmlFormatter().setEnableXmlFormatter(true);
        XmlCodeFormatter xcf = new XmlCodeFormatter(config);
        String unformattedXml = "<toto>  <tutu aaa=\"bb\"/> </toto>";

        String eol = System.getProperty("line.separator");
        String expectedXml = "";
        expectedXml += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + eol; //
        expectedXml += "<toto>" + eol; //
        expectedXml += "    <tutu aaa=\"bb\"/>" + eol; //
        expectedXml += "</toto>" + eol; //

        assertThat(xcf.format(unformattedXml)).isEqualTo(expectedXml);
    }
}
