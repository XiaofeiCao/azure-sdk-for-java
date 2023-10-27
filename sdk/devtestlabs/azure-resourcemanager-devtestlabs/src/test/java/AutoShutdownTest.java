import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.Region;
import com.azure.core.management.profile.AzureProfile;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.resourcemanager.compute.ComputeManager;
import com.azure.resourcemanager.compute.models.VirtualMachine;
import com.azure.resourcemanager.devtestlabs.DevTestLabsManager;
import com.azure.resourcemanager.devtestlabs.fluent.models.ScheduleInner;
import com.azure.resourcemanager.devtestlabs.models.DayDetails;
import com.azure.resourcemanager.devtestlabs.models.EnableStatus;
import org.junit.jupiter.api.Test;

/**
 * @author xiaofeicao
 * @createdAt 2023-10-27 10:51 AM
 */
public class AutoShutdownTest {

    @Test
    public void autoShutdown() {
        ComputeManager computeManager = ComputeManager.authenticate(new DefaultAzureCredentialBuilder().build(), new AzureProfile(AzureEnvironment.AZURE));
        String vmName = "test2023102702";
        VirtualMachine vm = computeManager.virtualMachines().getByResourceGroup("rg-xiaofei1", vmName);
        Region regionOfTheVM = Region.US_WEST;
        String resourceGroupNameOfTheVM = "rg-xiaofei1";
        DevTestLabsManager manager = DevTestLabsManager.authenticate(new DefaultAzureCredentialBuilder().build(), new AzureProfile(AzureEnvironment.AZURE));
        manager.globalSchedules()
            .define("shutdown-computevm-" + vmName)
            .withRegion(regionOfTheVM)
            .withExistingResourceGroup(resourceGroupNameOfTheVM)
            .withTargetResourceId(vm.id())
            .withStatus(EnableStatus.DISABLED)
            .withTaskType("ComputeVmShutdownTask")
            .withDailyRecurrence(new DayDetails().withTime("0000")) // this has no effect but is required by backend
            .withTimeZoneId("Pacific Standard time") // this has no effect but is required by backend
            .create();
    }
}
