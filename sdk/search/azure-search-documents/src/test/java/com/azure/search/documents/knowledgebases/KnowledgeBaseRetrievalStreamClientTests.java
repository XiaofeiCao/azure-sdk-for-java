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
import com.azure.search.documents.knowledgebases.models.KnowledgeBaseRetrievalOptions;
import com.azure.search.documents.knowledgebases.models.KnowledgeBaseRetrievalStreamEvent;
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
    private static final String JSON_RESPONSE = "{\"response\":[],\"activity\":[],\"references\":[]}";
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
        assertSseRequest(sentRequest.get());
        assertAuthorizationHeaders(sentRequest.get());
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

        assertSseRequest(sentRequest.get());
        assertAuthorizationHeaders(sentRequest.get());
    }

    @Test
    public void syncClientExposesSeparateProtocolMethods() {
        AtomicReference<HttpRequest> sentRequest = new AtomicReference<>();
        KnowledgeBaseRetrievalClient client = createBuilder(sentRequest).buildClient();

        Response<BinaryData> jsonResponse = client
            .retrieveWithResponse(BinaryData.fromObject(new KnowledgeBaseRetrievalOptions()), new RequestOptions());
        assertEquals(200, jsonResponse.getStatusCode());
        assertJsonRequest(sentRequest.get());

        Response<BinaryData> streamResponse = client.retrieveStreamWithResponse(
            BinaryData.fromObject(new KnowledgeBaseRetrievalOptions()), new RequestOptions());
        assertEquals(200, streamResponse.getStatusCode());
        assertSseRequest(sentRequest.get());
    }

    @Test
    public void asyncClientExposesSeparateProtocolMethods() {
        AtomicReference<HttpRequest> sentRequest = new AtomicReference<>();
        KnowledgeBaseRetrievalAsyncClient client = createBuilder(sentRequest).buildAsyncClient();

        StepVerifier
            .create(client.retrieveWithResponse(BinaryData.fromObject(new KnowledgeBaseRetrievalOptions()),
                new RequestOptions()))
            .assertNext(response -> assertEquals(200, response.getStatusCode()))
            .verifyComplete();
        assertJsonRequest(sentRequest.get());

        StepVerifier
            .create(client.retrieveStreamWithResponse(BinaryData.fromObject(new KnowledgeBaseRetrievalOptions()),
                new RequestOptions()))
            .assertNext(response -> assertEquals(200, response.getStatusCode()))
            .verifyComplete();
        assertSseRequest(sentRequest.get());
    }

    private static KnowledgeBaseRetrievalClientBuilder createBuilder(AtomicReference<HttpRequest> sentRequest) {
        return new KnowledgeBaseRetrievalClientBuilder().endpoint("https://test.search.windows.net")
            .credential(new AzureKeyCredential("key"))
            .knowledgeBaseName("kb")
            .serviceVersion(SearchServiceVersion.V2026_08_01_PREVIEW)
            .httpClient(request -> {
                sentRequest.set(request);
                boolean isEventStream
                    = "text/event-stream".equals(request.getHeaders().getValue(HttpHeaderName.ACCEPT));
                String contentType = isEventStream ? "text/event-stream" : "application/json";
                String body = isEventStream ? SSE_RESPONSE : JSON_RESPONSE;
                HttpHeaders responseHeaders = new HttpHeaders().set(HttpHeaderName.CONTENT_TYPE, contentType);
                return Mono
                    .just(new MockHttpResponse(request, 200, responseHeaders, body.getBytes(StandardCharsets.UTF_8)));
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

    private static void assertJsonRequest(HttpRequest request) {
        assertEquals("application/json;odata.metadata=minimal", request.getHeaders().getValue(HttpHeaderName.ACCEPT));
        assertEquals("application/json", request.getHeaders().getValue(HttpHeaderName.CONTENT_TYPE));
        assertTrue(request.getUrl().getQuery().contains("api-version=2026-08-01-preview"));
    }
}
