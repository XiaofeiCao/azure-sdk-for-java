// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.networkcloud.models;

import com.azure.core.http.rest.PagedIterable;
import com.azure.core.http.rest.Response;
import com.azure.core.util.Context;

/**
 * Resource collection API of TrunkedNetworks.
 */
public interface TrunkedNetworks {
    /**
     * List trunked networks in the subscription.
     * 
     * Get a list of trunked networks in the provided subscription.
     * 
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return a list of trunked networks in the provided subscription as paginated response with {@link PagedIterable}.
     */
    PagedIterable<TrunkedNetwork> list();

    /**
     * List trunked networks in the subscription.
     * 
     * Get a list of trunked networks in the provided subscription.
     * 
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return a list of trunked networks in the provided subscription as paginated response with {@link PagedIterable}.
     */
    PagedIterable<TrunkedNetwork> list(Context context);

    /**
     * List trunked networks in the resource group.
     * 
     * Get a list of trunked networks in the provided resource group.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return a list of trunked networks in the provided resource group as paginated response with
     * {@link PagedIterable}.
     */
    PagedIterable<TrunkedNetwork> listByResourceGroup(String resourceGroupName);

    /**
     * List trunked networks in the resource group.
     * 
     * Get a list of trunked networks in the provided resource group.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return a list of trunked networks in the provided resource group as paginated response with
     * {@link PagedIterable}.
     */
    PagedIterable<TrunkedNetwork> listByResourceGroup(String resourceGroupName, Context context);

    /**
     * Retrieve the trunked network.
     * 
     * Get properties of the provided trunked network.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param trunkedNetworkName The name of the trunked network.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return properties of the provided trunked network along with {@link Response}.
     */
    Response<TrunkedNetwork> getByResourceGroupWithResponse(String resourceGroupName, String trunkedNetworkName,
        Context context);

    /**
     * Retrieve the trunked network.
     * 
     * Get properties of the provided trunked network.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param trunkedNetworkName The name of the trunked network.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return properties of the provided trunked network.
     */
    TrunkedNetwork getByResourceGroup(String resourceGroupName, String trunkedNetworkName);

    /**
     * Delete the trunked network.
     * 
     * Delete the provided trunked network.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param trunkedNetworkName The name of the trunked network.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the current status of an async operation.
     */
    OperationStatusResult deleteByResourceGroup(String resourceGroupName, String trunkedNetworkName);

    /**
     * Delete the trunked network.
     * 
     * Delete the provided trunked network.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param trunkedNetworkName The name of the trunked network.
     * @param ifMatch The ETag of the transformation. Omit this value to always overwrite the current resource. Specify
     * the last-seen ETag value to prevent accidentally overwriting concurrent changes.
     * @param ifNoneMatch Set to '*' to allow a new record set to be created, but to prevent updating an existing
     * resource. Other values will result in error from server as they are not supported.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the current status of an async operation.
     */
    OperationStatusResult delete(String resourceGroupName, String trunkedNetworkName, String ifMatch,
        String ifNoneMatch, Context context);

    /**
     * Retrieve the trunked network.
     * 
     * Get properties of the provided trunked network.
     * 
     * @param id the resource ID.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return properties of the provided trunked network along with {@link Response}.
     */
    TrunkedNetwork getById(String id);

    /**
     * Retrieve the trunked network.
     * 
     * Get properties of the provided trunked network.
     * 
     * @param id the resource ID.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return properties of the provided trunked network along with {@link Response}.
     */
    Response<TrunkedNetwork> getByIdWithResponse(String id, Context context);

    /**
     * Delete the trunked network.
     * 
     * Delete the provided trunked network.
     * 
     * @param id the resource ID.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the current status of an async operation.
     */
    OperationStatusResult deleteById(String id);

    /**
     * Delete the trunked network.
     * 
     * Delete the provided trunked network.
     * 
     * @param id the resource ID.
     * @param ifMatch The ETag of the transformation. Omit this value to always overwrite the current resource. Specify
     * the last-seen ETag value to prevent accidentally overwriting concurrent changes.
     * @param ifNoneMatch Set to '*' to allow a new record set to be created, but to prevent updating an existing
     * resource. Other values will result in error from server as they are not supported.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the current status of an async operation.
     */
    OperationStatusResult deleteByIdWithResponse(String id, String ifMatch, String ifNoneMatch, Context context);

    /**
     * Begins definition for a new TrunkedNetwork resource.
     * 
     * @param name resource name.
     * @return the first stage of the new TrunkedNetwork definition.
     */
    TrunkedNetwork.DefinitionStages.Blank define(String name);
}
