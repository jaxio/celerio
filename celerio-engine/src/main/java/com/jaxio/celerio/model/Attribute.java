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

package com.jaxio.celerio.model;

import static com.google.common.base.Predicates.and;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.jaxio.celerio.configuration.Module.CHAR_PADDING;
import static com.jaxio.celerio.configuration.database.JdbcType.CHAR;
import static com.jaxio.celerio.configuration.database.support.SqlUtil.escapeSql;
import static com.jaxio.celerio.model.support.AttributePredicates.BLOB;
import static com.jaxio.celerio.model.support.AttributePredicates.NUMERIC;
import static com.jaxio.celerio.model.support.AttributePredicates.STRING;
import static com.jaxio.celerio.model.support.SuffixPrefixPredicates.CONTAINS_HTML;
import static com.jaxio.celerio.model.support.SuffixPrefixPredicates.HAS_FILE_ATTRIBUTES;
import static com.jaxio.celerio.model.support.SuffixPrefixPredicates.IS_BINARY_SUFFIX;
import static com.jaxio.celerio.model.support.SuffixPrefixPredicates.IS_CONTENT_TYPE_SUFFIX;
import static com.jaxio.celerio.model.support.SuffixPrefixPredicates.IS_EMAIL_SUFFIX;
import static com.jaxio.celerio.model.support.SuffixPrefixPredicates.IS_FILE_NAME_SUFFIX;
import static com.jaxio.celerio.model.support.SuffixPrefixPredicates.IS_FILE_SIZE_SUFFIX;
import static com.jaxio.celerio.model.support.SuffixPrefixPredicates.IS_LABEL;
import static com.jaxio.celerio.model.support.SuffixPrefixPredicates.IS_LANGUAGE_SUFFIX;
import static com.jaxio.celerio.model.support.SuffixPrefixPredicates.IS_LOCALE_SUFFIX;
import static com.jaxio.celerio.model.support.SuffixPrefixPredicates.IS_PASSWORD_SUFFIX;
import static com.jaxio.celerio.model.support.SuffixPrefixPredicates.IS_URL_SUFFIX;
import static com.jaxio.celerio.model.support.SuffixPrefixPredicates.IS_VERSION_SUFFIX;
import static com.jaxio.celerio.util.FallBackUtil.fallBack;
import static com.jaxio.celerio.util.MiscUtil.toReadableLabel;
import static org.apache.commons.lang.BooleanUtils.toBoolean;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.jaxio.celerio.Config;
import com.jaxio.celerio.aspects.ForbiddenWhenBuilding;
import com.jaxio.celerio.aspects.ForbiddenWhenBuildingAspect;
import com.jaxio.celerio.configuration.database.JdbcType;
import com.jaxio.celerio.configuration.entity.ColumnConfig;
import com.jaxio.celerio.configuration.entity.EnumConfig;
import com.jaxio.celerio.configuration.entity.IndexedField;
import com.jaxio.celerio.convention.CommentStyle;
import com.jaxio.celerio.factory.RelationCollisionUtil;
import com.jaxio.celerio.model.support.AttributeSetup;
import com.jaxio.celerio.model.support.EnumNamer;
import com.jaxio.celerio.model.support.SuffixPrefixPredicates.AttributeShareSameSuffix;
import com.jaxio.celerio.model.support.jpa.JpaAttribute;
import com.jaxio.celerio.support.AbstractNamer;
import com.jaxio.celerio.util.Labels;
import com.jaxio.celerio.util.MappedType;
import com.jaxio.celerio.util.Named;
import com.jaxio.celerio.util.StringUtil;

/**
 * JPA Attribute meta information.
 */
@Component
@Scope(SCOPE_PROTOTYPE)
@Getter
public class Attribute extends AbstractNamer implements Named, Map<String, Object> {
    @Autowired
    private Config config;
    @Autowired
    private RelationCollisionUtil collisionUtil;
    private Entity entity;
    private ColumnConfig columnConfig;
    private MappedType mappedType;
    private JpaAttribute jpa = new JpaAttribute(this); // TODO: make it an SPI

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public void setColumnConfig(ColumnConfig columnConfig) {
        Assert.isNull(this.columnConfig, "you can set the columnConfig only once");
        this.columnConfig = columnConfig;
    }

