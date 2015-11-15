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

package com.jaxio.celerio;

import com.jaxio.celerio.configuration.Celerio;
import com.jaxio.celerio.configuration.database.Metadata;
import com.jaxio.celerio.convention.WellKnownFolder;
import com.jaxio.celerio.model.Project;
import com.jaxio.celerio.output.OutputResult;
import com.thoughtworks.xstream.XStream;
import lombok.Data;
import org.springframework.stereotype.Service;

@Data
@Service
public class Config {
    private OutputResult outputResult;
    private Project project = new Project();
    private Celerio celerio = new Celerio();
    private Metadata metadata = new Metadata();
    private Metadata originalMetadata = new Metadata();
    private String baseDir = "";
    private boolean springfuseMode;

    public void reset() {
        outputResult = null;
        project = new Project();
        celerio = new Celerio();
        metadata = new Metadata();
        originalMetadata = new Metadata();
        baseDir = "";
        springfuseMode = false;
    }

    public void setMetadata(Metadata metadata) {
        if (metadata == null) {
            return;
        }
        this.metadata = metadata;
        this.metadata.setDatabaseInfo(metadata.getDatabaseInfo());
        this.metadata.setJdbcConnectivity(metadata.getJdbcConnectivity());
        this.originalMetadata = deepCopy(metadata);
    }

    private Metadata deepCopy(Metadata metadata) {
        XStream XSTREAM = new XStream();
        return (Metadata) XSTREAM.fromXML(XSTREAM.toXML(metadata));
    }

    public void setSpringfuseMode(boolean springfuseMode) {
        this.springfuseMode = springfuseMode;
    }

    public boolean isSpringfuseMode() {
        return springfuseMode;
    }

    public boolean requiresJavaBuildHelper(WellKnownFolder wkf) {
        if (wkf != WellKnownFolder.JAVA && wkf != WellKnownFolder.JAVA_TEST) {
            throw new IllegalArgumentException("Expecting JAVA or JAVA_TEST");
        }

        if (!outputResult.sameDirectory()) {
            return true;
        }

        return !wkf.sameAsGeneratedFolder();
    }
}