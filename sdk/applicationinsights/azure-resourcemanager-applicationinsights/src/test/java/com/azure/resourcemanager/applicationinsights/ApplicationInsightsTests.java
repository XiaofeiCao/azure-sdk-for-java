package com.azure.resourcemanager.applicationinsights;

import com.azure.core.http.policy.HttpLogDetailLevel;
import com.azure.core.http.policy.HttpLogOptions;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.Region;
import com.azure.core.management.profile.AzureProfile;
import com.azure.core.test.TestBase;
import com.azure.core.util.Context;
import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.resourcemanager.applicationinsights.models.ApplicationInsightsComponent;
import com.azure.resourcemanager.applicationinsights.models.ApplicationType;
import com.azure.resourcemanager.applicationinsights.models.FlowType;
import com.azure.resourcemanager.applicationinsights.models.RequestSource;
import com.azure.resourcemanager.loganalytics.LogAnalyticsManager;
import com.azure.resourcemanager.loganalytics.models.Workspace;
import com.azure.resourcemanager.loganalytics.models.WorkspacePurgeBody;
import com.azure.resourcemanager.loganalytics.models.WorkspacePurgeBodyFilters;
import com.azure.resourcemanager.loganalytics.models.WorkspaceSku;
import com.azure.resourcemanager.loganalytics.models.WorkspaceSkuNameEnum;
import com.azure.resourcemanager.resources.ResourceManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class ApplicationInsightsTests extends TestBase {

    @Test
    public void test() {
        DefaultAzureCredential credential = new DefaultAzureCredentialBuilder().build();
        AzureProfile profile = new AzureProfile(System.getenv("AZURE_TENANT_ID"), System.getenv("AZURE_SUBSCRIPTION_ID"), AzureEnvironment.AZURE);
        LogAnalyticsManager logAnalyticsManager = LogAnalyticsManager.configure().withLogOptions(new HttpLogOptions().setLogLevel(HttpLogDetailLevel.BODY_AND_HEADERS)).authenticate(credential, profile);
        ResourceManager resourceManager = ResourceManager.configure().withLogOptions(new HttpLogOptions().setLogLevel(HttpLogDetailLevel.BODY_AND_HEADERS)).authenticate(credential, profile).withDefaultSubscription();
        ApplicationInsightsManager applicationInsightsManager = ApplicationInsightsManager.configure().withLogOptions(new HttpLogOptions().setLogLevel(HttpLogDetailLevel.BODY_AND_HEADERS)).authenticate(credential, profile);
        String rgName = testResourceNamer.randomName("javamrg", 15);
        Region region = Region.US_EAST;
        try {
            resourceManager.resourceGroups().define(rgName).withRegion(region).create();

            String workspaceName = testResourceNamer.randomName("wsp", 15);
            Workspace workspace = logAnalyticsManager.workspaces()
                .define(workspaceName)
                .withRegion(region)
                .withExistingResourceGroup(rgName)
                .withSku(new WorkspaceSku().withName(WorkspaceSkuNameEnum.PER_GB2018))
                .withRetentionInDays(30)
                .create();

            String componentName = testResourceNamer.randomName("component", 15);
            ApplicationInsightsComponent component = applicationInsightsManager
                .components()
                .define(componentName)
                .withRegion(region)
                .withExistingResourceGroup(rgName)
                .withKind("web")
                .withApplicationType(ApplicationType.WEB)
                .withFlowType(FlowType.BLUEFIELD)
                .withRequestSource(RequestSource.REST)
                .withWorkspaceResourceId(workspace.id())
                .create();

            Assertions.assertNotNull(component);
            logAnalyticsManager.workspacePurges()
                .purgeWithResponse(
                    rgName,
                    workspaceName,
                    new WorkspacePurgeBody()
                        .withTable("Heartbeat")
                        .withFilters(
                            Arrays
                                .asList(
                                    new WorkspacePurgeBodyFilters()
                                        .withColumn("_ResourceId")
                                        .withOperator("==")
                                        .withValue(
                                            "/subscriptions/12341234-1234-1234-1234-123412341234/resourceGroups/SomeResourceGroup/providers/microsoft.insights/components/AppInsightResource"))),
                    Context.NONE);
        } finally {
            resourceManager.resourceGroups().beginDeleteByName(rgName);
        }
    }
}
