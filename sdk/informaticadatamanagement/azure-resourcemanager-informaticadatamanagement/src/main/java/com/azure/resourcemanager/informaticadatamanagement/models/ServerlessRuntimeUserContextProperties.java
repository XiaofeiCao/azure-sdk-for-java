// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) TypeSpec Code Generator.

package com.azure.resourcemanager.informaticadatamanagement.models;

import com.azure.core.annotation.Fluent;
import com.azure.core.util.logging.ClientLogger;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Informatica Serverless Runtime User context properties.
 */
@Fluent
public final class ServerlessRuntimeUserContextProperties {
    /*
     * User context token for OBO flow.
     */
    @JsonProperty(value = "userContextToken", required = true)
    private String userContextToken;

    /**
     * Creates an instance of ServerlessRuntimeUserContextProperties class.
     */
    public ServerlessRuntimeUserContextProperties() {
    }

    /**
     * Get the userContextToken property: User context token for OBO flow.
     * 
     * @return the userContextToken value.
     */
    public String userContextToken() {
        return this.userContextToken;
    }

    /**
     * Set the userContextToken property: User context token for OBO flow.
     * 
     * @param userContextToken the userContextToken value to set.
     * @return the ServerlessRuntimeUserContextProperties object itself.
     */
    public ServerlessRuntimeUserContextProperties withUserContextToken(String userContextToken) {
        this.userContextToken = userContextToken;
        return this;
    }

    /**
     * Validates the instance.
     * 
     * @throws IllegalArgumentException thrown if the instance is not valid.
     */
    public void validate() {
        if (userContextToken() == null) {
            throw LOGGER.atError()
                .log(new IllegalArgumentException(
                    "Missing required property userContextToken in model ServerlessRuntimeUserContextProperties"));
        }
    }

    private static final ClientLogger LOGGER = new ClientLogger(ServerlessRuntimeUserContextProperties.class);
}
