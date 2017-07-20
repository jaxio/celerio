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

package com.jaxio.celerio.factory;

import com.jaxio.celerio.Config;
import com.jaxio.celerio.configuration.Configuration;
import com.jaxio.celerio.configuration.database.ForeignKey;
import com.jaxio.celerio.configuration.database.ImportedKey;
import com.jaxio.celerio.configuration.database.Table;
import com.jaxio.celerio.configuration.entity.*;
import com.jaxio.celerio.model.Attribute;
import com.jaxio.celerio.model.Entity;
import com.jaxio.celerio.model.Relation;
import com.jaxio.celerio.model.relation.*;
import com.jaxio.celerio.support.Namer;
import com.jaxio.celerio.util.Labels;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.jaxio.celerio.util.MiscUtil.toReadableLabel;
import static com.jaxio.celerio.util.MiscUtil.toReadablePluralLabel;
import static com.jaxio.celerio.util.StringUtil.orderToString;
import static java.lang.Boolean.TRUE;

@Service
@Slf4j
public class RelationFactory {

    @Autowired
    private Config topLevelCfg;

    @Autowired
    private RelationCollisionUtil relationUtil;

    public void setupRelations(Entity entity) {
        if (isMiddleEntityCandidate(entity)) {
            setupNNRelations(entity);
        } else {
            setupOtherRelations(entity);
        }
    }

    public Configuration cfg() {
        return topLevelCfg.getCelerio().getConfiguration();
    }

    // -------------------------------------------
    // Private implementation
    // -------------------------------------------

    /**
     * Note: the passed entity may include fields that come from different tables: Not a problem, this method supports it.
     */
    private List<ForeignKey> getForeignKeys(Entity entity) {
        List<ForeignKey> result = newArrayList();

        // get all tables involved in this entity fk.
        List<Table> involvedTables = newArrayList(); // use list to preserve fk ordering.
        for (Attribute a : entity.getCurrentAttributes()) {
            if (a.isInFk()) {
                String fkTableName = a.getColumnConfig().getTableName();
                Assert.notNull(fkTableName);
                Table t = topLevelCfg.getMetadata().getTableByName(fkTableName);
                if (!involvedTables.contains(t)) {
                    involvedTables.add(t);
                }
            }
        }

        // get all the imported key of the involved tables
        for (Table table : involvedTables) {
            // keep only the fk that are really bound to the passed entity
            for (ForeignKey fk : table.getForeignKeys()) {
                if (relationExists(entity, fk)) {
                    result.add(fk);
                }
            }
        }

        return result;
    }

    private boolean isMiddleEntityCandidate(Entity entity) {
        if (entity.hasInheritance()) {
            return false;
            // TODO: inheritance + m2m ?
        }

        // by configuration
        if (entity.getEntityConfig().getMiddleTable() == Boolean.FALSE) {
            // user does not want any middle table here, no need to go further.
            return false;
        }

        List<ForeignKey> currentFks = getForeignKeys(entity);

        if (currentFks.size() == 2 /* required convention */
                && (entity.getEntityConfig().getMiddleTable() == Boolean.TRUE /* configuration */ || entity.getSimpleAttributes().getSize() == 0 /* convention */)) {

            for (ForeignKey fk : currentFks) {
                // we skip composite FK (to complex to handle)
                if (fk.isComposite()) {
                    return false;
                }

                // let's make sure fk do not point to the table they belong to
                if (fk.getImportedKey().getPkTableName().equalsIgnoreCase(entity.getTableName())) {
                    return false;
                }
            }

            log.info("Entity " + entity.getName() + " is a middle entity");
            return true;
        }
        return false;
    }

    private void setupOtherRelations(Entity entity) {
        for (ForeignKey fk : getForeignKeys(entity)) {
            if (!cfg().hasTable(fk.getImportedKey().getPkTableName())) {
                log.info("Skip association for entity " + entity.getName() + ". Reason: foreign key references an excluded table: " + fk.toString());
                continue;
            }

            if (fk.getSize() == 1) {
                setUpSimpleRelation(entity, fk);
            } else if (fk.getSize() > 1) {
                setUpCompositeRelation2(entity, fk);
            } else {
                throw new IllegalStateException();
            }
        }
    }

