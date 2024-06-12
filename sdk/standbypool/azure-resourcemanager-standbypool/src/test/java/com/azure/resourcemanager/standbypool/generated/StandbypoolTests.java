package com.azure.resourcemanager.standbypool.generated;

import com.azure.core.credential.TokenCredential;
import com.azure.core.http.policy.HttpLogDetailLevel;
import com.azure.core.http.policy.HttpLogOptions;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.Region;
import com.azure.core.management.profile.AzureProfile;
import com.azure.core.test.TestBase;
import com.azure.core.test.annotation.LiveOnly;
import com.azure.core.util.Configuration;
import com.azure.core.util.CoreUtils;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.resourcemanager.msi.MsiManager;
import com.azure.resourcemanager.resources.ResourceManager;
import com.azure.resourcemanager.standbypool.StandbyPoolManager;
import com.azure.resourcemanager.standbypool.models.StandbyVirtualMachinePoolElasticityProfile;
import com.azure.resourcemanager.standbypool.models.StandbyVirtualMachinePoolResource;
import com.azure.resourcemanager.standbypool.models.StandbyVirtualMachinePoolResourceProperties;
import com.azure.resourcemanager.standbypool.models.VirtualMachineState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Random;

public class StandbypoolTests extends TestBase {
    private static final Random RANDOM = new Random();
    private static final Region REGION = Region.US_EAST;
    private String resourceGroupName = "rg" + randomPadding();
    private MsiManager msiManager = null;
    private ResourceManager resourceManager;
    private StandbyPoolManager standbyPoolManager;
    private boolean testEnv;

    @Override
    public void beforeTest() {
        final TokenCredential credential = new DefaultAzureCredentialBuilder().build();
        final AzureProfile profile = new AzureProfile(AzureEnvironment.AZURE);

        msiManager = MsiManager
            .configure()
            .withLogOptions(new HttpLogOptions().setLogLevel(HttpLogDetailLevel.BASIC))
            .authenticate(credential, profile);

        resourceManager = ResourceManager
            .configure()
            .withLogOptions(new HttpLogOptions().setLogLevel(HttpLogDetailLevel.BASIC))
            .authenticate(credential, profile)
            .withDefaultSubscription();

        standbyPoolManager = StandbyPoolManager
            .configure()
            .withLogOptions(new HttpLogOptions().setLogLevel(HttpLogDetailLevel.BODY_AND_HEADERS))
            .authenticate(credential, profile);

        // use AZURE_RESOURCE_GROUP_NAME if run in LIVE CI
        String testResourceGroup = Configuration.getGlobalConfiguration().get("AZURE_RESOURCE_GROUP_NAME");
        testEnv = !CoreUtils.isNullOrEmpty(testResourceGroup);
        if (testEnv) {
            resourceGroupName = testResourceGroup;
        } else {
            resourceManager.resourceGroups()
                .define(resourceGroupName)
                .withRegion(REGION)
                .create();
        }
    }

    @Override
    protected void afterTest() {
        if (!testEnv) {
            resourceManager.resourceGroups().beginDeleteByName(resourceGroupName);
        }
    }

    @Test
    @LiveOnly
    public void testCRUDStandbyPool() {
        StandbyVirtualMachinePoolResource pool = null;
        try {
            String randomPadding = randomPadding();
            String vmPoolName = "vmPool" + randomPadding;
            //create
            pool = standbyPoolManager.standbyVirtualMachinePools()
                .define(vmPoolName)
                .withRegion(REGION)
                .withExistingResourceGroup(resourceGroupName)
                .withProperties(new StandbyVirtualMachinePoolResourceProperties().withElasticityProfile(new StandbyVirtualMachinePoolElasticityProfile().withMaxReadyCapacity(10)).withVirtualMachineState(VirtualMachineState.DEALLOCATED))
                .create();
            //update
            pool.update()
                .withTags(new HashMap<>() {{
                    this.put("myTag", "1");
                }})
                .apply();
            //get
            pool = standbyPoolManager.standbyVirtualMachinePools().getById(pool.id());
            Assertions.assertNotNull(pool);
        } finally {
            //delete
            if (pool != null) {
                standbyPoolManager.standbyVirtualMachinePools().deleteById(pool.id());
            }
            if (resourceGroupName != null) {
                resourceManager.resourceGroups().beginDeleteByName(resourceGroupName);
            }
        }
    }

    private static String randomPadding() {
        return String.format("%05d", Math.abs(RANDOM.nextInt() % 100000));
    }
}