    // -----------------------------------------------------
    // Namer override
    // -----------------------------------------------------
    @Autowired
    ForbiddenWhenBuildingAspect fwba;

    private String cachedVar;

    @ForbiddenWhenBuilding
    @Override
    public String getVar() {
        fwba.checkNotForbidden();
        if (cachedVar != null) {
            return cachedVar;
        } else {
            cachedVar = StringUtil.escape(columnConfig.getFieldName());
            return cachedVar;
        }
    }

    // -----------------------------------------------------
    // Enum Namers (lazy as attribute is not always enum)
    // -----------------------------------------------------
    private EnumType enumType;

    public EnumType getEnumType() {
        if (enumType == null) {
            enumType = new EnumType(this.getEnumConfig());
        }
        return enumType;
    }

    // -----------------------------------------------------
    // Entity shortcuts
    // -----------------------------------------------------

    public String getFullName() {
        return getEntity().getName() + "." + getVar();
    }

    public String getFullVar() {
        return getEntity().getModel().getVar() + "." + getVar();
    }

    public String getFullModelVar() {
        return getEntity().getModel().getVar() + "." + getVar();
    }

    public String getFullColumnName() {
        return getEntity().getTableName() + "." + columnConfig.getColumnName();
    }

    // -----------------------------------------------------
    // Validation
    // -----------------------------------------------------

    public String getValidate() {
        // since we generate inside a validator class,
        // no need to prefix it with 'validate'... it would
        // be to verbose in xhtml/jsf pages
        return getVar();
    }

    // -----------------------------------------------------
    // Internationalization
    // -----------------------------------------------------
    Labels labels;

    public String getLabelName() {
        return getEntity().getModel().getVar() + "_" + getVar();
    }

    public Labels getLabels() {
        if (labels == null) {
            labels = new Labels(getColumnConfig().getLabels());
            labels.setFallBack(fallBack(getColumnConfig().getLabel(), toReadableLabel(getColumnConfig().getFieldName())));
        }
        return labels;
    }

    // -----------------------------------------------------
    // PK
    // -----------------------------------------------------

    @Setter
    private boolean simplePk;
    private boolean inCpk;

    /**
     * Null means, we do not know... happens for example when the driver does not support the IS_AUTOINCREMENT feature.
     */
    @ForbiddenWhenBuilding
    public Boolean getAutoIncrement() {
        return getColumnConfig().getAutoIncrement();
    }

    @ForbiddenWhenBuilding
    public boolean isSimplePk() {
        return simplePk;
    }

    public void setInCpk(boolean inCpk) {
        this.inCpk = inCpk;
        // since this attribute is not going to be mapped, we can remove it from var clash
        collisionUtil.removeVar(getEntity().getName(), getColumnConfig().getFieldName());
    }

    @ForbiddenWhenBuilding
    public boolean isInCpk() {
        return inCpk;
    }

    // -----------------------------------------------------
    // FK
    // -----------------------------------------------------

    private boolean simpleFk;
    private boolean inCompositeFk;

    public void setSimpleFk(boolean simpleFk) {
        this.simpleFk = simpleFk;

        if (!isInPk()) {
            // since this attribute is not going to be mapped, we can remove it from var clash
            collisionUtil.removeVar(getEntity().getName(), getColumnConfig().getFieldName());
        }
    }

    @ForbiddenWhenBuilding
    public boolean isSimpleFk() {
        return simpleFk;
    }

    public void setInCompositeFk(boolean inCompositeFk) {
        this.inCompositeFk = inCompositeFk;
        if (!isInPk()) {
            // since this attribute is not going to be mapped, we can remove it from var clash
            collisionUtil.removeVar(getEntity().getName(), getColumnConfig().getFieldName());
        }
    }

