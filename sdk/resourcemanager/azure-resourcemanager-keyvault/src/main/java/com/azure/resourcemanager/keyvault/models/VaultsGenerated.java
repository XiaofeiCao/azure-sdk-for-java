package com.azure.resourcemanager.keyvault.models;

import com.azure.resourcemanager.keyvault.KeyVaultManager;
import com.azure.resourcemanager.resources.fluentcore.arm.collection.SupportsDeletingByResourceGroup;
import com.azure.resourcemanager.resources.fluentcore.arm.collection.SupportsGettingById;
import com.azure.resourcemanager.resources.fluentcore.arm.collection.SupportsGettingByResourceGroup;
import com.azure.resourcemanager.resources.fluentcore.arm.collection.SupportsListingByResourceGroup;
import com.azure.resourcemanager.resources.fluentcore.arm.models.HasManager;
import com.azure.resourcemanager.resources.fluentcore.collection.SupportsCreating;
import com.azure.resourcemanager.resources.fluentcore.collection.SupportsDeletingById;

public interface VaultsGenerated extends SupportsCreating<VaultGenerated.DefinitionStages.Blank>,
    SupportsDeletingById,
    SupportsListingByResourceGroup<VaultGenerated>,
    SupportsGettingByResourceGroup<VaultGenerated>,
    SupportsGettingById<VaultGenerated>,
    SupportsDeletingByResourceGroup,
    HasManager<KeyVaultManager> {
}
