// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.search.documents.codesnippets;

import com.azure.core.credential.AzureKeyCredential;
import com.azure.search.documents.SearchServiceVersion;
import com.azure.search.documents.knowledgebases.KnowledgeBaseRetrievalAsyncClient;
import com.azure.search.documents.knowledgebases.KnowledgeBaseRetrievalClient;
import com.azure.search.documents.knowledgebases.KnowledgeBaseRetrievalClientBuilder;
import com.azure.search.documents.knowledgebases.models.KnowledgeBaseMessageTextContent;
import com.azure.search.documents.knowledgebases.models.KnowledgeBaseReference;
import com.azure.search.documents.knowledgebases.models.KnowledgeBaseRetrievalOptions;
import com.azure.search.documents.knowledgebases.models.KnowledgeBaseRetrievalResult;
import com.azure.search.documents.knowledgebases.models.KnowledgeBaseRetrievalStreamEvent;
import com.azure.search.documents.knowledgebases.models.KnowledgeRetrievalSemanticIntent;
import com.azure.search.documents.knowledgebases.models.SearchIndexKnowledgeSourceParams;

import java.util.Arrays;

@SuppressWarnings("unused")
public class KnowledgeBaseRetrievalJavaDocSnippets {

    private static KnowledgeBaseRetrievalClient retrievalClient;
    private static KnowledgeBaseRetrievalAsyncClient retrievalAsyncClient;

    /**
     * Code snippet for creating a {@link KnowledgeBaseRetrievalClient}.
     */
    private static KnowledgeBaseRetrievalClient createRetrievalClient() {
        // BEGIN: com.azure.search.documents.knowledgebases.KnowledgeBaseRetrievalClient.instantiation
        KnowledgeBaseRetrievalClient retrievalClient = new KnowledgeBaseRetrievalClientBuilder()
            .credential(new AzureKeyCredential("{key}"))
            .endpoint("{endpoint}")
            .knowledgeBaseName("my-knowledge-base")
            .serviceVersion(SearchServiceVersion.V2026_08_01_PREVIEW)
            .buildClient();
        // END: com.azure.search.documents.knowledgebases.KnowledgeBaseRetrievalClient.instantiation
        return retrievalClient;
    }

    private static KnowledgeBaseRetrievalAsyncClient createRetrievalAsyncClient() {
        return new KnowledgeBaseRetrievalClientBuilder()
            .credential(new AzureKeyCredential("{key}"))
            .endpoint("{endpoint}")
            .knowledgeBaseName("my-knowledge-base")
            .serviceVersion(SearchServiceVersion.V2026_08_01_PREVIEW)
            .buildAsyncClient();
    }

    /**
     * Code snippet for a simple retrieval using a semantic intent.
     */
    public static void retrieve() {
        retrievalClient = createRetrievalClient();
        // BEGIN: com.azure.search.documents.knowledgebases.KnowledgeBaseRetrievalClient.retrieve#KnowledgeBaseRetrievalOptions
        KnowledgeBaseRetrievalOptions request = new KnowledgeBaseRetrievalOptions()
            .setIntents(new KnowledgeRetrievalSemanticIntent("What hotels are near the ocean?"));

        KnowledgeBaseRetrievalResult response = retrievalClient.retrieve(request);

        response.getResponse().forEach(message ->
            message.getContent().forEach(content -> {
                if (content instanceof KnowledgeBaseMessageTextContent) {
                    System.out.println(((KnowledgeBaseMessageTextContent) content).getText());
                }
            }));
        // END: com.azure.search.documents.knowledgebases.KnowledgeBaseRetrievalClient.retrieve#KnowledgeBaseRetrievalOptions
    }