    @ForbiddenWhenBuilding
    public boolean isInCompositeFk() {
        return inCompositeFk;
    }

    public boolean isSimple() {
        return !(isInPk() || isInFk() || isVersion());
    }

    private String setterAccessibility;

    public String getSetterAccessibility() {
        if (setterAccessibility == null) {
            if (!isInPk() && isInFk() && hasXToOneRelation()) {
                // developer are confused if it is public and are tempted to use it.
                // The only reason we generate a setter is for Hibernate search by example feature.
                // we make it private to avoid confusions..
                setterAccessibility = "private";
            } else {
                setterAccessibility = "public";
            }
        }
        return setterAccessibility;
    }

    public boolean isSetterAccessibilityPublic() {
        return getSetterAccessibility().equals("public");
    }

    // -----------------------------------------------------
    // BK
    // -----------------------------------------------------
    @Setter
    @Getter
    private boolean inBk;

    // -----------------------------------------------------
    // Column shortcuts
    // -----------------------------------------------------

    @Override
    public String getName() {
        return columnConfig.getFieldName();
    }

    public String getColumnName() {
        return columnConfig.getColumnName();
    }

    private String columnNameEscaped;

    public String getColumnNameEscaped() {
        if (columnNameEscaped == null) {
            columnNameEscaped = escapeSql(getColumnName());
        }
        return columnNameEscaped;
    }

    public String getColumnFullName() {
        return getTableName() + "." + getColumnName();
    }

    public String getTableName() {
        return columnConfig.getTableName();
    }

    public JdbcType getJdbcType() {
        return getColumnConfig().getType();
    }

    public boolean isJavaBaseClass() {
        return getMappedType().isJavaBaseClass() && !isEnum();
    }

    public boolean isEnum() {
        return getColumnConfig().hasEnum();
    }

    public boolean isSortable() {
        return !isBinary() && !isTransient();
    }

    public EnumConfig getEnumConfig() {
        return getColumnConfig().getEnumConfig();
    }

    public EnumNamer getEnumModel() {
        return getEnumType().getModel();
    }

    public String getEnumClass() {
        return getEnumModel().getType();
    }

    public String getEnumItemsType() {
        return getEnumType().getItems().getType();
    }

    public String getEnumItemsVar() {
        return getEnumType().getItems().getVar();
    }

    public int getSize() {
        return getColumnConfig().getSize();
    }

    public boolean isFixedSize() {
        if (getJdbcType() == CHAR) {
            return true;
        }

        if (getColumnConfig().getSize() != null && getColumnConfig().getSize().equals(getColumnConfig().getMin())) {
            return true;
        }
        return false;
    }

    public String getComment() {
        return getColumnConfig().getComment();
    }

    public String getJavadoc() {
        if (getColumnConfig().hasComment()) {
            return CommentStyle.JAVADOC.decorate(getColumnConfig().getComment(), "    ");
        } else if (getLabels().hasBaseLabel()) {
            return CommentStyle.JAVADOC.decorate(labels.getLabel(), "    ");
        }
        return "";
    }

    public boolean hasComment() {
        return getColumnConfig().hasComment();
    }

    @ForbiddenWhenBuilding
    public boolean isUnique() {
        return getColumnConfig().getUnique();
    }

    public boolean isRequired() {
        return !isNullable() || isUnique() || isCharPadding();
    }

    public boolean isCharPadding() {
        return isFixedSize() && !isEnum() && getConfig().getCelerio().getConfiguration().has(CHAR_PADDING);
    }

    public boolean isNullable() {
        return getColumnConfig().getNullable();
    }

    public boolean isNotNullable() {
        return !isNullable();
    }

    public boolean hasDefaultValue() {
        return getJavaDefaultValue() != null;
    }

    public boolean hasPertinentDefaultValue() {
        return !isInPk() && !isInFk() && !isVersion() && hasDefaultValue();
    }

