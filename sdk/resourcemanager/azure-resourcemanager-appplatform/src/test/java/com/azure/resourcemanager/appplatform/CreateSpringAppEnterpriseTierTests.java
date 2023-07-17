// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.resourcemanager.appplatform;

import com.azure.core.management.Region;
import com.azure.resourcemanager.appplatform.fluent.models.AppResourceInner;
import com.azure.resourcemanager.appplatform.fluent.models.BuildInner;
import com.azure.resourcemanager.appplatform.fluent.models.BuildResultInner;
import com.azure.resourcemanager.appplatform.fluent.models.BuildServiceAgentPoolResourceInner;
import com.azure.resourcemanager.appplatform.fluent.models.DeploymentResourceInner;
import com.azure.resourcemanager.appplatform.fluent.models.ServiceResourceInner;
import com.azure.resourcemanager.appplatform.models.AppResourceProperties;
import com.azure.resourcemanager.appplatform.models.BuildProperties;
import com.azure.resourcemanager.appplatform.models.BuildResultProvisioningState;
import com.azure.resourcemanager.appplatform.models.BuildResultUserSourceInfo;
import com.azure.resourcemanager.appplatform.models.BuildServiceAgentPoolProperties;
import com.azure.resourcemanager.appplatform.models.BuildServiceAgentPoolSizeProperties;
import com.azure.resourcemanager.appplatform.models.DeploymentResourceProperties;
import com.azure.resourcemanager.appplatform.models.DeploymentSettings;
import com.azure.resourcemanager.appplatform.models.ResourceRequests;
import com.azure.resourcemanager.appplatform.models.ResourceUploadDefinition;
import com.azure.resourcemanager.appplatform.models.Sku;
import com.azure.resourcemanager.resources.fluentcore.arm.ResourceUtils;
import com.azure.resourcemanager.resources.fluentcore.utils.ResourceManagerUtils;
import com.azure.storage.file.share.ShareFileAsyncClient;
import com.azure.storage.file.share.ShareFileClientBuilder;
import org.apache.commons.compress.utils.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;

/**
 * This example demonstrates how to deploy Spring App enterprise tier
 * - provision spring service
 * - provision spring app
 * - create default active app deployment
 * - upload file
 * - build file and wait
 * - deploy app
 */
public class CreateSpringAppEnterpriseTierTests extends AppPlatformTest {

    private static final String PETCLINIC_GATEWAY_JAR_URL = "https://github.com/weidongxu-microsoft/azure-sdk-for-java-management-tests/tree/master/spring-cloud/api-gateway.jar";

