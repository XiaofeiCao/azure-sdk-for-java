// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) TypeSpec Code Generator.

package com.azure.resourcemanager.devopsinfrastructure.models;

import com.azure.core.util.ExpandableStringEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Collection;

/**
 * The status of the machine resource.
 */
public final class ResourceStatus extends ExpandableStringEnum<ResourceStatus> {
    /**
     * Static value Ready for ResourceStatus.
     */
    public static final ResourceStatus READY = fromString("Ready");

    /**
     * Static value NotReady for ResourceStatus.
     */
    public static final ResourceStatus NOT_READY = fromString("NotReady");

    /**
     * Static value Allocated for ResourceStatus.
     */
    public static final ResourceStatus ALLOCATED = fromString("Allocated");

    /**
     * Static value PendingReturn for ResourceStatus.
     */
    public static final ResourceStatus PENDING_RETURN = fromString("PendingReturn");

    /**
     * Static value Returned for ResourceStatus.
     */
    public static final ResourceStatus RETURNED = fromString("Returned");

    /**
     * Static value Leased for ResourceStatus.
     */
    public static final ResourceStatus LEASED = fromString("Leased");

    /**
     * Static value Provisioning for ResourceStatus.
     */
    public static final ResourceStatus PROVISIONING = fromString("Provisioning");

    /**
     * Static value Updating for ResourceStatus.
     */
    public static final ResourceStatus UPDATING = fromString("Updating");

    /**
     * Static value Starting for ResourceStatus.
     */
    public static final ResourceStatus STARTING = fromString("Starting");

    /**
     * Static value PendingReimage for ResourceStatus.
     */
    public static final ResourceStatus PENDING_REIMAGE = fromString("PendingReimage");

    /**
     * Static value Reimaging for ResourceStatus.
     */
    public static final ResourceStatus REIMAGING = fromString("Reimaging");

    /**
     * Creates a new instance of ResourceStatus value.
     * 
     * @deprecated Use the {@link #fromString(String)} factory method.
     */
    @Deprecated
    public ResourceStatus() {
    }

    /**
     * Creates or finds a ResourceStatus from its string representation.
     * 
     * @param name a name to look for.
     * @return the corresponding ResourceStatus.
     */
    @JsonCreator
    public static ResourceStatus fromString(String name) {
        return fromString(name, ResourceStatus.class);
    }

    /**
     * Gets known ResourceStatus values.
     * 
     * @return known ResourceStatus values.
     */
    public static Collection<ResourceStatus> values() {
        return values(ResourceStatus.class);
    }
}