    public String getJavaDefaultValue() {
        if (getColumnConfig().getDefaultValue() == null || isBlob()) {
            return null;
        } else if (isEnum()) {
            EnumConfig enumConfig = getColumnConfig().getEnumConfig();
            if (enumConfig.isCustomType() || enumConfig.isOrdinal()) {
                return getEnumClass() + "." + enumConfig.getEnumNameByValue(getColumnConfig().getDefaultValue());
            } else {
                return getEnumClass() + "." + getColumnConfig().getDefaultValue();
            }
        } else if (isString()) {
            return "\"" + getColumnConfig().getDefaultValue() + "\"";
        } else if (isBoolean()) {
            if ("1".equals(getColumnConfig().getDefaultValue())) {
                return "true";
            } else {
                return toBoolean(getColumnConfig().getDefaultValue()) ? "true" : "false";
            }
        } else if (isDate()) {
            List<String> isNow = newArrayList("now()", "sysdate", "current_time");
            if (isNow.contains(getColumnConfig().getDefaultValue().toLowerCase())) {
                return "new " + getFullType() + "()";
            } else {
                return null;
            }
        } else if (isNumeric()) {
            String defaultValue = getColumnConfig().getDefaultValue();
            if (NumberUtils.isNumber(defaultValue)) {
                if (isBigDecimal()) {
                    return "new BigDecimal(\"" + defaultValue + "\")"; // use the right scale
                } else if (isBigInteger()) {
                    return "new BigInteger(\"" + defaultValue + "\")";
                } else if (isLong()) {
                    return defaultValue + "l";
                } else if (isDouble()) {
                    return defaultValue + "d"; // so it is considered as a double
                } else if (isFloat()) {
                    return defaultValue + "f"; // as by default it would be considered as a double
                } else {
                    return defaultValue;
                }
            } else {
                return null;
            }
        } else {
            return getColumnConfig().getDefaultValue();
        }
    }

    private String displayOrderAsString;

    /**
     * Used to sort columns in abstract list holder. Must be a String.
     */
    public String getDisplayOrderAsString() {
        if (displayOrderAsString == null) {
            if (getColumnConfig().getDisplayOrder() < 10) {
                displayOrderAsString = "00" + getColumnConfig().getDisplayOrder();
            } else if (getColumnConfig().getDisplayOrder() < 100) {
                displayOrderAsString = "0" + getColumnConfig().getDisplayOrder();
            } else {
                displayOrderAsString = "" + getColumnConfig().getDisplayOrder();
            }
        }
        return displayOrderAsString;
    }

    // -----------------------------------------------------
    // Column defaulting to MappedType
    // -----------------------------------------------------

    @Override
    public String getType() {
        if (isEnum()) {
            return getEnumModel().getType();
        }
        return getMappedType().getJavaType();
    }

    @Override
    public String getFullType() {
        if (isEnum()) {
            return getEnumFullType();
        }
        return getMappedType().getFullJavaType();
    }

    private String getEnumFullType() {
        return getEnumModel().getFullType();
    }

    /**
     * Since import is always complicated, I use full type when needed for now in XxxValidator...
     */
    public String getFullTypeIfImportNeeded() {
        return isJavaBaseClass() ? getType() : getFullType();
    }

    // -----------------------------------------------------
    // Convention over configuration
    // -----------------------------------------------------

    public boolean isLocaleKey() {
        // configuration
        if (null != getColumnConfig().getMessageKey()) {
            return getColumnConfig().getMessageKey();
        }

        // convention
        return IS_LOCALE_SUFFIX.apply(this);
    }

    public boolean columnNameHasLanguageSuffix() {
        // convention
        return IS_LANGUAGE_SUFFIX.apply(this);
    }

    public boolean isLabel() {
        // convention
        return isSimple() && IS_LABEL.apply(this);
    }

    public String getColumnNameWithoutLanguage() {
        if (columnNameHasLanguageSuffix()) {
            return StringUtils.substringBeforeLast(getColumnName(), "_").toLowerCase();
        } else {
            throw new IllegalStateException("Can be invoked only if columnNameHasLanguageSuffix returns true, please write safer code");
        }
    }

