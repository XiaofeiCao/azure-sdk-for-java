// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.appcontainers.models;

import com.azure.core.management.Region;
import com.azure.core.management.SystemData;
import com.azure.core.util.Context;
import com.azure.resourcemanager.appcontainers.fluent.models.BuilderResourceInner;
import java.util.List;
import java.util.Map;

/**
 * An immutable client-side representation of BuilderResource.
 */
public interface BuilderResource {
    /**
     * Gets the id property: Fully qualified resource Id for the resource.
     * 
     * @return the id value.
     */
    String id();

    /**
     * Gets the name property: The name of the resource.
     * 
     * @return the name value.
     */
    String name();

    /**
     * Gets the type property: The type of the resource.
     * 
     * @return the type value.
     */
    String type();

    /**
     * Gets the location property: The geo-location where the resource lives.
     * 
     * @return the location value.
     */
    String location();

    /**
     * Gets the tags property: Resource tags.
     * 
     * @return the tags value.
     */
    Map<String, String> tags();

    /**
     * Gets the identity property: The managed service identities assigned to this resource.
     * 
     * @return the identity value.
     */
    ManagedServiceIdentity identity();

    /**
     * Gets the systemData property: Azure Resource Manager metadata containing createdBy and modifiedBy information.
     * 
     * @return the systemData value.
     */
    SystemData systemData();

    /**
     * Gets the provisioningState property: Provisioning state of a builder resource.
     * 
     * @return the provisioningState value.
     */
    BuilderProvisioningState provisioningState();

    /**
     * Gets the environmentId property: Resource ID of the container apps environment that the builder is associated
     * with.
     * 
     * @return the environmentId value.
     */
    String environmentId();

    /**
     * Gets the containerRegistries property: List of mappings of container registries and the managed identity used to
     * connect to it.
     * 
     * @return the containerRegistries value.
     */
    List<ContainerRegistry> containerRegistries();

    /**
     * Gets the region of the resource.
     * 
     * @return the region of the resource.
     */
    Region region();

    /**
     * Gets the name of the resource region.
     * 
     * @return the name of the resource region.
     */
    String regionName();

    /**
     * Gets the name of the resource group.
     * 
     * @return the name of the resource group.
     */
    String resourceGroupName();

    /**
     * Gets the inner com.azure.resourcemanager.appcontainers.fluent.models.BuilderResourceInner object.
     * 
     * @return the inner object.
     */
    BuilderResourceInner innerModel();

    /**
     * The entirety of the BuilderResource definition.
     */
    interface Definition extends DefinitionStages.Blank, DefinitionStages.WithLocation,
        DefinitionStages.WithResourceGroup, DefinitionStages.WithCreate {
    }

    /**
     * The BuilderResource definition stages.
     */
    interface DefinitionStages {
        /**
         * The first stage of the BuilderResource definition.
         */
        interface Blank extends WithLocation {
        }

        /**
         * The stage of the BuilderResource definition allowing to specify location.
         */
        interface WithLocation {
            /**
             * Specifies the region for the resource.
             * 
             * @param location The geo-location where the resource lives.
             * @return the next definition stage.
             */
            WithResourceGroup withRegion(Region location);

            /**
             * Specifies the region for the resource.
             * 
             * @param location The geo-location where the resource lives.
             * @return the next definition stage.
             */
            WithResourceGroup withRegion(String location);
        }

        /**
         * The stage of the BuilderResource definition allowing to specify parent resource.
         */
        interface WithResourceGroup {
            /**
             * Specifies resourceGroupName.
             * 
             * @param resourceGroupName The name of the resource group. The name is case insensitive.
             * @return the next definition stage.
             */
            WithCreate withExistingResourceGroup(String resourceGroupName);
        }

