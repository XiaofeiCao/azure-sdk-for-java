package com.azure.ai.openai;

import com.azure.ai.openai.models.Completions;
import com.azure.ai.openai.models.CompletionsOptions;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.http.rest.RequestOptions;
import com.azure.core.test.TestProxyTestBase;
import com.azure.core.util.BinaryData;
import com.azure.core.util.Configuration;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xiaofeicao
 * @createdAt 2023-07-20 1:35 PM
 */
public class OpenAITests extends TestProxyTestBase {

    private final OpenAIClient client = new OpenAIClientBuilder()
        .credential(new AzureKeyCredential(Configuration.getGlobalConfiguration().get("AZURE_OPENAI_KEY")))
        .endpoint(Configuration.getGlobalConfiguration().get("ENDPOINT"))
        .buildClient();

    @Test
    public void testCompletion() {
        Completions completions = client.getCompletions(
            "gpt-35-turbo",
            new CompletionsOptions(Arrays.asList("Tell me 3 jokes about trains:"))
                .setTemperature(0.0)
                .setMaxTokens(128));
        System.out.println(completions.getChoices().get(0).getText());
    }

    @Test
    public void testCompletionProtocol() {
        Map<String, Object> completionOptions = new HashMap<>();
        completionOptions.put("prompt", Arrays.asList("Tell me 3 jokes about trains:"));
        completionOptions.put("temperature", 0.0f);
        completionOptions.put("max_tokens", 128);
        BinaryData result = client.getCompletionsWithResponse(
            "gpt-35-turbo",
            BinaryData.fromObject(completionOptions),
            new RequestOptions()).getValue();
        System.out.println(result.toString());
    }
}
