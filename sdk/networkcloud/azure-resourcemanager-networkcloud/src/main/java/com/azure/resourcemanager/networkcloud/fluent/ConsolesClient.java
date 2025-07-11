// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.networkcloud.fluent;

import com.azure.core.annotation.ReturnType;
import com.azure.core.annotation.ServiceMethod;
import com.azure.core.http.rest.PagedIterable;
import com.azure.core.http.rest.Response;
import com.azure.core.management.polling.PollResult;
import com.azure.core.util.Context;
import com.azure.core.util.polling.SyncPoller;
import com.azure.resourcemanager.networkcloud.fluent.models.ConsoleInner;
import com.azure.resourcemanager.networkcloud.fluent.models.OperationStatusResultInner;
import com.azure.resourcemanager.networkcloud.models.ConsolePatchParameters;

/**
 * An instance of this class provides access to all the operations defined in ConsolesClient.
 */
public interface ConsolesClient {
    /**
     * List consoles of the virtual machine.
     * 
     * Get a list of consoles for the provided virtual machine.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param virtualMachineName The name of the virtual machine.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return a list of consoles for the provided virtual machine as paginated response with {@link PagedIterable}.
     */
    @ServiceMethod(returns = ReturnType.COLLECTION)
    PagedIterable<ConsoleInner> listByVirtualMachine(String resourceGroupName, String virtualMachineName);

    /**
     * List consoles of the virtual machine.
     * 
     * Get a list of consoles for the provided virtual machine.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param virtualMachineName The name of the virtual machine.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return a list of consoles for the provided virtual machine as paginated response with {@link PagedIterable}.
     */
    @ServiceMethod(returns = ReturnType.COLLECTION)
    PagedIterable<ConsoleInner> listByVirtualMachine(String resourceGroupName, String virtualMachineName,
        Context context);

    /**
     * Retrieve the virtual machine console.
     * 
     * Get properties of the provided virtual machine console.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param virtualMachineName The name of the virtual machine.
     * @param consoleName The name of the virtual machine console.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return properties of the provided virtual machine console along with {@link Response}.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    Response<ConsoleInner> getWithResponse(String resourceGroupName, String virtualMachineName, String consoleName,
        Context context);

    /**
     * Retrieve the virtual machine console.
     * 
     * Get properties of the provided virtual machine console.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param virtualMachineName The name of the virtual machine.
     * @param consoleName The name of the virtual machine console.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return properties of the provided virtual machine console.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    ConsoleInner get(String resourceGroupName, String virtualMachineName, String consoleName);

    /**
     * Create or update the virtual machine console.
     * 
     * Create a new virtual machine console or update the properties of the existing virtual machine console.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param virtualMachineName The name of the virtual machine.
     * @param consoleName The name of the virtual machine console.
     * @param consoleParameters The request body.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the {@link SyncPoller} for polling of console represents the console of an on-premises Network Cloud
     * virtual machine.
     */
    @ServiceMethod(returns = ReturnType.LONG_RUNNING_OPERATION)
    SyncPoller<PollResult<ConsoleInner>, ConsoleInner> beginCreateOrUpdate(String resourceGroupName,
        String virtualMachineName, String consoleName, ConsoleInner consoleParameters);

    /**
     * Create or update the virtual machine console.
     * 
     * Create a new virtual machine console or update the properties of the existing virtual machine console.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param virtualMachineName The name of the virtual machine.
     * @param consoleName The name of the virtual machine console.
     * @param consoleParameters The request body.
     * @param ifMatch The ETag of the transformation. Omit this value to always overwrite the current resource. Specify
     * the last-seen ETag value to prevent accidentally overwriting concurrent changes.
     * @param ifNoneMatch Set to '*' to allow a new record set to be created, but to prevent updating an existing
     * resource. Other values will result in error from server as they are not supported.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the {@link SyncPoller} for polling of console represents the console of an on-premises Network Cloud
     * virtual machine.
     */
    @ServiceMethod(returns = ReturnType.LONG_RUNNING_OPERATION)
    SyncPoller<PollResult<ConsoleInner>, ConsoleInner> beginCreateOrUpdate(String resourceGroupName,
        String virtualMachineName, String consoleName, ConsoleInner consoleParameters, String ifMatch,
        String ifNoneMatch, Context context);

    /**
     * Create or update the virtual machine console.
     * 
     * Create a new virtual machine console or update the properties of the existing virtual machine console.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param virtualMachineName The name of the virtual machine.
     * @param consoleName The name of the virtual machine console.
     * @param consoleParameters The request body.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return console represents the console of an on-premises Network Cloud virtual machine.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    ConsoleInner createOrUpdate(String resourceGroupName, String virtualMachineName, String consoleName,
        ConsoleInner consoleParameters);

    /**
     * Create or update the virtual machine console.
     * 
     * Create a new virtual machine console or update the properties of the existing virtual machine console.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param virtualMachineName The name of the virtual machine.
     * @param consoleName The name of the virtual machine console.
     * @param consoleParameters The request body.
     * @param ifMatch The ETag of the transformation. Omit this value to always overwrite the current resource. Specify
     * the last-seen ETag value to prevent accidentally overwriting concurrent changes.
     * @param ifNoneMatch Set to '*' to allow a new record set to be created, but to prevent updating an existing
     * resource. Other values will result in error from server as they are not supported.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return console represents the console of an on-premises Network Cloud virtual machine.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    ConsoleInner createOrUpdate(String resourceGroupName, String virtualMachineName, String consoleName,
        ConsoleInner consoleParameters, String ifMatch, String ifNoneMatch, Context context);

    /**
     * Delete the virtual machine console.
     * 
     * Delete the provided virtual machine console.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param virtualMachineName The name of the virtual machine.
     * @param consoleName The name of the virtual machine console.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the {@link SyncPoller} for polling of the current status of an async operation.
     */
    @ServiceMethod(returns = ReturnType.LONG_RUNNING_OPERATION)
    SyncPoller<PollResult<OperationStatusResultInner>, OperationStatusResultInner> beginDelete(String resourceGroupName,
        String virtualMachineName, String consoleName);