        /**
         * The stage of the BuilderResource definition which contains all the minimum required properties for the
         * resource to be created, but also allows for any other optional properties to be specified.
         */
        interface WithCreate extends DefinitionStages.WithTags, DefinitionStages.WithIdentity,
            DefinitionStages.WithEnvironmentId, DefinitionStages.WithContainerRegistries {
            /**
             * Executes the create request.
             * 
             * @return the created resource.
             */
            BuilderResource create();

            /**
             * Executes the create request.
             * 
             * @param context The context to associate with this operation.
             * @return the created resource.
             */
            BuilderResource create(Context context);
        }

        /**
         * The stage of the BuilderResource definition allowing to specify tags.
         */
        interface WithTags {
            /**
             * Specifies the tags property: Resource tags..
             * 
             * @param tags Resource tags.
             * @return the next definition stage.
             */
            WithCreate withTags(Map<String, String> tags);
        }

        /**
         * The stage of the BuilderResource definition allowing to specify identity.
         */
        interface WithIdentity {
            /**
             * Specifies the identity property: The managed service identities assigned to this resource..
             * 
             * @param identity The managed service identities assigned to this resource.
             * @return the next definition stage.
             */
            WithCreate withIdentity(ManagedServiceIdentity identity);
        }

        /**
         * The stage of the BuilderResource definition allowing to specify environmentId.
         */
        interface WithEnvironmentId {
            /**
             * Specifies the environmentId property: Resource ID of the container apps environment that the builder is
             * associated with..
             * 
             * @param environmentId Resource ID of the container apps environment that the builder is associated with.
             * @return the next definition stage.
             */
            WithCreate withEnvironmentId(String environmentId);
        }

        /**
         * The stage of the BuilderResource definition allowing to specify containerRegistries.
         */
        interface WithContainerRegistries {
            /**
             * Specifies the containerRegistries property: List of mappings of container registries and the managed
             * identity used to connect to it..
             * 
             * @param containerRegistries List of mappings of container registries and the managed identity used to
             * connect to it.
             * @return the next definition stage.
             */
            WithCreate withContainerRegistries(List<ContainerRegistry> containerRegistries);
        }
    }

    /**
     * Begins update for the BuilderResource resource.
     * 
     * @return the stage of resource update.
     */
    BuilderResource.Update update();

    /**
     * The template for BuilderResource update.
     */
    interface Update extends UpdateStages.WithTags, UpdateStages.WithIdentity, UpdateStages.WithEnvironmentId {
        /**
         * Executes the update request.
         * 
         * @return the updated resource.
         */
        BuilderResource apply();

        /**
         * Executes the update request.
         * 
         * @param context The context to associate with this operation.
         * @return the updated resource.
         */
        BuilderResource apply(Context context);
    }

    /**
     * The BuilderResource update stages.
     */
    interface UpdateStages {
        /**
         * The stage of the BuilderResource update allowing to specify tags.
         */
        interface WithTags {
            /**
             * Specifies the tags property: Resource tags..
             * 
             * @param tags Resource tags.
             * @return the next definition stage.
             */
            Update withTags(Map<String, String> tags);
        }

        /**
         * The stage of the BuilderResource update allowing to specify identity.
         */
        interface WithIdentity {
            /**
             * Specifies the identity property: The managed service identities assigned to this resource..
             * 
             * @param identity The managed service identities assigned to this resource.
             * @return the next definition stage.
             */
            Update withIdentity(ManagedServiceIdentity identity);
        }

        /**
         * The stage of the BuilderResource update allowing to specify environmentId.
         */
        interface WithEnvironmentId {
            /**
             * Specifies the environmentId property: Resource ID of the container apps environment that the builder is
             * associated with..
             * 
             * @param environmentId Resource ID of the container apps environment that the builder is associated with.
             * @return the next definition stage.
             */
            Update withEnvironmentId(String environmentId);
        }
    }

    /**
     * Refreshes the resource to sync with Azure.
     * 
     * @return the refreshed resource.
     */
    BuilderResource refresh();

    /**
     * Refreshes the resource to sync with Azure.
     * 
     * @param context The context to associate with this operation.
     * @return the refreshed resource.
     */
    BuilderResource refresh(Context context);
}
