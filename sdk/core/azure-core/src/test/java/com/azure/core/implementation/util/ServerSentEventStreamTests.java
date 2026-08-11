// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.implementation.util;

import com.azure.core.http.HttpHeaderName;
import com.azure.core.http.HttpHeaders;
import com.azure.core.http.ServerSentEvent;
import com.azure.core.http.ServerSentEventListener;
import com.azure.core.http.ServerSentEventStreams;
import com.azure.core.http.rest.ResponseBase;
import com.azure.core.util.BinaryData;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServerSentEventStreamTests {
    @Test
    public void toFluxParsesFragmentedEventMetadata() {
        byte[] bytes = ("\uFEFF: comment\rid: 42\r\nevent: greeting\nretry: 2000\ndata: caf\u00e9\r\ndata: second\n\n")
            .getBytes(StandardCharsets.UTF_8);
        List<ByteBuffer> buffers = new ArrayList<>();
        for (byte value : bytes) {
            buffers.add(ByteBuffer.wrap(new byte[] { value }));
        }

        TestResponse response = response(200, BinaryData.fromFlux(Flux.fromIterable(buffers), null, false).block());

        StepVerifier.create(ServerSentEventStreams.toFlux(response, (event, data) -> data)).assertNext(event -> {
            assertEquals("42", event.getId());
            assertEquals("greeting", event.getEvent());
            assertEquals("caf\u00e9\nsecond", event.getData());
            assertEquals("comment", event.getComment());
            assertEquals(Duration.ofSeconds(2), event.getRetryAfter());
        }).verifyComplete();

        assertTrue(response.closed.get());
    }

    @Test
    public void toFluxCompletesOnEofWithoutReconnecting() {
        TestResponse response = response(200, BinaryData.fromString("id: 1\nretry: 0\ndata: one\n\n"));

        StepVerifier.create(ServerSentEventStreams.toFlux(response, (event, data) -> data))
            .assertNext(event -> assertEquals("one", event.getData()))
            .verifyComplete();

        assertTrue(response.closed.get());
    }

    @Test
    public void toFluxReturnsEmptyForNoContent() {
        TestResponse response = response(204, null);

        StepVerifier.create(ServerSentEventStreams.toFlux(response, (event, data) -> data)).verifyComplete();

        assertTrue(response.closed.get());
    }

    @Test
    public void toFluxRejectsInvalidContentTypeAndClosesResponse() {
        TestResponse response = response(200, BinaryData.fromString("data: one\n\n"), "application/json");

        assertThrows(IllegalStateException.class,
            () -> ServerSentEventStreams.toFlux(response, (event, data) -> data).blockLast());

        assertTrue(response.closed.get());
    }

    @Test
    public void toFluxCancellationClosesResponse() {
        AtomicBoolean cancelled = new AtomicBoolean();
        BinaryData body = BinaryData.fromFlux(
            Flux.concat(Flux.just(ByteBuffer.wrap("data: one\n\n".getBytes(StandardCharsets.UTF_8))), Flux.never())
                .doOnCancel(() -> cancelled.set(true)),
            null, false).block();
        TestResponse response = response(200, body);

        StepVerifier.create(ServerSentEventStreams.toFlux(response, (event, data) -> data))
            .assertNext(event -> assertEquals("one", event.getData()))
            .thenCancel()
            .verify();

        assertTrue(cancelled.get());
        assertTrue(response.closed.get());
    }

    @Test
    public void listenCompletesOnEofAndNotifiesLifecycleOnce() {
        TestResponse response = response(200, BinaryData.fromString("data: one\n\ndata: two\n\n"));
        List<String> events = new ArrayList<>();
        AtomicBoolean closed = new AtomicBoolean();

        ServerSentEventStreams.listen(response, (event, data) -> data, new ServerSentEventListener<String>() {
            @Override
            public void onEvent(ServerSentEvent<String> event) {
                events.add(event.getData());
            }

            @Override
            public void onClose() {
                assertFalse(closed.getAndSet(true));
            }
        });

        assertEquals(2, events.size());
        assertTrue(closed.get());
        assertTrue(response.closed.get());
    }

    @Test
    public void listenNotifiesErrorAndClosesResponse() {
        RuntimeException failure = new IllegalStateException("listener failed");
        AtomicReference<Throwable> reportedError = new AtomicReference<>();
        TestResponse response = response(200, BinaryData.fromString("data: one\n\n"));

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> ServerSentEventStreams.listen(response, (event, data) -> data, new ServerSentEventListener<String>() {
                @Override
                public void onEvent(ServerSentEvent<String> event) {
                    throw failure;
                }

                @Override
                public void onError(Throwable error) {
                    reportedError.set(error);
                }
            }));

        assertSame(failure, exception);
        assertSame(failure, reportedError.get());
        assertTrue(response.closed.get());
    }

    @Test
    public void idAndRetryAreMetadataOnly() {
        TestResponse response = response(200, BinaryData.fromString("id: 42\nretry: 1000\ndata: one\n\n"));

        StepVerifier.create(ServerSentEventStreams.toFlux(response, (event, data) -> data)).assertNext(event -> {
            assertEquals("42", event.getId());
            assertEquals(Duration.ofSeconds(1), event.getRetryAfter());
        }).verifyComplete();
    }

    private static TestResponse response(int statusCode, BinaryData body) {
        return response(statusCode, body, "text/event-stream");
    }

    private static TestResponse response(int statusCode, BinaryData body, String contentType) {
        return new TestResponse(statusCode, new HttpHeaders().set(HttpHeaderName.CONTENT_TYPE, contentType), body);
    }

    private static final class TestResponse extends ResponseBase<Object, BinaryData> implements Closeable {
        private final AtomicBoolean closed = new AtomicBoolean();

        private TestResponse(int statusCode, HttpHeaders headers, BinaryData value) {
            super(null, statusCode, headers, value, null);
        }

        @Override
        public void close() throws IOException {
            closed.set(true);
        }
    }
}
