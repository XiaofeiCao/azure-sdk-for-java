// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.oracledatabase.models;

import com.azure.core.util.ExpandableStringEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Collection;

/**
 * DatabaseEditionType enum.
 */
public final class DatabaseEditionType extends ExpandableStringEnum<DatabaseEditionType> {
    /**
     * Static value StandardEdition for DatabaseEditionType.
     */
    public static final DatabaseEditionType STANDARD_EDITION = fromString("StandardEdition");

    /**
     * Static value EnterpriseEdition for DatabaseEditionType.
     */
    public static final DatabaseEditionType ENTERPRISE_EDITION = fromString("EnterpriseEdition");

    /**
     * Creates a new instance of DatabaseEditionType value.
     * 
     * @deprecated Use the {@link #fromString(String)} factory method.
     */
    @Deprecated
    public DatabaseEditionType() {
    }

    /**
     * Creates or finds a DatabaseEditionType from its string representation.
     * 
     * @param name a name to look for.
     * @return the corresponding DatabaseEditionType.
     */
    @JsonCreator
    public static DatabaseEditionType fromString(String name) {
        return fromString(name, DatabaseEditionType.class);
    }

    /**
     * Gets known DatabaseEditionType values.
     * 
     * @return known DatabaseEditionType values.
     */
    public static Collection<DatabaseEditionType> values() {
        return values(DatabaseEditionType.class);
    }
}
