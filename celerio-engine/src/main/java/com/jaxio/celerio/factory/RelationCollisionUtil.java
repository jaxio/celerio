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

import com.jaxio.celerio.configuration.entity.ColumnConfig;
import com.jaxio.celerio.configuration.entity.ManyToManyConfig;
import com.jaxio.celerio.configuration.entity.OneToManyConfig;
import com.jaxio.celerio.model.Attribute;
import com.jaxio.celerio.support.AccessorNamer;
import com.jaxio.celerio.support.Namer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static org.apache.commons.lang.StringUtils.isNotBlank;

@Service
@Slf4j
public class RelationCollisionUtil {

    private Set<String> fullVars = newHashSet();

    public void addVar(String entityType, String attributeVar) {
        fullVars.add(clashKey(entityType, attributeVar));
    }

    /**
     * During construction, we may free a var.
     */
    public void removeVar(String entityType, String attributeVar) {
        fullVars.remove(clashKey(entityType, attributeVar));
    }

    /**
     * Compute the appropriate namer for the Java field marked by @ManyToOne
     *
     * @param fromAttribute     the column that is the foreign key.
     * @param targetEntityNamer the default namer for the entity on the other side of the relation.
     */
    public Namer getManyToOneNamer(Attribute fromAttribute, Namer targetEntityNamer) {
        // configuration
        Namer result = getManyToOneNamerFromConf(fromAttribute.getColumnConfig(), targetEntityNamer);

        // fall back
        if (result == null) {
            result = getXToOneNamerFallBack(fromAttribute, targetEntityNamer);
        }

        return result;
    }

    /**
     * Compute the appropriate namer for the Java field marked by @OneToMany
     *
     * @param fromAttributeColumnConfig the column that is the foreign key
     * @param fromEntityNamer           the default namer for the entity contained in the collection.
     */
    public Namer getOneToManyNamer(ColumnConfig fromAttributeColumnConfig, Namer fromEntityNamer) {
        // configuration
        Namer result = getOneToManyNamerFromConf(fromAttributeColumnConfig.getOneToManyConfig(), fromEntityNamer);

        // convention
        if (result == null) {
            result = fromEntityNamer;
        }

        return result;
    }

    /**
     * Compute the appropriate namer for the Java field marked by @ManyToMany
     */
    public Namer getManyToManyNamer(Namer fromEntityNamer, ColumnConfig pointsOnToEntity, Namer toEntityNamer) {

        // new configuration
        Namer result = getManyToManyNamerFromConf(pointsOnToEntity.getManyToManyConfig(), toEntityNamer);

        // fallback
        if (result == null) {
            result = getManyToManyNamerFallBack(fromEntityNamer, pointsOnToEntity, toEntityNamer);
        }

        return result;
    }

    /**
     * Compute the appropriate namer for the Java field @ManyToOne (with intermediate table)
     */
    public Namer getManyToOneIntermediateNamer(Namer fromEntityNamer, ColumnConfig pointsOnToEntity, Namer toEntityNamer) {

        // new configuration
        Namer result = getManyToOneNamerFromConf(pointsOnToEntity, toEntityNamer);

        // fallback
        if (result == null) {
            // NOTE: we use the many to many fallback as it is totally complient with intermediate m2o fallback expectations.
            result = getManyToManyNamerFallBack(fromEntityNamer, pointsOnToEntity, toEntityNamer);
        }

        return result;
    }

    /**
     * Compute the appropriate namer for the Java field @ManyToOne (with intermediate table)
     */
    public Namer getOneToManyIntermediateNamer(Namer fromEntityNamer, ColumnConfig pointsOnToEntity, Namer toEntityNamer) {
        // new configuration
        Namer result = getOneToManyNamerFromConf(pointsOnToEntity.getOneToManyConfig(), toEntityNamer);

        // fallback
        if (result == null) {
            // NOTE: we use the many to many fallback as it is totally complient with intermediate m2o fallback expectations.
            result = getManyToManyNamerFallBack(fromEntityNamer, pointsOnToEntity, toEntityNamer);
        }

        return result;
    }

    /**
     * Compute the namer for the Java field marked by @OneToOne
     *
     * @param fromAttribute     the fk column config
     * @param targetEntityNamer
     */
    public Namer getOneToOneNamer(Attribute fromAttribute, Namer targetEntityNamer) {
        // configuration
        Namer result = getOneToOneNamerFromConf(fromAttribute.getColumnConfig(), targetEntityNamer);

        // fall back
        if (result == null) {
            result = getXToOneNamerFallBack(fromAttribute, targetEntityNamer);
        }

        return result;
    }

    /**
     * Compute the namer for the Java field marked by @OneToOne (inverse side of the relation)
     *
     * @param fromAttribute   the fk column config
     * @param fromEntityNamer
     */
    public Namer getInverseOneToOneNamer(Attribute fromAttribute, Namer fromEntityNamer) {
        Namer result = getInverseOneToOneNamerFromConf(fromAttribute.getColumnConfig(), fromEntityNamer);

        if (result == null) {
            result = fromEntityNamer;
        }

        return result;
    }

    // --------------------------------------------------------------
    // Helper methods splited and protected for unit tests purposes
    // --------------------------------------------------------------