    private boolean relationExists(Entity entity, ForeignKey fk) {
        for (ImportedKey importedKey : fk.getImportedKeys()) {

            // make sure all attributes of the current entity are involved
            Attribute fromAttribute = entity.getAttributeByTableAndColumnName(fk.getFkTableName(), importedKey.getFkColumnName());
            if (fromAttribute == null) {
                return false;
            }

            Entity targetEntity = getTargetEntity(importedKey, fromAttribute.getColumnConfig());

            if (targetEntity == null) {
                log.warn("Could not find target entity pkTableName: " + importedKey.getPkTableName());
                return false;
            }

            Attribute targetAttribute = getTargetAttribute(importedKey, targetEntity);
            if (targetAttribute == null) {
                log.warn("Could not find target attribute pkColumnName: " + importedKey.getPkColumnName());
                return false;
            }
        }

        return true;
    }

    // ---------------------------------------------------
    // NN RELATIONS - AKA RELATION WITH INTERMEDIATE TABLE
    // ---------------------------------------------------

    private void setupNNRelations(Entity entity) {
        Iterator<ForeignKey> i = getForeignKeys(entity).iterator();

        //
        // Our terminology and convention are as follow:
        //
        // INTERMEDIATE TABLE
        // Left Table <--------[left col config/fk left | right col configu/fk right]--------> Right table
        //
        // The forward relation (ie a collection of right entities) is on the left entity.
        // However, from a configuration standpoint, to make it easier to read, the xxxConfig used to configure the forward relation is a child
        // of the right column config. The xxxConfig to configure the inverse relation is a child of the left col config.
        //

        ForeignKey fkLeft = i.next();
        ForeignKey fkRight = i.next();

        // Let's prevent association to table that are filtered out.
        if (!cfg().hasTable(fkLeft.getImportedKey().getPkTableName())) {
            log.info("Skip simple NN association carried by entity " + entity.getName() + ". Reason: foreign key references an excluded table: "
                    + fkLeft.toString());
            return;
        }

        if (!cfg().hasTable(fkRight.getImportedKey().getPkTableName())) {
            log.info("Skip simple NN association carried by entity " + entity.getName() + ". Reason: foreign key references an excluded table: "
                    + fkRight.toString());
            return;
        }

        if (log.isInfoEnabled()) {
            log.info("middle table: " + entity.getTableName() + " " + getForeignKeys(entity).size() + " " + entity.getSimpleAttributes().getSize());
        }

        // first: look at natural order, the higher ordinal position is the inverse side by convention.
        Attribute attrLeft = entity.getAttributeByTableAndColumnName(entity.getTableName(), fkLeft.getImportedKey().getFkColumnName());
        Attribute attrRight = entity.getAttributeByTableAndColumnName(entity.getTableName(), fkRight.getImportedKey().getFkColumnName());

        if (attrLeft.getColumnConfig().getOrdinalPosition() > attrRight.getColumnConfig().getOrdinalPosition()) {
            ForeignKey fkTemp = fkLeft;
            fkLeft = fkRight;
            fkRight = fkTemp;

            attrLeft = attrRight;
            attrRight = null; // so we do not use it by mistake.
        }

        // Now that we have the real left side by convention, let's check if the user want to change the convention.
        if (attrLeft.getColumnConfig().getInverse() == TRUE) {
            ForeignKey fkTemp = fkLeft;
            fkLeft = fkRight;
            fkRight = fkTemp;
        }

        // TODO: support composite in many to many
        if (fkLeft.getSize() == 1 && fkRight.getSize() == 1) {

            // build intermediate relations to ease our pain (!)
            Relation middleToLeft = buildSimpleRelation(entity, fkLeft);
            Relation middleToRight = buildSimpleRelation(entity, fkRight);

            if (middleToRight.getFromAttribute().getColumnConfig().getManyToOneConfig() == null) {
                // many to many + inverse many to many
                buildIntermediateManyToManyManyToMany(middleToLeft, entity, middleToRight);
            } else {
                // MANY TO ONE with intermediate table
                buildIntermediateManyToOneOneToMany(middleToLeft, entity, middleToRight);
            }
            // TODO: intermediate one to one ...

            // mark this middle join entity
            entity.setManyToManyJoinEntity(true);
        } else {
            log.warn("NN relations with composite FK is not supported. Skipping " + entity.getTableName());
        }
    }

