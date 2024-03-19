// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.resourcemanager.network.models;

/**
 * @author xiaofeicao
 * @createdAt 2024-03-19 2:40 PM
 */
public interface PublicIpAddressWithDeleteOptions<ReturnT> {
    ReturnT withNewPublicIpAddress(DeleteOptions deleteOptions);
}
