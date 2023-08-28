// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.resourcemanager.network;

import com.azure.core.management.Region;
import com.azure.core.util.BinaryData;
import com.azure.core.util.polling.SyncPoller;
import com.azure.resourcemanager.network.fluent.models.NatGatewayInner;
import com.azure.resourcemanager.network.models.ApplicationSecurityGroup;
import com.azure.resourcemanager.network.models.DeleteOptions;
import com.azure.resourcemanager.network.models.NatGatewaySku;
import com.azure.resourcemanager.network.models.NatGatewaySkuName;
import com.azure.resourcemanager.network.models.Network;
import com.azure.resourcemanager.network.models.NetworkInterface;
import com.azure.resourcemanager.network.models.NetworkInterfaces;
import com.azure.resourcemanager.network.models.Networks;
import com.azure.resourcemanager.network.models.NicIpConfiguration;
import com.azure.resourcemanager.network.models.ProvisioningState;
import com.azure.resourcemanager.network.models.PublicIPSkuType;
import com.azure.resourcemanager.network.models.PublicIpAddress;
import com.azure.resourcemanager.network.models.Subnet;
import com.azure.resourcemanager.resources.fluentcore.arm.ResourceUtils;
import com.azure.resourcemanager.resources.fluentcore.model.Accepted;
import com.azure.resourcemanager.resources.fluentcore.model.Creatable;
import com.azure.resourcemanager.resources.fluentcore.model.CreatedResources;
import com.azure.resourcemanager.resources.models.Deployment;
import com.azure.resourcemanager.resources.models.DeploymentMode;
import com.azure.resourcemanager.resources.models.ResourceGroup;
import com.azure.resourcemanager.resources.models.ResourceGroups;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class NetworkInterfaceOperationsTests extends NetworkManagementTest {

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void canUseMultipleIPConfigs() throws Exception {
        String networkName = generateRandomResourceName("net", 15);
        String[] nicNames = new String[3];
        for (int i = 0; i < nicNames.length; i++) {
            nicNames[i] = generateRandomResourceName("nic", 15);
        }

        Network network =
            networkManager
                .networks()
                .define(networkName)
                .withRegion(Region.US_EAST)
                .withNewResourceGroup(rgName)
                .withAddressSpace("10.0.0.0/27")
                .withSubnet("subnet1", "10.0.0.0/28")
                .withSubnet("subnet2", "10.0.0.16/28")
                .create();

        List<Creatable<NetworkInterface>> nicDefinitions =
            Arrays
                .asList(
                    // 0 - NIC that starts with one IP config and ends with two
                    (Creatable<NetworkInterface>)
                        (networkManager
                            .networkInterfaces()
                            .define(nicNames[0])
                            .withRegion(Region.US_EAST)
                            .withNewResourceGroup(rgName)
                            .withExistingPrimaryNetwork(network)
                            .withSubnet("subnet1")
                            .withPrimaryPrivateIPAddressDynamic()),

                    // 1 - NIC that starts with two IP configs and ends with one
                    networkManager
                        .networkInterfaces()
                        .define(nicNames[1])
                        .withRegion(Region.US_EAST)
                        .withNewResourceGroup(rgName)
                        .withExistingPrimaryNetwork(network)
                        .withSubnet("subnet1")
                        .withPrimaryPrivateIPAddressDynamic()
                        .defineSecondaryIPConfiguration("nicip2")
                        .withExistingNetwork(network)
                        .withSubnet("subnet1")
                        .withPrivateIpAddressDynamic()
                        .attach(),

                    // 2 - NIC that starts with two IP configs and ends with two
                    networkManager
                        .networkInterfaces()
                        .define(nicNames[2])
                        .withRegion(Region.US_EAST)
                        .withNewResourceGroup(rgName)
                        .withExistingPrimaryNetwork(network)
                        .withSubnet("subnet1")
                        .withPrimaryPrivateIPAddressDynamic()
                        .defineSecondaryIPConfiguration("nicip2")
                        .withExistingNetwork(network)
                        .withSubnet("subnet1")
                        .withPrivateIpAddressDynamic()
                        .attach());

        // Create the NICs in parallel
        CreatedResources<NetworkInterface> createdNics = networkManager.networkInterfaces().create(nicDefinitions);

        NetworkInterface[] nics = new NetworkInterface[nicDefinitions.size()];
        for (int i = 0; i < nicDefinitions.size(); i++) {
            nics[i] = createdNics.get(nicDefinitions.get(i).key());
        }

        NicIpConfiguration primaryIPConfig, secondaryIPConfig;
        NetworkInterface nic;

        // Verify NIC0
        nic = nics[0];
        Assertions.assertNotNull(nic);
        primaryIPConfig = nic.primaryIPConfiguration();
        Assertions.assertNotNull(primaryIPConfig);
        Assertions.assertTrue("subnet1".equalsIgnoreCase(primaryIPConfig.subnetName()));
        Assertions.assertTrue(network.id().equalsIgnoreCase(primaryIPConfig.networkId()));

        // Verify NIC1
        nic = nics[1];
        Assertions.assertNotNull(nic);
        Assertions.assertEquals(2, nic.ipConfigurations().size());

        primaryIPConfig = nic.primaryIPConfiguration();
        Assertions.assertNotNull(primaryIPConfig);
        Assertions.assertTrue("subnet1".equalsIgnoreCase(primaryIPConfig.subnetName()));
        Assertions.assertTrue(network.id().equalsIgnoreCase(primaryIPConfig.networkId()));

        secondaryIPConfig = nic.ipConfigurations().get("nicip2");
        Assertions.assertNotNull(secondaryIPConfig);
        Assertions.assertTrue("subnet1".equalsIgnoreCase(primaryIPConfig.subnetName()));
        Assertions.assertTrue(network.id().equalsIgnoreCase(secondaryIPConfig.networkId()));

        // Verify NIC2
        nic = nics[2];
        Assertions.assertNotNull(nic);
        Assertions.assertEquals(2, nic.ipConfigurations().size());

        primaryIPConfig = nic.primaryIPConfiguration();
        Assertions.assertNotNull(primaryIPConfig);
        Assertions.assertTrue("subnet1".equalsIgnoreCase(primaryIPConfig.subnetName()));
        Assertions.assertTrue(network.id().equalsIgnoreCase(primaryIPConfig.networkId()));

        secondaryIPConfig = nic.ipConfigurations().get("nicip2");
        Assertions.assertNotNull(secondaryIPConfig);
        Assertions.assertTrue("subnet1".equalsIgnoreCase(secondaryIPConfig.subnetName()));
        Assertions.assertTrue(network.id().equalsIgnoreCase(secondaryIPConfig.networkId()));

        nic = null;

        List<Mono<NetworkInterface>> nicUpdates =
            Arrays
                .asList(
                    // Update NIC0
                    nics[0]
                        .update()
                        .defineSecondaryIPConfiguration("nicip2")
                        .withExistingNetwork(network)
                        .withSubnet("subnet1")
                        .withPrivateIpAddressDynamic()
                        .attach()
                        .applyAsync(),

                    // Update NIC2
                    nics[1]
                        .update()
                        .withoutIPConfiguration("nicip2")
                        .updateIPConfiguration("primary")
                        .withSubnet("subnet2")
                        .parent()
                        .applyAsync(),

                    // Update NIC3
                    nics[2]
                        .update()
                        .withoutIPConfiguration("nicip2")
                        .defineSecondaryIPConfiguration("nicip3")
                        .withExistingNetwork(network)
                        .withSubnet("subnet1")
                        .withPrivateIpAddressDynamic()
                        .attach()
                        .applyAsync());

        List<NetworkInterface> updatedNics =
            Flux
                .mergeDelayError(32, (Mono<NetworkInterface>[]) nicUpdates.toArray(new Mono[0]))
                .collectList()
                .block();

        // Verify updated NICs
        for (NetworkInterface n : updatedNics) {
            Assertions.assertNotNull(n);
            if (n.id().equalsIgnoreCase(nics[0].id())) {
                // Verify NIC0
                Assertions.assertEquals(2, n.ipConfigurations().size());

                primaryIPConfig = n.primaryIPConfiguration();
                Assertions.assertNotNull(primaryIPConfig);
                Assertions.assertTrue("subnet1".equalsIgnoreCase(primaryIPConfig.subnetName()));
                Assertions.assertTrue(network.id().equalsIgnoreCase(primaryIPConfig.networkId()));

                secondaryIPConfig = n.ipConfigurations().get("nicip2");
                Assertions.assertNotNull(secondaryIPConfig);
                Assertions.assertTrue("subnet1".equalsIgnoreCase(secondaryIPConfig.subnetName()));
                Assertions.assertTrue(network.id().equalsIgnoreCase(secondaryIPConfig.networkId()));

            } else if (n.id().equals(nics[1].id())) {
                // Verify NIC1
                Assertions.assertEquals(1, n.ipConfigurations().size());
                primaryIPConfig = n.primaryIPConfiguration();
                Assertions.assertNotNull(primaryIPConfig);
                Assertions.assertNotEquals("nicip2", primaryIPConfig.name());
                Assertions.assertTrue("subnet2".equalsIgnoreCase(primaryIPConfig.subnetName()));
                Assertions.assertTrue(network.id().equalsIgnoreCase(primaryIPConfig.networkId()));

            } else if (n.id().equals(nics[2].id())) {
                // Verify NIC
                Assertions.assertEquals(2, n.ipConfigurations().size());

                primaryIPConfig = n.primaryIPConfiguration();
                Assertions.assertNotNull(primaryIPConfig);
                Assertions.assertNotEquals("nicip2", primaryIPConfig.name());
                Assertions.assertNotEquals("nicip3", primaryIPConfig.name());
                Assertions.assertTrue("subnet1".equalsIgnoreCase(primaryIPConfig.subnetName()));
                Assertions.assertTrue(network.id().equalsIgnoreCase(primaryIPConfig.networkId()));

                secondaryIPConfig = n.ipConfigurations().get("nicip3");
                Assertions.assertNotNull(secondaryIPConfig);
                Assertions.assertTrue("subnet1".equalsIgnoreCase(secondaryIPConfig.subnetName()));
                Assertions.assertTrue(network.id().equalsIgnoreCase(secondaryIPConfig.networkId()));
            } else {
                Assertions.assertTrue(false, "Unrecognized NIC ID");
            }
        }
    }

    @Test
    public void canCreateBatchOfNetworkInterfaces() throws Exception {
        ResourceGroups resourceGroups = resourceManager.resourceGroups();
        Networks networks = networkManager.networks();
        NetworkInterfaces networkInterfaces = networkManager.networkInterfaces();

        Creatable<ResourceGroup> resourceGroupCreatable = resourceGroups.define(rgName).withRegion(Region.US_EAST);

        final String vnetName = "vnet1212";
        Creatable<Network> networkCreatable =
            networks
                .define(vnetName)
                .withRegion(Region.US_EAST)
                .withNewResourceGroup(resourceGroupCreatable)
                .withAddressSpace("10.0.0.0/28");

        // Prepare a batch of nics
        //
        final String nic1Name = "nic1";
        Creatable<NetworkInterface> networkInterface1Creatable =
            networkInterfaces
                .define(nic1Name)
                .withRegion(Region.US_EAST)
                .withNewResourceGroup(resourceGroupCreatable)
                .withNewPrimaryNetwork(networkCreatable)
                .withPrimaryPrivateIPAddressStatic("10.0.0.5");

        final String nic2Name = "nic2";
        Creatable<NetworkInterface> networkInterface2Creatable =
            networkInterfaces
                .define(nic2Name)
                .withRegion(Region.US_EAST)
                .withNewResourceGroup(resourceGroupCreatable)
                .withNewPrimaryNetwork(networkCreatable)
                .withPrimaryPrivateIPAddressStatic("10.0.0.6");

        final String nic3Name = "nic3";
        Creatable<NetworkInterface> networkInterface3Creatable =
            networkInterfaces
                .define(nic3Name)
                .withRegion(Region.US_EAST)
                .withNewResourceGroup(resourceGroupCreatable)
                .withNewPrimaryNetwork(networkCreatable)
                .withPrimaryPrivateIPAddressStatic("10.0.0.7");

        final String nic4Name = "nic4";
        Creatable<NetworkInterface> networkInterface4Creatable =
            networkInterfaces
                .define(nic4Name)
                .withRegion(Region.US_EAST)
                .withNewResourceGroup(resourceGroupCreatable)
                .withNewPrimaryNetwork(networkCreatable)
                .withPrimaryPrivateIPAddressStatic("10.0.0.8");

        @SuppressWarnings("unchecked")
        Collection<NetworkInterface> batchNics =
            networkInterfaces
                .create(
                    networkInterface1Creatable,
                    networkInterface2Creatable,
                    networkInterface3Creatable,
                    networkInterface4Creatable)
                .values();

        Assertions.assertTrue(batchNics.size() == 4);
        HashMap<String, Boolean> found = new LinkedHashMap<>();
        for (NetworkInterface nic : batchNics) {
            if (nic.name().equalsIgnoreCase(nic1Name)) {
                found.put(nic1Name, true);
            }
            if (nic.name().equalsIgnoreCase(nic2Name)) {
                found.put(nic2Name, true);
            }
            if (nic.name().equalsIgnoreCase(nic3Name)) {
                found.put(nic3Name, true);
            }
            if (nic.name().equalsIgnoreCase(nic4Name)) {
                found.put(nic4Name, true);
            }
        }
        Assertions.assertTrue(found.size() == 4);
    }

    @Test
    public void canCreateNicWithApplicationSecurityGroup() {
        Network network =
            networkManager
                .networks()
                .define("vnet1")
                .withRegion(Region.US_EAST)
                .withNewResourceGroup(rgName)
                .withAddressSpace("10.0.0.0/27")
                .withSubnet("subnet1", "10.0.0.0/28")
                .withSubnet("subnet2", "10.0.0.16/28")
                .create();

        ApplicationSecurityGroup asg1 = networkManager.applicationSecurityGroups().define("asg1")
            .withRegion(Region.US_EAST)
            .withExistingResourceGroup(rgName)
            .create();

        NetworkInterface nic = networkManager.networkInterfaces().define("nic1")
            .withRegion(Region.US_EAST)
            .withExistingResourceGroup(rgName)
            .withExistingPrimaryNetwork(network)
            .withSubnet("subnet1")
            .withPrimaryPrivateIPAddressDynamic()
            .withExistingApplicationSecurityGroup(asg1)
            .create();

        List<ApplicationSecurityGroup> applicationSecurityGroups = nic.primaryIPConfiguration().listAssociatedApplicationSecurityGroups();
        Assertions.assertEquals(1, applicationSecurityGroups.size());
        Assertions.assertEquals("asg1", applicationSecurityGroups.iterator().next().name());

        ApplicationSecurityGroup asg2 = networkManager.applicationSecurityGroups().define("asg2")
            .withRegion(Region.US_EAST)
            .withExistingResourceGroup(rgName)
            .create();

        nic.update()
            .withoutApplicationSecurityGroup(asg1.name())
            .withExistingApplicationSecurityGroup(asg2)
            .defineSecondaryIPConfiguration("nicip2")
                .withExistingNetwork(network)
                .withSubnet("subnet1")
                .withPrivateIpAddressDynamic()
                .attach()
            .apply();

        applicationSecurityGroups = nic.primaryIPConfiguration().listAssociatedApplicationSecurityGroups();
        Assertions.assertEquals(1, applicationSecurityGroups.size());
        Assertions.assertEquals("asg2", applicationSecurityGroups.iterator().next().name());

        nic.update()
            .withoutApplicationSecurityGroup(asg1.name())
            .withExistingApplicationSecurityGroup(asg1)
            .apply();

        Assertions.assertEquals(2, nic.ipConfigurations().get("nicip2").innerModel().applicationSecurityGroups().size());
        Assertions.assertEquals(
            new HashSet<>(Arrays.asList("asg1", "asg2")),
            nic.ipConfigurations().get("nicip2").innerModel().applicationSecurityGroups().stream().map(inner -> ResourceUtils.nameFromResourceId(inner.id())).collect(Collectors.toSet()));
        if (!isPlaybackMode()) {
            // avoid concurrent request in playback
            applicationSecurityGroups = nic.ipConfigurations().get("nicip2").listAssociatedApplicationSecurityGroups();
            Assertions.assertEquals(2, applicationSecurityGroups.size());
            Assertions.assertEquals(
                new HashSet<>(Arrays.asList("asg1", "asg2")),
                applicationSecurityGroups.stream().map(ApplicationSecurityGroup::name).collect(Collectors.toSet()));
        }
    }

    @Test
    @Disabled("Deadlock from CountDownLatch")
    public void canDeleteNetworkWithServiceCallBack() {
        String vnetName = generateRandomResourceName("vnet", 15);
        networkManager
            .networks()
            .define(vnetName)
            .withRegion(Region.US_EAST)
            .withNewResourceGroup(rgName)
            .withAddressSpace("172.16.0.0/16")
            .defineSubnet("Front-end")
            .withAddressPrefix("172.16.1.0/24")
            .attach()
            .defineSubnet("Back-end")
            .withAddressPrefix("172.16.3.0/24")
            .attach()
            .create();

        // TODO: Fix deadlock
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicInteger counter = new AtomicInteger(0);
        networkManager
            .networks()
            .deleteByResourceGroupAsync(rgName, vnetName)
            .doOnSuccess(
                aVoid -> {
                    counter.incrementAndGet();
                    latch.countDown();
                })
            .doOnError(throwable -> latch.countDown());

        try {
            latch.await();
        } catch (InterruptedException exception) {
            throw new RuntimeException(exception);
        }
        Assertions.assertEquals(counter.intValue(), 1);
    }

    @Test
    public void canListSubnetAvailableIpAddresses() {
        String networkName = generateRandomResourceName("vnet", 10);
        String subnetName = "subnet1";
        String nicName = generateRandomResourceName("nic", 10);

        Network network = networkManager.networks()
            .define(networkName)
            .withRegion(Region.US_EAST)
            .withNewResourceGroup(rgName)
            .withAddressSpace("10.0.0.0/24")
            .withSubnet(subnetName, "10.0.0.0/29")
            .create();

        Subnet subnet = network.subnets().get(subnetName);
        Set<String> availableIps = subnet.listAvailablePrivateIPAddresses();
        Assertions.assertTrue(availableIps.size() > 0);

        String availableIp = availableIps.iterator().next();

        // occupy the available ip address
        NetworkInterface nic = networkManager.networkInterfaces()
            .define(nicName)
            .withRegion(Region.US_EAST)
            .withExistingResourceGroup(rgName)
            .withExistingPrimaryNetwork(network)
            .withSubnet(subnetName)
            .withPrimaryPrivateIPAddressStatic(availableIp)
            .create();

        availableIps = subnet.listAvailablePrivateIPAddresses();
        Assertions.assertFalse(availableIps.contains(availableIp));
    }

    @Test
    public void canAssociateNatGateway() {
        String networkName = generateRandomResourceName("vnet", 10);
        String subnetName = "subnet1";
        String subnet2Name = "subnet2";

        ResourceGroup resourceGroup = resourceManager.resourceGroups().define(rgName)
            .withRegion(Region.US_EAST)
            .create();

        NatGatewayInner gateway1 = createNatGateway();

        Network network = networkManager.networks()
            .define(networkName)
            .withRegion(Region.US_EAST)
            .withExistingResourceGroup(resourceGroup)
            .withAddressSpace("10.0.0.0/16")
            .defineSubnet(subnetName)
                .withAddressPrefix("10.0.0.0/24")
                .withExistingNatGateway(gateway1.id())
                .attach()
            .create();

        Subnet subnet = network.subnets().get(subnetName);
        Assertions.assertEquals(gateway1.id(), subnet.natGatewayId());

        NatGatewayInner gateway2 = createNatGateway();

        network.update()
            .updateSubnet(subnetName)
                .withExistingNatGateway(gateway2.id())
                .parent()
            .defineSubnet(subnet2Name)
                .withAddressPrefix("10.0.1.0/24")
                .withExistingNatGateway(gateway2.id())
                .attach()
            .apply();

        subnet = network.subnets().get(subnetName);
        Assertions.assertEquals(gateway2.id(), subnet.natGatewayId());

        Subnet subnet2 = network.subnets().get(subnet2Name);
        Assertions.assertEquals(gateway2.id(), subnet2.natGatewayId());
    }

    @Test
    public void deploymentNicTests() throws IOException, InterruptedException {
        String pipName1 = generateRandomResourceName("pip", 15);
        String pipName2 = generateRandomResourceName("pip", 15);
        String pipName3 = generateRandomResourceName("pip", 15);
        String nicName1 = generateRandomResourceName("nic", 15);
        String nicName2 = generateRandomResourceName("nic", 15);
        String nicName3 = generateRandomResourceName("nic", 15);
        String dpName1 = generateRandomResourceName("dp", 15);
        String dpName2 = generateRandomResourceName("dp", 15);
        String dpName3 = generateRandomResourceName("dp", 15);

        String vnetName = generateRandomResourceName("net", 15);

        Region region = Region.US_EAST;

        Network network = networkManager
            .networks()
            .define(vnetName)
            .withRegion(region)
            .withNewResourceGroup(rgName)
            .withAddressSpace("10.0.0.0/24")
            .withSubnet("subnet1", "10.0.0.0/28")
            .withSubnet("subnet2", "10.0.0.16/28")
            .withSubnet("subnet3", "10.0.0.32/28")
            .create();

        PublicIpAddress pip1 = networkManager
            .publicIpAddresses()
            .define(pipName1)
            .withRegion(region)
            .withExistingResourceGroup(rgName)
            .withStaticIP()
            .withSku(PublicIPSkuType.STANDARD)
            .create();
        PublicIpAddress pip2 = networkManager
            .publicIpAddresses()
            .define(pipName2)
            .withRegion(region)
            .withExistingResourceGroup(rgName)
            .withStaticIP()
            .withSku(PublicIPSkuType.STANDARD)
            .create();
        PublicIpAddress pip3 = networkManager
            .publicIpAddresses()
            .define(pipName3)
            .withRegion(region)
            .withExistingResourceGroup(rgName)
            .withStaticIP()
            .withSku(PublicIPSkuType.STANDARD)
            .create();

        String deploymentJson = "\n" +
            "{\n" +
            "  \"$schema\": \"https://schema.management.azure.com/schemas/2019-04-01/deploymentTemplate.json#\",\n" +
            "  \"contentVersion\": \"1.0.0.0\",\n" +
            "  \"parameters\":{\n" +
            "    \"nicName\":{\n" +
            "      \"type\": \"string\"\n" +
            "    },\n" +
            "    \"pipId\" :{\n" +
            "      \"type\": \"string\"\n" +
            "    },\n" +
            "    \"subnetId\": {\n" +
            "      \"type\": \"string\"\n" +
            "    },\n" +
            "    \"deleteOption\": {\n" +
            "      \"type\": \"string\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"resources\":[{\n" +
            "    \"type\": \"Microsoft.Network/networkInterfaces\",\n" +
            "    \"apiVersion\": \"2023-04-01\",\n" +
            "    \"name\": \"[parameters('nicName')]\",\n" +
            "    \"location\": \"[resourceGroup().location]\",\n" +
            "    \"properties\": {\n" +
            "        \"ipConfigurations\": [\n" +
            "          {\n" +
            "            \"name\": \"ipConfig\",\n" +
            "            \"properties\": {\n" +
            "              \"publicIPAddress\": {\n" +
            "                \"id\": \"[parameters('pipId')]\",\n" +
            "                  \"properties\": {\n" +
            "                    \"deleteOption\": \"[parameters('deleteOption')]\"\n" +
            "                  }\n" +
            "              },\n" +
            "              \"subnet\": {\n" +
            "                \"id\": \"[parameters('subnetId')]\"\n" +
            "              }\n" +
            "            }\n" +
            "          }\n" +
            "        ]\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}\n";
        pip1.refresh();

        // pip1, nic1
        Map<String, Object> parameters = new HashMap<>();
        Subnet subnet1 = network.subnets().get("subnet1");
        parameters.put("subnetId", new HashMap<String, String>(){{this.put("value", subnet1.id());}});
        parameters.put("nicName", new HashMap<String, String>(){{this.put("value", nicName1);}});
        parameters.put("pipId", new HashMap<String, String>(){{this.put("value",pip1.id());}});
        parameters.put("deleteOption", new HashMap<String, String>(){{this.put("value", DeleteOptions.DELETE.toString());}});
        Accepted<Deployment> poller = resourceManager.deployments()
            .define(dpName1)
            .withExistingResourceGroup(rgName)
            .withTemplate(deploymentJson)
            .withParameters(parameters)
            .withMode(DeploymentMode.COMPLETE)
            .beginCreate();

        Deployment deployment = poller.getFinalResult();

        NetworkInterface nic1 = waitForCompletion(nicName1);

        parameters.put("deleteOption", new HashMap<String, String>(){{this.put("value", DeleteOptions.DETACH.toString());}});

        deployment.update()
            .withParameters(deploymentJson)
            .withParameters(parameters)
            .withMode(DeploymentMode.INCREMENTAL)
            .apply();

        networkManager.networkInterfaces().deleteById(nic1.id());

        Assertions.assertEquals(1, networkManager.publicIpAddresses().listByResourceGroup(rgName).stream().count());

        // pip2, nic2
        parameters.clear();

        pip2.refresh();

        Subnet subnet2 = network.subnets().get("subnet2");
        parameters.put("subnetId", new HashMap<String, String>(){{this.put("value", subnet2.id());}});
        parameters.put("nicName", new HashMap<String, String>(){{this.put("value", nicName2);}});
        parameters.put("pipId", new HashMap<String, String>(){{this.put("value",pip2.id());}});
        parameters.put("deleteOption", new HashMap<String, String>(){{this.put("value", DeleteOptions.DETACH.toString());}});
        resourceManager.deployments()
            .define(dpName2)
            .withExistingResourceGroup(rgName)
            .withTemplate(deploymentJson)
            .withParameters(BinaryData.fromObject(parameters).toString())
            .withMode(DeploymentMode.COMPLETE)
            .beginCreate();

        NetworkInterface nic2 = waitForCompletion(nicName2);
        networkManager.networkInterfaces().deleteById(nic2.id());

        Assertions.assertEquals(1, networkManager.publicIpAddresses().listByResourceGroup(rgName).stream().count());
    }

    @Test
    public void updateNic() throws IOException {
        String rg = "javanwmrg23721";
        String nicId = "/subscriptions/ec0aa5f7-9e78-40c9-85cd-535c6305b380/resourceGroups/javanwmrg23721/providers/Microsoft.Network/networkInterfaces/nic23751feb";
        NetworkInterface nic = networkManager.networkInterfaces().getById(nicId);
        String dpName1 = generateRandomResourceName("dp", 15);
        String deploymentJson = "\n" +
            "{\n" +
            "  \"$schema\": \"https://schema.management.azure.com/schemas/2019-04-01/deploymentTemplate.json#\",\n" +
            "  \"contentVersion\": \"1.0.0.0\",\n" +
            "  \"parameters\":{\n" +
            "    \"nicName\":{\n" +
            "      \"type\": \"string\"\n" +
            "    },\n" +
            "    \"pipId\" :{\n" +
            "      \"type\": \"string\"\n" +
            "    },\n" +
            "    \"subnetId\": {\n" +
            "      \"type\": \"string\"\n" +
            "    },\n" +
            "    \"deleteOption\": {\n" +
            "      \"type\": \"string\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"resources\":[{\n" +
            "    \"type\": \"Microsoft.Network/networkInterfaces\",\n" +
            "    \"apiVersion\": \"2023-04-01\",\n" +
            "    \"name\": \"[parameters('nicName')]\",\n" +
            "    \"location\": \"[resourceGroup().location]\",\n" +
            "    \"properties\": {\n" +
            "        \"ipConfigurations\": [\n" +
            "          {\n" +
            "            \"name\": \"ipConfig\",\n" +
            "            \"properties\": {\n" +
            "              \"publicIPAddress\": {\n" +
            "                \"id\": \"[parameters('pipId')]\",\n" +
            "                  \"properties\": {\n" +
            "                    \"deleteOption\": \"[parameters('deleteOption')]\"\n" +
            "                  }\n" +
            "              },\n" +
            "              \"subnet\": {\n" +
            "                \"id\": \"[parameters('subnetId')]\"\n" +
            "              }\n" +
            "            }\n" +
            "          }\n" +
            "        ]\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}\n";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("subnetId", new HashMap<String, String>(){{this.put("value", "/subscriptions/ec0aa5f7-9e78-40c9-85cd-535c6305b380/resourceGroups/javanwmrg23721/providers/Microsoft.Network/virtualNetworks/net734904a0/subnets/subnet1");}});
        parameters.put("nicName", new HashMap<String, String>(){{this.put("value", "nic23751feb");}});
        parameters.put("pipId", new HashMap<String, String>(){{this.put("value","/subscriptions/ec0aa5f7-9e78-40c9-85cd-535c6305b380/resourceGroups/javanwmrg23721/providers/Microsoft.Network/publicIPAddresses/pip39298a18");}});
        parameters.put("deleteOption", new HashMap<String, String>(){{this.put("value", DeleteOptions.DETACH.toString());}});
        Deployment deployment = resourceManager.deployments()
            .define(dpName1)
            .withExistingResourceGroup(rg)
            .withTemplate(deploymentJson)
            .withParameters(parameters)
            .withMode(DeploymentMode.COMPLETE)
            .create();

        nic.refresh();
        Assertions.assertEquals(DeleteOptions.DETACH, nic.ipConfigurations().values().iterator().next().innerModel().publicIpAddress().deleteOption());

        networkManager.networkInterfaces().deleteById(nicId);
        Assertions.assertEquals(1, networkManager.publicIpAddresses().listByResourceGroup(rg).stream().count());
    }

    @Test
    public void testPipDeleteOptions() {
        String pipName1 = generateRandomResourceName("pip", 15);
        String pipName2 = generateRandomResourceName("pip", 15);
        String pipName3 = generateRandomResourceName("pip", 15);
        String nicName1 = generateRandomResourceName("nic", 15);
        String nicName2 = generateRandomResourceName("nic", 15);
        String nicName3 = generateRandomResourceName("nic", 15);
        String vmName = generateRandomResourceName("nic", 15);
        String dpName1 = generateRandomResourceName("dp", 15);
        String dpName2 = generateRandomResourceName("dp", 15);
        String dpName3 = generateRandomResourceName("dp", 15);

        String vnetName = generateRandomResourceName("net", 15);

        Region region = Region.US_EAST;

        Network network = networkManager
            .networks()
            .define(vnetName)
            .withRegion(region)
            .withNewResourceGroup(rgName)
            .withAddressSpace("10.0.0.0/24")
            .withSubnet("subnet1", "10.0.0.0/28")
            .withSubnet("subnet2", "10.0.0.16/28")
            .withSubnet("subnet3", "10.0.0.32/28")
            .create();

        PublicIpAddress pip1 = networkManager
            .publicIpAddresses()
            .define(pipName1)
            .withRegion(region)
            .withExistingResourceGroup(rgName)
            .withStaticIP()
            .withSku(PublicIPSkuType.STANDARD)
            .create();

        NetworkInterface nic1 = networkManager
            .networkInterfaces()
            .define(nicName1)
            .withRegion(region)
            .withExistingResourceGroup(rgName)
            .withExistingPrimaryNetwork(network)
            .withSubnet("subnet1")
            .withPrimaryPrivateIPAddressDynamic()
            .withExistingPrimaryPublicIPAddress(pip1)
            .create();

        Assertions.assertEquals(0, networkManager.publicIpAddresses().listByResourceGroup(rgName).stream().count());

        PublicIpAddress pip2 = networkManager
            .publicIpAddresses()
            .define(pipName2)
            .withRegion(region)
            .withExistingResourceGroup(rgName)
            .withStaticIP()
            .withSku(PublicIPSkuType.STANDARD)
            .create();

        NetworkInterface nic2 = networkManager
            .networkInterfaces()
            .define(nicName2)
            .withRegion(region)
            .withExistingResourceGroup(rgName)
            .withExistingPrimaryNetwork(network)
            .withSubnet("subnet2")
            .withPrimaryPrivateIPAddressDynamic()
            .withExistingPrimaryPublicIPAddress(pip2)
            .create();

        nic2.innerModel().ipConfigurations().iterator().next().publicIpAddress().withDeleteOption(DeleteOptions.DETACH);
        nic2.update().apply();

        networkManager.networkInterfaces().deleteById(nic2.id());

        Assertions.assertEquals(1, networkManager.publicIpAddresses().listByResourceGroup(rgName).stream().count());

        networkManager.publicIpAddresses().deleteById(pip2.id());
        Assertions.assertEquals(0, networkManager.publicIpAddresses().listByResourceGroup(rgName).stream().count());

        PublicIpAddress pip3 = networkManager
            .publicIpAddresses()
            .define(pipName3)
            .withRegion(region)
            .withExistingResourceGroup(rgName)
            .withStaticIP()
            .withSku(PublicIPSkuType.STANDARD)
            .create();

        NetworkInterface nic3 = networkManager
            .networkInterfaces()
            .define(nicName3)
            .withRegion(region)
            .withExistingResourceGroup(rgName)
            .withExistingPrimaryNetwork(network)
            .withSubnet("subnet3")
            .withPrimaryPrivateIPAddressDynamic()
            .withExistingPrimaryPublicIPAddress(pip3)
            .create();

        nic3.innerModel().ipConfigurations().iterator().next().publicIpAddress().withDeleteOption(DeleteOptions.DETACH);
        nic3.update().apply();

        nic3.innerModel().ipConfigurations().iterator().next().publicIpAddress().withDeleteOption(DeleteOptions.DELETE);
        nic3.update().apply();

        networkManager.networkInterfaces().deleteById(nic3.id());

        Assertions.assertEquals(1, networkManager.publicIpAddresses().listByResourceGroup(rgName).stream().count());
    }

    @Test
    public void deleteTest() {
        String rgName = "javanwmrg23721";
        String nicId = "/subscriptions/ec0aa5f7-9e78-40c9-85cd-535c6305b380/resourceGroups/javanwmrg23721/providers/Microsoft.Network/networkInterfaces/nic23751feb";
        Assertions.assertEquals(1, networkManager.publicIpAddresses().listByResourceGroup(rgName).stream().count());

        networkManager.networkInterfaces().deleteById(nicId);

        Assertions.assertEquals(1, networkManager.publicIpAddresses().listByResourceGroup(rgName).stream().count());
    }

    private NetworkInterface waitForCompletion(String nicName1) throws InterruptedException {
        Optional<NetworkInterface> nicOptional;
        while ((nicOptional = networkManager.networkInterfaces().listByResourceGroup(rgName).stream().filter(nic -> nic.name().equals(nicName1)).findAny()).isEmpty()) {
            TimeUnit.SECONDS.sleep(1);
        }

        NetworkInterface nic1 = nicOptional.get();
        while (nic1.innerModel().provisioningState() != ProvisioningState.SUCCEEDED) {
            TimeUnit.SECONDS.sleep(1);
        }
        return nic1;
    }

    @Test
    public void getNicTest() {
        String nicId = "/subscriptions/ec0aa5f7-9e78-40c9-85cd-535c6305b380/resourceGroups/javanwmrg86791/providers/Microsoft.Network/networkInterfaces/nic008843cf";
        NetworkInterface nic = networkManager.networkInterfaces().getById(nicId);
        Assertions.assertEquals(DeleteOptions.DELETE, nic.ipConfigurations().values().iterator().next().innerModel().publicIpAddress().deleteOption());

        nic.innerModel().ipConfigurations().iterator().next().publicIpAddress().withDeleteOption(DeleteOptions.DETACH);
        nic.update().apply();

        PublicIpAddress pip = networkManager.publicIpAddresses().getById(nic.ipConfigurations().values().iterator().next().innerModel().publicIpAddress().id());
        System.out.println(pip.innerModel().deleteOption());

        networkManager.networkInterfaces().deleteById(nic.id());

        Assertions.assertEquals(1, networkManager.publicIpAddresses().listByResourceGroup(rgName).stream().count());
    }

    private NatGatewayInner createNatGateway() {
        String natGatewayName = generateRandomResourceName("natgw", 10);
        return networkManager.serviceClient()
            .getNatGateways()
            .createOrUpdate(
                rgName,
                natGatewayName,
                new NatGatewayInner()
                    .withLocation(Region.US_EAST.toString())
                    .withSku(new NatGatewaySku().withName(NatGatewaySkuName.STANDARD))
            );
    }
}
