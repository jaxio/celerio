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
import com.jaxio.celerio.configuration.convention.XmlFormatter;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Pretty-prints xml, supplied as a string.
 * <p>
 * eg. <code>
 * String formattedXml = new XmlFormatter().format("&lt;tag&gt;&lt;nested&gt;hello&lt;/nested&gt;&lt;/tag&gt;");
 * </code> http://stackoverflow.com/questions/139076/how-to-pretty-print-xml-from-java
 */
@SuppressWarnings("deprecation")
@Service
@Slf4j
public class XmlCodeFormatter {
    @Setter
    @Getter
    XmlFormatter xmlFormatterConfig = new XmlFormatter();

    @Autowired
    public XmlCodeFormatter(Config config) {
        XmlFormatter xf = config.getCelerio().getConfiguration().getConventions().getXmlFormatter();
        if (xf != null) {
            xmlFormatterConfig = xf;
        }
    }

    public String format(String unformattedXml) {
        if (!xmlFormatterConfig.isEnableXmlFormatter()) {
            return unformattedXml;
        }

        try {
            final Document document = parseXmlFile(unformattedXml);
            OutputFormat format = new OutputFormat(document);
            format.setLineWidth(xmlFormatterConfig.getMaximumLineWidth());
            format.setIndenting(true);
            format.setIndent(xmlFormatterConfig.getIndent());
            format.setLineSeparator(System.getProperty("line.separator"));
            Writer out = new StringWriter();
            XMLSerializer serializer = new XMLSerializer(out, format);
            serializer.serialize(document);
            return out.toString();
        } catch (Exception e) {
            log.warn("Could not format the content: " + unformattedXml);
            throw new RuntimeException(e);
        }
    }

    private Document parseXmlFile(String in) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setValidating(false);
            dbf.setExpandEntityReferences(false);

            // prevent dtd download...
            // http://stackoverflow.com/questions/155101/make-documentbuilder-parse-ignore-dtd-references
            dbf.setFeature("http://xml.org/sax/features/namespaces", false);
            dbf.setFeature("http://xml.org/sax/features/validation", false);
            dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
            dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

            DocumentBuilder db = dbf.newDocumentBuilder();

            // // prevent dtd download...
            // db.setEntityResolver(new EntityResolver() {
            // @Override
            // public InputSource resolveEntity(String publicId, String systemId)
            // throws SAXException, IOException {
            // return new InputSource(new StringReader(""));
            // }
            // });
            //
            InputSource is = new InputSource(new StringReader(in));
            return db.parse(is);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