    // -------------------------------------------------
    // intermediate MANY TO ONE + INVERSE MANY TO MANY
    // -------------------------------------------------

    void buildIntermediateManyToManyManyToMany(Relation middleToLeft, Entity middleEntity, Relation middleToRight) {
        // MANY TO MANY
        // Note: if the manyToManyConfig is null, by convention, we create the m2m relation

        Namer toNamer = relationUtil.getManyToManyNamer(middleToLeft.getTo(), middleToRight.getFromAttribute().getColumnConfig(), middleToRight.getTo());
        Namer fromNamer = relationUtil.getManyToManyNamer(middleToRight.getTo(), middleToLeft.getFromAttribute().getColumnConfig(), middleToLeft.getTo());

        ManyToMany m2m = new ManyToMany(fromNamer, toNamer, middleEntity, middleToLeft, middleToRight);
        ManyToManyConfig m2mConfig = middleToRight.getFromAttribute().getColumnConfig().getManyToManyConfig();
        m2m.setCascadeGetter(m2mConfig);
        m2m.setFetchTypeGetter(m2mConfig);
        m2m.setOrderByGetter(m2mConfig);

        setLabelPlural(m2mConfig, m2m);
        initDisplayOrderAsString(m2mConfig, m2m);
        configureActions(m2m, cfg().getDefaultManyToManyConfig(), m2mConfig);
        middleToLeft.getToEntity().addRelation(m2m);

        if (shouldCreateInverse(m2m)) {
            // MANY TO MANY inverse
            ManyToMany inverseM2m = (ManyToMany) m2m.createInverse(); // TODO: fix cast ?
            ManyToManyConfig inverseM2mConfig = middleToLeft.getFromAttribute().getColumnConfig().getManyToManyConfig();
            inverseM2m.setCascadeGetter(inverseM2mConfig);
            inverseM2m.setFetchTypeGetter(inverseM2mConfig);
            inverseM2m.setOrderByGetter(inverseM2mConfig);
            setLabelPlural(inverseM2mConfig, inverseM2m);
            initDisplayOrderAsString(inverseM2mConfig, inverseM2m);
            configureActions(inverseM2m, cfg().getDefaultInverseManyToManyConfig(), inverseM2mConfig);
            middleToRight.getToEntity().addRelation(inverseM2m);
        }
    }

    // ----------------------------------------
    // intermediate MANY TO ONE + ONE TO MANY
    // ----------------------------------------

    void buildIntermediateManyToOneOneToMany(Relation middleToLeft, Entity middleEntity, Relation middleToRight) {
        Namer toNamer = relationUtil.getManyToOneIntermediateNamer(middleToLeft.getTo(), middleToRight.getFromAttribute().getColumnConfig(),
                middleToRight.getTo());
        Namer fromNamer = relationUtil.getOneToManyIntermediateNamer(middleToRight.getTo(), middleToLeft.getFromAttribute().getColumnConfig(),
                middleToLeft.getTo());

        IntermediateManyToOne m2o = new IntermediateManyToOne(fromNamer, toNamer, middleEntity, middleToLeft, middleToRight);
        ManyToOneConfig m2oConfig = middleToRight.getFromAttribute().getColumnConfig().getManyToOneConfig();

        m2o.setCascadeGetter(m2oConfig);
        m2o.setFetchTypeGetter(m2oConfig);
        setLabelSingular(m2oConfig, m2o);
        configureActions(m2o, cfg().getDefaultManyToOneConfig(), m2oConfig);

        middleToLeft.getToEntity().addRelation(m2o);

        if (shouldCreateInverse(m2o)) {
            // ONE TO MANY with intermediate table
            IntermediateOneToMany o2m = (IntermediateOneToMany) m2o.createInverse(); // TODO: fix cast ?
            OneToManyConfig o2mConfig = middleToLeft.getFromAttribute().getColumnConfig().getOneToManyConfig();

            o2m.setCascadeGetter(o2mConfig);
            o2m.setFetchTypeGetter(o2mConfig);
            o2m.setOrderByGetter(o2mConfig);

            setLabelPlural(o2mConfig, o2m);
            initDisplayOrderAsString(o2mConfig, o2m);

            configureActions(o2m, cfg().getDefaultOneToManyConfig(), o2mConfig);
            middleToRight.getToEntity().addRelation(o2m);
        }
    }

