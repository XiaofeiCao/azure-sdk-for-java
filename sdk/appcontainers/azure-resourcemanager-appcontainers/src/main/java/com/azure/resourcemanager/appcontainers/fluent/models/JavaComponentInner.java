// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.appcontainers.fluent.models;

import com.azure.core.annotation.Fluent;
import com.azure.core.management.ProxyResource;
import com.azure.core.management.SystemData;
import com.azure.resourcemanager.appcontainers.models.JavaComponentConfigurationProperty;
import com.azure.resourcemanager.appcontainers.models.JavaComponentProvisioningState;
import com.azure.resourcemanager.appcontainers.models.JavaComponentServiceBind;
import com.azure.resourcemanager.appcontainers.models.JavaComponentType;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Java Component.
 */
@Fluent
public final class JavaComponentInner extends ProxyResource {
    /*
     * Java Component resource specific properties
     */
    @JsonProperty(value = "properties")
    private JavaComponentProperties innerProperties;

    /*
     * Azure Resource Manager metadata containing createdBy and modifiedBy information.
     */
    @JsonProperty(value = "systemData", access = JsonProperty.Access.WRITE_ONLY)
    private SystemData systemData;

    /**
     * Creates an instance of JavaComponentInner class.
     */
    public JavaComponentInner() {
    }

    /**
     * Get the innerProperties property: Java Component resource specific properties.
     * 
     * @return the innerProperties value.
     */
    private JavaComponentProperties innerProperties() {
        return this.innerProperties;
    }

    /**
     * Get the systemData property: Azure Resource Manager metadata containing createdBy and modifiedBy information.
     * 
     * @return the systemData value.
     */
    public SystemData systemData() {
        return this.systemData;
    }

    /**
     * Get the componentType property: Type of the Java Component.
     * 
     * @return the componentType value.
     */
    public JavaComponentType componentType() {
        return this.innerProperties() == null ? null : this.innerProperties().componentType();
    }

    /**
     * Set the componentType property: Type of the Java Component.
     * 
     * @param componentType the componentType value to set.
     * @return the JavaComponentInner object itself.
     */
    public JavaComponentInner withComponentType(JavaComponentType componentType) {
        if (this.innerProperties() == null) {
            this.innerProperties = new JavaComponentProperties();
        }
        this.innerProperties().withComponentType(componentType);
        return this;
    }

    /**
     * Get the provisioningState property: Provisioning state of the Java Component.
     * 
     * @return the provisioningState value.
     */
    public JavaComponentProvisioningState provisioningState() {
        return this.innerProperties() == null ? null : this.innerProperties().provisioningState();
    }

    /**
     * Get the configurations property: List of Java Components configuration properties.
     * 
     * @return the configurations value.
     */
    public List<JavaComponentConfigurationProperty> configurations() {
        return this.innerProperties() == null ? null : this.innerProperties().configurations();
    }

    /**
     * Set the configurations property: List of Java Components configuration properties.
     * 
     * @param configurations the configurations value to set.
     * @return the JavaComponentInner object itself.
     */
    public JavaComponentInner withConfigurations(List<JavaComponentConfigurationProperty> configurations) {
        if (this.innerProperties() == null) {
            this.innerProperties = new JavaComponentProperties();
        }
        this.innerProperties().withConfigurations(configurations);
        return this;
    }

    /**
     * Get the serviceBinds property: List of Java Components that are bound to the Java component.
     * 
     * @return the serviceBinds value.
     */
    public List<JavaComponentServiceBind> serviceBinds() {
        return this.innerProperties() == null ? null : this.innerProperties().serviceBinds();
    }

    /**
     * Set the serviceBinds property: List of Java Components that are bound to the Java component.
     * 
     * @param serviceBinds the serviceBinds value to set.
     * @return the JavaComponentInner object itself.
     */
    public JavaComponentInner withServiceBinds(List<JavaComponentServiceBind> serviceBinds) {
        if (this.innerProperties() == null) {
            this.innerProperties = new JavaComponentProperties();
        }
        this.innerProperties().withServiceBinds(serviceBinds);
        return this;
    }

    /**
     * Validates the instance.
     * 
     * @throws IllegalArgumentException thrown if the instance is not valid.
     */
    public void validate() {
        if (innerProperties() != null) {
            innerProperties().validate();
        }
    }
}
