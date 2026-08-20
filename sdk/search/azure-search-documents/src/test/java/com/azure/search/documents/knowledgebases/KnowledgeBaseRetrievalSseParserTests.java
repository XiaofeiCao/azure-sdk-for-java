// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.search.documents.knowledgebases;

import com.azure.core.http.HttpHeaderName;
import com.azure.core.http.HttpHeaders;
import com.azure.core.http.HttpMethod;
import com.azure.core.http.HttpRequest;
import com.azure.core.http.rest.StreamResponse;
import com.azure.core.test.http.MockHttpResponse;
import com.azure.search.documents.knowledgebases.models.KnowledgeBaseRetrievalStreamEvent;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KnowledgeBaseRetrievalSseParserTests {
    private static final HttpRequest REQUEST = new HttpRequest(HttpMethod.GET, "https://example.test");

    @Test
    public void parsesMetadataCommentsMultilineDataAndLineEndings() {
        String stream = ": comment\r\n" + "id: event-1\r\n" + "retry: 250\r\n" + "event: retrieval.started\r\n"
            + "data: {\"requestId\":\"request-1\",\r\n" + "data: \"knowledgeBaseName\":\"café\"}\r\n" + "\r\n"
            + "event: future.event\r" + "data: first\r" + "data: second\r" + "\r";

        List<KnowledgeBaseRetrievalStreamEvent> events = new ArrayList<>();
        KnowledgeBaseRetrievalSseParser.parse(response(200, "Text/Event-Stream; charset=UTF-8", chunks(stream)))
            .doOnNext(events::add)
            .blockLast();

        assertEquals(2, events.size());
        assertEquals("event-1", events.get(0).getId());
        assertEquals(Long.valueOf(250), events.get(0).getRetry());
        assertEquals("café", events.get(0).asRetrievalStarted().getKnowledgeBaseName());
        assertEquals("event-1", events.get(1).getId());
        assertEquals("future.event", events.get(1).getEvent());
        assertEquals("first\nsecond", events.get(1).getData());
    }

    @Test
    public void decodesUtf8SplitAcrossDirectReadOnlyChunks() {
        String stream = "event: future.event\n" + "data: {\"text\":\"café 😀\"}\n\n";
        byte[] bytes = stream.getBytes(StandardCharsets.UTF_8);
        List<ByteBuffer> chunks = new ArrayList<>();
        for (byte value : bytes) {
            ByteBuffer direct = ByteBuffer.allocateDirect(1);
            direct.put(value).flip();
            chunks.add(direct.asReadOnlyBuffer());
        }

        StepVerifier
            .create(
                KnowledgeBaseRetrievalSseParser.parse(response(200, "text/event-stream", Flux.fromIterable(chunks))))
            .assertNext(event -> assertEquals("{\"text\":\"café 😀\"}", event.getData()))
            .verifyComplete();
    }

    @Test
    public void handlesBomDefaultEventAndInvalidMetadataFields() {
        String stream = "\uFEFFid: retained\n" + "retry: 125\n\n" + "id: ignored\0value\n" + "retry: invalid\n"
            + "data: {\"future\":true}\n\n";

        StepVerifier.create(KnowledgeBaseRetrievalSseParser.parse(response(200, "text/event-stream", chunks(stream))))
            .assertNext(event -> {
                assertEquals("retained", event.getId());
                assertEquals("message", event.getEvent());
                assertEquals(Long.valueOf(125), event.getRetry());
                assertEquals("{\"future\":true}", event.getData());
            })
            .verifyComplete();
    }

    @Test
    public void retryMetadataDoesNotReconnect() {
        AtomicInteger subscriptions = new AtomicInteger();
        Flux<ByteBuffer> body = chunks("retry: 1\nevent: future.event\ndata: {}\n\n")
            .doOnSubscribe(ignored -> subscriptions.incrementAndGet());

        StepVerifier.create(KnowledgeBaseRetrievalSseParser.parse(response(200, "text/event-stream", body)))
            .assertNext(event -> assertEquals(Long.valueOf(1), event.getRetry()))
            .verifyComplete();

        assertEquals(1, subscriptions.get());
    }

    @Test
    public void terminalEventCancelsTrailingBodyAndClosesResponse() {
        String terminal = "event: error\n" + "data: {\"error\":{\"code\":\"failed\"},\"activity\":[]}\n\n";
        AtomicBoolean cancelled = new AtomicBoolean();
        TrackingHttpResponse response = trackingResponse(200, "text/event-stream",
            Flux.concat(chunks(terminal), Flux.<ByteBuffer>never()).doOnCancel(() -> cancelled.set(true)));

        StepVerifier.create(KnowledgeBaseRetrievalSseParser.parse(new StreamResponse(response)))
            .assertNext(event -> assertTrue(event.isError()))
            .verifyComplete();

        assertTrue(cancelled.get());
        assertTrue(response.isClosed());
    }

    @Test
    public void terminalEventIgnoresMalformedTrailingEventInSameBuffer() {
        String stream = "event: response.completed\n"
            + "data: {\"statusCode\":200,\"response\":{\"response\":[],\"activity\":[],\"references\":[]}}\n\n"
            + "event: retrieval.started\n" + "data: not-json\n\n";

        StepVerifier.create(KnowledgeBaseRetrievalSseParser.parse(response(200, "text/event-stream", chunks(stream))))
            .assertNext(event -> assertTrue(event.isResponseCompleted()))
            .verifyComplete();
    }

    @Test
    public void terminalEventIgnoresInvalidUtf8InSameBuffer() {
        byte[] terminal = ("event: response.completed\n"
            + "data: {\"statusCode\":200,\"response\":{\"response\":[],\"activity\":[],\"references\":[]}}\n\n")
                .getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.allocate(terminal.length + 2).put(terminal).put((byte) 0xC3).put((byte) 0x28);
        buffer.flip();

        StepVerifier
            .create(KnowledgeBaseRetrievalSseParser.parse(response(200, "text/event-stream", Flux.just(buffer))))
            .assertNext(event -> assertTrue(event.isResponseCompleted()))
            .verifyComplete();
    }

    @Test
    public void downstreamCancellationClosesResponse() {
        AtomicBoolean cancelled = new AtomicBoolean();
        TrackingHttpResponse response = trackingResponse(200, "text/event-stream",
            Flux.<ByteBuffer>never().doOnCancel(() -> cancelled.set(true)));

        StepVerifier.create(KnowledgeBaseRetrievalSseParser.parse(new StreamResponse(response))).thenCancel().verify();

        assertTrue(cancelled.get());
        assertTrue(response.isClosed());
    }

    @Test
    public void listenerFailureClosesResponse() {
        TrackingHttpResponse response
            = trackingResponse(200, "text/event-stream", chunks("event: future.event\ndata: {}\n\n"));

        StepVerifier.create(KnowledgeBaseRetrievalSseParser.parse(new StreamResponse(response)).doOnNext(ignored -> {
            throw new IllegalStateException("listener failed");
        })).expectErrorMessage("listener failed").verify();

        assertTrue(response.isClosed());
    }

    @Test
    public void noContentCompletesAndClosesWithoutSubscribingToBody() {
        AtomicInteger subscriptions = new AtomicInteger();
        TrackingHttpResponse response = trackingResponse(204, null,
            Flux.<ByteBuffer>empty().doOnSubscribe(ignored -> subscriptions.incrementAndGet()));

        StepVerifier.create(KnowledgeBaseRetrievalSseParser.parse(new StreamResponse(response))).verifyComplete();

        assertEquals(0, subscriptions.get());
        assertTrue(response.isClosed());
    }

    @Test
    public void rejectsWrongContentTypeAndClosesResponse() {
        TrackingHttpResponse response = trackingResponse(200, "application/json", chunks("{}"));

        StepVerifier.create(KnowledgeBaseRetrievalSseParser.parse(new StreamResponse(response)))
            .expectErrorMatches(
                error -> error instanceof IllegalStateException && error.getMessage().contains("text/event-stream"))
            .verify();

        assertTrue(response.isClosed());
    }

    @Test
    public void rejectsMalformedUtf8AndClosesResponse() {
        TrackingHttpResponse response = trackingResponse(200, "text/event-stream",
            Flux.just(ByteBuffer.wrap(new byte[] { (byte) 0xC3, (byte) 0x28 })));

        StepVerifier.create(KnowledgeBaseRetrievalSseParser.parse(new StreamResponse(response)))
            .expectErrorMatches(
                error -> error instanceof IllegalArgumentException && error.getMessage().contains("invalid UTF-8"))
            .verify();

        assertTrue(response.isClosed());
    }

    @Test
    public void ignoresIncompleteEventAtEndOfStream() {
        StreamResponse response = response(200, "text/event-stream", chunks("event: future.event\ndata: {}"));
        StepVerifier.create(KnowledgeBaseRetrievalSseParser.parse(response)).verifyComplete();
    }

    private static StreamResponse response(int statusCode, String contentType, Flux<ByteBuffer> body) {
        return new StreamResponse(trackingResponse(statusCode, contentType, body));
    }

    private static TrackingHttpResponse trackingResponse(int statusCode, String contentType, Flux<ByteBuffer> body) {
        HttpHeaders headers = new HttpHeaders();
        if (contentType != null) {
            headers.set(HttpHeaderName.CONTENT_TYPE, contentType);
        }
        return new TrackingHttpResponse(REQUEST, statusCode, headers, body);
    }

    private static Flux<ByteBuffer> chunks(String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        int first = Math.max(1, bytes.length / 3);
        int second = Math.max(first + 1, bytes.length * 2 / 3);
        return Flux.just(ByteBuffer.wrap(bytes, 0, first), ByteBuffer.wrap(bytes, first, second - first),
            ByteBuffer.wrap(bytes, second, bytes.length - second));
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
