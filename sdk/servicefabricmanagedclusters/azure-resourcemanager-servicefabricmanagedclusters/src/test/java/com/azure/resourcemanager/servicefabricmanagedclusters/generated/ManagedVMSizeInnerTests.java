// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) TypeSpec Code Generator.

package com.azure.resourcemanager.servicefabricmanagedclusters.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.servicefabricmanagedclusters.fluent.models.ManagedVMSizeInner;

public final class ManagedVMSizeInnerTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        ManagedVMSizeInner model = BinaryData
            .fromString(
                "{\"properties\":{\"size\":\"lvpnpp\"},\"id\":\"flrwd\",\"name\":\"dlxyjrxs\",\"type\":\"afcnih\"}")
            .toObject(ManagedVMSizeInner.class);
    }
}