    // ---------------------------------------------------
    // SIMPLE RELATIONS - no nn, no composite fk
    // ---------------------------------------------------

    private void setUpSimpleRelation(Entity entity, ForeignKey fk) {
        Relation forwardRelation = buildSimpleRelation(entity, fk);
        entity.addRelation(forwardRelation);

        if (shouldCreateInverse(forwardRelation)) {
            Relation reverseRelation = forwardRelation.createInverse();
            forwardRelation.getToEntity().addRelation(reverseRelation);
        }
    }

    /**
     * For non intermediate relation, inverse association should be created if either the associationDirection attribute of the columnConfig is set to BIDIRECTIONAL or if the expected inverse
     * association config is found. For example, if the forward relation is a many to one and if oneToManyConfig is found, then we create the inverse
     * association.
     * <br> <br>
     * For intermediate relation, inverse association should be created if either the associationDirection attribute of the parent entityConfig is set to BIDIRECTIONAL or if the expected
     * inverse association config is found on the 'left-side' columnConfig. For example, if the forward relation is a many to one and if oneToManyConfig is
     * found, then we create the inverse association.
     */
    private boolean shouldCreateInverse(Relation forward) {
        if (forward.isIntermediate()) {
            AssociationDirection direction = forward.getMiddleEntity().getEntityConfig().getAssociationDirection();
            ColumnConfig leftCc = forward.getMiddleToLeft().getFromAttribute().getColumnConfig();

            return AssociationDirection.BIDIRECTIONAL == direction //
                    || (forward.isManyToMany() && leftCc.getManyToManyConfig() != null) //
                    || (forward.isManyToOne() && leftCc.getOneToManyConfig() != null) //
                    || (forward.isOneToOne() && leftCc.getInverseOneToOneConfig() != null);
        } else {
            ColumnConfig cc = forward.getFromAttribute().getColumnConfig();
            return AssociationDirection.BIDIRECTIONAL == cc.getAssociationDirection() //
                    || (forward.isManyToOne() && cc.getOneToManyConfig() != null) //
                    || (forward.isOneToOne() && cc.getInverseOneToOneConfig() != null);
        }
    }

    private SimpleRelation buildSimpleRelation(Entity entity, ForeignKey fk) {
        ImportedKey importedKey = fk.getImportedKeys().iterator().next();
        return buildSimpleRelation(entity, importedKey, fk);
    }

    private SimpleRelation buildSimpleRelation(Entity entity, ImportedKey importedKey, ForeignKey fk) {
        final Attribute fromAttribute = entity.getAttributeByTableAndColumnName(fk.getFkTableName(), importedKey.getFkColumnName());
        Assert.isTrue(fromAttribute.getEntity().equals(entity));
        Entity targetEntity = getTargetEntity(importedKey, fromAttribute.getColumnConfig());
        Attribute targetAttribute = getTargetAttribute(importedKey, targetEntity);

        if (fromAttribute.isUnique() && fromAttribute.getColumnConfig().getEnableOneToVirtualOne()) {
            // many to one + one to virtual one
            return buildSimpleManyToOneOneToVirtualOne(entity, fromAttribute, targetEntity, targetAttribute);
        } else if (fromAttribute.isUnique() || fromAttribute.getColumnConfig().getOneToOneConfig() != null) {
            // one to one + inverse one to one
            return buildSimpleOneToOneOneToOne(entity, fromAttribute, targetEntity, targetAttribute);
        } else {
            // many to one + one to many
            return buildSimpleManyToOneOneToMany(entity, fromAttribute, targetEntity, targetAttribute);
        }
    }

