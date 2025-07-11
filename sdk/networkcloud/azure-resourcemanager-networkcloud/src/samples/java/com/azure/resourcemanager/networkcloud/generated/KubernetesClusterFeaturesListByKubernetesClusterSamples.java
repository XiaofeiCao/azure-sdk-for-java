// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.networkcloud.generated;

/**
 * Samples for KubernetesClusterFeatures ListByKubernetesCluster.
 */
public final class KubernetesClusterFeaturesListByKubernetesClusterSamples {
    /*
     * x-ms-original-file:
     * specification/networkcloud/resource-manager/Microsoft.NetworkCloud/stable/2025-02-01/examples/
     * KubernetesClusterFeatures_ListByKubernetesCluster.json
     */
    /**
     * Sample code: List features for the Kubernetes cluster.
     * 
     * @param manager Entry point to NetworkCloudManager.
     */
    public static void
        listFeaturesForTheKubernetesCluster(com.azure.resourcemanager.networkcloud.NetworkCloudManager manager) {
        manager.kubernetesClusterFeatures()
            .listByKubernetesCluster("resourceGroupName", "kubernetesClusterName", com.azure.core.util.Context.NONE);
    }
}