    public String getColumnNameLanguage() {
        if (columnNameHasLanguageSuffix()) {
            return StringUtils.substringAfterLast(getColumnName(), "_").toLowerCase();
        } else {
            throw new IllegalStateException("Can be invoked only if columnNameHasLanguageSuffix returns true, please write safer code");
        }
    }

    public boolean isVersion() {
        boolean isVersion = false;

        if (null != getColumnConfig().getVersion()) {
            // configuration
            isVersion = getColumnConfig().getVersion();
            if (isVersion && !getMappedType().isEligibleForVersion()) {
                throw new IllegalStateException("The column " + getFullColumnName()
                        + " type cannot be used with @Version. Please review the entityConfig of entityName=" + getEntity().getName());
            }
        } else {
            // convention
            isVersion = IS_VERSION_SUFFIX.apply(this);
        }

        return isVersion && getMappedType().isEligibleForVersion();
    }

    private Boolean isInFileDefinition;

    public boolean isInFileDefinition() {
        if (isInFileDefinition == null) {
            isInFileDefinition = HAS_FILE_ATTRIBUTES.apply(this) && (isFile() || isFileSize() || isFilename() || isContentType());
        }
        return isInFileDefinition;
    }

    public boolean isFile() {
        return (isBlob() || isFileBinary()) && !isInPk();
    }

    private Boolean isFile;

    public boolean isFileBinary() {
        if (isFile == null) {
            isFile = IS_FILE_BINARY.apply(this);
        }
        return isFile;
    }

    private Boolean isFileSize;

    public boolean isFileSize() {
        if (isFileSize == null) {
            isFileSize = IS_FILE_SIZE.apply(this);
        }

        return isFileSize;
    }

    private Boolean isFilename;

    public boolean isFilename() {
        if (isFilename == null) {
            isFilename = IS_FILE_NAME.apply(this);
        }
        return isFilename;
    }

    private Boolean isContentType;

    public boolean isContentType() {
        if (isContentType == null) {
            isContentType = IS_FILE_CONTENT_TYPE.apply(this);
        }
        return isContentType;
    }

    public boolean isPatternSearchable() {
        return isString() && isSimple() && !isEnum() && !isTransient();
    }

    public boolean isCpkPatternSearchable() {
        return isString() && !isEnum() && !isTransient();
    }

    public boolean isTransient() {
        return getColumnConfig().isTransient();
    }

    public Attribute getFileSize() {
        return findFileAttributeOrNull(IS_FILE_SIZE);
    }

    public Attribute getFileContentType() {
        return findFileAttributeOrNull(IS_FILE_CONTENT_TYPE);
    }

    public Attribute getFilename() {
        return findFileAttributeOrNull(IS_FILE_NAME);
    }

    public Attribute getFile() {
        Attribute result = isBlob() ? this : findFileAttributeOrNull(IS_FILE_BINARY);
        if (result == null) {
            // Note: in some case, when we have multiple binary, there could be only one filename (ex: an image and the thumbnail)
            // Here, the suffix does not match, let's return the first binary file we find. This way we 
            // do not return null and avoid issue in front end templates.
            result = find(getEntity().getAttributes().getList(), BLOB);
        }
        return result;
    }

    private Attribute findFileAttributeOrNull(Predicate<Attribute> predicate) {
        try {
            return find(getEntity().getAttributes().getList(), and(predicate, new AttributeShareSameSuffix(this)));
        } catch (NoSuchElementException nse) {
            return null; // can be interpreted as 'false' in velocity if statement
        }
    }

    private Boolean isEmail;

    public boolean isEmail() {
        if (isEmail == null) {
            isEmail = IS_EMAIL.apply(this);
        }
        return isEmail;
    }

    private Boolean isPassword;

    public boolean isPassword() {
        if (isPassword == null) {
            isPassword = IS_PASSWORD.apply(this);
        }
        return isPassword;
    }