    // ----------------------------------------
    // simple MANY TO ONE + ONE TO VIRTUAL ONE
    // ----------------------------------------

    private SimpleRelation buildSimpleManyToOneOneToVirtualOne(Entity entity, final Attribute fromAttribute, Entity targetEntity, Attribute targetAttribute) {
        Namer targetNamer = relationUtil.getManyToOneNamer(fromAttribute, targetEntity.getModel());
        Namer inverseNamer = relationUtil.getOneToManyNamer(fromAttribute.getColumnConfig(), entity.getModel());

        SimpleRelation m2o = new SimpleManyToOne(inverseNamer, targetNamer, fromAttribute, entity, targetEntity, targetAttribute) {
            @Override
            protected AbstractRelation buildInverse() {
                AbstractRelation o2vo = new SimpleOneToVirtualOne(getTo(), getFrom(), getToAttribute(), getToEntity(), getFromEntity(), getFromAttribute());
                OneToManyConfig o2voConfig = fromAttribute.getColumnConfig().getOneToManyConfig();
                setLabelSingular(o2voConfig, o2vo);
                configureActions(o2vo, cfg().getDefaultOneToManyConfig(), o2voConfig);
                return o2vo;
            }
        };

        ManyToOneConfig m2oConfig = fromAttribute.getColumnConfig().getManyToOneConfig();
        setLabelSingular(m2oConfig, m2o);
        configureActions(m2o, cfg().getDefaultManyToOneConfig(), m2oConfig);
        return m2o;
    }

    // ----------------------------------------
    // simple ONE TO ONE + INVERSE ONE TO ONE
    // ----------------------------------------

    SimpleRelation buildSimpleOneToOneOneToOne(Entity entity, final Attribute fromAttribute, Entity targetEntity, Attribute targetAttribute) {
        Namer targetNamer = relationUtil.getOneToOneNamer(fromAttribute, targetEntity.getModel());
        Namer inverseNamer = relationUtil.getInverseOneToOneNamer(fromAttribute, entity.getModel());
        SimpleRelation o2o = new SimpleOneToOne(inverseNamer, targetNamer, fromAttribute, entity, targetEntity, targetAttribute) {

            @Override
            protected AbstractRelation buildInverse() {
                SimpleOneToOne invo2o = new SimpleOneToOne(getTo(), getFrom(), getToAttribute(), getToEntity(), getFromEntity(), fromAttribute);
                OneToOneConfig iso2oConfig = getFromAttribute().getColumnConfig().getInverseOneToOneConfig();
                setLabelSingular(iso2oConfig, invo2o);
                configureActions(invo2o, cfg().getDefaultInverseOneToOneConfig(), iso2oConfig);
                return invo2o;
            }
        };

        OneToOneConfig o2oConfig = fromAttribute.getColumnConfig().getOneToOneConfig();
        setLabelSingular(o2oConfig, o2o);

        configureActions(o2o, cfg().getDefaultOneToOneConfig(), o2oConfig);
        return o2o;
    }

    // ----------------------------------------
    // simple MANY TO ONE + ONE TO MANY
    // ----------------------------------------

    private SimpleRelation buildSimpleManyToOneOneToMany(Entity entity, final Attribute fromAttribute, Entity targetEntity, Attribute targetAttribute) {
        Namer targetNamer = relationUtil.getManyToOneNamer(fromAttribute, targetEntity.getModel());
        Namer inverseNamer = relationUtil.getOneToManyNamer(fromAttribute.getColumnConfig(), entity.getModel());

        SimpleRelation m2o = new SimpleManyToOne(inverseNamer, targetNamer, fromAttribute, entity, targetEntity, targetAttribute) {
            @Override
            protected AbstractRelation buildInverse() {
                AbstractRelation o2m = new SimpleOneToMany(getTo(), getFrom(), getToAttribute(), getToEntity(), getFromEntity(), getFromAttribute());
                OneToManyConfig o2mConfig = fromAttribute.getColumnConfig().getOneToManyConfig();
                setLabelPlural(o2mConfig, o2m);
                initDisplayOrderAsString(o2mConfig, o2m);
                configureActions(o2m, cfg().getDefaultOneToManyConfig(), o2mConfig);
                return o2m;
            }
        };

        ManyToOneConfig m2oConfig = fromAttribute.getColumnConfig().getManyToOneConfig();
        setLabelSingular(m2oConfig, m2o);
        configureActions(m2o, cfg().getDefaultManyToOneConfig(), m2oConfig);
        return m2o;
    }

