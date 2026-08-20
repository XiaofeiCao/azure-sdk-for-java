// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.search.documents.knowledgebases;

import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.http.HttpHeaderName;
import com.azure.core.http.HttpHeaders;
import com.azure.core.http.HttpRequest;
import com.azure.core.http.HttpResponse;
import com.azure.core.http.policy.HttpLogDetailLevel;
import com.azure.core.http.policy.HttpLogOptions;
import com.azure.core.http.rest.RequestOptions;
import com.azure.core.http.rest.StreamResponse;
import com.azure.core.exception.HttpResponseException;
import com.azure.core.test.http.MockHttpResponse;
import com.azure.core.util.BinaryData;
import com.azure.search.documents.knowledgebases.models.KnowledgeBaseRetrievalOptions;
import com.azure.search.documents.knowledgebases.models.KnowledgeBaseRetrievalStreamEvent;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KnowledgeBaseRetrievalStreamClientTests {
    private static final String SSE_RESPONSE = "id: event-1\n" + "event: retrieval.started\n"
        + "data: {\"requestId\":\"request-1\",\"knowledgeBaseName\":\"kb\","
        + "\"outputMode\":\"answerSynthesis\",\"reasoningEffort\":{\"kind\":\"minimal\"}}\n\n" + "id: event-2\n"
        + "event: future.event\n" + "data: {\"value\":\"future\"}\n\n" + "id: event-3\n" + "event: response.completed\n"
        + "data: {\"statusCode\":200,\"response\":{\"response\":[],\"activity\":[],\"references\":[]}}\n\n";

    @Test
    public void syncClientListensToTypedEventsAndAppliesRequestOptions() {
        AtomicReference<HttpRequest> sentRequest = new AtomicReference<>();
        KnowledgeBaseRetrievalClient client
            = createBuilder(sentRequest, request -> eventStreamResponse(request)).buildClient();
        List<KnowledgeBaseRetrievalStreamEvent> events = new ArrayList<>();
        RequestOptions requestOptions = new RequestOptions().setHeader(HttpHeaderName.fromString("x-test"), "value");

        client.retrieveStream(new KnowledgeBaseRetrievalOptions(), events::add, requestOptions);

        assertEquals(3, events.size());
        assertTrue(events.get(0).isRetrievalStarted());
        assertEquals("event-1", events.get(0).getId());
        assertEquals("future.event", events.get(1).getEvent());
        assertEquals("{\"value\":\"future\"}", events.get(1).getData());
        assertFalse(events.get(1).isTerminal());
        assertTrue(events.get(2).isResponseCompleted());
        assertSseRequest(sentRequest.get());
        assertEquals("value", sentRequest.get().getHeaders().getValue(HttpHeaderName.fromString("x-test")));
    }

    @Test
    public void asyncClientReturnsTypedEventsAndAppliesAuthorizationHeaders() {
        AtomicReference<HttpRequest> sentRequest = new AtomicReference<>();
        KnowledgeBaseRetrievalAsyncClient client
            = createBuilder(sentRequest, request -> eventStreamResponse(request)).buildAsyncClient();

        StepVerifier.create(client.retrieveStream(new KnowledgeBaseRetrievalOptions(), "query-token", "work-iq-token"))
            .assertNext(event -> {
                assertTrue(event.isRetrievalStarted());
                assertEquals("event-1", event.getId());
            })
            .assertNext(event -> assertEquals("future.event", event.getEvent()))
            .assertNext(event -> assertTrue(event.isResponseCompleted()))
            .verifyComplete();

        assertSseRequest(sentRequest.get());
        assertEquals("query-token",
            sentRequest.get().getHeaders().getValue(HttpHeaderName.fromString("x-ms-query-source-authorization")));
        assertEquals("work-iq-token",
            sentRequest.get()
                .getHeaders()
                .getValue(HttpHeaderName.fromString("x-ms-query-work-iq-source-authorization")));
    }

    @Test
    public void protocolMethodsExposeStreamingResponse() {
        AtomicReference<HttpRequest> sentRequest = new AtomicReference<>();
        KnowledgeBaseRetrievalClientBuilder builder
            = createBuilder(sentRequest, request -> eventStreamResponse(request));

        StreamResponse syncResponse = builder.buildClient()
            .retrieveStreamWithResponse(BinaryData.fromObject(new KnowledgeBaseRetrievalOptions()),
                new RequestOptions());
        assertEquals(200, syncResponse.getStatusCode());
        assertEquals(SSE_RESPONSE, collect(syncResponse));

        StepVerifier.create(builder.buildAsyncClient()
            .retrieveStreamWithResponse(BinaryData.fromObject(new KnowledgeBaseRetrievalOptions()),
                new RequestOptions()))
            .assertNext(response -> {
                assertEquals(200, response.getStatusCode());
                assertEquals(SSE_RESPONSE, collect(response));
            })
            .verifyComplete();
    }

    @Test
    public void bodyAndHeadersLoggingStillDeliversEvents() {
        AtomicReference<HttpRequest> sentRequest = new AtomicReference<>();
        KnowledgeBaseRetrievalAsyncClient client
            = createBuilder(sentRequest, KnowledgeBaseRetrievalStreamClientTests::eventStreamResponse)
                .httpLogOptions(new HttpLogOptions().setLogLevel(HttpLogDetailLevel.BODY_AND_HEADERS))
                .buildAsyncClient();

        StepVerifier.create(client.retrieveStream(new KnowledgeBaseRetrievalOptions()))
            .expectNextCount(3)
            .verifyComplete();
    }

    @Test
    public void noContentCompletesWithoutEventsAndClosesResponse() {
        AtomicReference<TrackingHttpResponse> response = new AtomicReference<>();
        KnowledgeBaseRetrievalAsyncClient client = createBuilder(new AtomicReference<>(), request -> {
            TrackingHttpResponse value = new TrackingHttpResponse(request, 204, new HttpHeaders(), Flux.empty());
            response.set(value);
            return value;
        }).buildAsyncClient();

        StepVerifier.create(client.retrieveStream(new KnowledgeBaseRetrievalOptions())).verifyComplete();
        assertTrue(response.get().isClosed());
    }

    @Test
    public void httpErrorsPropagateWithoutParsingAsSse() {
        KnowledgeBaseRetrievalAsyncClient client = createBuilder(new AtomicReference<>(),
            request -> new MockHttpResponse(request, 500,
                new HttpHeaders().set(HttpHeaderName.CONTENT_TYPE, "application/json"),
                "{\"error\":{\"code\":\"InternalError\"}}".getBytes(StandardCharsets.UTF_8))).buildAsyncClient();

        StepVerifier.create(client.retrieveStream(new KnowledgeBaseRetrievalOptions()))
            .expectError(HttpResponseException.class)
            .verify();
    }

    @Test
    public void interruptingSyncListenerCancelsAndClosesResponse() throws Exception {
        AtomicReference<TrackingHttpResponse> response = new AtomicReference<>();
        CountDownLatch subscribed = new CountDownLatch(1);
        KnowledgeBaseRetrievalClient client = createBuilder(new AtomicReference<>(), request -> {
            HttpHeaders headers = new HttpHeaders().set(HttpHeaderName.CONTENT_TYPE, "text/event-stream");
            TrackingHttpResponse value = new TrackingHttpResponse(request, 200, headers,
                Flux.<ByteBuffer>never().doOnSubscribe(ignored -> subscribed.countDown()));
            response.set(value);
            return value;
        }).buildClient();
        AtomicReference<Throwable> failure = new AtomicReference<>();
        AtomicBoolean interrupted = new AtomicBoolean();
        Thread thread = new Thread(() -> {
            try {
                client.retrieveStream(new KnowledgeBaseRetrievalOptions(), event -> {
                });
            } catch (Throwable throwable) {
                failure.set(throwable);
                interrupted.set(Thread.currentThread().isInterrupted());
            }
        });

        thread.start();
        assertTrue(subscribed.await(5, TimeUnit.SECONDS));
        thread.interrupt();
        thread.join(5000);

        assertFalse(thread.isAlive());
        assertNotNull(failure.get());
        assertTrue(interrupted.get());
        assertTrue(response.get().isClosed());
    }

    private static KnowledgeBaseRetrievalClientBuilder createBuilder(AtomicReference<HttpRequest> sentRequest,
        Function<HttpRequest, HttpResponse> responseFactory) {
        return new KnowledgeBaseRetrievalClientBuilder().endpoint("https://test.search.windows.net")
            .credential(new AzureKeyCredential("key"))
            .knowledgeBaseName("kb")
            .httpClient(request -> {
                sentRequest.set(request);
                return Mono.just(responseFactory.apply(request));
            });
    }

    private static HttpResponse eventStreamResponse(HttpRequest request) {
        HttpHeaders headers = new HttpHeaders().set(HttpHeaderName.CONTENT_TYPE, "text/event-stream; charset=utf-8")
            .set(HttpHeaderName.CONTENT_LENGTH, String.valueOf(SSE_RESPONSE.getBytes(StandardCharsets.UTF_8).length));
        return new MockHttpResponse(request, 200, headers, SSE_RESPONSE.getBytes(StandardCharsets.UTF_8));
    }

    private static String collect(StreamResponse response) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        response.getValue().doOnNext(buffer -> {
            ByteBuffer duplicate = buffer.duplicate();
            byte[] bytes = new byte[duplicate.remaining()];
            duplicate.get(bytes);
            output.write(bytes, 0, bytes.length);
        }).blockLast();
        return new String(output.toByteArray(), StandardCharsets.UTF_8);
    }

    private static void assertSseRequest(HttpRequest request) {
        assertEquals("text/event-stream", request.getHeaders().getValue(HttpHeaderName.ACCEPT));
        assertEquals("application/json", request.getHeaders().getValue(HttpHeaderName.CONTENT_TYPE));
        assertTrue(request.getUrl().getQuery().contains("api-version=2026-08-01-preview"));
    }

    private static final class TrackingHttpResponse extends MockHttpResponse {
        private final Flux<ByteBuffer> body;
        private final AtomicBoolean closed = new AtomicBoolean();

        private TrackingHttpResponse(HttpRequest request, int statusCode, HttpHeaders headers, Flux<ByteBuffer> body) {
            super(request, statusCode, headers);
            this.body = body;
        }

        @Override
        public Flux<ByteBuffer> getBody() {
            return body;
        }

        @Override
        public void close() {
            closed.set(true);
        }

        private boolean isClosed() {
            return closed.get();
        }
    }
}