    /**
     * Delete the virtual machine console.
     * 
     * Delete the provided virtual machine console.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param virtualMachineName The name of the virtual machine.
     * @param consoleName The name of the virtual machine console.
     * @param ifMatch The ETag of the transformation. Omit this value to always overwrite the current resource. Specify
     * the last-seen ETag value to prevent accidentally overwriting concurrent changes.
     * @param ifNoneMatch Set to '*' to allow a new record set to be created, but to prevent updating an existing
     * resource. Other values will result in error from server as they are not supported.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the {@link SyncPoller} for polling of the current status of an async operation.
     */
    @ServiceMethod(returns = ReturnType.LONG_RUNNING_OPERATION)
    SyncPoller<PollResult<OperationStatusResultInner>, OperationStatusResultInner> beginDelete(String resourceGroupName,
        String virtualMachineName, String consoleName, String ifMatch, String ifNoneMatch, Context context);

    /**
     * Delete the virtual machine console.
     * 
     * Delete the provided virtual machine console.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param virtualMachineName The name of the virtual machine.
     * @param consoleName The name of the virtual machine console.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the current status of an async operation.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    OperationStatusResultInner delete(String resourceGroupName, String virtualMachineName, String consoleName);

    /**
     * Delete the virtual machine console.
     * 
     * Delete the provided virtual machine console.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param virtualMachineName The name of the virtual machine.
     * @param consoleName The name of the virtual machine console.
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
    @ServiceMethod(returns = ReturnType.SINGLE)
    OperationStatusResultInner delete(String resourceGroupName, String virtualMachineName, String consoleName,
        String ifMatch, String ifNoneMatch, Context context);

    /**
     * Patch the virtual machine console.
     * 
     * Patch the properties of the provided virtual machine console, or update the tags associated with the virtual
     * machine console. Properties and tag updates can be done independently.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param virtualMachineName The name of the virtual machine.
     * @param consoleName The name of the virtual machine console.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the {@link SyncPoller} for polling of console represents the console of an on-premises Network Cloud
     * virtual machine.
     */
    @ServiceMethod(returns = ReturnType.LONG_RUNNING_OPERATION)
    SyncPoller<PollResult<ConsoleInner>, ConsoleInner> beginUpdate(String resourceGroupName, String virtualMachineName,
        String consoleName);

    /**
     * Patch the virtual machine console.
     * 
     * Patch the properties of the provided virtual machine console, or update the tags associated with the virtual
     * machine console. Properties and tag updates can be done independently.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param virtualMachineName The name of the virtual machine.
     * @param consoleName The name of the virtual machine console.
     * @param ifMatch The ETag of the transformation. Omit this value to always overwrite the current resource. Specify
     * the last-seen ETag value to prevent accidentally overwriting concurrent changes.
     * @param ifNoneMatch Set to '*' to allow a new record set to be created, but to prevent updating an existing
     * resource. Other values will result in error from server as they are not supported.
     * @param consoleUpdateParameters The request body.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the {@link SyncPoller} for polling of console represents the console of an on-premises Network Cloud
     * virtual machine.
     */
    @ServiceMethod(returns = ReturnType.LONG_RUNNING_OPERATION)
    SyncPoller<PollResult<ConsoleInner>, ConsoleInner> beginUpdate(String resourceGroupName, String virtualMachineName,
        String consoleName, String ifMatch, String ifNoneMatch, ConsolePatchParameters consoleUpdateParameters,
        Context context);

    /**
     * Patch the virtual machine console.
     * 
     * Patch the properties of the provided virtual machine console, or update the tags associated with the virtual
     * machine console. Properties and tag updates can be done independently.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param virtualMachineName The name of the virtual machine.
     * @param consoleName The name of the virtual machine console.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return console represents the console of an on-premises Network Cloud virtual machine.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    ConsoleInner update(String resourceGroupName, String virtualMachineName, String consoleName);

    /**
     * Patch the virtual machine console.
     * 
     * Patch the properties of the provided virtual machine console, or update the tags associated with the virtual
     * machine console. Properties and tag updates can be done independently.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param virtualMachineName The name of the virtual machine.
     * @param consoleName The name of the virtual machine console.
     * @param ifMatch The ETag of the transformation. Omit this value to always overwrite the current resource. Specify
     * the last-seen ETag value to prevent accidentally overwriting concurrent changes.
     * @param ifNoneMatch Set to '*' to allow a new record set to be created, but to prevent updating an existing
     * resource. Other values will result in error from server as they are not supported.
     * @param consoleUpdateParameters The request body.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return console represents the console of an on-premises Network Cloud virtual machine.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    ConsoleInner update(String resourceGroupName, String virtualMachineName, String consoleName, String ifMatch,
        String ifNoneMatch, ConsolePatchParameters consoleUpdateParameters, Context context);
}
