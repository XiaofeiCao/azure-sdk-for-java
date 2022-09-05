// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.resourcemanager.network.models;

/**
 * A service tag represents a group of IP address prefixes from a given Azure service.
 * You can use service tags to define network access controls on network security groups, Azure Firewall, and user-defined routes.
 * Use service tags in place of specific IP addresses when you create security rules and routes.
 */
public class ServiceTag {

    /** Action Group. */
    public static final ServiceTag ACTION_GROUP = new ServiceTag("ActionGroup", true, false, false, false);


    // service tag name
    private final String name;
    // can use in inbound rule
    private final boolean inbound;
    // can use in outbound rule
    private final boolean outbound;
    // can be regional, e.g. Storage.WestUS
    private final boolean regional;
    // can use with Azure Firewall
    private final boolean firewall;

    private ServiceTag(String name, boolean inbound, boolean outbound, boolean regional, boolean firewall) {
        this.name = name;
        this.inbound = inbound;
        this.outbound = outbound;
        this.regional = regional;
        this.firewall = firewall;
    }
}
