// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.alertsmanagement.models;

import com.azure.core.util.ExpandableStringEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Collection;

/** Defines values for SmartGroupsSortByFields. */
public final class SmartGroupsSortByFields extends ExpandableStringEnum<SmartGroupsSortByFields> {
    /** Static value alertsCount for SmartGroupsSortByFields. */
    public static final SmartGroupsSortByFields ALERTS_COUNT = fromString("alertsCount");

    /** Static value state for SmartGroupsSortByFields. */
    public static final SmartGroupsSortByFields STATE = fromString("state");

    /** Static value severity for SmartGroupsSortByFields. */
    public static final SmartGroupsSortByFields SEVERITY = fromString("severity");

    /** Static value startDateTime for SmartGroupsSortByFields. */
    public static final SmartGroupsSortByFields START_DATE_TIME = fromString("startDateTime");

    /** Static value lastModifiedDateTime for SmartGroupsSortByFields. */
    public static final SmartGroupsSortByFields LAST_MODIFIED_DATE_TIME = fromString("lastModifiedDateTime");

    /**
     * Creates or finds a SmartGroupsSortByFields from its string representation.
     *
     * @param name a name to look for.
     * @return the corresponding SmartGroupsSortByFields.
     */
    @JsonCreator
    public static SmartGroupsSortByFields fromString(String name) {
        return fromString(name, SmartGroupsSortByFields.class);
    }

    /**
     * Gets known SmartGroupsSortByFields values.
     *
     * @return known SmartGroupsSortByFields values.
     */
    public static Collection<SmartGroupsSortByFields> values() {
        return values(SmartGroupsSortByFields.class);
    }
}
