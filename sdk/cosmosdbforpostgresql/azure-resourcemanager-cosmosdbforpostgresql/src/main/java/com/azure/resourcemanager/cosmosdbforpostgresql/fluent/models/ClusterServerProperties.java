// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.cosmosdbforpostgresql.fluent.models;

import com.azure.core.annotation.Fluent;
import com.azure.resourcemanager.cosmosdbforpostgresql.models.ServerProperties;
import com.azure.resourcemanager.cosmosdbforpostgresql.models.ServerRole;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The properties of a server in cluster.
 */
@Fluent
public final class ClusterServerProperties extends ServerProperties {
    /*
     * The fully qualified domain name of a server.
     */
    @JsonProperty(value = "fullyQualifiedDomainName", access = JsonProperty.Access.WRITE_ONLY)
    private String fullyQualifiedDomainName;

    /*
     * The role of server in the cluster.
     */
    @JsonProperty(value = "role")
    private ServerRole role;

    /*
     * A state of a cluster/server that is visible to user.
     */
    @JsonProperty(value = "state", access = JsonProperty.Access.WRITE_ONLY)
    private String state;

    /*
     * A state of HA feature for the cluster.
     */
    @JsonProperty(value = "haState", access = JsonProperty.Access.WRITE_ONLY)
    private String haState;

    /*
     * Availability Zone information of the server.
     */
    @JsonProperty(value = "availabilityZone")
    private String availabilityZone;

    /*
     * The major PostgreSQL version of server.
     */
    @JsonProperty(value = "postgresqlVersion")
    private String postgresqlVersion;

    /*
     * The Citus extension version of server.
     */
    @JsonProperty(value = "citusVersion")
    private String citusVersion;

    /**
     * Creates an instance of ClusterServerProperties class.
     */
    public ClusterServerProperties() {
    }

    /**
     * Get the fullyQualifiedDomainName property: The fully qualified domain name of a server.
     * 
     * @return the fullyQualifiedDomainName value.
     */
    public String fullyQualifiedDomainName() {
        return this.fullyQualifiedDomainName;
    }

    /**
     * Get the role property: The role of server in the cluster.
     * 
     * @return the role value.
     */
    public ServerRole role() {
        return this.role;
    }

    /**
     * Set the role property: The role of server in the cluster.
     * 
     * @param role the role value to set.
     * @return the ClusterServerProperties object itself.
     */
    public ClusterServerProperties withRole(ServerRole role) {
        this.role = role;
        return this;
    }

    /**
     * Get the state property: A state of a cluster/server that is visible to user.
     * 
     * @return the state value.
     */
    public String state() {
        return this.state;
    }

    /**
     * Get the haState property: A state of HA feature for the cluster.
     * 
     * @return the haState value.
     */
    public String haState() {
        return this.haState;
    }

    /**
     * Get the availabilityZone property: Availability Zone information of the server.
     * 
     * @return the availabilityZone value.
     */
    public String availabilityZone() {
        return this.availabilityZone;
    }

    /**
     * Set the availabilityZone property: Availability Zone information of the server.
     * 
     * @param availabilityZone the availabilityZone value to set.
     * @return the ClusterServerProperties object itself.
     */
    public ClusterServerProperties withAvailabilityZone(String availabilityZone) {
        this.availabilityZone = availabilityZone;
        return this;
    }

    /**
     * Get the postgresqlVersion property: The major PostgreSQL version of server.
     * 
     * @return the postgresqlVersion value.
     */
    public String postgresqlVersion() {
        return this.postgresqlVersion;
    }

    /**
     * Set the postgresqlVersion property: The major PostgreSQL version of server.
     * 
     * @param postgresqlVersion the postgresqlVersion value to set.
     * @return the ClusterServerProperties object itself.
     */
    public ClusterServerProperties withPostgresqlVersion(String postgresqlVersion) {
        this.postgresqlVersion = postgresqlVersion;
        return this;
    }

    /**
     * Get the citusVersion property: The Citus extension version of server.
     * 
     * @return the citusVersion value.
     */
    public String citusVersion() {
        return this.citusVersion;
    }

    /**
     * Set the citusVersion property: The Citus extension version of server.
     * 
     * @param citusVersion the citusVersion value to set.
     * @return the ClusterServerProperties object itself.
     */
    public ClusterServerProperties withCitusVersion(String citusVersion) {
        this.citusVersion = citusVersion;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClusterServerProperties withServerEdition(String serverEdition) {
        super.withServerEdition(serverEdition);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClusterServerProperties withStorageQuotaInMb(Integer storageQuotaInMb) {
        super.withStorageQuotaInMb(storageQuotaInMb);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClusterServerProperties withVCores(Integer vCores) {
        super.withVCores(vCores);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClusterServerProperties withEnableHa(Boolean enableHa) {
        super.withEnableHa(enableHa);
        return this;
    }

    /**
     * Validates the instance.
     * 
     * @throws IllegalArgumentException thrown if the instance is not valid.
     */
    @Override
    public void validate() {
        super.validate();
    }
}
