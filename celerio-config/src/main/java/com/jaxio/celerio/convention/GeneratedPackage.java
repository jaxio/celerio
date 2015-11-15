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

package com.jaxio.celerio.convention;

import lombok.Getter;
import lombok.Setter;

import static com.jaxio.celerio.util.PackageUtil.assemblePackage;

/*
 * Commonly used package names.
 */
public enum GeneratedPackage {
    /* package: <code>&lt;root package&gt;.audit</code> */
    Audit("audit"), //
    /* package: <code>&lt;root package&gt;.configuration</code> */
    Configuration("configuration"), //
    /* package: <code>&lt;root package&gt;.context</code> */
    Context("context"), //
    /* package: <code>&lt;root package&gt;.converter</code> */
    Converter("converter"), //
    /* package: <code>&lt;root package&gt;.dao</code> */
    Dao("dao"), //
    /* package: <code>&lt;root package&gt;.dao.listener</code> */
    DaoListener("dao.listener"), //
    /* package: <code>&lt;root package&gt;.dao.support</code> */
    DaoSupport("dao.support"), //
    /* package: <code>&lt;root package&gt;.service.email</code> */
    EmailService("service.email"), //
    /* package: <code>&lt;root package&gt;.web.domain</code> */
    EnumItems("web.domain"), //
    /* package: <code>&lt;root package&gt;.domain</code> */
    EnumModel("domain"), //
    /* package: <code>&lt;root package&gt;.domain</code> */
    EnumModelSupport("domain"), //
    /* package: <code>&lt;root package&gt;.formatter</code> */
    Formatter("formatter"), //
    /* package: <code>&lt;root package&gt;.formatter.support</code> */
    FormatterSupport("formatter.support"), //
    /* package: <code>&lt;root package&gt;.dao</code> */
    Hibernate("dao"), //
    /* package: <code>&lt;root package&gt;.dao.hibernate.listener</code> */
    HibernateListener("dao.hibernate.listener"), //
    /* package: <code>&lt;root package&gt;.dao.hibernate</code> */
    HibernateSupport("dao.hibernate"), //
    /* package: <code>&lt;root package&gt;.domain</code> */
    Model("domain"), //
    /* package: <code>&lt;root package&gt;.domain</code> */
    ModelSupport("domain"), //
    /* package: <code>&lt;root package&gt;.printer</code> */
    Printer("printer"), //
    /* package: <code>&lt;root package&gt;.printer.support</code> */
    PrinterSupport("printer.support"), //
    /* package: <code>&lt;root package&gt;.random</code> */
    Random("random"), //
    /* package: <code>&lt;root package&gt;.repository</code> */
    Repository("repository"), //
    /* package: <code>&lt;root package&gt;.repository.support</code> */
    RepositorySupport("repository.support"), //
    /* package: <code>&lt;root package&gt;</code> */
    Root(""), //
    /* package: <code>&lt;root package&gt;.search</code> */
    Search("search"), //
    /* package: <code>&lt;root package&gt;.security</code> */
    Security("security"), //
    /* package: <code>&lt;root package&gt;.service</code> */
    Service("service"), //
    /* package: <code>&lt;root package&gt;.service</code> */
    ServiceImpl("service"), //
    /* package: <code>&lt;root package&gt;.service.support</code> */
    ServiceSupport("service.support"), //
    /* package: <code>&lt;root package&gt;.util</code> */
    Util("util"), //
    /* package: <code>&lt;root package&gt;.validation</code> */
    Validation("validation"), //
    /* package: <code>&lt;root package&gt;.validation.impl</code> */
    ValidationImpl("validation.impl"), //
    // WEB
    /* package: <code>&lt;root package&gt;.web.controller</code> */
    RestController("web.controller"), //
    /* package: <code>&lt;root package&gt;.web</code> */
    Web("web"), //
    /* package: <code>&lt;root package&gt;.web.action</code> */
    WebAction("web.action"), //
    /* package: <code>&lt;root package&gt;.web.component</code> */
    WebComponent("web.component"), //
    /* package: <code>&lt;root package&gt;.web.configuration</code> */
    WebConfiguration("web.configuration"), //
    /* package: <code>&lt;root package&gt;.web.context</code> */
    WebContext("web.context"), //
    /* package: <code>&lt;root package&gt;.web.controller</code> */
    WebController("web.controller"), //
    /* package: <code>&lt;root package&gt;.web.conversation</code> */
    WebConversation("web.conversation"), //
    /* package: <code>&lt;root package&gt;.web.conversation.component</code> */
    WebConversationComponent("web.conversation.component"), //
    /* package: <code>&lt;root package&gt;.web.converter</code> */
    WebConverter("web.converter"), //
    /* package: <code>&lt;root package&gt;.web.converter.support</code> */
    WebConverterSupport("web.converter.support"), //
    /* package: <code>&lt;root package&gt;.web.ui.el</code> */
    WebEl("web.ui.el"), //
    /* package: <code>&lt;root package&gt;.web.faces</code> */
    WebFaces("web.faces"), //
    /* package: <code>&lt;root package&gt;.web.filter</code> */
    WebFilter("web.filter"), //
    /* package: <code>&lt;root package&gt;.web.flow</code> */
    WebFlow("web.flow"), //
    /* package: <code>&lt;root package&gt;.web.interceptor</code> */
    WebInterceptor("web.interceptor"), //
    /* package: <code>&lt;root package&gt;.web.listener</code> */
    WebListener("web.listener"), //
    /* package: <code>&lt;root package&gt;.web.domain</code> */
    WebModel("web.domain"), //
    /* package: <code>&lt;root package&gt;.web.converter</code> */
    WebModelConverter("web.converter"), //
    /* package: <code>&lt;root package&gt;.web.domain</code> */
    WebModelEntityForm("web.domain"), //
    /* package: <code>&lt;root package&gt;.web.domain</code> */
    WebModelItems("web.domain"), //
    /* package: <code>&lt;root package&gt;.web.domain</code> */
    WebModelSearchForm("web.domain"), //
    /* package: <code>&lt;root package&gt;.web.domain.support</code> */
    WebModelSupport("web.domain.support"), //
    /* package: <code>&lt;root package&gt;.web.domain</code> */
    WebModelValidator("web.domain"), //
    /* package: <code>&lt;root package&gt;.web.permission</code> */
    WebPermission("web.permission"), //
    /* package: <code>&lt;root package&gt;.web.permission.support</code> */
    WebPermissionSupport("web.permission.support"), //
    /* package: <code>&lt;root package&gt;.web.security</code> */
    WebSecurity("web.security"), //
    /* package: <code>&lt;root package&gt;.web.service</code> */
    WebService("web.service"), //
    /* package: <code>&lt;root package&gt;.web.servlet</code> */
    WebServlet("web.servlet"), //
    /* package: <code>&lt;root package&gt;.web.ui</code> */
    WebUi("web.ui"), //
    /* package: <code>&lt;root package&gt;.web.util</code> */
    WebUtil("web.util"), //
    /* package: <code>&lt;root package&gt;.web.validation</code> */
    WebValidation("web.validation"), //
    /* package: <code>&lt;root package&gt;.web.validator</code> */
    WebValidator("web.validator"), //
    // SELENIUM
    /* package: <code>&lt;root package&gt;.web.selenium</code> */
    Selenium("web.selenium"), //
    /* package: <code>&lt;root package&gt;.web.selenium.page</code> */
    SeleniumPage("web.selenium.page"), //
    /* package: <code>&lt;root package&gt;.web.selenium.support</code> */
    SeleniumSupport("web.selenium.support"), //
    /* package: <code>&lt;root package&gt;.web.selenium.support.elements</code> */
    SeleniumElement("web.selenium.support.elements");

    @Getter
    @Setter
    private String subPackage;

    @Getter
    @Setter
    private String rootPackage;

    GeneratedPackage(String subPackage) {
        this.subPackage = subPackage;
    }

    public String getPackageName() {
        return assemblePackage(getRootPackage(), getSubPackage());
    }

    public String getSubPackagePath() {
        return subPackage.replace('.', '/');
    }

    /*
     * It is convenient for creating resources under src/main/resources/ + com/jaxio.
     * 
     * @return the package as a folder. Example: com.jaxio becomes com/jaxio
     */
    public String getPackagePath() {
        return getPackageName().replace('.', '/');
    }
}