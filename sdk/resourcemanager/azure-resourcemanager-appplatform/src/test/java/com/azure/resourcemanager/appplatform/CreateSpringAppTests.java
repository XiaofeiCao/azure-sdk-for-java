// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.resourcemanager.appplatform;

import com.azure.core.management.Region;
import com.azure.resourcemanager.appplatform.fluent.models.AppResourceInner;
import com.azure.resourcemanager.appplatform.fluent.models.DeploymentResourceInner;
import com.azure.resourcemanager.appplatform.fluent.models.ServiceResourceInner;
import com.azure.resourcemanager.appplatform.models.AppResourceProperties;
import com.azure.resourcemanager.appplatform.models.DeploymentResourceProperties;
import com.azure.resourcemanager.appplatform.models.DeploymentSettings;
import com.azure.resourcemanager.appplatform.models.JarUploadedUserSourceInfo;
import com.azure.resourcemanager.appplatform.models.ResourceRequests;
import com.azure.resourcemanager.appplatform.models.ResourceUploadDefinition;
import com.azure.resourcemanager.appplatform.models.Sku;
import com.azure.resourcemanager.appplatform.models.SourceUploadedUserSourceInfo;
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

public class CreateSpringAppTests extends AppPlatformTest {

    private static final String PIGGYMETRICS_TAR_GZ_URL = "https://github.com/weidongxu-microsoft/azure-sdk-for-java-management-tests/raw/master/spring-cloud/piggymetrics.tar.gz";

    @Test
    public void springAppSampleTest() throws Exception {
        String serviceName = generateRandomResourceName("ss", 15);
        String appName = generateRandomResourceName("app", 15);
        String deploymentName = generateRandomResourceName("deployment", 15);

        Region region = Region.US_EAST;

        appPlatformManager.resourceManager().resourceGroups().define(rgName).withRegion(region).create();

        // provision spring service
        ServiceResourceInner serviceResourceInner = new ServiceResourceInner()
            .withLocation(region.toString())
            .withSku(new Sku().withName("S0"));
        serviceResourceInner = appPlatformManager.serviceClient().getServices().createOrUpdate(rgName, serviceName, serviceResourceInner);

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
                    .withSource(new JarUploadedUserSourceInfo().withRelativePath("<default>")));

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
        File gzFile = downloadFile(PIGGYMETRICS_TAR_GZ_URL);

        fileClient.create(gzFile.length())
            .flatMap(fileInfo -> fileClient.uploadFromFile(gzFile.getAbsolutePath()))
            .block();

        // --------------------------------------
        // deploy app
        SourceUploadedUserSourceInfo sourceInfo = new SourceUploadedUserSourceInfo();
        sourceInfo.withRelativePath(uploadUrl.relativePath());
        sourceInfo.withArtifactSelector("gateway"); // withTargetModule

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
