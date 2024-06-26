// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) TypeSpec Code Generator.

package com.azure.resourcemanager.informaticadatamanagement.generated;

import com.azure.core.credential.AccessToken;
import com.azure.core.http.HttpClient;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.core.test.http.MockHttpResponse;
import com.azure.resourcemanager.informaticadatamanagement.InformaticaDataManagementManager;
import com.azure.resourcemanager.informaticadatamanagement.models.InformaticaServerlessRuntimeResourceList;
import com.azure.resourcemanager.informaticadatamanagement.models.RuntimeType;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

public final class OrganizationsGetAllServerlessRuntimesWithResponseMockTests {
    @Test
    public void testGetAllServerlessRuntimesWithResponse() throws Exception {
        String responseStr
            = "{\"informaticaRuntimeResources\":[{\"name\":\"nmwcpmgu\",\"createdTime\":\"adraufactkahzo\",\"updatedTime\":\"ajjziuxxpshne\",\"createdBy\":\"kulfg\",\"updatedBy\":\"lqubkwdlen\",\"id\":\"d\",\"type\":\"SERVERLESS\",\"status\":\"tujbazpju\",\"statusLocalized\":\"hminyflnorwmduv\",\"statusMessage\":\"pklvxw\",\"serverlessConfigProperties\":{\"subnet\":\"gdxpg\",\"applicationType\":\"chisze\",\"resourceGroupName\":\"nb\",\"advancedCustomProperties\":\"rxgibbd\",\"supplementaryFileLocation\":\"confozauors\",\"platform\":\"okwbqplh\",\"tags\":\"nuuepzlrp\",\"vnet\":\"zsoldwey\",\"executionTimeout\":\"dunvmnnrwrbiorkt\",\"computeUnits\":\"ywjhhgdnhx\",\"tenantId\":\"ivfomiloxgg\",\"subscriptionId\":\"fi\",\"region\":\"dieuzaofj\",\"serverlessArmResourceId\":\"vcyy\"},\"description\":\"fgdo\"},{\"name\":\"cubiipuipw\",\"createdTime\":\"qonmacj\",\"updatedTime\":\"k\",\"createdBy\":\"izsh\",\"updatedBy\":\"vcimpev\",\"id\":\"gmblrri\",\"type\":\"SERVERLESS\",\"status\":\"ywdxsmic\",\"statusLocalized\":\"wrwfscjfnyns\",\"statusMessage\":\"qujizdvo\",\"serverlessConfigProperties\":{\"subnet\":\"tiby\",\"applicationType\":\"bblgyavut\",\"resourceGroupName\":\"hjoxo\",\"advancedCustomProperties\":\"msksbp\",\"supplementaryFileLocation\":\"lqol\",\"platform\":\"kcgxxlxsffgcvi\",\"tags\":\"zdwlvwlyoupfgfb\",\"vnet\":\"ubdyhgk\",\"executionTimeout\":\"in\",\"computeUnits\":\"owzfttsttkt\",\"tenantId\":\"hbq\",\"subscriptionId\":\"tx\",\"region\":\"zukxitmmqtgqq\",\"serverlessArmResourceId\":\"hrnxrxc\"},\"description\":\"uisavokq\"}]}";

        HttpClient httpClient
            = response -> Mono.just(new MockHttpResponse(response, 200, responseStr.getBytes(StandardCharsets.UTF_8)));
        InformaticaDataManagementManager manager = InformaticaDataManagementManager.configure()
            .withHttpClient(httpClient)
            .authenticate(tokenRequestContext -> Mono.just(new AccessToken("this_is_a_token", OffsetDateTime.MAX)),
                new AzureProfile("", "", AzureEnvironment.AZURE));

        InformaticaServerlessRuntimeResourceList response = manager.organizations()
            .getAllServerlessRuntimesWithResponse("xtdr", "futacoebjvewzc", com.azure.core.util.Context.NONE)
            .getValue();

        Assertions.assertEquals("nmwcpmgu", response.informaticaRuntimeResources().get(0).name());
        Assertions.assertEquals("adraufactkahzo", response.informaticaRuntimeResources().get(0).createdTime());
        Assertions.assertEquals("ajjziuxxpshne", response.informaticaRuntimeResources().get(0).updatedTime());
        Assertions.assertEquals("kulfg", response.informaticaRuntimeResources().get(0).createdBy());
        Assertions.assertEquals("lqubkwdlen", response.informaticaRuntimeResources().get(0).updatedBy());
        Assertions.assertEquals("d", response.informaticaRuntimeResources().get(0).id());
        Assertions.assertEquals(RuntimeType.SERVERLESS, response.informaticaRuntimeResources().get(0).type());
        Assertions.assertEquals("tujbazpju", response.informaticaRuntimeResources().get(0).status());
        Assertions.assertEquals("hminyflnorwmduv", response.informaticaRuntimeResources().get(0).statusLocalized());
        Assertions.assertEquals("pklvxw", response.informaticaRuntimeResources().get(0).statusMessage());
        Assertions.assertEquals("gdxpg",
            response.informaticaRuntimeResources().get(0).serverlessConfigProperties().subnet());
        Assertions.assertEquals("chisze",
            response.informaticaRuntimeResources().get(0).serverlessConfigProperties().applicationType());
        Assertions.assertEquals("nb",
            response.informaticaRuntimeResources().get(0).serverlessConfigProperties().resourceGroupName());
        Assertions.assertEquals("rxgibbd",
            response.informaticaRuntimeResources().get(0).serverlessConfigProperties().advancedCustomProperties());
        Assertions.assertEquals("confozauors",
            response.informaticaRuntimeResources().get(0).serverlessConfigProperties().supplementaryFileLocation());
        Assertions.assertEquals("okwbqplh",
            response.informaticaRuntimeResources().get(0).serverlessConfigProperties().platform());
        Assertions.assertEquals("nuuepzlrp",
            response.informaticaRuntimeResources().get(0).serverlessConfigProperties().tags());
        Assertions.assertEquals("zsoldwey",
            response.informaticaRuntimeResources().get(0).serverlessConfigProperties().vnet());
        Assertions.assertEquals("dunvmnnrwrbiorkt",
            response.informaticaRuntimeResources().get(0).serverlessConfigProperties().executionTimeout());
        Assertions.assertEquals("ywjhhgdnhx",
            response.informaticaRuntimeResources().get(0).serverlessConfigProperties().computeUnits());
        Assertions.assertEquals("ivfomiloxgg",
            response.informaticaRuntimeResources().get(0).serverlessConfigProperties().tenantId());
        Assertions.assertEquals("fi",
            response.informaticaRuntimeResources().get(0).serverlessConfigProperties().subscriptionId());
        Assertions.assertEquals("dieuzaofj",
            response.informaticaRuntimeResources().get(0).serverlessConfigProperties().region());
        Assertions.assertEquals("vcyy",
            response.informaticaRuntimeResources().get(0).serverlessConfigProperties().serverlessArmResourceId());
        Assertions.assertEquals("fgdo", response.informaticaRuntimeResources().get(0).description());
    }
}
