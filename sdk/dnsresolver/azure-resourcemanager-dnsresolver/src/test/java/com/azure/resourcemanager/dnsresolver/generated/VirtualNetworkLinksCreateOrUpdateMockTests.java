// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.dnsresolver.generated;

import com.azure.core.credential.AccessToken;
import com.azure.core.http.HttpClient;
import com.azure.core.management.SubResource;
import com.azure.core.management.profile.AzureProfile;
import com.azure.core.models.AzureCloud;
import com.azure.core.test.http.MockHttpResponse;
import com.azure.resourcemanager.dnsresolver.DnsResolverManager;
import com.azure.resourcemanager.dnsresolver.models.VirtualNetworkLink;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

public final class VirtualNetworkLinksCreateOrUpdateMockTests {
    @Test
    public void testCreateOrUpdate() throws Exception {
        String responseStr
            = "{\"etag\":\"qcaaewdaomdjvl\",\"properties\":{\"virtualNetwork\":{\"id\":\"x\"},\"metadata\":{\"geivsiykzkdncj\":\"brm\"},\"provisioningState\":\"Succeeded\"},\"id\":\"bzo\",\"name\":\"gculap\",\"type\":\"wyrpgogtqxepnyl\"}";

        HttpClient httpClient
            = response -> Mono.just(new MockHttpResponse(response, 200, responseStr.getBytes(StandardCharsets.UTF_8)));
        DnsResolverManager manager = DnsResolverManager.configure()
            .withHttpClient(httpClient)
            .authenticate(tokenRequestContext -> Mono.just(new AccessToken("this_is_a_token", OffsetDateTime.MAX)),
                new AzureProfile("", "", AzureCloud.AZURE_PUBLIC_CLOUD));

        VirtualNetworkLink response = manager.virtualNetworkLinks()
            .define("iogsjkmnwq")
            .withExistingDnsForwardingRuleset("wjplma", "stcyohpfkyrkdbd")
            .withVirtualNetwork(new SubResource().withId("m"))
            .withMetadata(mapOf("alxlllchp", "pmvmemfnczdwvv", "evwrdnhfuk", "db", "fcvlerch", "vsjcswsmystuluqy"))
            .withIfMatch("g")
            .withIfNoneMatch("ddrihpf")
            .create();

        Assertions.assertEquals("x", response.virtualNetwork().id());
        Assertions.assertEquals("brm", response.metadata().get("geivsiykzkdncj"));
    }

    // Use "Map.of" if available
    @SuppressWarnings("unchecked")
    private static <T> Map<String, T> mapOf(Object... inputs) {
        Map<String, T> map = new HashMap<>();
        for (int i = 0; i < inputs.length; i += 2) {
            String key = (String) inputs[i];
            T value = (T) inputs[i + 1];
            map.put(key, value);
        }
        return map;
    }
}