    @Test
    public void springAppSampleTest() throws Exception {
        String serviceName = generateRandomResourceName("asa", 15);
        String appName = generateRandomResourceName("app", 15);
        String deploymentName = generateRandomResourceName("deployment", 15);

        Region region = Region.US_EAST;

        appPlatformManager.resourceManager().resourceGroups().define(rgName).withRegion(region).create();
        // --------------------------------------
        // provision spring service
        ServiceResourceInner serviceResourceInner = new ServiceResourceInner()
            .withLocation(region.toString())
            .withSku(new Sku().withName("E0"));
        serviceResourceInner = appPlatformManager.serviceClient().getServices().createOrUpdate(rgName, serviceName, serviceResourceInner);
        // initialize build service agent pool
        appPlatformManager.serviceClient().getBuildServiceAgentPools()
            .updatePut(
                rgName,
                serviceName,
                "default",
                "default",
                new BuildServiceAgentPoolResourceInner()
                    .withProperties(
                        new BuildServiceAgentPoolProperties()
                            .withPoolSize(new BuildServiceAgentPoolSizeProperties().withName("S1")) // S1, S2, S3, S4, S5
                    )
            );

        // --------------------------------------
        // provision spring app
        AppResourceInner appResourceInner = new AppResourceInner();
        appResourceInner.withProperties(new AppResourceProperties());
        appPlatformManager.serviceClient().getApps().createOrUpdate(rgName, serviceName, appName, appResourceInner);

        // create default active deployment
        DeploymentResourceInner resourceInner = new DeploymentResourceInner()
            .withSku(
                new Sku()
                    .withName(serviceResourceInner.sku().name())
                    // instance count
                    .withCapacity(1))
            .withProperties(
                new DeploymentResourceProperties()
                    .withActive(true)
                    .withDeploymentSettings(
                        new DeploymentSettings()
                            .withResourceRequests(new ResourceRequests().withCpu("1").withMemory("2Gi")))
                    .withSource(new BuildResultUserSourceInfo().withBuildResultId("<default>")));

        resourceInner = appPlatformManager.serviceClient().getDeployments().createOrUpdate(rgName, serviceName, appName, deploymentName, resourceInner);

        // get upload url
        ResourceUploadDefinition uploadUrl = appPlatformManager.serviceClient().getApps().getResourceUploadUrlAsync(
            rgName, serviceName, appName).block();

        // --------------------------------------
        // upload file
        ShareFileAsyncClient fileClient = new ShareFileClientBuilder()
            .endpoint(uploadUrl.uploadUrl())
            .httpClient(appPlatformManager.httpPipeline().getHttpClient())
            .buildFileAsyncClient();
        File jarFile = downloadFile(PETCLINIC_GATEWAY_JAR_URL);

        fileClient.create(jarFile.length())
            .flatMap(fileInfo -> fileClient.uploadFromFile(jarFile.getAbsolutePath()))
            .block();

        // --------------------------------------
        // build file with jar
        BuildProperties buildProperties = new BuildProperties()
            .withBuilder(String.format("%s/buildservices/%s/builders/%s", serviceResourceInner.id(), "default", "default"))
            .withAgentPool(String.format("%s/buildservices/%s/agentPools/%s", serviceResourceInner.id(), "default", "default"))
            .withRelativePath(uploadUrl.relativePath());

        // enqueue build
        BuildInner enqueueResult = appPlatformManager.serviceClient().getBuildServices()
            .createOrUpdateBuild(
                rgName,
                serviceName,
                "default",
                appName,
                new BuildInner().withProperties(buildProperties)
            );
        String buildId = enqueueResult.properties().triggeredBuildResult().id();
        // get build state
        BuildResultInner buildResult = appPlatformManager.serviceClient().getBuildServices()
            .getBuildResult(
                rgName,
                serviceName,
                "default",
                appName,
                ResourceUtils.nameFromResourceId(buildId));
        BuildResultProvisioningState buildState = buildResult.properties().provisioningState();
        // wait for build
        while (buildState == BuildResultProvisioningState.QUEUING || buildState == BuildResultProvisioningState.BUILDING) {
            buildResult = appPlatformManager.serviceClient().getBuildServices()
                .getBuildResult(
                    rgName,
                    serviceName,
                    "default",
                    appName,
                    ResourceUtils.nameFromResourceId(buildId));
            buildState = buildResult.properties().provisioningState();
            ResourceManagerUtils.sleep(Duration.ofSeconds(30));
        }
        if (buildState != BuildResultProvisioningState.SUCCEEDED) {
            throw new RuntimeException(
                String.format("build failed with state [%s]", buildState));
        }

        // --------------------------------------
        // deploy app
        BuildResultUserSourceInfo sourceInfo = new BuildResultUserSourceInfo();
        sourceInfo.withBuildResultId(buildId);

        resourceInner.properties()
            .withSource(sourceInfo);

        appPlatformManager.serviceClient().getDeployments().update(rgName, serviceName, appName, deploymentName, resourceInner);
    }

    private File downloadFile(String remoteFileUrl) throws Exception {
        String[] split = remoteFileUrl.split("/");
        String filename = split[split.length - 1];
        File downloaded = new File(filename);
        if (!downloaded.exists()) {
            HttpURLConnection connection = (HttpURLConnection) new URL(remoteFileUrl).openConnection();
            connection.connect();
            try (InputStream inputStream = connection.getInputStream();
                 OutputStream outputStream = new FileOutputStream(downloaded)) {
                IOUtils.copy(inputStream, outputStream);
            } finally {
                connection.disconnect();
            }
        }
        return downloaded;
    }
}
