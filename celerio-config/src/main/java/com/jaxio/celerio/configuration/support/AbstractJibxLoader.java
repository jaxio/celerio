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

package com.jaxio.celerio.configuration.support;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.springframework.oxm.XmlMappingException;
import org.springframework.oxm.jibx.JibxMarshaller;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;

public abstract class AbstractJibxLoader<T> {

    public abstract JibxMarshaller getMarshaller();

    public T load(String filename) throws XmlMappingException, IOException {
        return load(new File(filename));
    }

    public T load(File file) throws XmlMappingException, IOException {
        return load(new FileInputStream(file));
    }

    @SuppressWarnings("unchecked")
    public T load(InputStream inputStream) throws XmlMappingException, IOException {
        return (T) getMarshaller().unmarshal(new StreamSource(inputStream));
    }

    public void write(T object, String filename) throws XmlMappingException, IOException {
        write(object, new File(filename));
    }

    public void write(T object, File file) throws XmlMappingException, IOException {
        try {
            file.getParentFile().mkdirs();
        } catch (Exception e) {
            //
        }
        write(object, new FileOutputStream(file));
    }

    public void write(T object, OutputStream outputStream) throws XmlMappingException, IOException {
        getMarshaller().marshal(object, new StreamResult(outputStream));
    }

    public T fromXml(String xml) throws XmlMappingException, IOException {
        return load(new ByteArrayInputStream(xml.getBytes()));
    }

    public String toXml(T object) throws XmlMappingException, IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        write(object, stream);
        return stream.toString();
    }
}
