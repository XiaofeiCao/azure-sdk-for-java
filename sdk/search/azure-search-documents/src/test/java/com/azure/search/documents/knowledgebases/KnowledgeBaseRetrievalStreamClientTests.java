// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.search.documents.knowledgebases;

import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.http.HttpHeaderName;
import com.azure.core.http.HttpHeaders;
import com.azure.core.http.HttpRequest;
import com.azure.core.http.ServerSentEvent;
import com.azure.core.test.http.MockHttpResponse;
import com.azure.search.documents.SearchServiceVersion;
import com.azure.search.documents.knowledgebases.models.KnowledgeBaseRetrievalOptions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KnowledgeBaseRetrievalStreamClientTests {
    private static final String SSE_RESPONSE = "id: event-1\n" + "event: retrieval.started\n"
        + "data: {\"requestId\":\"request-1\",\"knowledgeBaseName\":\"kb\","
        + "\"outputMode\":\"answerSynthesis\",\"reasoningEffort\":{\"kind\":\"minimal\"}}\n\n" + "id: event-2\n"
        + "event: future.event\n" + "data: {\"value\":\"future\"}\n\n" + "id: event-3\n" + "event: response.completed\n"
        + "data: {\"statusCode\":200,\"response\":{\"response\":[],\"activity\":[],\"references\":[]}}\n\n";

    @Test
    public void syncClientListensToTypedEvents() {
        AtomicReference<HttpRequest> sentRequest = new AtomicReference<>();
        KnowledgeBaseRetrievalClient client = createBuilder(sentRequest).buildClient();
        List<ServerSentEvent<KnowledgeBaseRetrievalStreamEvent>> events = new ArrayList<>();

        client.retrieveStream(new KnowledgeBaseRetrievalOptions(), "query-token", "work-iq-token", events::add);

        assertEquals(3, events.size());
        assertTrue(events.get(0).getData().isRetrievalStarted());
        assertEquals("event-1", events.get(0).getId());
        assertEquals("future.event", events.get(1).getEvent());
        assertFalse(events.get(1).getData().isTerminal());
        assertTrue(events.get(2).getData().isResponseCompleted());
        assertRequest(sentRequest.get());
    }

    @Test
    public void asyncClientReturnsTypedEvents() {
        AtomicReference<HttpRequest> sentRequest = new AtomicReference<>();
        KnowledgeBaseRetrievalAsyncClient client = createBuilder(sentRequest).buildAsyncClient();

        StepVerifier.create(client.retrieveStream(new KnowledgeBaseRetrievalOptions(), "query-token", "work-iq-token"))
            .assertNext(event -> {
                assertTrue(event.getData().isRetrievalStarted());
                assertEquals("event-1", event.getId());
            })
            .assertNext(event -> {
                assertEquals("future.event", event.getEvent());
                assertFalse(event.getData().isTerminal());
            })
            .assertNext(event -> assertTrue(event.getData().isResponseCompleted()))
            .verifyComplete();

        assertRequest(sentRequest.get());
    }

    private static KnowledgeBaseRetrievalClientBuilder createBuilder(AtomicReference<HttpRequest> sentRequest) {
        HttpHeaders responseHeaders = new HttpHeaders().set(HttpHeaderName.CONTENT_TYPE, "text/event-stream");
        return new KnowledgeBaseRetrievalClientBuilder().endpoint("https://test.search.windows.net")
            .credential(new AzureKeyCredential("key"))
            .knowledgeBaseName("kb")
            .serviceVersion(SearchServiceVersion.V2026_08_01_PREVIEW)
            .httpClient(request -> {
                sentRequest.set(request);
                return Mono.just(
                    new MockHttpResponse(request, 200, responseHeaders, SSE_RESPONSE.getBytes(StandardCharsets.UTF_8)));
            });
    }

    private static void assertRequest(HttpRequest request) {
        assertEquals("text/event-stream", request.getHeaders().getValue(HttpHeaderName.ACCEPT));
        assertEquals("query-token",
            request.getHeaders().getValue(HttpHeaderName.fromString("x-ms-query-source-authorization")));
        assertEquals("work-iq-token",
            request.getHeaders().getValue(HttpHeaderName.fromString("x-ms-query-work-iq-source-authorization")));
        assertTrue(request.getUrl().getQuery().contains("api-version=2026-08-01-preview"));
    }
}
