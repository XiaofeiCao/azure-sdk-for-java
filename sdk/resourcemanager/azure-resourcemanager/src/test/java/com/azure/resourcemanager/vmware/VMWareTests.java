// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.resourcemanager.vmware;

import com.azure.core.credential.TokenCredential;
import com.azure.core.http.HttpClient;
import com.azure.core.http.HttpPipeline;
import com.azure.core.http.policy.HttpLogOptions;
import com.azure.core.http.policy.HttpPipelinePolicy;
import com.azure.core.http.policy.RetryPolicy;
import com.azure.core.management.Region;
import com.azure.core.management.profile.AzureProfile;
import com.azure.core.util.logging.ClientLogger;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.AzureResourceManagerTests;
import com.azure.resourcemanager.resources.fluentcore.model.Accepted;
import com.azure.resourcemanager.resources.fluentcore.utils.HttpPipelineProvider;
import com.azure.resourcemanager.resources.fluentcore.utils.ResourceManagerUtils;
import com.azure.resourcemanager.resources.models.Deployment;
import com.azure.resourcemanager.resources.models.DeploymentMode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class VMWareTests extends AzureResourceManagerTests {
    private static final ClientLogger LOGGER = new ClientLogger(VMWareTests.class);
    @Test
    public void testVmware() throws IOException {
        String templateJson = new String(readAllBytes(AzureResourceManager.class.getResourceAsStream("/vm-template.json")), Charset.forName("utf-8"));
        Assertions.assertNotNull(templateJson);
        AtomicInteger round = new AtomicInteger();
        AtomicInteger counter = new AtomicInteger();
        AtomicInteger failures = new AtomicInteger();
        LOGGER.info("round: {}, request count: {}, failures: {}", round.get(), counter.get(), failures.get());
        while (true) {
            try {
                int concurrency = 4;
                CountDownLatch ctl = new CountDownLatch(concurrency);

                for (int i = 0; i < concurrency; i++) {
                    counter.incrementAndGet();
                    Accepted<Deployment> accepted;
                    String resourceGroupName = generateRandomResourceName("vmware", 15);
                    try {
                        String deploymentName = generateRandomResourceName("dp", 15);
                        accepted = azureResourceManager
                            .deployments()
                            .define(deploymentName)
                            .withNewResourceGroup(resourceGroupName, Region.US_WEST)
                            .withTemplate(templateJson)
                            .withParameters(new HashMap<String, Object>() {{
                                setParameter(this, "adminUsername", "azureUser");
                                setParameter(this, "adminPassword", generateRandomResourceName("Pa5$", 15));
                            }}).withMode(DeploymentMode.INCREMENTAL)
                            .beginCreate();
                    } catch (Exception e) {
                        LOGGER.error("", e);
                        failures.incrementAndGet();
                        ctl.countDown();
                        continue;
                    }

                    new Thread(() -> {
                        try {
                            Deployment deployment = accepted.getFinalResult();
                        } catch (Exception e) {
                            LOGGER.error("", e);
                            failures.incrementAndGet();
                        } finally {
                            ctl.countDown();
                            azureResourceManager.resourceGroups().beginDeleteByName(resourceGroupName);
                        }
                    }, "Thread-" + counter.get()).start();
                }
                try {
                    ctl.await();
                } catch (InterruptedException e) {
                    LOGGER.error("", e);
                }
                ResourceManagerUtils.sleep(Duration.ofSeconds(10));
            } catch (Exception e) {
                LOGGER.error("", e);
            } finally {
                round.incrementAndGet();
            }
        }
    }
    protected byte[] readAllBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int readValue;
        byte[] data = new byte[0xFFFF];
        while ((readValue = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, readValue);
        }
        return buffer.toByteArray();
    }
    private static void setParameter(Map<String, Object> parameters, final String name, final Object value) {
        parameters.put(name, new HashMap<String, Object>(){{this.put("value", value);}});
    }
    @Override
    protected HttpPipeline buildHttpPipeline(
        TokenCredential credential,
        AzureProfile profile,
        HttpLogOptions httpLogOptions,
        List<HttpPipelinePolicy> policies,
        HttpClient httpClient) {
        policies.add(new DynamicThrottlePolicy(clientIdFromFile()));
        return HttpPipelineProvider.buildHttpPipeline(
            credential,
            profile,
            null,
            httpLogOptions,
            null,
            new RetryPolicy("Retry-After", ChronoUnit.SECONDS),
            policies,
            httpClient);
    }
}