    protected Namer getManyToOneNamerFromConf(ColumnConfig fromAttributeColumnConfig, Namer targetEntityNamer) {
        if (fromAttributeColumnConfig.getManyToOneConfig() != null && fromAttributeColumnConfig.getManyToOneConfig().hasVar()) {
            return new AccessorNamer(targetEntityNamer, fromAttributeColumnConfig.getManyToOneConfig().getVar());
        }

        return null;
    }

    private Namer getXToOneNamerFallBack(Attribute fromAttribute, Namer targetEntityNamer) {
        return getXToOneNamerByConvention(fromAttribute, fromAttribute.getEntity().getName(), fromAttribute.getVar(), targetEntityNamer);
    }

    protected Namer getXToOneNamerByConvention(Attribute fromAttribute, String fromEntityName, String fromAttributeVar, Namer targetEntityNamer) {
        String var = null;
        if (fromAttributeVar.toUpperCase().endsWith("ID")) {
            if (fromAttributeVar.length() > 2) {
                // attribute is "somethingId" ==> x to one is "something"
                var = fromAttributeVar.substring(0, fromAttributeVar.length() - 2);
            }
        } else {
            var = fromAttributeVar; // + "Ref";
        }

        if (var == null) {
            var = targetEntityNamer.getVar();
        }

        // with convention we do our best to avoid clash.
        return new AccessorNamer(targetEntityNamer, getClashSafeVar(fromEntityName, var));
    }

    protected Namer getOneToManyNamerFromConf(OneToManyConfig o2m, Namer fromEntityNamer) {
        if (o2m != null) {

            if (o2m.hasElementVar() && !o2m.hasVar()) {
                // the plural is calculated
                return new AccessorNamer(fromEntityNamer, o2m.getElementVar());
            }

            if (o2m.hasElementVar() && o2m.hasVar()) {
                return new AccessorNamer(fromEntityNamer, o2m.getElementVar(), o2m.getVar());
            }

            if (!o2m.hasElementVar() && o2m.hasVar()) {
                return new AccessorNamer(fromEntityNamer, fromEntityNamer.getVar(), o2m.getVar());
            }
        }

        return null;
    }

    protected Namer getManyToManyNamerFromConf(ManyToManyConfig m2m, Namer toEntityNamer) {
        if (m2m != null) {

            if (m2m.hasElementVar() && !m2m.hasVar()) {
                // the plural is calculated
                return new AccessorNamer(toEntityNamer, m2m.getElementVar());
            }

            if (m2m.hasElementVar() && m2m.hasVar()) {
                return new AccessorNamer(toEntityNamer, m2m.getElementVar(), m2m.getVar());
            }

            if (!m2m.hasElementVar() && m2m.hasVar()) {
                return new AccessorNamer(toEntityNamer, toEntityNamer.getVar(), m2m.getVar());
            }
        }

        return null;
    }

    protected Namer getOneToOneNamerFromConf(ColumnConfig fromAttributeColumnConfig, Namer targetEntityNamer) {
        if (fromAttributeColumnConfig.getOneToOneConfig() != null && fromAttributeColumnConfig.getOneToOneConfig().hasVar()) {
            return new AccessorNamer(targetEntityNamer, fromAttributeColumnConfig.getOneToOneConfig().getVar());
        }

        return null;
    }

    protected Namer getInverseOneToOneNamerFromConf(ColumnConfig fromAttributeColumnConfig, Namer fromEntityNamer) {
        if (fromAttributeColumnConfig.getInverseOneToOneConfig() != null && fromAttributeColumnConfig.getInverseOneToOneConfig().hasVar()) {
            return new AccessorNamer(fromEntityNamer, fromAttributeColumnConfig.getInverseOneToOneConfig().getVar());
        }

        return null;
    }

    private Namer getManyToManyNamerFallBack(Namer fromEntityNamer, ColumnConfig cc, Namer toEntityNamer) {
        // convention
        return getManyToManyNamerByConvention(fromEntityNamer, cc, toEntityNamer);
    }

    protected Namer getOneToXNamerByConvention(ColumnConfig fromAttributeColumnConfig, Namer fromEntityNamer) {
        if (isNotBlank(fromAttributeColumnConfig.getFieldName())) {
            return new AccessorNamer(fromEntityNamer, fromAttributeColumnConfig.getFieldName());
        }
        return fromEntityNamer;
    }

    protected Namer getManyToManyNamerByConvention(Namer fromEntityNamer, ColumnConfig cc, Namer toEntityNamer) {
        String javaField = toEntityNamer.getVar();
        return new AccessorNamer(toEntityNamer, getClashSafeVar(fromEntityNamer.getType(), javaField));
    }

    public String getClashSafeVar(String entityName, String javaField) {
        if (alreadyUsed(entityName, javaField)) {
            // already used...
            int i = 2;
            while (true) {
                String candidate = javaField + (i++);
                if (!alreadyUsed(entityName, candidate)) {
                    log.info("Var name clash resolution for " + entityName + ": " + javaField + " => " + candidate);
                    javaField = candidate;
                    break;
                }
            }
        }
        addVar(entityName, javaField);
        return javaField;
    }

    private boolean alreadyUsed(String entityName, String javaField) {
        return fullVars.contains(clashKey(entityName, javaField));
    }

    private String clashKey(String entityName, String javaField) {
        return (entityName + "." + javaField).toUpperCase();
    }
}
