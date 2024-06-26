// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.scvmm.fluent;

import com.azure.core.annotation.ReturnType;
import com.azure.core.annotation.ServiceMethod;
import com.azure.core.http.rest.PagedIterable;
import com.azure.core.http.rest.Response;
import com.azure.core.management.polling.PollResult;
import com.azure.core.util.Context;
import com.azure.core.util.polling.SyncPoller;
import com.azure.resourcemanager.scvmm.fluent.models.VirtualNetworkInner;
import com.azure.resourcemanager.scvmm.models.ResourcePatch;

/** An instance of this class provides access to all the operations defined in VirtualNetworksClient. */
public interface VirtualNetworksClient {
    /**
     * Implements VirtualNetwork GET method.
     *
     * @param resourceGroupName The name of the resource group.
     * @param virtualNetworkName Name of the VirtualNetwork.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the VirtualNetworks resource definition.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    VirtualNetworkInner getByResourceGroup(String resourceGroupName, String virtualNetworkName);

    /**
     * Implements VirtualNetwork GET method.
     *
     * @param resourceGroupName The name of the resource group.
     * @param virtualNetworkName Name of the VirtualNetwork.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the VirtualNetworks resource definition along with {@link Response}.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    Response<VirtualNetworkInner> getByResourceGroupWithResponse(
        String resourceGroupName, String virtualNetworkName, Context context);

    /**
     * Onboards the ScVmm virtual network as an Azure virtual network resource.
     *
     * @param resourceGroupName The name of the resource group.
     * @param virtualNetworkName Name of the VirtualNetwork.
     * @param body Request payload.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the {@link SyncPoller} for polling of the VirtualNetworks resource definition.
     */
    @ServiceMethod(returns = ReturnType.LONG_RUNNING_OPERATION)
    SyncPoller<PollResult<VirtualNetworkInner>, VirtualNetworkInner> beginCreateOrUpdate(
        String resourceGroupName, String virtualNetworkName, VirtualNetworkInner body);

    /**
     * Onboards the ScVmm virtual network as an Azure virtual network resource.
     *
     * @param resourceGroupName The name of the resource group.
     * @param virtualNetworkName Name of the VirtualNetwork.
     * @param body Request payload.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the {@link SyncPoller} for polling of the VirtualNetworks resource definition.
     */
    @ServiceMethod(returns = ReturnType.LONG_RUNNING_OPERATION)
    SyncPoller<PollResult<VirtualNetworkInner>, VirtualNetworkInner> beginCreateOrUpdate(
        String resourceGroupName, String virtualNetworkName, VirtualNetworkInner body, Context context);

    /**
     * Onboards the ScVmm virtual network as an Azure virtual network resource.
     *
     * @param resourceGroupName The name of the resource group.
     * @param virtualNetworkName Name of the VirtualNetwork.
     * @param body Request payload.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the VirtualNetworks resource definition.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    VirtualNetworkInner createOrUpdate(String resourceGroupName, String virtualNetworkName, VirtualNetworkInner body);

    /**
     * Onboards the ScVmm virtual network as an Azure virtual network resource.
     *
     * @param resourceGroupName The name of the resource group.
     * @param virtualNetworkName Name of the VirtualNetwork.
     * @param body Request payload.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the VirtualNetworks resource definition.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    VirtualNetworkInner createOrUpdate(
        String resourceGroupName, String virtualNetworkName, VirtualNetworkInner body, Context context);

    /**
     * Deregisters the ScVmm virtual network from Azure.
     *
     * @param resourceGroupName The name of the resource group.
     * @param virtualNetworkName Name of the VirtualNetwork.
     * @param force Forces the resource to be deleted from azure. The corresponding CR would be attempted to be deleted
     *     too.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the {@link SyncPoller} for polling of long-running operation.
     */
    @ServiceMethod(returns = ReturnType.LONG_RUNNING_OPERATION)
    SyncPoller<PollResult<Void>, Void> beginDelete(String resourceGroupName, String virtualNetworkName, Boolean force);

    /**
     * Deregisters the ScVmm virtual network from Azure.
     *
     * @param resourceGroupName The name of the resource group.
     * @param virtualNetworkName Name of the VirtualNetwork.
     * @param force Forces the resource to be deleted from azure. The corresponding CR would be attempted to be deleted
     *     too.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the {@link SyncPoller} for polling of long-running operation.
     */
    @ServiceMethod(returns = ReturnType.LONG_RUNNING_OPERATION)
    SyncPoller<PollResult<Void>, Void> beginDelete(
        String resourceGroupName, String virtualNetworkName, Boolean force, Context context);