    /**
     * Code snippet for retrieval using an explicit semantic intent (bypasses model query planning).
     */
    public static void retrieveWithIntent() {
        retrievalClient = createRetrievalClient();
        // BEGIN: com.azure.search.documents.knowledgebases.KnowledgeBaseRetrievalClient.retrieve.withIntent
        KnowledgeBaseRetrievalOptions request = new KnowledgeBaseRetrievalOptions()
            .setIntents(new KnowledgeRetrievalSemanticIntent("hotels near the ocean with free parking"));

        KnowledgeBaseRetrievalResult response = retrievalClient.retrieve(request);

        response.getResponse().forEach(message ->
            message.getContent().forEach(content -> {
                if (content instanceof KnowledgeBaseMessageTextContent) {
                    System.out.println(((KnowledgeBaseMessageTextContent) content).getText());
                }
            }));
        // END: com.azure.search.documents.knowledgebases.KnowledgeBaseRetrievalClient.retrieve.withIntent
    }

    /**
     * Code snippet for retrieval with runtime knowledge source params and references.
     */
    public static void retrieveWithSourceParamsAndReferences() {
        retrievalClient = createRetrievalClient();
        // BEGIN: com.azure.search.documents.knowledgebases.KnowledgeBaseRetrievalClient.retrieve.withSourceParams
        KnowledgeBaseRetrievalOptions request = new KnowledgeBaseRetrievalOptions()
            .setIntents(new KnowledgeRetrievalSemanticIntent("What hotels are available in Virginia?"))
            .setKnowledgeSourceParams(Arrays.asList(
                new SearchIndexKnowledgeSourceParams("my-knowledge-source")
                    .setFilterAddOn("Address/StateProvince eq 'VA'")
                    .setIncludeReferences(true)
                    .setIncludeReferenceSourceData(true)))
            .setIncludeActivity(true);

        KnowledgeBaseRetrievalResult response = retrievalClient.retrieve(request);

        // Print the assistant response
        response.getResponse().forEach(message ->
            message.getContent().forEach(content -> {
                if (content instanceof KnowledgeBaseMessageTextContent) {
                    System.out.println(((KnowledgeBaseMessageTextContent) content).getText());
                }
            }));

        // Print the source references
        for (KnowledgeBaseReference reference : response.getReferences()) {
            System.out.println("Reference [" + reference.getId() + "] score: " + reference.getRerankerScore());
        }
        // END: com.azure.search.documents.knowledgebases.KnowledgeBaseRetrievalClient.retrieve.withSourceParams
    }

    /**
     * Code snippet for synchronous streaming retrieval.
     */
    public static void retrieveStream() {
        retrievalClient = createRetrievalClient();
        // BEGIN: com.azure.search.documents.knowledgebases.KnowledgeBaseRetrievalClient.retrieveStream
        KnowledgeBaseRetrievalOptions request = new KnowledgeBaseRetrievalOptions()
            .setIntents(new KnowledgeRetrievalSemanticIntent("What hotels are near the ocean?"));

        retrievalClient.retrieveStream(request, event -> {
            KnowledgeBaseRetrievalStreamEvent data = event.getData();
            if (data != null && data.isAnswerCompleted()) {
                System.out.println(data.asAnswerCompleted().getMessage());
            } else if (data != null && data.isResponseCompleted()) {
                System.out.println("Retrieval completed with status "
                    + data.asResponseCompleted().getStatusCode());
            }
        });
        // END: com.azure.search.documents.knowledgebases.KnowledgeBaseRetrievalClient.retrieveStream
    }

    /**
     * Code snippet for asynchronous streaming retrieval.
     */
    public static void retrieveStreamAsync() {
        retrievalAsyncClient = createRetrievalAsyncClient();
        // BEGIN: com.azure.search.documents.knowledgebases.KnowledgeBaseRetrievalAsyncClient.retrieveStream
        KnowledgeBaseRetrievalOptions request = new KnowledgeBaseRetrievalOptions()
            .setIntents(new KnowledgeRetrievalSemanticIntent("What hotels are near the ocean?"));

        retrievalAsyncClient.retrieveStream(request)
            .map(event -> event.getData())
            .filter(data -> data != null && data.isAnswerCompleted())
            .doOnNext(data -> System.out.println(data.asAnswerCompleted().getMessage()))
            .blockLast();
        // END: com.azure.search.documents.knowledgebases.KnowledgeBaseRetrievalAsyncClient.retrieveStream
    }
}
