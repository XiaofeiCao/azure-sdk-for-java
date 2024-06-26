// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.security.models;

import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeId;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * The CSPM P1 for GCP offering.
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "offeringType",
    defaultImpl = DefenderCspmGcpOffering.class,
    visible = true)
@JsonTypeName("DefenderCspmGcp")
@Fluent
public final class DefenderCspmGcpOffering extends CloudOffering {
    /*
     * The type of the security offering.
     */
    @JsonTypeId
    @JsonProperty(value = "offeringType", required = true)
    private OfferingType offeringType = OfferingType.DEFENDER_CSPM_GCP;

    /*
     * GCP Defenders CSPM Permissions Management OIDC (Open ID connect) connection configurations
     */
    @JsonProperty(value = "ciemDiscovery")
    private DefenderCspmGcpOfferingCiemDiscovery ciemDiscovery;

    /*
     * The Microsoft Defender for CSPM VM scanning configuration
     */
    @JsonProperty(value = "vmScanners")
    private DefenderCspmGcpOfferingVmScanners vmScanners;

    /*
     * The Microsoft Defender Data Sensitivity discovery configuration
     */
    @JsonProperty(value = "dataSensitivityDiscovery")
    private DefenderCspmGcpOfferingDataSensitivityDiscovery dataSensitivityDiscovery;

    /*
     * The Microsoft Defender Container image assessment configuration
     */
    @JsonProperty(value = "mdcContainersImageAssessment")
    private DefenderCspmGcpOfferingMdcContainersImageAssessment mdcContainersImageAssessment;

    /*
     * The Microsoft Defender Container agentless discovery configuration
     */
    @JsonProperty(value = "mdcContainersAgentlessDiscoveryK8s")
    private DefenderCspmGcpOfferingMdcContainersAgentlessDiscoveryK8S mdcContainersAgentlessDiscoveryK8S;

    /**
     * Creates an instance of DefenderCspmGcpOffering class.
     */
    public DefenderCspmGcpOffering() {
    }

    /**
     * Get the offeringType property: The type of the security offering.
     * 
     * @return the offeringType value.
     */
    @Override
    public OfferingType offeringType() {
        return this.offeringType;
    }

    /**
     * Get the ciemDiscovery property: GCP Defenders CSPM Permissions Management OIDC (Open ID connect) connection
     * configurations.
     * 
     * @return the ciemDiscovery value.
     */
    public DefenderCspmGcpOfferingCiemDiscovery ciemDiscovery() {
        return this.ciemDiscovery;
    }

    /**
     * Set the ciemDiscovery property: GCP Defenders CSPM Permissions Management OIDC (Open ID connect) connection
     * configurations.
     * 
     * @param ciemDiscovery the ciemDiscovery value to set.
     * @return the DefenderCspmGcpOffering object itself.
     */
    public DefenderCspmGcpOffering withCiemDiscovery(DefenderCspmGcpOfferingCiemDiscovery ciemDiscovery) {
        this.ciemDiscovery = ciemDiscovery;
        return this;
    }

    /**
     * Get the vmScanners property: The Microsoft Defender for CSPM VM scanning configuration.
     * 
     * @return the vmScanners value.
     */
    public DefenderCspmGcpOfferingVmScanners vmScanners() {
        return this.vmScanners;
    }

    /**
     * Set the vmScanners property: The Microsoft Defender for CSPM VM scanning configuration.
     * 
     * @param vmScanners the vmScanners value to set.
     * @return the DefenderCspmGcpOffering object itself.
     */
    public DefenderCspmGcpOffering withVmScanners(DefenderCspmGcpOfferingVmScanners vmScanners) {
        this.vmScanners = vmScanners;
        return this;
    }

    /**
     * Get the dataSensitivityDiscovery property: The Microsoft Defender Data Sensitivity discovery configuration.
     * 
     * @return the dataSensitivityDiscovery value.
     */
    public DefenderCspmGcpOfferingDataSensitivityDiscovery dataSensitivityDiscovery() {
        return this.dataSensitivityDiscovery;
    }

    /**
     * Set the dataSensitivityDiscovery property: The Microsoft Defender Data Sensitivity discovery configuration.
     * 
     * @param dataSensitivityDiscovery the dataSensitivityDiscovery value to set.
     * @return the DefenderCspmGcpOffering object itself.
     */
    public DefenderCspmGcpOffering
        withDataSensitivityDiscovery(DefenderCspmGcpOfferingDataSensitivityDiscovery dataSensitivityDiscovery) {
        this.dataSensitivityDiscovery = dataSensitivityDiscovery;
        return this;
    }

    /**
     * Get the mdcContainersImageAssessment property: The Microsoft Defender Container image assessment configuration.
     * 
     * @return the mdcContainersImageAssessment value.
     */
    public DefenderCspmGcpOfferingMdcContainersImageAssessment mdcContainersImageAssessment() {
        return this.mdcContainersImageAssessment;
    }

    /**
     * Set the mdcContainersImageAssessment property: The Microsoft Defender Container image assessment configuration.
     * 
     * @param mdcContainersImageAssessment the mdcContainersImageAssessment value to set.
     * @return the DefenderCspmGcpOffering object itself.
     */
    public DefenderCspmGcpOffering withMdcContainersImageAssessment(
        DefenderCspmGcpOfferingMdcContainersImageAssessment mdcContainersImageAssessment) {
        this.mdcContainersImageAssessment = mdcContainersImageAssessment;
        return this;
    }

    /**
     * Get the mdcContainersAgentlessDiscoveryK8S property: The Microsoft Defender Container agentless discovery
     * configuration.
     * 
     * @return the mdcContainersAgentlessDiscoveryK8S value.
     */
    public DefenderCspmGcpOfferingMdcContainersAgentlessDiscoveryK8S mdcContainersAgentlessDiscoveryK8S() {
        return this.mdcContainersAgentlessDiscoveryK8S;
    }

    /**
     * Set the mdcContainersAgentlessDiscoveryK8S property: The Microsoft Defender Container agentless discovery
     * configuration.
     * 
     * @param mdcContainersAgentlessDiscoveryK8S the mdcContainersAgentlessDiscoveryK8S value to set.
     * @return the DefenderCspmGcpOffering object itself.
     */
    public DefenderCspmGcpOffering withMdcContainersAgentlessDiscoveryK8S(
        DefenderCspmGcpOfferingMdcContainersAgentlessDiscoveryK8S mdcContainersAgentlessDiscoveryK8S) {
        this.mdcContainersAgentlessDiscoveryK8S = mdcContainersAgentlessDiscoveryK8S;
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
        if (ciemDiscovery() != null) {
            ciemDiscovery().validate();
        }
        if (vmScanners() != null) {
            vmScanners().validate();
        }
        if (dataSensitivityDiscovery() != null) {
            dataSensitivityDiscovery().validate();
        }
        if (mdcContainersImageAssessment() != null) {
            mdcContainersImageAssessment().validate();
        }
        if (mdcContainersAgentlessDiscoveryK8S() != null) {
            mdcContainersAgentlessDiscoveryK8S().validate();
        }
    }
}
