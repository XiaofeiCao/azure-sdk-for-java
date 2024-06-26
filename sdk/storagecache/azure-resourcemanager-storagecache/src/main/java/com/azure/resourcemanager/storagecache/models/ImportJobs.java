// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.storagecache.models;

import com.azure.core.http.rest.PagedIterable;
import com.azure.core.http.rest.Response;
import com.azure.core.util.Context;

/**
 * Resource collection API of ImportJobs.
 */
public interface ImportJobs {
    /**
     * Schedules an import job for deletion.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param amlFilesystemName Name for the AML file system. Allows alphanumerics, underscores, and hyphens. Start and
     * end with alphanumeric.
     * @param importJobName Name for the import job. Allows alphanumerics, underscores, and hyphens. Start and end with
     * alphanumeric.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     */
    void delete(String resourceGroupName, String amlFilesystemName, String importJobName);

    /**
     * Schedules an import job for deletion.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param amlFilesystemName Name for the AML file system. Allows alphanumerics, underscores, and hyphens. Start and
     * end with alphanumeric.
     * @param importJobName Name for the import job. Allows alphanumerics, underscores, and hyphens. Start and end with
     * alphanumeric.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     */
    void delete(String resourceGroupName, String amlFilesystemName, String importJobName, Context context);

    /**
     * Returns an import job.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param amlFilesystemName Name for the AML file system. Allows alphanumerics, underscores, and hyphens. Start and
     * end with alphanumeric.
     * @param importJobName Name for the import job. Allows alphanumerics, underscores, and hyphens. Start and end with
     * alphanumeric.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return an import job instance along with {@link Response}.
     */
    Response<ImportJob> getWithResponse(String resourceGroupName, String amlFilesystemName, String importJobName,
        Context context);

    /**
     * Returns an import job.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param amlFilesystemName Name for the AML file system. Allows alphanumerics, underscores, and hyphens. Start and
     * end with alphanumeric.
     * @param importJobName Name for the import job. Allows alphanumerics, underscores, and hyphens. Start and end with
     * alphanumeric.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return an import job instance.
     */
    ImportJob get(String resourceGroupName, String amlFilesystemName, String importJobName);

    /**
     * Returns all import jobs the user has access to under an AML File System.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param amlFilesystemName Name for the AML file system. Allows alphanumerics, underscores, and hyphens. Start and
     * end with alphanumeric.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return result of the request to list import jobs as paginated response with {@link PagedIterable}.
     */
    PagedIterable<ImportJob> listByAmlFilesystem(String resourceGroupName, String amlFilesystemName);

    /**
     * Returns all import jobs the user has access to under an AML File System.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param amlFilesystemName Name for the AML file system. Allows alphanumerics, underscores, and hyphens. Start and
     * end with alphanumeric.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return result of the request to list import jobs as paginated response with {@link PagedIterable}.
     */
    PagedIterable<ImportJob> listByAmlFilesystem(String resourceGroupName, String amlFilesystemName, Context context);

    /**
     * Returns an import job.
     * 
     * @param id the resource ID.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return an import job instance along with {@link Response}.
     */
    ImportJob getById(String id);

    /**
     * Returns an import job.
     * 
     * @param id the resource ID.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return an import job instance along with {@link Response}.
     */
    Response<ImportJob> getByIdWithResponse(String id, Context context);

    /**
     * Schedules an import job for deletion.
     * 
     * @param id the resource ID.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     */
    void deleteById(String id);

    /**
     * Schedules an import job for deletion.
     * 
     * @param id the resource ID.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     */
    void deleteByIdWithResponse(String id, Context context);

    /**
     * Begins definition for a new ImportJob resource.
     * 
     * @param name resource name.
     * @return the first stage of the new ImportJob definition.
     */
    ImportJob.DefinitionStages.Blank define(String name);
}