    // ---------------------------------------------------
    // RELATIONS WITH COMPOSITE FK/PK
    // ---------------------------------------------------

    private void setUpCompositeRelation2(Entity entity, ForeignKey fk) {
        Relation forwardRelation = buildCompositeRelation2(entity, fk);
        entity.addRelation(forwardRelation);
        if (shouldCreateInverse(forwardRelation)) {
            Relation reverseRelation = forwardRelation.createInverse();
            forwardRelation.getToEntity().addRelation(reverseRelation);
        }
    }

    private CompositeRelation buildCompositeRelation2(Entity entity, ForeignKey fk) {
        final List<Attribute> fromAttributes = new ArrayList<Attribute>();
        for (ImportedKey importedKey : fk.getImportedKeys()) {
            Attribute fromAttribute = entity.getAttributeByTableAndColumnName(fk.getFkTableName(), importedKey.getFkColumnName());
            Assert.isTrue(fromAttribute.getEntity().equals(entity));
            fromAttributes.add(fromAttribute);
        }

        // composite many to one + one to many
        return buildCompositeManyToOneOneToMany(entity, fromAttributes, fk);

        // TODO: composite ONE TO ONE ?
    }

    // ---------------------------------------------------
    // composite MANY TO ONE + ONE TO MANY
    // ---------------------------------------------------

    CompositeManyToOne buildCompositeManyToOneOneToMany(Entity entity, final List<Attribute> fromAttributes, ForeignKey fk) {
        Entity targetEntity = getTargetEntity(fk.getImportedKey(), fromAttributes.get(0).getColumnConfig());
        List<Attribute> targetAttributes = getTargetAttributes(fk, targetEntity);

        // composite MANY TO ONE + ONE TO MANY
        Namer targetNamer = relationUtil.getManyToOneNamer(fromAttributes.get(0), targetEntity.getModel());
        Namer inverseNamer = relationUtil.getOneToManyNamer(fromAttributes.get(0).getColumnConfig(), entity.getModel());

        CompositeManyToOne m2o = new CompositeManyToOne(inverseNamer, targetNamer, fromAttributes, entity, targetEntity, targetAttributes) {
            @Override
            protected AbstractRelation buildInverse() {
                CompositeOneToMany co2m2 = new CompositeOneToMany(getTo(), getFrom(), getToAttributes(), getToEntity(), getFromEntity(), getFromAttributes());
                OneToManyConfig o2mConfig = fromAttributes.get(0).getColumnConfig().getOneToManyConfig();
                setLabelPlural(o2mConfig, co2m2);
                initDisplayOrderAsString(o2mConfig, co2m2);
                configureActions(co2m2, cfg().getDefaultOneToManyConfig(), o2mConfig);
                return co2m2;
            }
        };

        ManyToOneConfig m2oConfig = fromAttributes.get(0).getColumnConfig().getManyToOneConfig();
        setLabelSingular(m2oConfig, m2o);
        configureActions(m2o, cfg().getDefaultManyToOneConfig(), m2oConfig);
        return m2o;
    }

    // ---------------------------------------------------
    // UTILS STUFF
    // ---------------------------------------------------

    private Entity getTargetEntity(ImportedKey importedKey, ColumnConfig cc) {
        if (cc.hasTargetEntityName()) {
            return topLevelCfg.getProject().getEntityByName(cc.lookupTargetEntityName());
        } else {
            return topLevelCfg.getProject().getEntityBySchemaAndTableName(importedKey.getPkTableSchema(), importedKey.getPkTableName());
        }
    }

