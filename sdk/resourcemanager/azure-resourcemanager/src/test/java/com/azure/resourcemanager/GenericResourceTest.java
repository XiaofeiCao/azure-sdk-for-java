// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.resourcemanager;

import com.azure.core.credential.TokenCredential;
import com.azure.core.http.HttpClient;
import com.azure.core.http.HttpPipeline;
import com.azure.core.http.policy.HttpLogOptions;
import com.azure.core.http.policy.HttpPipelinePolicy;
import com.azure.core.http.policy.RetryPolicy;
import com.azure.core.management.Region;
import com.azure.core.management.profile.AzureProfile;
import com.azure.resourcemanager.appservice.fluent.models.SiteConfigResourceInner;
import com.azure.resourcemanager.appservice.models.NetFrameworkVersion;
import com.azure.resourcemanager.appservice.models.PricingTier;
import com.azure.resourcemanager.appservice.models.SupportedTlsVersions;
import com.azure.resourcemanager.compute.models.DeleteOptions;
import com.azure.resourcemanager.compute.models.KnownLinuxVirtualMachineImage;
import com.azure.resourcemanager.compute.models.VirtualMachine;
import com.azure.resourcemanager.compute.models.VirtualMachineDiskOptions;
import com.azure.resourcemanager.compute.models.VirtualMachineSizeTypes;
import com.azure.resourcemanager.network.models.Network;
import com.azure.resourcemanager.network.models.NetworkInterface;
import com.azure.resourcemanager.network.models.PublicIpAddress;
import com.azure.resourcemanager.privatedns.models.PrivateDnsZone;
import com.azure.resourcemanager.resources.fluentcore.utils.HttpPipelineProvider;
import com.azure.resourcemanager.resources.fluentcore.utils.ResourceManagerUtils;
import com.azure.resourcemanager.resources.models.Deployment;
import com.azure.resourcemanager.resources.models.DeploymentMode;
import com.azure.resourcemanager.resources.models.GenericResource;
import com.azure.resourcemanager.resources.models.PolicyAssignment;
import com.azure.resourcemanager.resources.models.PolicyDefinition;
import com.azure.resourcemanager.resources.models.PolicyType;
import com.azure.resourcemanager.resources.models.ResourceGroup;
import com.azure.resourcemanager.sql.models.SqlElasticPool;
import com.azure.resourcemanager.sql.models.SqlServer;
import com.azure.resourcemanager.test.ResourceManagerTestProxyTestBase;
import com.azure.resourcemanager.test.utils.TestDelayProvider;
import com.azure.resourcemanager.test.utils.TestIdentifierProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenericResourceTest extends ResourceManagerTestProxyTestBase {

    private AzureResourceManager azureResourceManager;

    protected String rgName = "";
    protected final Region region = Region.US_EAST;
    private final String policyRule = "{\"if\":{\"not\":{\"field\":\"location\",\"in\":[\"southcentralus\",\"eastus\"]}},\"then\":{\"effect\":\"deny\"}}";

    @Override
    protected HttpPipeline buildHttpPipeline(
        TokenCredential credential,
        AzureProfile profile,
        HttpLogOptions httpLogOptions,
        List<HttpPipelinePolicy> policies,
        HttpClient httpClient) {
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

    @Override
    protected void initializeClients(HttpPipeline httpPipeline, AzureProfile profile) {
        ResourceManagerUtils.InternalRuntimeContext.setDelayProvider(new TestDelayProvider(!isPlaybackMode()));
        ResourceManagerUtils.InternalRuntimeContext internalContext = new ResourceManagerUtils.InternalRuntimeContext();
        internalContext.setIdentifierFunction(name -> new TestIdentifierProvider(testResourceNamer));
        azureResourceManager = buildManager(AzureResourceManager.class, httpPipeline, profile);
        setInternalContext(internalContext, azureResourceManager);

        rgName = generateRandomResourceName("javacsmrg", 15);
    }

    @Override
    protected void cleanUpResources() {
        try {
            azureResourceManager.resourceGroups().beginDeleteByName(rgName);
        } catch (Exception e) {
        }
    }

    @Test
    public void testResourceWithSpaceInName() {
        SqlElasticPool elasticPool = ensureElasticPoolWithSpace();
        String poolId = elasticPool.id();
        String poolName = elasticPool.name();

        GenericResource pool = azureResourceManager.genericResources().getById(poolId);
        Assertions.assertNotNull(pool);
        Assertions.assertEquals(poolName, pool.name());

        pool = pool.refresh();
        Assertions.assertEquals(poolName, pool.name());

        pool.update()
            .withTag("keyInSpaceTest", "value")
            .apply();
        Assertions.assertEquals(poolName, pool.name());
        Assertions.assertTrue(pool.tags().containsKey("keyInSpaceTest"));

        // status code 405
//        Assertions.assertTrue(azureResourceManager.genericResources().checkExistenceById(poolId));

        // policy assignment
        String policyName = generateRandomResourceName("policy", 15);
        String assignmentName = generateRandomResourceName("assign", 15);
        PolicyDefinition definition = azureResourceManager.policyDefinitions().define(policyName)
            .withPolicyRuleJson(policyRule)
            .withPolicyType(PolicyType.CUSTOM)
            .withDisplayName(policyName)
            .withDescription("This is my policy")
            .create();
        PolicyAssignment assignment = azureResourceManager.policyAssignments()
            .define(assignmentName)
            .forResource(pool)
            .withPolicyDefinition(definition)
            .withDisplayName("My Assignment")
            .create();
        assignment = azureResourceManager.policyAssignments().getById(assignment.id());
        Assertions.assertEquals(assignmentName, assignment.name());

        azureResourceManager.policyAssignments().deleteById(assignment.id());

        // tags
        Map<String, String> tags = new HashMap<>();
        tags.put("myTagKey", "myTagValue");
        azureResourceManager.tagOperations()
            .updateTags(pool, tags);

        pool = pool.refresh();
        Assertions.assertTrue(pool.tags().containsKey("myTagKey"));

        azureResourceManager.genericResources().deleteById(poolId);
    }

    @Test
    public void canGetDefaultApiVersionForChildResources() {
        // test privateDnsZones/virtualNetworkLinks which has the same child resource name as dnsForwardingRulesets/virtualNetworkLinks
        String vnetName = generateRandomResourceName("vnet", 15);
        String topLevelDomain = "www.contoso" + generateRandomResourceName("z", 10) + ".com";
        String vnetLinkName = generateRandomResourceName("pdvnl", 15);

        Network network = azureResourceManager.networks().define(vnetName)
            .withRegion(region)
            .withNewResourceGroup(rgName)
            .withAddressSpace("10.0.0.0/28")
            .withSubnet("subnetA", "10.0.0.0/29")
            .create();

        PrivateDnsZone pdz = azureResourceManager.privateDnsZones().define(topLevelDomain)
            .withExistingResourceGroup(rgName)
            .defineVirtualNetworkLink(vnetLinkName)
            .disableAutoRegistration()
            .withVirtualNetworkId(network.id())
            .withETagCheck()
            .attach()
            .create();

        String vnetLinkId = pdz.virtualNetworkLinks().list().stream().iterator().next().id();
        GenericResource vnetLink = azureResourceManager.genericResources().getById(vnetLinkId);
        Assertions.assertEquals(vnetLinkName, vnetLink.name());

        azureResourceManager.genericResources().deleteById(vnetLinkId);

        // test sites/config, which searches its parent's(sites) api-version
        String webappName = generateRandomResourceName("webapp", 15);
        azureResourceManager.webApps()
            .define(webappName)
            .withRegion(Region.US_EAST)
            .withNewResourceGroup(rgName)
            .withNewWindowsPlan(PricingTier.BASIC_B1)
            .withNetFrameworkVersion(NetFrameworkVersion.V3_0)
            .withMinTlsVersion(SupportedTlsVersions.ONE_ONE)
            .create();

        SiteConfigResourceInner configInner = azureResourceManager
            .webApps()
            .manager()
            .serviceClient()
            .getWebApps()
            .getConfiguration(rgName, webappName);

        GenericResource genericConfig = azureResourceManager.genericResources().getById(configInner.id());
        Assertions.assertEquals(configInner.name(), genericConfig.name());
    }

    @Test
    public void canCreatePip() throws IOException {
        ResourceGroup resourceGroup = azureResourceManager.resourceGroups().define(rgName).withRegion(region).create();

        try {
            String dpName = generateRandomResourceName("dp", 15);
            String vmName = generateRandomResourceName("vm", 15);
            String pipName = generateRandomResourceName("pip", 15);
            String nicName = generateRandomResourceName("nic", 15);
//            String deploymentTemplate = "{\n" +
//                "  \"apiVersion\": \"2023-04-01\",\n" +
//                "  \"type\": \"Microsoft.Network/publicIPAddresses\",\n" +
//                "  \"name\": \"" + pipName + "\",\n" +
//                "  \"location\": \"[resourceGroup().location]\",\n" +
//                "  \"sku\": {\n" +
//                "    \"name\": \"Standard\"\n" +
//                "  },\n" +
//                "  \"properties\": {\n" +
//                "    \"publicIPAllocationMethod\": \"Static\",\n" +
//                "    \"publicIPAddressVersion\": \"IPv4\",\n" +
//                "    \"deleteOption\": \"Delete\"\n" +
//                "  }\n" +
//                "}";

            String deploymentTemplate = "{\n" +
                "  \"$schema\": \"https://schema.management.azure.com/schemas/2019-04-01/deploymentTemplate.json#\",\n" +
                "  \"contentVersion\": \"1.0.0.0\",\n" +
                "  \"resources\": [{\n" +
                "    \"apiVersion\": \"2023-04-01\",\n" +
                "    \"type\": \"Microsoft.Network/publicIPAddresses\",\n" +
                "  \"name\": \"" + pipName + "\",\n" +
                "    \"location\": \"[resourceGroup().location]\",\n" +
                "    \"sku\": {\n" +
                "      \"name\": \"Standard\"\n" +
                "    },\n" +
                "    \"properties\": {\n" +
                "      \"publicIPAllocationMethod\": \"Static\",\n" +
                "      \"publicIPAddressVersion\": \"IPv4\",\n" +
                "      \"deleteOption\": \"Delete\"\n" +
                "    }\n" +
                "  }]\n" +
                "}";

            Deployment deployment = azureResourceManager.deployments()
                .define(dpName)
                .withExistingResourceGroup(resourceGroup)
                .withTemplate(deploymentTemplate)
                .withParameters("{}")
                .withMode(DeploymentMode.COMPLETE)
                .create();

            Assertions.assertEquals(1, azureResourceManager.publicIpAddresses().listByResourceGroup(rgName).stream().count());

            PublicIpAddress pip = azureResourceManager.publicIpAddresses().getByResourceGroup(rgName, pipName);

            NetworkInterface nic = azureResourceManager.networkInterfaces()
                .define(nicName)
                .withRegion(region)
                .withExistingResourceGroup(rgName)
                .withNewPrimaryNetwork("10.0.0.0/24")
                .withPrimaryPrivateIPAddressDynamic()
                .withExistingPrimaryPublicIPAddress(pip)
                .create();

            VirtualMachine vm = azureResourceManager.virtualMachines()
                .define(vmName)
                .withRegion(region)
                .withExistingResourceGroup(rgName)
                .withExistingPrimaryNetworkInterface(nic)
                .withPopularLinuxImage(KnownLinuxVirtualMachineImage.UBUNTU_SERVER_20_04_LTS_GEN2)
                .withRootUsername("Foo12")
                .withSsh(sshPublicKey())
                .withNewDataDisk(10, 1, new VirtualMachineDiskOptions().withDeleteOptions(DeleteOptions.DELETE))
                .withSize(VirtualMachineSizeTypes.STANDARD_DS3_V2)
                .withPrimaryNetworkInterfaceDeleteOptions(DeleteOptions.DELETE)
                .withOSDiskDeleteOptions(DeleteOptions.DELETE)
                .create();

            azureResourceManager.virtualMachines().deleteById(vm.id());

            Assertions.assertEquals(0, azureResourceManager.publicIpAddresses().listByResourceGroup(rgName).stream().count());
        } finally {
            azureResourceManager.resourceGroups().beginDeleteByName(rgName);
        }
    }

    private SqlElasticPool ensureElasticPoolWithSpace() {
        String sqlServerName = generateRandomResourceName("JMonitorSql-", 18);

        SqlServer sqlServer = azureResourceManager
            .sqlServers()
            .define(sqlServerName)
            .withRegion(region)
            .withNewResourceGroup(rgName)
            .withAdministratorLogin("admin123")
            .withAdministratorPassword(password())
            .create();

        // white space in pool name
        SqlElasticPool pool = sqlServer.elasticPools()
            .define("name with space")
            .withBasicPool()
            .create();
        return pool;
    }
}
