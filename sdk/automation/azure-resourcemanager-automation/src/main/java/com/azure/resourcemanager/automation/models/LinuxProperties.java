// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.automation.models;

import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/** Linux specific update configuration. */
@Fluent
public final class LinuxProperties {
    /*
     * Update classifications included in the software update configuration.
     */
    @JsonProperty(value = "includedPackageClassifications")
    private LinuxUpdateClasses includedPackageClassifications;

    /*
     * packages excluded from the software update configuration.
     */
    @JsonProperty(value = "excludedPackageNameMasks")
    private List<String> excludedPackageNameMasks;

    /*
     * packages included from the software update configuration.
     */
    @JsonProperty(value = "includedPackageNameMasks")
    private List<String> includedPackageNameMasks;

    /*
     * Reboot setting for the software update configuration.
     */
    @JsonProperty(value = "rebootSetting")
    private String rebootSetting;

    /**
     * Get the includedPackageClassifications property: Update classifications included in the software update
     * configuration.
     *
     * @return the includedPackageClassifications value.
     */
    public LinuxUpdateClasses includedPackageClassifications() {
        return this.includedPackageClassifications;
    }

    /**
     * Set the includedPackageClassifications property: Update classifications included in the software update
     * configuration.
     *
     * @param includedPackageClassifications the includedPackageClassifications value to set.
     * @return the LinuxProperties object itself.
     */
    public LinuxProperties withIncludedPackageClassifications(LinuxUpdateClasses includedPackageClassifications) {
        this.includedPackageClassifications = includedPackageClassifications;
        return this;
    }

    /**
     * Get the excludedPackageNameMasks property: packages excluded from the software update configuration.
     *
     * @return the excludedPackageNameMasks value.
     */
    public List<String> excludedPackageNameMasks() {
        return this.excludedPackageNameMasks;
    }

    /**
     * Set the excludedPackageNameMasks property: packages excluded from the software update configuration.
     *
     * @param excludedPackageNameMasks the excludedPackageNameMasks value to set.
     * @return the LinuxProperties object itself.
     */
    public LinuxProperties withExcludedPackageNameMasks(List<String> excludedPackageNameMasks) {
        this.excludedPackageNameMasks = excludedPackageNameMasks;
        return this;
    }

    /**
     * Get the includedPackageNameMasks property: packages included from the software update configuration.
     *
     * @return the includedPackageNameMasks value.
     */
    public List<String> includedPackageNameMasks() {
        return this.includedPackageNameMasks;
    }

    /**
     * Set the includedPackageNameMasks property: packages included from the software update configuration.
     *
     * @param includedPackageNameMasks the includedPackageNameMasks value to set.
     * @return the LinuxProperties object itself.
     */
    public LinuxProperties withIncludedPackageNameMasks(List<String> includedPackageNameMasks) {
        this.includedPackageNameMasks = includedPackageNameMasks;
        return this;
    }

    /**
     * Get the rebootSetting property: Reboot setting for the software update configuration.
     *
     * @return the rebootSetting value.
     */
    public String rebootSetting() {
        return this.rebootSetting;
    }

    /**
     * Set the rebootSetting property: Reboot setting for the software update configuration.
     *
     * @param rebootSetting the rebootSetting value to set.
     * @return the LinuxProperties object itself.
     */
    public LinuxProperties withRebootSetting(String rebootSetting) {
        this.rebootSetting = rebootSetting;
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
