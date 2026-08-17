// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.search.documents.knowledgebases;

import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.http.HttpHeaderName;
import com.azure.core.http.HttpHeaders;
import com.azure.core.http.HttpRequest;
import com.azure.core.http.ServerSentEvent;
import com.azure.core.http.rest.RequestOptions;
import com.azure.core.http.rest.Response;
import com.azure.core.test.http.MockHttpResponse;
import com.azure.core.util.BinaryData;
import com.azure.search.documents.SearchServiceVersion;
import com.azure.search.documents.knowledgebases.models.KnowledgeBaseResponseCompletedEvent;
import com.azure.search.documents.knowledgebases.models.KnowledgeBaseRetrievalOptions;
import com.azure.search.documents.knowledgebases.models.KnowledgeBaseRetrievalStartedEvent;
import com.azure.search.documents.knowledgebases.models.KnowledgeBaseRetrievalStreamEvent;
import com.azure.search.documents.knowledgebases.models.UnknownKnowledgeBaseRetrievalStreamEvent;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KnowledgeBaseRetrievalStreamClientTests {
    private static final String SSE_RESPONSE = "id: event-1\n" + "event: retrieval.started\n"
        + "data: {\"requestId\":\"request-1\",\"knowledgeBaseName\":\"kb\","
        + "\"outputMode\":\"answerSynthesis\",\"reasoningEffort\":{\"kind\":\"minimal\"}}\n\n" + "id: event-2\n"
        + "event: future.event\n" + "data: {\"value\":\"future\"}\n\n" + "id: event-3\n" + "event: response.completed\n"
        + "data: {\"statusCode\":200,\"response\":{\"response\":[],\"activity\":[],\"references\":[]}}\n\n"
        + "id: event-4\n" + "event: retrieval.started\n"
        + "data: {\"requestId\":\"ignored\",\"knowledgeBaseName\":\"kb\",\"outputMode\":\"answerSynthesis\"}\n\n";
    private static final String SSE_EOF_RESPONSE = "id: event-1\n" + "event: retrieval.started\n"
        + "data: {\"requestId\":\"request-1\",\"knowledgeBaseName\":\"kb\","
        + "\"outputMode\":\"answerSynthesis\",\"reasoningEffort\":{\"kind\":\"minimal\"}}\n\n";

    @Test
    public void syncClientListensToTypedEvents() {
        AtomicReference<HttpRequest> sentRequest = new AtomicReference<>();
        KnowledgeBaseRetrievalClient client = createBuilder(sentRequest).buildClient();
        List<ServerSentEvent<KnowledgeBaseRetrievalStreamEvent>> events = new ArrayList<>();

        client.retrieveStream(new KnowledgeBaseRetrievalOptions(), "query-token", "work-iq-token", events::add);

        assertEquals(3, events.size());
        assertTrue(events.get(0).getData() instanceof KnowledgeBaseRetrievalStartedEvent);
        assertEquals("event-1", events.get(0).getId());
        assertEquals("future.event", events.get(1).getEvent());
        UnknownKnowledgeBaseRetrievalStreamEvent unknown
            = (UnknownKnowledgeBaseRetrievalStreamEvent) events.get(1).getData();
        assertEquals("{\"value\":\"future\"}", unknown.getRawData());
        assertFalse(events.get(1).getData().isTerminal());
        assertTrue(events.get(2).getData() instanceof KnowledgeBaseResponseCompletedEvent);
        assertTrue(events.get(2).getData().isTerminal());
        assertSseRequest(sentRequest.get());
        assertAuthorizationHeaders(sentRequest.get());
    }

    @Test
    public void asyncClientReturnsTypedEvents() {
        AtomicReference<HttpRequest> sentRequest = new AtomicReference<>();
        KnowledgeBaseRetrievalAsyncClient client = createBuilder(sentRequest).buildAsyncClient();

        StepVerifier.create(client.retrieveStream(new KnowledgeBaseRetrievalOptions(), "query-token", "work-iq-token"))
            .assertNext(event -> {
                assertTrue(event.getData() instanceof KnowledgeBaseRetrievalStartedEvent);
                assertEquals("event-1", event.getId());
            })
            .assertNext(event -> {
                assertEquals("future.event", event.getEvent());
                assertTrue(event.getData() instanceof UnknownKnowledgeBaseRetrievalStreamEvent);
                assertFalse(event.getData().isTerminal());
            })
            .assertNext(event -> {
                assertTrue(event.getData() instanceof KnowledgeBaseResponseCompletedEvent);
                assertTrue(event.getData().isTerminal());
            })
            .verifyComplete();

        assertSseRequest(sentRequest.get());
        assertAuthorizationHeaders(sentRequest.get());
    }

    @Test
    public void syncClientFailsAtEofWithoutReconnecting() {
        AtomicReference<HttpRequest> sentRequest = new AtomicReference<>();
        AtomicInteger requestCount = new AtomicInteger();
        KnowledgeBaseRetrievalClient client = createBuilder(sentRequest, SSE_EOF_RESPONSE, requestCount).buildClient();
        List<ServerSentEvent<KnowledgeBaseRetrievalStreamEvent>> events = new ArrayList<>();

        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> client.retrieveStream(new KnowledgeBaseRetrievalOptions(), events::add));

        assertEquals("The server-sent event stream ended before a terminal event.", exception.getMessage());
        assertEquals(1, events.size());
        assertTrue(events.get(0).getData() instanceof KnowledgeBaseRetrievalStartedEvent);
        assertEquals(1, requestCount.get());
    }

    @Test
    public void asyncClientFailsAtEofWithoutReconnecting() {
        AtomicReference<HttpRequest> sentRequest = new AtomicReference<>();
        AtomicInteger requestCount = new AtomicInteger();
        KnowledgeBaseRetrievalAsyncClient client
            = createBuilder(sentRequest, SSE_EOF_RESPONSE, requestCount).buildAsyncClient();

        StepVerifier.create(client.retrieveStream(new KnowledgeBaseRetrievalOptions()))
            .assertNext(event -> assertTrue(event.getData() instanceof KnowledgeBaseRetrievalStartedEvent))
            .expectErrorMessage("The server-sent event stream ended before a terminal event.")
            .verify();

        assertEquals(1, requestCount.get());
    }

    @Test
    public void syncClientExposesStreamProtocolMethod() {
        AtomicReference<HttpRequest> sentRequest = new AtomicReference<>();
        KnowledgeBaseRetrievalClient client = createBuilder(sentRequest).buildClient();

        Response<BinaryData> streamResponse = client.retrieveStreamWithResponse(
            BinaryData.fromObject(new KnowledgeBaseRetrievalOptions()), new RequestOptions());
        assertEquals(200, streamResponse.getStatusCode());
        assertSseRequest(sentRequest.get());
    }

    @Test
    public void asyncClientExposesStreamProtocolMethod() {
        AtomicReference<HttpRequest> sentRequest = new AtomicReference<>();
        KnowledgeBaseRetrievalAsyncClient client = createBuilder(sentRequest).buildAsyncClient();

        StepVerifier
            .create(client.retrieveStreamWithResponse(BinaryData.fromObject(new KnowledgeBaseRetrievalOptions()),
                new RequestOptions()))
            .assertNext(response -> assertEquals(200, response.getStatusCode()))
            .verifyComplete();
        assertSseRequest(sentRequest.get());
    }

    private static KnowledgeBaseRetrievalClientBuilder createBuilder(AtomicReference<HttpRequest> sentRequest) {
        return createBuilder(sentRequest, SSE_RESPONSE, new AtomicInteger());
    }

    private static KnowledgeBaseRetrievalClientBuilder createBuilder(AtomicReference<HttpRequest> sentRequest,
        String responseBody, AtomicInteger requestCount) {
        return new KnowledgeBaseRetrievalClientBuilder().endpoint("https://test.search.windows.net")
            .credential(new AzureKeyCredential("key"))
            .knowledgeBaseName("kb")
            .serviceVersion(SearchServiceVersion.V2026_08_01_PREVIEW)
            .httpClient(request -> {
                requestCount.incrementAndGet();
                sentRequest.set(request);
                HttpHeaders responseHeaders = new HttpHeaders().set(HttpHeaderName.CONTENT_TYPE, "text/event-stream");
                return Mono.just(
                    new MockHttpResponse(request, 200, responseHeaders, responseBody.getBytes(StandardCharsets.UTF_8)));
            });
    }

    private static void assertSseRequest(HttpRequest request) {
        assertEquals("text/event-stream", request.getHeaders().getValue(HttpHeaderName.ACCEPT));
        assertEquals("application/json", request.getHeaders().getValue(HttpHeaderName.CONTENT_TYPE));
        assertTrue(request.getUrl().getQuery().contains("api-version=2026-08-01-preview"));
    }

    private static void assertAuthorizationHeaders(HttpRequest request) {
        assertEquals("query-token",
            request.getHeaders().getValue(HttpHeaderName.fromString("x-ms-query-source-authorization")));
        assertEquals("work-iq-token",
            request.getHeaders().getValue(HttpHeaderName.fromString("x-ms-query-work-iq-source-authorization")));
    }
}