    /**
     * Deregisters the ScVmm virtual network from Azure.
     *
     * @param resourceGroupName The name of the resource group.
     * @param virtualNetworkName Name of the VirtualNetwork.
     * @param force Forces the resource to be deleted from azure. The corresponding CR would be attempted to be deleted
     *     too.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    void delete(String resourceGroupName, String virtualNetworkName, Boolean force);

    /**
     * Deregisters the ScVmm virtual network from Azure.
     *
     * @param resourceGroupName The name of the resource group.
     * @param virtualNetworkName Name of the VirtualNetwork.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    void delete(String resourceGroupName, String virtualNetworkName);

    /**
     * Deregisters the ScVmm virtual network from Azure.
     *
     * @param resourceGroupName The name of the resource group.
     * @param virtualNetworkName Name of the VirtualNetwork.
     * @param force Forces the resource to be deleted from azure. The corresponding CR would be attempted to be deleted
     *     too.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    void delete(String resourceGroupName, String virtualNetworkName, Boolean force, Context context);

    /**
     * Updates the VirtualNetworks resource.
     *
     * @param resourceGroupName The name of the resource group.
     * @param virtualNetworkName Name of the VirtualNetwork.
     * @param body VirtualNetworks patch payload.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the {@link SyncPoller} for polling of the VirtualNetworks resource definition.
     */
    @ServiceMethod(returns = ReturnType.LONG_RUNNING_OPERATION)
    SyncPoller<PollResult<VirtualNetworkInner>, VirtualNetworkInner> beginUpdate(
        String resourceGroupName, String virtualNetworkName, ResourcePatch body);

    /**
     * Updates the VirtualNetworks resource.
     *
     * @param resourceGroupName The name of the resource group.
     * @param virtualNetworkName Name of the VirtualNetwork.
     * @param body VirtualNetworks patch payload.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the {@link SyncPoller} for polling of the VirtualNetworks resource definition.
     */
    @ServiceMethod(returns = ReturnType.LONG_RUNNING_OPERATION)
    SyncPoller<PollResult<VirtualNetworkInner>, VirtualNetworkInner> beginUpdate(
        String resourceGroupName, String virtualNetworkName, ResourcePatch body, Context context);

    /**
     * Updates the VirtualNetworks resource.
     *
     * @param resourceGroupName The name of the resource group.
     * @param virtualNetworkName Name of the VirtualNetwork.
     * @param body VirtualNetworks patch payload.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the VirtualNetworks resource definition.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    VirtualNetworkInner update(String resourceGroupName, String virtualNetworkName, ResourcePatch body);

    /**
     * Updates the VirtualNetworks resource.
     *
     * @param resourceGroupName The name of the resource group.
     * @param virtualNetworkName Name of the VirtualNetwork.
     * @param body VirtualNetworks patch payload.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the VirtualNetworks resource definition.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    VirtualNetworkInner update(
        String resourceGroupName, String virtualNetworkName, ResourcePatch body, Context context);

    /**
     * List of VirtualNetworks in a resource group.
     *
     * @param resourceGroupName The name of the resource group.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return list of VirtualNetworks as paginated response with {@link PagedIterable}.
     */
    @ServiceMethod(returns = ReturnType.COLLECTION)
    PagedIterable<VirtualNetworkInner> listByResourceGroup(String resourceGroupName);

    /**
     * List of VirtualNetworks in a resource group.
     *
     * @param resourceGroupName The name of the resource group.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return list of VirtualNetworks as paginated response with {@link PagedIterable}.
     */
    @ServiceMethod(returns = ReturnType.COLLECTION)
    PagedIterable<VirtualNetworkInner> listByResourceGroup(String resourceGroupName, Context context);

    /**
     * List of VirtualNetworks in a subscription.
     *
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return list of VirtualNetworks as paginated response with {@link PagedIterable}.
     */
    @ServiceMethod(returns = ReturnType.COLLECTION)
    PagedIterable<VirtualNetworkInner> list();

    /**
     * List of VirtualNetworks in a subscription.
     *
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return list of VirtualNetworks as paginated response with {@link PagedIterable}.
     */
    @ServiceMethod(returns = ReturnType.COLLECTION)
    PagedIterable<VirtualNetworkInner> list(Context context);
}
