// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.datafactory.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.datafactory.models.Activity;
import com.azure.resourcemanager.datafactory.models.ActivityDependency;
import com.azure.resourcemanager.datafactory.models.ActivityOnInactiveMarkAs;
import com.azure.resourcemanager.datafactory.models.ActivityState;
import com.azure.resourcemanager.datafactory.models.DependencyCondition;
import com.azure.resourcemanager.datafactory.models.Expression;
import com.azure.resourcemanager.datafactory.models.ForEachActivity;
import com.azure.resourcemanager.datafactory.models.UserProperty;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;

public final class ForEachActivityTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        ForEachActivity model = BinaryData.fromString(
            "{\"type\":\"o\",\"typeProperties\":{\"isSequential\":false,\"batchCount\":305424192,\"items\":{\"value\":\"fdgnaoirrufdgt\"},\"activities\":[{\"type\":\"esrhvgp\",\"name\":\"fzdgsmeeqelmrpvg\",\"description\":\"rgqskd\",\"state\":\"Active\",\"onInactiveMarkAs\":\"Succeeded\",\"dependsOn\":[{\"activity\":\"dxjxkxvgodekfe\",\"dependencyConditions\":[\"Failed\",\"Completed\"],\"\":{\"ooqjraknngad\":\"datarfeqefqd\",\"pvomxtosdbv\":\"datallhzlicvrdwjght\",\"eebzewbifcyptl\":\"datadoieo\"}}],\"userProperties\":[{\"name\":\"hdlrrivvuewrhk\",\"value\":\"datamphfhmuaoouu\"}],\"\":{\"olhhlggobjc\":\"datadggsr\"}}]},\"name\":\"rphzd\",\"description\":\"kjyhaqkglupmyqi\",\"state\":\"Inactive\",\"onInactiveMarkAs\":\"Failed\",\"dependsOn\":[{\"activity\":\"vvmmjw\",\"dependencyConditions\":[\"Succeeded\",\"Skipped\"],\"\":{\"catyryykonvenmv\":\"dataglm\",\"zqfzbiyv\":\"databgpgvliinueltcoi\"}},{\"activity\":\"wyyvsbjpyxlzxjir\",\"dependencyConditions\":[\"Succeeded\",\"Completed\",\"Completed\",\"Failed\"],\"\":{\"scitizroru\":\"datadgoqxf\",\"ehagorbspotq\":\"dataduwqovlqfz\"}}],\"userProperties\":[{\"name\":\"vubfugd\",\"value\":\"datapmtzqpivochmexim\"},{\"name\":\"misve\",\"value\":\"datauqibkjam\"},{\"name\":\"h\",\"value\":\"datarulgypnaqwjsdwn\"}],\"\":{\"ygn\":\"dataqntxbeeziiqix\",\"euztpss\":\"datarynfoa\"}}")
            .toObject(ForEachActivity.class);
        Assertions.assertEquals("rphzd", model.name());
        Assertions.assertEquals("kjyhaqkglupmyqi", model.description());
        Assertions.assertEquals(ActivityState.INACTIVE, model.state());
        Assertions.assertEquals(ActivityOnInactiveMarkAs.FAILED, model.onInactiveMarkAs());
        Assertions.assertEquals("vvmmjw", model.dependsOn().get(0).activity());
        Assertions.assertEquals(DependencyCondition.SUCCEEDED, model.dependsOn().get(0).dependencyConditions().get(0));
        Assertions.assertEquals("vubfugd", model.userProperties().get(0).name());
        Assertions.assertEquals(false, model.isSequential());
        Assertions.assertEquals(305424192, model.batchCount());
        Assertions.assertEquals("fdgnaoirrufdgt", model.items().value());
        Assertions.assertEquals("fzdgsmeeqelmrpvg", model.activities().get(0).name());
        Assertions.assertEquals("rgqskd", model.activities().get(0).description());
        Assertions.assertEquals(ActivityState.ACTIVE, model.activities().get(0).state());
        Assertions.assertEquals(ActivityOnInactiveMarkAs.SUCCEEDED, model.activities().get(0).onInactiveMarkAs());
        Assertions.assertEquals("dxjxkxvgodekfe", model.activities().get(0).dependsOn().get(0).activity());
        Assertions.assertEquals(DependencyCondition.FAILED,
            model.activities().get(0).dependsOn().get(0).dependencyConditions().get(0));
        Assertions.assertEquals("hdlrrivvuewrhk", model.activities().get(0).userProperties().get(0).name());
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        ForEachActivity model
            = new ForEachActivity().withName("rphzd")
                .withDescription("kjyhaqkglupmyqi")
                .withState(ActivityState.INACTIVE)
                .withOnInactiveMarkAs(ActivityOnInactiveMarkAs.FAILED)
                .withDependsOn(Arrays.asList(
                    new ActivityDependency().withActivity("vvmmjw")
                        .withDependencyConditions(
                            Arrays.asList(DependencyCondition.SUCCEEDED, DependencyCondition.SKIPPED))
                        .withAdditionalProperties(mapOf()),
                    new ActivityDependency().withActivity("wyyvsbjpyxlzxjir")
                        .withDependencyConditions(Arrays.asList(DependencyCondition.SUCCEEDED,
                            DependencyCondition.COMPLETED, DependencyCondition.COMPLETED, DependencyCondition.FAILED))
                        .withAdditionalProperties(mapOf())))
                .withUserProperties(Arrays.asList(
                    new UserProperty().withName("vubfugd").withValue("datapmtzqpivochmexim"),
                    new UserProperty().withName("misve").withValue("datauqibkjam"),
                    new UserProperty().withName("h").withValue("datarulgypnaqwjsdwn")))
                .withIsSequential(false)
                .withBatchCount(305424192)
                .withItems(new Expression().withValue("fdgnaoirrufdgt"))
                .withActivities(Arrays.asList(new Activity().withName("fzdgsmeeqelmrpvg")
                    .withDescription("rgqskd")
                    .withState(ActivityState.ACTIVE)
                    .withOnInactiveMarkAs(ActivityOnInactiveMarkAs.SUCCEEDED)
                    .withDependsOn(Arrays.asList(new ActivityDependency().withActivity("dxjxkxvgodekfe")
                        .withDependencyConditions(
                            Arrays.asList(DependencyCondition.FAILED, DependencyCondition.COMPLETED))
                        .withAdditionalProperties(mapOf())))
                    .withUserProperties(
                        Arrays.asList(new UserProperty().withName("hdlrrivvuewrhk").withValue("datamphfhmuaoouu")))
                    .withAdditionalProperties(mapOf("type", "esrhvgp"))));
        model = BinaryData.fromObject(model).toObject(ForEachActivity.class);
        Assertions.assertEquals("rphzd", model.name());
        Assertions.assertEquals("kjyhaqkglupmyqi", model.description());
        Assertions.assertEquals(ActivityState.INACTIVE, model.state());
        Assertions.assertEquals(ActivityOnInactiveMarkAs.FAILED, model.onInactiveMarkAs());
        Assertions.assertEquals("vvmmjw", model.dependsOn().get(0).activity());
        Assertions.assertEquals(DependencyCondition.SUCCEEDED, model.dependsOn().get(0).dependencyConditions().get(0));
        Assertions.assertEquals("vubfugd", model.userProperties().get(0).name());
        Assertions.assertEquals(false, model.isSequential());
        Assertions.assertEquals(305424192, model.batchCount());
        Assertions.assertEquals("fdgnaoirrufdgt", model.items().value());
        Assertions.assertEquals("fzdgsmeeqelmrpvg", model.activities().get(0).name());
        Assertions.assertEquals("rgqskd", model.activities().get(0).description());
        Assertions.assertEquals(ActivityState.ACTIVE, model.activities().get(0).state());
        Assertions.assertEquals(ActivityOnInactiveMarkAs.SUCCEEDED, model.activities().get(0).onInactiveMarkAs());
        Assertions.assertEquals("dxjxkxvgodekfe", model.activities().get(0).dependsOn().get(0).activity());
        Assertions.assertEquals(DependencyCondition.FAILED,
            model.activities().get(0).dependsOn().get(0).dependencyConditions().get(0));
        Assertions.assertEquals("hdlrrivvuewrhk", model.activities().get(0).userProperties().get(0).name());
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
