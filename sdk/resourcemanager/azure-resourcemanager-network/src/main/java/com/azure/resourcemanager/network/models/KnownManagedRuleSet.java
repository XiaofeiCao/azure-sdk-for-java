// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.resourcemanager.network.models;

/**
 * Known managed rule set.
 */
public enum KnownManagedRuleSet {
    /** Managed Rule Set based off OWASP CRS 3.2.0 version. */
    OWASP_3_2("OWASP", "3.2"),
    /** Managed Rule Set based off OWASP CRS 3.1.1 version. */
    OWASP_3_1("OWASP", "3.1"),
    /** Managed Rule Set based off OWASP CRS 3.0.0 version.*/
    OWASP_3_0("OWASP", "3.0"),
    /** The Azure-managed Default Rule Set (DRS) 2.1 version. */
    MICROSOFT_DEFAULT_RULESET_2_1("Microsoft_DefaultRuleSet", "2.1");

    private final String type;
    private final String version;

    KnownManagedRuleSet(String type, String version) {
        this.type = type;
        this.version = version;
    }

    /** @return the type of the Managed Rule Set */
    public String type() {
        return type;
    }

    /** @return the version of the Managed Rule Set */
    public String version() {
        return version;
    }
}
