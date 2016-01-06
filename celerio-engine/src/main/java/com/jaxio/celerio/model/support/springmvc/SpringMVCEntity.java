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

package com.jaxio.celerio.model.support.springmvc;

import com.jaxio.celerio.convention.ClassType;
import com.jaxio.celerio.model.Entity;
import com.jaxio.celerio.model.support.ClassNamer;
import com.jaxio.celerio.spi.EntitySpi;
import com.jaxio.celerio.support.Namer;
import lombok.Getter;

import static com.jaxio.celerio.convention.ClassType.webConverter;

@Getter
public class SpringMVCEntity implements EntitySpi {
    private Entity entity;
    private Namer formatter;
    private Namer printer;
    private Namer converter;
    private Namer restController;

    /**
     * @return "springMvc";
     */
    @Override
    public String velocityVar() {
        return "springMvc";
    }

    @Override
    public Object getTarget() {
        return this;
    }

    @Override
    public void init(Entity entity) {
        this.entity = entity;
        this.formatter = new ClassNamer(entity, ClassType.formatter);
        this.converter = new ClassNamer(entity, webConverter);
        this.restController = new ClassNamer(entity, ClassType.restController);
    }

    public String getUrl() {
        return "${pageContext.request.contextPath}" + getUrlPath();
    }

    public String getUrlSubPath() {
        return "/" + getViewPath();
    }

    public String getUrlPath() {
        return getUrlSubPath();
    }

    public String getViewPath() {
        return "domain/" + entity.getModel().getVar().toLowerCase();
    }

    public String getRestUrlSubPath() {
        return "domain/rest/" + entity.getModel().getVar().toLowerCase();
    }

    public String getRestUrlPath() {
        return "/" + getRestUrlSubPath();
    }

    public String getRestUrl() {
        return "${pageContext.request.contextPath}" + getRestUrlPath();
    }
}
