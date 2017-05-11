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

package com.jaxio.celerio.configuration.convention;

import com.jaxio.celerio.configuration.EclipseFormatter;
import com.jaxio.celerio.configuration.TrueFalse;
import lombok.Setter;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.jaxio.celerio.configuration.Util.*;

/*
 * Change the default Celerio conventions to your own needs.
 */
public class Conventions {
    private TrueFalse renamePkToIdentifiableProperty = TrueFalse.TRUE;
    private String identifiableProperty = "id";
    private EclipseFormatter eclipseFormatter = new EclipseFormatter();
    @Setter
    private CollectionType collectionType = CollectionType.ArrayList;
    private XmlFormatter xmlFormatter = new XmlFormatter();
    private TrueFalse entitySubPackagePreprended = TrueFalse.TRUE;
    private List<ClassTypeOverride> classTypes = newArrayList();
    private List<GeneratedPackageOverride> generatedPackages = newArrayList();
    private List<MethodConventionOverride> methodConventions = newArrayList();
    private List<WellKnownFolderOverride> wellKnownFolders = newArrayList();
    private List<Renamer> tableRenamers = newArrayList();
    private List<Renamer> columnRenamers = newArrayList();

    /*
     * You can override the default collection type for this entity
     */
    public CollectionType getCollectionType() {
        return firstNonNull(collectionType, CollectionType.ArrayList);
    }

    /**
     * Whether to rename the pk property to the common identifiableProperty. Defaults to TRUE.
     */
    public TrueFalse getRenamePkToIdentifiableProperty() {
        return renamePkToIdentifiableProperty;
    }

    public void setRenamePkToIdentifiableProperty(TrueFalse renamePkToIdentifiableProperty) {
        this.renamePkToIdentifiableProperty = firstNonNull(renamePkToIdentifiableProperty, this.renamePkToIdentifiableProperty);
    }

    /*
     * The property name used in the Identifiable interface. Defaults to 'id'. If all your primary key are mapped to the same property name, you should change
     * the identifiable property here to limit redundancy.
     */
    public String getIdentifiableProperty() {
        return identifiableProperty;
    }

    public void setIdentifiableProperty(String identifiableProperty) {
        this.identifiableProperty = firstNotEmpty(identifiableProperty, this.identifiableProperty);
    }

    /*
     * Defines the formatting option of the generated Java files.
     */
    public EclipseFormatter getEclipseFormatter() {
        return eclipseFormatter;
    }

    public void setEclipseFormatter(EclipseFormatter eclipseFormatter) {
        this.eclipseFormatter = firstNonNull(eclipseFormatter, this.eclipseFormatter);
    }

    /*
     * Defines the formatting options of the generated XML/XHTML files.
     */
    public XmlFormatter getXmlFormatter() {
        return xmlFormatter;
    }

    public void setXmlFormatter(XmlFormatter xmlFormatter) {
        this.xmlFormatter = firstNonNull(xmlFormatter, this.xmlFormatter);
    }

    /*
     * Override the conventions for classes
     */
    public List<ClassTypeOverride> getClassTypes() {
        return classTypes;
    }

    public void setClassTypes(List<ClassTypeOverride> classTypes) {
        this.classTypes = nonNull(classTypes);
    }

    /*
     * Override the conventions for packages
     */
    public List<GeneratedPackageOverride> getGeneratedPackages() {
        return generatedPackages;
    }

    public void setGeneratedPackages(List<GeneratedPackageOverride> generatedPackages) {
        this.generatedPackages = nonNull(generatedPackages);
    }

    /*
     * Override the conventions for methods
     */
    public List<MethodConventionOverride> getMethodConventions() {
        return methodConventions;
    }

    public void setMethodConventions(List<MethodConventionOverride> methodConventions) {
        this.methodConventions = nonNull(methodConventions);
    }

    /*
     * Override the conventions for folders
     */
    public List<WellKnownFolderOverride> getWellKnownFolders() {
        return wellKnownFolders;
    }

    public void setWellKnownFolders(List<WellKnownFolderOverride> wellKnownFolders) {
        this.wellKnownFolders = nonNull(wellKnownFolders);
    }

    public void setEntitySubPackagePreprended(TrueFalse entitySubPackagePreprended) {
        this.entitySubPackagePreprended = firstNonNull(entitySubPackagePreprended, this.entitySubPackagePreprended);
    }

    /*
     * When constructing the package name of a class constructed using a GeneratedPackage, tell if the GeneratedPackage subPackage should be appended. For
     * example given the entity 'MyEntity' with subpackage 'mysubpackage', and the generated package ManagerImpl with subpackage 'impl' then the packageName of
     * all classes for MyEntity constructed using ManagerImpl will have the subpackage 'impl.mysubpackage'
     */
    public TrueFalse getEntitySubPackagePreprended() {
        return entitySubPackagePreprended;
    }

    public boolean prependEntitySubPackage() {
        return getEntitySubPackagePreprended().toBoolean();
    }

    /*
     * Add renamers for tables
     */
    public List<Renamer> getTableRenamers() {
        return tableRenamers;
    }

    public void setTableRenamers(List<Renamer> tableRenamers) {
        this.tableRenamers = nonNull(tableRenamers);
    }

    /*
     * Add renamers for columns
     */
    public List<Renamer> getColumnRenamers() {
        return columnRenamers;
    }

    public void setColumnRenamers(List<Renamer> columnRenamers) {
        this.columnRenamers = nonNull(columnRenamers);
    }

}
