// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) TypeSpec Code Generator.

package com.azure.resourcemanager.informaticadatamanagement.models;

import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Patchable Properties of the Informatica Organization resource.
 */
@Fluent
public final class OrganizationPropertiesCustomUpdate {
    /*
     * Informatica Organization properties
     */
    @JsonProperty(value = "informaticaOrganizationProperties")
    private InformaticaOrganizationResourceUpdate informaticaOrganizationProperties;

    /*
     * Marketplace details
     */
    @JsonProperty(value = "marketplaceDetails")
    private MarketplaceDetailsUpdate marketplaceDetails;

    /*
     * User details
     */
    @JsonProperty(value = "userDetails")
    private UserDetailsUpdate userDetails;

    /*
     * Company Details
     */
    @JsonProperty(value = "companyDetails")
    private CompanyDetailsUpdate companyDetails;

    /*
     * Existing Resource Id
     */
    @JsonProperty(value = "existingResourceId")
    private String existingResourceId;

    /**
     * Creates an instance of OrganizationPropertiesCustomUpdate class.
     */
    public OrganizationPropertiesCustomUpdate() {
    }

    /**
     * Get the informaticaOrganizationProperties property: Informatica Organization properties.
     * 
     * @return the informaticaOrganizationProperties value.
     */
    public InformaticaOrganizationResourceUpdate informaticaOrganizationProperties() {
        return this.informaticaOrganizationProperties;
    }

    /**
     * Set the informaticaOrganizationProperties property: Informatica Organization properties.
     * 
     * @param informaticaOrganizationProperties the informaticaOrganizationProperties value to set.
     * @return the OrganizationPropertiesCustomUpdate object itself.
     */
    public OrganizationPropertiesCustomUpdate
        withInformaticaOrganizationProperties(InformaticaOrganizationResourceUpdate informaticaOrganizationProperties) {
        this.informaticaOrganizationProperties = informaticaOrganizationProperties;
        return this;
    }

    /**
     * Get the marketplaceDetails property: Marketplace details.
     * 
     * @return the marketplaceDetails value.
     */
    public MarketplaceDetailsUpdate marketplaceDetails() {
        return this.marketplaceDetails;
    }

    /**
     * Set the marketplaceDetails property: Marketplace details.
     * 
     * @param marketplaceDetails the marketplaceDetails value to set.
     * @return the OrganizationPropertiesCustomUpdate object itself.
     */
    public OrganizationPropertiesCustomUpdate withMarketplaceDetails(MarketplaceDetailsUpdate marketplaceDetails) {
        this.marketplaceDetails = marketplaceDetails;
        return this;
    }

    /**
     * Get the userDetails property: User details.
     * 
     * @return the userDetails value.
     */
    public UserDetailsUpdate userDetails() {
        return this.userDetails;
    }

    /**
     * Set the userDetails property: User details.
     * 
     * @param userDetails the userDetails value to set.
     * @return the OrganizationPropertiesCustomUpdate object itself.
     */
    public OrganizationPropertiesCustomUpdate withUserDetails(UserDetailsUpdate userDetails) {
        this.userDetails = userDetails;
        return this;
    }

    /**
     * Get the companyDetails property: Company Details.
     * 
     * @return the companyDetails value.
     */
    public CompanyDetailsUpdate companyDetails() {
        return this.companyDetails;
    }

    /**
     * Set the companyDetails property: Company Details.
     * 
     * @param companyDetails the companyDetails value to set.
     * @return the OrganizationPropertiesCustomUpdate object itself.
     */
    public OrganizationPropertiesCustomUpdate withCompanyDetails(CompanyDetailsUpdate companyDetails) {
        this.companyDetails = companyDetails;
        return this;
    }

    /**
     * Get the existingResourceId property: Existing Resource Id.
     * 
     * @return the existingResourceId value.
     */
    public String existingResourceId() {
        return this.existingResourceId;
    }

    /**
     * Set the existingResourceId property: Existing Resource Id.
     * 
     * @param existingResourceId the existingResourceId value to set.
     * @return the OrganizationPropertiesCustomUpdate object itself.
     */
    public OrganizationPropertiesCustomUpdate withExistingResourceId(String existingResourceId) {
        this.existingResourceId = existingResourceId;
        return this;
    }

    /**
     * Validates the instance.
     * 
     * @throws IllegalArgumentException thrown if the instance is not valid.
     */
    public void validate() {
        if (informaticaOrganizationProperties() != null) {
            informaticaOrganizationProperties().validate();
        }
        if (marketplaceDetails() != null) {
            marketplaceDetails().validate();
        }
        if (userDetails() != null) {
            userDetails().validate();
        }
        if (companyDetails() != null) {
            companyDetails().validate();
        }
    }
}