    private Attribute getTargetAttribute(ImportedKey importedKey, Entity targetEntity) {
        // take our chance to get it the first time.
        Attribute targetAttribute = targetEntity.getAttributeByTableAndColumnName(importedKey.getPkTableName(), importedKey.getPkColumnName());

        if (targetAttribute == null) {
            if (targetEntity.isRoot()) {
                if (targetEntity.getPrimaryKey().isSimple()) {
                    // fallback to pk attribute
                    targetAttribute = targetEntity.getPrimaryKey().getAttribute();
                } else {
                    // TODO:
                    log.warn("TODO: support composite PK");
                }
            } else {
                // move up the hierarchy and look after the target attribute which could be a non pk!
                for (Entity e = targetEntity.getParent(); targetAttribute == null && e != null; e = e.getParent()) {
                    targetAttribute = e.getAttributeByTableAndColumnName(importedKey.getPkTableName(), importedKey.getPkColumnName());
                }

                if (targetAttribute == null) {
                    if (targetEntity.getRoot().getPrimaryKey().isSimple()) {
                        // fallback to pk attribute
                        targetAttribute = targetEntity.getRoot().getPrimaryKey().getAttribute();
                    } else {
                        // TODO:
                        log.warn("TODO: support composite PK");
                    }
                }
            }
        }

        return targetAttribute;
    }

    private List<Attribute> getTargetAttributes(ForeignKey fk, Entity targetEntity) {
        List<Attribute> ta = new ArrayList<Attribute>();

        for (ImportedKey ik : fk.getImportedKeys()) {
            ta.add(getTargetAttribute(ik, targetEntity));
        }

        return ta;
    }

    // ---------------------------------------------------
    // ACTIONS
    // ---------------------------------------------------

    /**
     * @param relation
     * @param associationActionGetters should be ordered from the broadest granularity to the finest granularity
     */
    private void configureActions(Relation relation, AssociationActionGetter... associationActionGetters) {
        if (associationActionGetters == null) {
            return;
        }

        for (AssociationActionGetter associationActionGetter : associationActionGetters) {
            if (associationActionGetter != null) {
                AssociationAction associationAction = associationActionGetter.getAssociationAction();
                if (associationAction != null) {
                    if (associationAction.getCreate() != null) {
                        relation.setGenCreate(associationAction.getCreate());
                    }
                    if (associationAction.getEdit() != null) {
                        relation.setGenEdit(associationAction.getEdit());
                    }
                    if (associationAction.getView() != null) {
                        relation.setGenView(associationAction.getView());
                    }
                    if (associationAction.getSelect() != null) {
                        relation.setGenSelect(associationAction.getSelect());
                    }
                    if (associationAction.getAutoComplete() != null) {
                        relation.setGenAutoComplete(associationAction.getAutoComplete());
                    }
                    if (associationAction.getRemove() != null) {
                        relation.setGenRemove(associationAction.getRemove());
                    }
                }
            }
        }
    }

    // ---------------------------------------------------
    // LABELS
    // ---------------------------------------------------

    private void setLabelSingular(LabelGetter lg, AbstractRelation ar) {
        Labels labels = lg != null ? new Labels(lg.getLabels()) : new Labels();
        labels.setFallBack(toReadableLabel(ar.getTo().getVar()));
        ar.setLabels(labels);
    }

    private void setLabelPlural(LabelGetter lg, AbstractRelation ar) {
        Labels labels = lg != null ? new Labels(lg.getLabels()) : new Labels();
        labels.setFallBack(toReadablePluralLabel(ar.getTo().getVars()));
        ar.setLabels(labels);
    }

    private void initDisplayOrderAsString(OneToManyConfig o2mConfig, AbstractRelation ar) {
        if (o2mConfig != null) {
            ar.setDisplayOrderAsString(orderToString(o2mConfig.getDisplayOrder(), o2mConfig.getVar()));
        }
    }
    private void initDisplayOrderAsString(ManyToManyConfig m2mConfig, AbstractRelation ar) {
        if (m2mConfig != null) {
            ar.setDisplayOrderAsString(orderToString(m2mConfig.getDisplayOrder(), m2mConfig.getVar()));
        }
    }
}