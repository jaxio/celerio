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

package com.jaxio.celerio.support;

import com.jaxio.celerio.util.StringUtil;
import lombok.Getter;

@Getter
public class AccessorNamer extends AbstractNamer {
    private String var;
    private String vars;
    private String type;
    private String packageName;

    public AccessorNamer(Namer namer) {
        this.var = StringUtil.escape(namer.getVar());
        this.type = namer.getType();
        this.packageName = namer.getPackageName();
    }

    public AccessorNamer(Namer namer, String var) {
        this.var = StringUtil.escape(var);
        this.type = namer.getType();
        this.packageName = namer.getPackageName();
    }

    public AccessorNamer(Namer namer, String var, String vars) {
        this.var = StringUtil.escape(var);
        this.vars = StringUtil.escape(vars);
        this.type = namer.getType();
        this.packageName = namer.getPackageName();
    }

    // used for tests
    public AccessorNamer(String var, String type, String packageName) {
        this.var = StringUtil.escape(var);
        this.type = type;
        this.packageName = packageName;
    }

    @Override
    public String getVars() {
        return vars != null ? vars : super.getVars();
    }
}