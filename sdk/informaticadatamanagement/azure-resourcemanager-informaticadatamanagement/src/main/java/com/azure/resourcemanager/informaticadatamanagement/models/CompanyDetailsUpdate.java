// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) TypeSpec Code Generator.

package com.azure.resourcemanager.informaticadatamanagement.models;

import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Company details of Informatica Organization resource.
 */
@Fluent
public final class CompanyDetailsUpdate {
    /*
     * company Name
     */
    @JsonProperty(value = "companyName")
    private String companyName;

    /*
     * Office Address
     */
    @JsonProperty(value = "officeAddress")
    private String officeAddress;

    /*
     * Country name
     */
    @JsonProperty(value = "country")
    private String country;

    /*
     * Domain name
     */
    @JsonProperty(value = "domain")
    private String domain;

    /*
     * Business phone number
     */
    @JsonProperty(value = "business")
    private String business;

    /*
     * Number Of Employees
     */
    @JsonProperty(value = "numberOfEmployees")
    private Integer numberOfEmployees;

    /**
     * Creates an instance of CompanyDetailsUpdate class.
     */
    public CompanyDetailsUpdate() {
    }

    /**
     * Get the companyName property: company Name.
     * 
     * @return the companyName value.
     */
    public String companyName() {
        return this.companyName;
    }

    /**
     * Set the companyName property: company Name.
     * 
     * @param companyName the companyName value to set.
     * @return the CompanyDetailsUpdate object itself.
     */
    public CompanyDetailsUpdate withCompanyName(String companyName) {
        this.companyName = companyName;
        return this;
    }

    /**
     * Get the officeAddress property: Office Address.
     * 
     * @return the officeAddress value.
     */
    public String officeAddress() {
        return this.officeAddress;
    }

    /**
     * Set the officeAddress property: Office Address.
     * 
     * @param officeAddress the officeAddress value to set.
     * @return the CompanyDetailsUpdate object itself.
     */
    public CompanyDetailsUpdate withOfficeAddress(String officeAddress) {
        this.officeAddress = officeAddress;
        return this;
    }

    /**
     * Get the country property: Country name.
     * 
     * @return the country value.
     */
    public String country() {
        return this.country;
    }

    /**
     * Set the country property: Country name.
     * 
     * @param country the country value to set.
     * @return the CompanyDetailsUpdate object itself.
     */
    public CompanyDetailsUpdate withCountry(String country) {
        this.country = country;
        return this;
    }

    /**
     * Get the domain property: Domain name.
     * 
     * @return the domain value.
     */
    public String domain() {
        return this.domain;
    }

    /**
     * Set the domain property: Domain name.
     * 
     * @param domain the domain value to set.
     * @return the CompanyDetailsUpdate object itself.
     */
    public CompanyDetailsUpdate withDomain(String domain) {
        this.domain = domain;
        return this;
    }

    /**
     * Get the business property: Business phone number.
     * 
     * @return the business value.
     */
    public String business() {
        return this.business;
    }

    /**
     * Set the business property: Business phone number.
     * 
     * @param business the business value to set.
     * @return the CompanyDetailsUpdate object itself.
     */
    public CompanyDetailsUpdate withBusiness(String business) {
        this.business = business;
        return this;
    }

    /**
     * Get the numberOfEmployees property: Number Of Employees.
     * 
     * @return the numberOfEmployees value.
     */
    public Integer numberOfEmployees() {
        return this.numberOfEmployees;
    }

    /**
     * Set the numberOfEmployees property: Number Of Employees.
     * 
     * @param numberOfEmployees the numberOfEmployees value to set.
     * @return the CompanyDetailsUpdate object itself.
     */
    public CompanyDetailsUpdate withNumberOfEmployees(Integer numberOfEmployees) {
        this.numberOfEmployees = numberOfEmployees;
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
