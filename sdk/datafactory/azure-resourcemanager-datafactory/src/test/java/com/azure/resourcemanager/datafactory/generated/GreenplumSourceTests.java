// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.datafactory.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.datafactory.models.GreenplumSource;

public final class GreenplumSourceTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        GreenplumSource model = BinaryData.fromString(
            "{\"type\":\"yl\",\"query\":\"datawctjhdbi\",\"queryTimeout\":\"datatfaekpxv\",\"additionalColumns\":\"datadrcmtsorwta\",\"sourceRetryCount\":\"datarfvoskwujhskx\",\"sourceRetryWait\":\"datak\",\"maxConcurrentConnections\":\"datasa\",\"disableMetricsCollection\":\"dataf\",\"\":{\"qicsfaqypj\":\"dataaxgtwpzqti\"}}")
            .toObject(GreenplumSource.class);
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        GreenplumSource model = new GreenplumSource().withSourceRetryCount("datarfvoskwujhskx")
            .withSourceRetryWait("datak")
            .withMaxConcurrentConnections("datasa")
            .withDisableMetricsCollection("dataf")
            .withQueryTimeout("datatfaekpxv")
            .withAdditionalColumns("datadrcmtsorwta")
            .withQuery("datawctjhdbi");
        model = BinaryData.fromObject(model).toObject(GreenplumSource.class);
    }
}
