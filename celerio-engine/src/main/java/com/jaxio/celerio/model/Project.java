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

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Sets;
import com.jaxio.celerio.configuration.database.Table;
import com.jaxio.celerio.convention.ClassType;
import com.jaxio.celerio.convention.GeneratedPackage;
import com.jaxio.celerio.model.support.EntityPredicates;
import com.jaxio.celerio.util.support.SimpleListHolder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Iterables.any;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static com.jaxio.celerio.model.support.EntityPredicates.*;
import static java.util.Collections.unmodifiableList;

@Slf4j
@Getter
public class Project implements Map<String, Object> {
    private List<Entity> currentEntities = newArrayList();
    @Getter(value = AccessLevel.PRIVATE)
    private Map<String, Entity> currentEntitiesByTableName = newHashMap();
    @Getter(value = AccessLevel.PRIVATE)
    private Map<String, Entity> currentEntitiesBySchemaAndTableName = newHashMap();
    @Getter(value = AccessLevel.PRIVATE)
    private Map<String, Entity> currentEntitiesByName = newHashMap();
    private SimpleListHolder<Entity> entities = newEntityHolder();
    private SimpleListHolder<Entity> rootEntities = newEntityHolder(ROOT);
    private SimpleListHolder<Entity> entitiesWithFiles = newEntityHolder(HAS_FILE);
    private SimpleListHolder<Entity> virtuals = newEntityHolder(VIRTUAL);
    private SimpleListHolder<Entity> views = newEntityHolder(VIEW);
    private SimpleListHolder<Entity> tables = newEntityHolder(TABLE);
    private SimpleListHolder<Entity> withoutManyToManyJoinEntities = newEntityHolder(NOT_MANY_TO_MANY_JOIN);
    private SimpleListHolder<Entity> search = newEntityHolder(SEARCH);
    private Map<String, Object> spis = newHashMap();

    public Project() {
    }

    public Project(List<Entity> entities) {
        this.currentEntities = entities;
    }

    private SimpleListHolder<Entity> newEntityHolder() {
        return new SimpleListHolder<Entity>(currentEntities);
    }

    private SimpleListHolder<Entity> newEntityHolder(Predicate<Entity> predicate) {
        return new SimpleListHolder<Entity>(currentEntities, predicate, "name");
    }

    public void reset() {
        currentEntities.clear();
        currentEntitiesByTableName.clear();
        currentEntitiesBySchemaAndTableName.clear();
        currentEntitiesByName.clear();
        entities = new SimpleListHolder<Entity>(currentEntities);
    }

    public void addEntity(Entity entity) {
        if (log.isDebugEnabled()) {
            log.debug("Adding entity entityName=" + entity.getName() + " tableName=" + entity.getTableName());
        }

        putEntityByName(entity);
        currentEntities.add(entity);
    }

    // -------------------------------
    // Entity by name
    // -------------------------------

    public List<Entity> getCurrentEntities() {
        return unmodifiableList(currentEntities);
    }

    private void putEntityByName(Entity entity) {
        Assert.isTrue(!hasEntityByName(entity.getName()), "An entity with the same name is already present!: " + entity.getName());
        currentEntitiesByName.put(entity.getName().toUpperCase(), entity);
    }

    public boolean hasEntityByName(String entityName) {
        return currentEntitiesByName.get(entityName.toUpperCase()) != null;
    }

    // -------------------------------
    // Entity by table name
    // -------------------------------

    public Entity getEntityByName(String entityName) {
        Entity entity = currentEntitiesByName.get(entityName.toUpperCase());
        Assert.notNull(entity, "You must be sure that an entity is present. Entity name=" + entityName);
        return entity;
    }

    /**
     * Store 1 entity per table name. In case of inheritance you must pass the root entity only.
     */
    public void putEntity(Entity entity) {
        Assert.isTrue(entity.hasTableName(), "Expecting a table name for the entity: " + entity.getName());
        Assert.isTrue(!hasEntityBySchemaAndTableName(entity.getTable().getSchemaName(), entity.getTable().getName()), "Entity was already added!: " + entity.getTableName());
        currentEntitiesByTableName.put(entity.getTable().getName().toUpperCase(), entity);
        currentEntitiesBySchemaAndTableName.put(entity.getTable().asKeyForMap(), entity);
    }