    public boolean isVisible() {
        if (getColumnConfig().getVisible() != null) {
            return getColumnConfig().getVisible();
        } else if (isSimplePk() && jpa.isManuallyAssigned()) {
            return true;
        } else if (isSimplePk() && !isInFk()) {
            return false;
        } else if (isSimpleFk()) {
            return true;
        } else if (isInFk()) {
            return false;
        } else if (isVersion() || getEntity().getAuditEntityAttributes().contains(this)) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isFormField() {
        // configuration
        if (getEntity().getEntityConfig().useFormFieldConfig()) {
            if (getColumnConfig().getFormField() != null) {
                return getColumnConfig().getFormField();
            } else {
                return false;
            }
        }

        // convention
        if (isSimplePk() || isVersion()) {
            return true;
        } else if (isFile()) {
            return true;
        } else if (isInFileDefinition()) {
            return false;
        } else {
            return isVisible();
        }
    }

    public boolean isSearchResultFieldConvention() {
        return isVisible() && !isHtml() && !isBinary() && !isFileSize() && !isContentType() && !(hasXToOneRelation() && !getXToOneRelation().hasInverse());
    }

    public boolean isSearchResultField() {
        if (getColumnConfig().getSearchResult() != null) {
            // configuration
            return getColumnConfig().getSearchResult();
        } else {
            // convention
            return isSearchResultFieldConvention();
        }
    }

    public boolean isSearchField() {
        if (getColumnConfig().getSearchField() != null) {
            // configuration
            return getColumnConfig().getSearchField();
        } else {
            // convention
            return isVisible() && !isInFileDefinition() && !isBinary() && !isHtml() && !isTransient();
        }
    }

    /**
     * Can we apply a search by range with this attribute?
     */
    public boolean isRangeable() {
        return (!isInPk() && !isInFk() && !isVersion()) && (isDate() || isNumeric()) && !isEnum();
    }

    /**
     * Can we apply a search with a PropertySelector on this attribute?
     */
    public boolean isMultiSelectable() {
        return ((!isInPk() && !isInFk() && !isVersion()) || (isSimplePk() && jpa.isManuallyAssigned())) //
                && (isBoolean() || isEnum() || isString() || isNumeric());
    }

    // -----------------------------------------------------
    // Mapped Type & shortcuts
    // -----------------------------------------------------

    public MappedType getMappedType() {
        if (mappedType == null) {
            mappedType = new AttributeSetup(this).getMappedType();
        }

        return mappedType;
    }

    @Override
    final public String getPackageName() {
        if (isEnum()) {
            return getEnumModel().getPackageName();
        } else {
            return getMappedType().getPackageName();
        }
    }

    /**
     * Whether this attribute is numeric and is neither an enum nor a version.
     * @return
     */
    final public boolean hasDigits() {
        return isNumeric() && !isEnum() && !isVersion();
    }

    final public boolean isNumeric() {
        return getMappedType().isNumeric();
    }

    final public boolean isLong() {
        return getMappedType().isLong();
    }

    final public boolean isInteger() {
        return getMappedType().isInteger();
    }

    final public boolean isBigInteger() {
        return getMappedType().isBigInteger();
    }

    final public boolean isDouble() {
        return getMappedType().isDouble();
    }

    final public boolean isFloat() {
        return getMappedType().isFloat();
    }

    final public boolean isBigDecimal() {
        return getMappedType().isBigDecimal();
    }

    final public boolean isString() {
        return getMappedType().isString();
    }

    final public boolean isChar() {
        return getMappedType().isChar();
    }

    final public boolean isBoolean() {
        return getMappedType().isBoolean();
    }

    final public boolean isDate() {
        return getMappedType().isDate();
    }

    final public boolean isJavaUtilDate() {
        return getMappedType().isJavaUtilDate();
    }

    final public boolean isJavaUtilOnlyDate() {
        return getMappedType().isJavaUtilDate() && getJdbcType() == JdbcType.DATE;
    }

    final public boolean isJavaUtilDateAndTime() {
        return getMappedType().isJavaUtilDate() && getJdbcType() == JdbcType.TIMESTAMP;
    }

    final public boolean isJavaUtilOnlyTime() {
        return getMappedType().isJavaUtilDate() && getJdbcType() == JdbcType.TIME;
    }

    final public boolean isLocalDateOrTime() {
        return isLocalDate() || isLocalDateTime();
    }

    final public boolean isLocalDate() {
        return getMappedType().isLocalDate();
    }

    final public boolean isLocalDateTime() {
        return getMappedType().isLocalDateTime();
    }

    final public boolean isLob() {
        return getMappedType().isLob();
    }

    final public boolean isBlob() {
        return getMappedType().isBlob();
    }

    final public boolean isClob() {
        return getMappedType().isClob();
    }

    final public boolean isComparable() {
        return getMappedType().isComparable();
    }

    // -----------------------------------------------
    // Derived
    // -----------------------------------------------


    /**
     * Whether this attribute is an integer, a long or a big integer.
     */
    final public boolean isIntegralNumber() {
        return isInteger() || isLong() || isBigInteger();
    }
    
    /**
     * Is the corresponding column a LOB, BLOB or CLOB.
     * @return
     */
    final public boolean isBinary() {
        return isLob() || isBlob() || isClob();
    }

    /**
     * Whether this attribute has a non null {@link IndexedField} configuration element. 
     * @see ColumnConfig#getIndexedField
     */
    public boolean isIndexed() {
        return getColumnConfig().getIndexedField() != null;
    }

    public boolean isInPk() {
        return isSimplePk() || isInCpk();
    }

    public boolean isInFk() {
        return isSimpleFk() || isInCompositeFk();
    }

    public boolean isHidden() {
        return !isVisible();
    }

    public boolean isLazyLoaded() {
        return getColumnConfig().hasLazy() ? getColumnConfig().getLazy() : isLob();
    }

    public boolean isLocalizable() {
        return isLocaleKey() || isDate() || isBoolean() || isEnum();
    }

    public boolean hasIntSetter() {
        return !isInteger() && isNumeric() && !isVersion() && !isEnum() && isSetterAccessibilityPublic();
    }

    public boolean isHtml() {
        // configuration
        if (getColumnConfig().hasHtml()) {
            return getColumnConfig().getHtml();
        } else if (getColumnConfig().hasSafeHtml()) {
            return true;
        } else {
            // convention
            return CONTAINS_HTML.apply(this);
        }
    }

    public boolean isSafeHtml() {
        if (getColumnConfig().getHtml() == Boolean.FALSE) {
            return false;
        } else if (getColumnConfig().hasSafeHtml()) {
            return true;
        }
        return false;
    }

    public boolean isUrl() {
        return isString() && IS_URL_SUFFIX.apply(this);
    }

    public boolean isTextArea() {
        return isString() && getSize() >= 255;
    }

    @Override
    public String toString() {
        return getName() + " " + ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    public boolean isAuditEntityAttribute() {
        return getEntity().getAuditEntityAttributes().contains(this);
    }

    // -----------------------------------------------
    // equals & hashCode
    // -----------------------------------------------

    @Override
    final public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Attribute)) {
            return false;
        }

