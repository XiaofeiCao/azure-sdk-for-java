// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.security.fluent;

import com.azure.core.annotation.ReturnType;
import com.azure.core.annotation.ServiceMethod;
import com.azure.core.http.rest.PagedIterable;
import com.azure.core.http.rest.Response;
import com.azure.core.util.Context;
import com.azure.resourcemanager.security.fluent.models.GitLabGroupInner;
import com.azure.resourcemanager.security.fluent.models.GitLabGroupListResponseInner;

/**
 * An instance of this class provides access to all the operations defined in GitLabGroupsClient.
 */
public interface GitLabGroupsClient {
    /**
     * Returns a list of all GitLab groups accessible by the user token consumed by the connector.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param securityConnectorName The security connector name.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return list of RP resources which supports pagination along with {@link Response}.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    Response<GitLabGroupListResponseInner> listAvailableWithResponse(String resourceGroupName,
        String securityConnectorName, Context context);

    /**
     * Returns a list of all GitLab groups accessible by the user token consumed by the connector.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param securityConnectorName The security connector name.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return list of RP resources which supports pagination.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    GitLabGroupListResponseInner listAvailable(String resourceGroupName, String securityConnectorName);

    /**
     * Returns a list of GitLab groups onboarded to the connector.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param securityConnectorName The security connector name.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return list of RP resources which supports pagination as paginated response with {@link PagedIterable}.
     */
    @ServiceMethod(returns = ReturnType.COLLECTION)
    PagedIterable<GitLabGroupInner> list(String resourceGroupName, String securityConnectorName);

    /**
     * Returns a list of GitLab groups onboarded to the connector.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param securityConnectorName The security connector name.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return list of RP resources which supports pagination as paginated response with {@link PagedIterable}.
     */
    @ServiceMethod(returns = ReturnType.COLLECTION)
    PagedIterable<GitLabGroupInner> list(String resourceGroupName, String securityConnectorName, Context context);

    /**
     * Returns a monitored GitLab Group resource for a given fully-qualified name.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param securityConnectorName The security connector name.
     * @param groupFQName The GitLab group fully-qualified name.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return gitLab Group resource along with {@link Response}.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    Response<GitLabGroupInner> getWithResponse(String resourceGroupName, String securityConnectorName,
        String groupFQName, Context context);

    /**
     * Returns a monitored GitLab Group resource for a given fully-qualified name.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param securityConnectorName The security connector name.
     * @param groupFQName The GitLab group fully-qualified name.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return gitLab Group resource.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    GitLabGroupInner get(String resourceGroupName, String securityConnectorName, String groupFQName);
}
