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

package com.jaxio.celerio.model.support.jpa;

import com.jaxio.celerio.configuration.entity.*;
import com.jaxio.celerio.model.PackageImportAdder;
import com.jaxio.celerio.util.AttributeBuilder;
import org.springframework.util.Assert;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class JpaConfigHelper {

    // ------------------------------------------
    // OneToXConfig orphanRemoval
    // ------------------------------------------

    public static String orphanRemoval(OneToManyConfig... oneToManyConfigs) {
        return hasTrueOrphanRemoval(oneToManyConfigs) ? "orphanRemoval = true" : "";
    }

    public static boolean hasTrueOrphanRemoval(OneToManyConfig... oneToManyConfigs) {
        if (oneToManyConfigs == null) {
            return false;
        }

        for (OneToManyConfig oneToManyConfig : oneToManyConfigs) {
            if (oneToManyConfig != null && oneToManyConfig.getOrphanRemoval() != null) {
                return oneToManyConfig.getOrphanRemoval().booleanValue();
            }
        }

        return false; // false is the jpa default
    }

    public static String orphanRemoval(OneToOneConfig... oneToOneConfigs) {
        return hasTrueOrphanRemoval(oneToOneConfigs) ? "orphanRemoval = true" : "";
    }

    public static boolean hasTrueOrphanRemoval(OneToOneConfig... oneToOneConfigs) {
        if (oneToOneConfigs == null) {
            return false;
        }

        for (OneToOneConfig oneToOneConfig : oneToOneConfigs) {
            if (oneToOneConfig != null && oneToOneConfig.getOrphanRemoval() != null) {
                return oneToOneConfig.getOrphanRemoval().booleanValue();
            }
        }

        return false; // let jpa default applies
    }

    // ------------------------------------------
    // CASCADE
    // ------------------------------------------

    public static String jpaCascade(PackageImportAdder importAdder, CascadeGetter... cascadeGetters) {
        if (cascadeGetters == null) {
            return "";
        }

        // we look for the first non empty conf.
        // not that it could be a NONE conf => user does not want any cascade.

        for (CascadeGetter cascadeGetter : cascadeGetters) {
            if (cascadeGetter != null) {
                List<Cascade> cascadeConf = cascadeGetter.getCascades();

                if (cascadeConf != null && cascadeConf.size() > 0) {
                    List<CascadeType> cascades = convertJpaCascade(cascadeConf);
                    // we could have removed the NONE element, so we check for emptiness.
                    if (!cascades.isEmpty()) {
                        for (CascadeType ct : cascades) {
                            importAdder.addImport("static javax.persistence.CascadeType." + ct.name());
                        }

                        AttributeBuilder ab = new AttributeBuilder();
                        ab.add("cascade", convertJpaCascadeToStrings(cascades));
                        return ab.getAttributes();
                    } else {
                        return ""; // there was 1 element: NONE => user does not want anything, we bail out.
                    }
                }
            }
        }

        return "";
    }

    private static String[] convertJpaCascadeToStrings(List<CascadeType> cascadeTypes) {
        if (cascadeTypes == null) {
            return null;
        }

        String[] result = new String[cascadeTypes.size()];
        int i = 0;

        for (CascadeType ct : cascadeTypes) {
            result[i++] = ct.name();
        }

        return result;
    }

    private static List<CascadeType> convertJpaCascade(List<Cascade> cascades) {
        if (cascades == null) {
            return null;
        }

        List<CascadeType> result = newArrayList();

        for (Cascade c : cascades) {
            if (c.getType().isJpaType()) {
                result.add(c.getType().asJpaType());
            }
        }

        return result;
    }

    // ------------------------------------------
    // FETCH
    // ------------------------------------------

    public static String jpaFetch(PackageImportAdder adder, FetchTypeGetter... fetchTypeGetters) {
        Assert.notNull(adder);

        if (fetchTypeGetters == null) {
            return "";
        }

        // we look for the first non empty conf.
        // not that it could be a NONE conf => user does not want any fetch type.

        for (FetchTypeGetter fetchTypeGetter : fetchTypeGetters) {
            if (fetchTypeGetter != null) {
                if (fetchTypeGetter.getFetch() != null) {
                    if (fetchTypeGetter.getFetch().isJpaType()) {
                        FetchType fetchType = fetchTypeGetter.getFetch().asJpaType();
                        adder.addImport("static javax.persistence.FetchType." + fetchType.name());
                        return "fetch = " + fetchType.name();
                    } else { // NONE
                        return ""; // user explicitly said he does not want any fetch type.
                    }
                }
            }
        }

        return "";
    }

    // ------------------------------------------
    // ORDER BY
    // ------------------------------------------

    public static String orderByAnnotation(PackageImportAdder adder, OrderByGetter... orderByGetters) {
        Assert.notNull(adder);

        if (orderByGetters == null) {
            return null;
        }

        // we look for the first non empty conf.
        for (OrderByGetter getter : orderByGetters) {
            if (getter != null) {
                if (getter.getOrderBy() != null && !getter.getOrderBy().isEmpty()) {
                    adder.addImport("javax.persistence.OrderBy");
                    AttributeBuilder ab = new AttributeBuilder();
                    ab.addString(getter.getOrderBy());
                    return ab.bindAttributesTo("@OrderBy");
                }
            }
        }

        return null;
    }

    // ------------------------------------------
    // CacheConfig
    // ------------------------------------------

    public static String getCacheAnnotation(PackageImportAdder packageImportAdder, CacheConfigGetter... cacheConfigGetters) {
        if (cacheConfigGetters == null) {
            return "";
        }

        for (CacheConfigGetter cacheConfigGetter : cacheConfigGetters) {
            if (cacheConfigGetter != null) {
                CacheConfig cacheConfig = cacheConfigGetter.getCacheConfig();

                if (cacheConfig != null) {
                    if (cacheConfig.getUsage().isNone()) {
                        return ""; // cleaner than disabling the cache using @Cache (usage = NONE)
                    } else {
                        packageImportAdder.addImport("org.hibernate.annotations.Cache");
                        packageImportAdder.addImport("static org.hibernate.annotations.CacheConcurrencyStrategy." + cacheConfig.getUsage().name());

                        AttributeBuilder attr = new AttributeBuilder();
                        attr.add("usage = " + cacheConfig.getUsage().name());

                        if (cacheConfig.hasInclude()) {
                            attr.addString("include", cacheConfig.getInclude());
                        }

                        if (cacheConfig.hasRegion()) {
                            attr.addString("region", cacheConfig.getInclude());
                        }

                        return attr.bindAttributesTo("@Cache");
                    }
                }
            }
        }

        return "";
    }
}
