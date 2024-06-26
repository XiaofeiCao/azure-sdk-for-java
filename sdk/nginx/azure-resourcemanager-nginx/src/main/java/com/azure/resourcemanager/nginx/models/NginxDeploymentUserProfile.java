// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.nginx.models;

import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The NginxDeploymentUserProfile model.
 */
@Fluent
public final class NginxDeploymentUserProfile {
    /*
     * The preferred support contact email address of the user used for sending alerts and notification. Can be an empty string or a valid email address.
     */
    @JsonProperty(value = "preferredEmail")
    private String preferredEmail;

    /**
     * Creates an instance of NginxDeploymentUserProfile class.
     */
    public NginxDeploymentUserProfile() {
    }

    /**
     * Get the preferredEmail property: The preferred support contact email address of the user used for sending alerts
     * and notification. Can be an empty string or a valid email address.
     * 
     * @return the preferredEmail value.
     */
    public String preferredEmail() {
        return this.preferredEmail;
    }

    /**
     * Set the preferredEmail property: The preferred support contact email address of the user used for sending alerts
     * and notification. Can be an empty string or a valid email address.
     * 
     * @param preferredEmail the preferredEmail value to set.
     * @return the NginxDeploymentUserProfile object itself.
     */
    public NginxDeploymentUserProfile withPreferredEmail(String preferredEmail) {
        this.preferredEmail = preferredEmail;
        return this;
    }

    /**
     * Validates the instance.
     * 
     * @throws IllegalArgumentException thrown if the instance is not valid.
     */
    public void validate() {
    }
}