    public boolean hasEntityBySchemaAndTableName(String schemaName, String tableName) {
        return currentEntitiesBySchemaAndTableName.containsKey(Table.keyForMap(schemaName, tableName));
    }
    public boolean hasEntityByTableName(String tableName) {
        return currentEntitiesByTableName.containsKey(tableName.toUpperCase());
    }

    // -------------------------------
    // Account Convention
    // -------------------------------

    /**
     * Return the entity corresponding to the passed table. In case of inheritance, the root entity is returned.
     */
    public Entity getEntityBySchemaAndTableName(String schemaName, String tableName) {
        return currentEntitiesBySchemaAndTableName.get(Table.keyForMap(schemaName, tableName));
    }

    public boolean isAccountEntityPresent() {
        return any(currentEntities, ACCOUNT);
    }

    public Entity getAccountEntity() {
        return find(currentEntities, ACCOUNT);
    }

    public boolean isRoleEntityPresent() {
        return any(currentEntities, ROLE);
    }

    public Entity getRoleEntity() {
        return find(currentEntities, ROLE);
    }

    public boolean isAuditLogPresent() {
        return any(currentEntities, AUDIT_LOG);
    }

    public Entity getAuditLog() {
        return find(currentEntities, AUDIT_LOG);
    }

    public boolean isSavedSearchPresent() {
        return any(currentEntities, SAVED_SEARCH);
    }

    public Entity getSavedSearch() {
        return find(currentEntities, SAVED_SEARCH);
    }

    public String getPackagesToScan() {
        Set<String> packages = newHashSet();
        for (Entity entity : entities.getList()) {
            packages.add(entity.getEntityConfig().getRootPackage());
        }
        for (ClassType classType : ClassType.values()) {
            packages.add(classType.getRootPackage());
        }
        for (GeneratedPackage generatedPackage : GeneratedPackage.values()) {
            packages.add(generatedPackage.getRootPackage());
        }
        return Joiner.on(',').skipNulls().join(packages);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    // TODO: we should have a central place for enum types
    public Set<EnumType> getEnumTypes() {
        Set<EnumType> ret = Sets.newTreeSet();
        for (Entity entity : getEntities().getList()) {
            for (Attribute attribute : entity.getAttributes().getList()) {
                if (attribute.isEnum()) {
                    ret.add(attribute.getEnumType());
                }
            }
        }
        return ret;
    }

    public boolean isDefaultSchema() {
        if (!hasTableNames("ADDRESS", "ACCOUNT", "ROLE", "ACCOUNT_ROLE", "DOCUMENT", "BOOK", "MORE_TYPES_DEMO", "LEGACY")) {
            return false;
        }
        if (!hasHomeAddress()) {
            return false;
        }
        return true;
    }

    private boolean hasHomeAddress() {
        Entity account = getEntityByName("Account");
        for (Relation r : account.getXToOne().getList()) {
            if (r.getTo().getVar().equals("homeAddress")) {
                return true;
            }
        }
        return false;
    }

    private boolean hasTableNames(String... tableNames) {
        for (String tableName : tableNames) {
            if (!hasEntityByTableName(tableName)) {
                return false;
            }
        }
        return true;
    }

    public boolean getHibernateSearchUsed() {
        return any(currentEntities, EntityPredicates.IS_INDEXED);
    }

    /**
     * Helper to print comma, semi colon, double slash etc. inside velocity foreach loop.<br>
     * Example: <code>${enumValue.name}("$enumValue.value")$project.print($velocityHasNext, ", //", ";")</code>
     *
     * @param velocityHasNext special velocity var available inside foreach loop.
     * @param whenTrue        the string to return when velocityHasNext is true
     * @param whenFalse       the string to to return when velocityHasNext is false
     */
    public String print(boolean velocityHasNext, String whenTrue, String whenFalse) {
        return velocityHasNext ? whenTrue : whenFalse;
    }

    // ------------------------------------
    // SPI are put in a Map so we can access
    // from velocity templates as if we had getter.
    // ------------------------------------

    /**
     * Helper to print comma, semi colon, double slash etc. inside velocity foreach loop.<br>
     * Example: <code>${enumValue.name}("$enumValue.value")$project.print($velocityHasNext, ", //")</code>
     *
     * @param velocityHasNext special velocity var available inside foreach loop.
     * @param whenTrue        the string to return when velocityHasNext is true
     * @return whenTrue the string to return when velocityHasNext is true, an empty String otherwise.
     */
    public String print(boolean velocityHasNext, String whenTrue) {
        return velocityHasNext ? whenTrue : "";
    }

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
