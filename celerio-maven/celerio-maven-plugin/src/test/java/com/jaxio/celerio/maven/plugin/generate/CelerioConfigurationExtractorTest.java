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

package com.jaxio.celerio.maven.plugin.generate;

import com.jaxio.celerio.maven.plugin.celerio.CelerioConfigurationExtractor;
import junit.framework.Assert;
import org.junit.Test;

public class CelerioConfigurationExtractorTest {

    @Test
    public void normalCelerioConfig() {
        String xml = "\n" //
                + "<configuration><celerio:celerio>\n" //
                + "		<celerio:configuration>\n" //
                + "			<celerio:conventions>\n" //
                + "				<celerio:convention name=\"formService\">\n" //
                + "				glouglou\n" //
                + "				</celerio:convention>\n" //
                + "			</celerio:conventions>\n" //
                + "		</celerio:configuration>\n" //
                + "	</celerio:celerio>\n" //
                + "</configuration>";
        CelerioConfigurationExtractor extractor = new CelerioConfigurationExtractor(xml);
        String expected = "\n" //
                + "		<configuration>\n" //
                + "			<conventions>\n" //
                + "				<convention name=\"formService\">\n" //
                + "				glouglou\n" //
                + "				</convention>\n" //
                + "			</conventions>\n" //
                + "		</configuration>\n" //
                + "	";
        Assert.assertEquals(CelerioConfigurationExtractor.CELERIO_XML_START + expected + CelerioConfigurationExtractor.CELERIO_XML_END,
                extractor.getCelerioConfig());
        Assert.assertEquals(true, true);
    }

    @Test
    public void noCelerioConfig() {
        String xml = "\n<configuration><jdbcUser>me</jdbcUser></configuration>";
        CelerioConfigurationExtractor extractor = new CelerioConfigurationExtractor(xml);
        Assert.assertEquals(null, extractor.getCelerioConfig());
    }

    @Test
    public void mixedCelerioConfig() {
        String xml = "\n" //
                + "<configuration><jdbcUser>me</jdbcUser><celerio:celerio>\n" //
                + "		<celerio:configuration>\n" //
                + "			<celerio:conventions>\n" //
                + "				<celerio:convention name=\"formService\">\n" //
                + "				glouglou\n" //
                + "				</celerio:convention>\n" //
                + "			</celerio:conventions>\n" //
                + "		</celerio:configuration>\n" //
                + "	</celerio:celerio>" //
                + "</configuration>";
        CelerioConfigurationExtractor extractor = new CelerioConfigurationExtractor(xml);
        String expected = "\n" //
                + "		<configuration>\n" //
                + "			<conventions>\n" //
                + "				<convention name=\"formService\">\n" //
                + "				glouglou\n" //
                + "				</convention>\n" //
                + "			</conventions>\n" //
                + "		</configuration>\n	";
        Assert.assertEquals(CelerioConfigurationExtractor.CELERIO_XML_START + expected + CelerioConfigurationExtractor.CELERIO_XML_END,
                extractor.getCelerioConfig());
    }
}