        Attribute other = (Attribute) o;
        return getColumnFullName().equals(other.getColumnFullName());
    }

    @Override
    final public int hashCode() {
        return getColumnFullName().hashCode();
    }

    // -----------------------------------------------------
    // Account Entity Related
    // -----------------------------------------------------

    public String getDefaultValueForCurrentAccountId() {
        if (getEntity().isAccount()) {
            if (getMappedType().isString()) {
                return "\"-1\"";
            }

            if (getMappedType().isLong()) {
                return "-1l";
            }

            // default
            return "-1";
        }

        return "null"; // won't work...
    }

    // -----------------------------------------------------
    // Very useful from the View
    // -----------------------------------------------------

    /**
     * When there are dozen of attributes, it is convenient to have next to the attribute declaration a short comment such as "// not null"
     */
    public String getOneLineComment() {
        if (isSimplePk()) {
            return " // pk";
        }
        if (isNullable() && isUnique()) {
            return " // unique (but null allowed)";
        }
        if (isNotNullable() && isUnique()) {
            return " // unique (not null)";
        }

        if (isNotNullable()) {
            return " // not null";
        }

        return "";
    }

    public boolean hasXToOneRelation() {
        return getXToOneRelation() != null;
    }

    /**
     * Should replace hasXToOneRelation.
     */
    public boolean hasForwardXToOneRelation() {
        return getXToOneRelation() != null && !getXToOneRelation().isInverse();
    }

    private Relation xToOneRelation;
    private boolean xToOneRelationSet;

    @ForbiddenWhenBuilding
    public Relation getXToOneRelation() {
        if (!xToOneRelationSet) {
            for (Relation r : getEntity().getXToOne().getList()) {
                if (r.isIntermediate()) {
                    continue;
                } else if (r.getFromAttribute() == this) {
                    xToOneRelation = r;
                    xToOneRelationSet = true;
                    break;
                }
            }
        }

        return xToOneRelation;
    }

    public Entity getEntityIPointTo() {
        Relation relation = getXToOneRelation();
        if (relation == null) {
            throw new IllegalStateException("you should have at least a XToOne relation");
        }
        return relation.getToEntity();
    }

    public String getVarPath() {
        return isInCpk() ? getEntity().getPrimaryKey().getVar() + "." + getVar() : getVar();
    }

    // derived
    public static Predicate<Attribute> IS_FILE_SIZE = and(NUMERIC, and(IS_FILE_SIZE_SUFFIX, HAS_FILE_ATTRIBUTES));
    public static Predicate<Attribute> IS_FILE_NAME = and(STRING, and(IS_FILE_NAME_SUFFIX, HAS_FILE_ATTRIBUTES));
    public static Predicate<Attribute> IS_FILE_BINARY = and(BLOB, and(IS_BINARY_SUFFIX, HAS_FILE_ATTRIBUTES));
    public static Predicate<Attribute> IS_FILE_CONTENT_TYPE = and(STRING, and(IS_CONTENT_TYPE_SUFFIX, HAS_FILE_ATTRIBUTES));
    public static Predicate<Attribute> IS_EMAIL = and(STRING, IS_EMAIL_SUFFIX);
    public static Predicate<Attribute> IS_PASSWORD = and(STRING, IS_PASSWORD_SUFFIX);
    
    // ------------------------------------
    // SPI are put in a Map so we can access
    // from velocity templates as if we had getter.
    // ------------------------------------

    private Map<String, Object> spis = newHashMap();

    @Override
    public void clear() {
        spis.clear();
    }

    @Override
    public boolean containsKey(Object arg0) {
        return spis.containsKey(arg0);
    }

    @Override
    public boolean containsValue(Object arg0) {
        return spis.containsValue(arg0);
    }

    @Override
    public Set<java.util.Map.Entry<String, Object>> entrySet() {
        return spis.entrySet();
    }

    @Override
    public Object get(Object arg0) {
        Object o = spis.get(arg0);
        Preconditions.checkNotNull(o, "No SPI having its var=" + arg0 + " was found. Tip: in your template for predicate method, use always ref.isSomething() instead of xxx.something");
        return o;
    }

    @Override
    public boolean isEmpty() {
        return spis.isEmpty();
    }

    @Override
    public Set<String> keySet() {
        return spis.keySet();
    }

    @Override
    public Object put(String arg0, Object arg1) {
        return spis.put(arg0, arg1);
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> arg0) {
        spis.putAll(arg0);
    }

    @Override
    public Object remove(Object arg0) {
        return spis.remove(arg0);
    }

    @Override
    public int size() {
        return spis.size();
    }

    @Override
    public Collection<Object> values() {
        return spis.values();
    }
}
