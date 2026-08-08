// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.implementation.util;

import com.azure.core.http.ServerSentEvent;
import com.azure.core.http.ServerSentEventListener;
import com.azure.core.util.BinaryData;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServerSentEventStreamTests {
    @Test
    public void syncReconnectsWithMetadataOnlyState() {
        BinaryData firstBody
            = BinaryData.fromString("id: first\nretry: 0\ndata: one\n\n" + "id: reconnect\nretry: invalid\n\n");
        List<ServerSentEvent<String>> events = new ArrayList<>();
        AtomicReference<String> reconnectEventId = new AtomicReference<>();
        AtomicInteger reconnectCount = new AtomicInteger();

        ServerSentEventStream.process(firstBody, eventId -> {
            reconnectEventId.set(eventId);
            reconnectCount.incrementAndGet();
            return BinaryData.fromString("data: [DONE]\n\n");
        }, (event, data) -> data, event -> "[DONE]".equals(event.getData()), events::add);

        assertEquals(1, reconnectCount.get());
        assertEquals("reconnect", reconnectEventId.get());
        assertEquals(2, events.size());
        assertEquals("one", events.get(0).getData());
        assertEquals("reconnect", events.get(1).getId());
        assertEquals(Duration.ZERO, events.get(1).getRetryAfter());
    }

    @Test
    public void asyncReconnectsSeriallyWithRetainedState() {
        BinaryData firstBody = BinaryData.fromString("id: first\nretry: 0\ndata: one\n\nid: second\n\n");
        AtomicInteger reconnectCount = new AtomicInteger();
        AtomicReference<String> reconnectEventId = new AtomicReference<>();

        StepVerifier.create(ServerSentEventStream.decode(firstBody, eventId -> {
            reconnectEventId.set(eventId);
            reconnectCount.incrementAndGet();
            return Mono.just(BinaryData.fromString("data: [DONE]\n\n"));
        }, (event, data) -> data).takeUntil(event -> "[DONE]".equals(event.getData())))
            .assertNext(event -> assertEquals("one", event.getData()))
            .assertNext(event -> {
                assertEquals("[DONE]", event.getData());
                assertEquals("second", event.getId());
                assertEquals(Duration.ZERO, event.getRetryAfter());
            })
            .verifyComplete();

        assertEquals(1, reconnectCount.get());
        assertEquals("second", reconnectEventId.get());
    }

    @Test
    public void emptyIdOmitsLastEventIdOnReconnect() {
        BinaryData firstBody = BinaryData.fromString("id: first\nretry: 0\ndata: one\n\nid:\n\n");
        AtomicReference<String> reconnectEventId = new AtomicReference<>("not-called");

        ServerSentEventStream.process(firstBody, eventId -> {
            reconnectEventId.set(eventId);
            return BinaryData.fromString("data: [DONE]\n\n");
        }, (event, data) -> data, event -> "[DONE]".equals(event.getData()), event -> {
        });

        assertNull(reconnectEventId.get());
    }

    @Test
    public void cleanCompletionWithoutRetryDoesNotReconnect() {
        AtomicInteger reconnectCount = new AtomicInteger();

        ServerSentEventStream.process(BinaryData.fromString("data: one\n\n"), eventId -> {
            reconnectCount.incrementAndGet();
            return BinaryData.fromString("data: unexpected\n\n");
        }, (event, data) -> data, event -> false, event -> {
        });

        assertEquals(0, reconnectCount.get());
    }

    @Test
    public void asyncBodyErrorReconnectsWithRetainedRetry() {
        IOException disconnect = new IOException("connection closed");
        byte[] prefix = "retry: 0\ndata: one\n\n".getBytes(StandardCharsets.UTF_8);
        BinaryData body
            = BinaryData.fromFlux(Flux.concat(Flux.just(ByteBuffer.wrap(prefix)), Flux.error(disconnect)), null, false)
                .block();
        AtomicInteger reconnectCount = new AtomicInteger();

        StepVerifier.create(ServerSentEventStream.decode(body, eventId -> {
            reconnectCount.incrementAndGet();
            return Mono.just(BinaryData.fromString("data: [DONE]\n\n"));
        }, (event, data) -> data).takeUntil(event -> "[DONE]".equals(event.getData())))
            .assertNext(event -> assertEquals("one", event.getData()))
            .assertNext(event -> assertEquals("[DONE]", event.getData()))
            .verifyComplete();

        assertEquals(1, reconnectCount.get());
    }

    @Test
    public void syncBodyErrorReconnectsWithRetainedRetry() {
        IOException disconnect = new IOException("connection closed");
        byte[] prefix = "retry: 0\ndata: one\n\n".getBytes(StandardCharsets.UTF_8);
        BinaryData body
            = BinaryData.fromFlux(Flux.concat(Flux.just(ByteBuffer.wrap(prefix)), Flux.error(disconnect)), null, false)
                .block();
        AtomicInteger reconnectCount = new AtomicInteger();
        List<String> events = new ArrayList<>();

        ServerSentEventStream.process(body, eventId -> {
            reconnectCount.incrementAndGet();
            return BinaryData.fromString("data: [DONE]\n\n");
        }, (event, data) -> data, event -> "[DONE]".equals(event.getData()), event -> events.add(event.getData()));

        assertEquals(1, reconnectCount.get());
        assertEquals(2, events.size());
        assertEquals("one", events.get(0));
        assertEquals("[DONE]", events.get(1));
    }

    @Test
    public void bodyErrorWithoutRetryTerminatesAsyncStream() {
        IOException disconnect = new IOException("connection closed");
        BinaryData body = BinaryData
            .fromFlux(Flux.concat(Flux.just(ByteBuffer.wrap("data: one\n\n".getBytes(StandardCharsets.UTF_8))),
                Flux.error(disconnect)), null, false)
            .block();
        AtomicInteger reconnectCount = new AtomicInteger();

        StepVerifier.create(ServerSentEventStream.decode(body, eventId -> {
            reconnectCount.incrementAndGet();
            return Mono.just(BinaryData.fromString("data: unexpected\n\n"));
        }, (event, data) -> data))
            .assertNext(event -> assertEquals("one", event.getData()))
            .expectErrorMatches(error -> error == disconnect)
            .verify();

        assertEquals(0, reconnectCount.get());
    }

    @Test
    public void deserializerErrorDoesNotReconnectAsyncStream() {
        RuntimeException deserializerError = new IllegalStateException("deserialization failed");
        BinaryData body = BinaryData.fromString("retry: 0\ndata: one\n\n");
        AtomicInteger reconnectCount = new AtomicInteger();

        StepVerifier.create(ServerSentEventStream.decode(body, eventId -> {
            reconnectCount.incrementAndGet();
            return Mono.just(BinaryData.fromString("data: unexpected\n\n"));
        }, (event, data) -> {
            throw deserializerError;
        })).expectErrorMatches(error -> error == deserializerError).verify();

        assertEquals(0, reconnectCount.get());
    }

    @Test
    public void asyncReconnectsWithoutGrowingPublisherDepth() {
        int eventCount = 10_000;
        BinaryData body = BinaryData.fromString("retry: 0\ndata: one\n\n");
        AtomicInteger reconnectCount = new AtomicInteger();

        StepVerifier.create(ServerSentEventStream.decode(body, eventId -> {
            reconnectCount.incrementAndGet();
            return Mono.just(BinaryData.fromString("data: one\n\n"));
        }, (event, data) -> data).take(eventCount)).expectNextCount(eventCount).verifyComplete();

        assertEquals(eventCount - 1, reconnectCount.get());
    }

    @Test
    public void zeroRetryUsesAsyncSchedulingBoundary() {
        BinaryData body = BinaryData.fromString("retry: 0\ndata: one\n\n");
        Thread subscriptionThread = Thread.currentThread();
        AtomicReference<Thread> reconnectThread = new AtomicReference<>();

        StepVerifier.create(ServerSentEventStream.decode(body, eventId -> {
            reconnectThread.set(Thread.currentThread());
            return Mono.just(BinaryData.fromString("data: [DONE]\n\n"));
        }, (event, data) -> data).takeUntil(event -> "[DONE]".equals(event.getData())))
            .expectNextCount(2)
            .verifyComplete();

        assertNotSame(subscriptionThread, reconnectThread.get());
    }

    @Test
    public void reconnectRequestErrorTerminatesAsyncStream() {
        RuntimeException requestError = new IllegalStateException("reconnect failed");
        BinaryData body = BinaryData.fromString("retry: 0\ndata: one\n\n");
        AtomicInteger reconnectCount = new AtomicInteger();

        StepVerifier.create(ServerSentEventStream.decode(body, eventId -> {
            reconnectCount.incrementAndGet();
            return Mono.error(requestError);
        }, (event, data) -> data))
            .assertNext(event -> assertEquals("one", event.getData()))
            .expectErrorMatches(error -> error == requestError)
            .verify();

        assertEquals(1, reconnectCount.get());
    }

    @Test
    public void listenerErrorDoesNotReconnect() {
        RuntimeException listenerError = new IllegalStateException("listener failed");
        BinaryData body = BinaryData.fromString("retry: 0\ndata: one\n\n");
        AtomicInteger reconnectCount = new AtomicInteger();
        AtomicReference<Throwable> reportedError = new AtomicReference<>();

        RuntimeException exception
            = assertThrows(RuntimeException.class, () -> ServerSentEventStream.process(body, eventId -> {
                reconnectCount.incrementAndGet();
                return BinaryData.fromString("data: unexpected\n\n");
            }, (event, data) -> data, event -> false, new ServerSentEventListener<String>() {
                @Override
                public void onEvent(ServerSentEvent<String> event) {
                    throw listenerError;
                }

                @Override
                public void onError(Throwable error) {
                    reportedError.set(error);
                }
            }));

        assertSame(listenerError, exception);
        assertSame(listenerError, reportedError.get());
        assertEquals(0, reconnectCount.get());
    }

    @Test
    public void asyncCancellationDuringRetryDelayPreventsReconnect() {
        BinaryData body = BinaryData.fromString("retry: 60000\ndata: one\n\n");
        AtomicInteger reconnectCount = new AtomicInteger();

        StepVerifier.create(ServerSentEventStream.decode(body, eventId -> {
            reconnectCount.incrementAndGet();
            return Mono.just(BinaryData.fromString("data: unexpected\n\n"));
        }, (event, data) -> data)).assertNext(event -> assertEquals("one", event.getData())).thenCancel().verify();

        assertEquals(0, reconnectCount.get());
    }

    @Test
    public void maximumRetryValueDoesNotOverflowAsyncDelay() {
        BinaryData body = BinaryData.fromString("retry: 9223372036854775807\ndata: one\n\n");
        AtomicInteger reconnectCount = new AtomicInteger();

        StepVerifier.withVirtualTime(() -> ServerSentEventStream.decode(body, eventId -> {
            reconnectCount.incrementAndGet();
            return Mono.just(BinaryData.fromString("data: unexpected\n\n"));
        }, (event, data) -> data))
            .assertNext(event -> assertEquals(Duration.ofMillis(Long.MAX_VALUE), event.getRetryAfter()))
            .thenCancel()
            .verify();

        assertEquals(0, reconnectCount.get());
    }

    @Test
    public void syncInterruptionDuringRetryDelayIsPropagated() {
        BinaryData body = BinaryData.fromString("retry: 60000\n\n");
        AtomicReference<Throwable> listenerError = new AtomicReference<>();
        AtomicBoolean closed = new AtomicBoolean();
        ServerSentEventListener<String> listener = new ServerSentEventListener<String>() {
            @Override
            public void onEvent(ServerSentEvent<String> event) {
            }

            @Override
            public void onError(Throwable error) {
                listenerError.set(error);
            }

            @Override
            public void onClose() {
                closed.set(true);
            }
        };

        Thread.currentThread().interrupt();
        try {
            RuntimeException exception = assertThrows(RuntimeException.class,
                () -> ServerSentEventStream.process(body, eventId -> BinaryData.fromString("data: unexpected\n\n"),
                    (event, data) -> data, event -> false, listener));

            assertTrue(Thread.currentThread().isInterrupted());
            assertSame(exception, listenerError.get());
            assertTrue(closed.get());
        } finally {
            Thread.interrupted();
        }
    }

    @Test
    public void syncInterruptionBeforeZeroDelayReconnectIsPropagated() {
        BinaryData body = BinaryData.fromString("retry: 0\ndata: one\n\n");
        AtomicReference<Throwable> listenerError = new AtomicReference<>();
        AtomicInteger reconnectCount = new AtomicInteger();
        AtomicInteger eventCount = new AtomicInteger();
        ServerSentEventListener<String> listener = new ServerSentEventListener<String>() {
            @Override
            public void onEvent(ServerSentEvent<String> event) {
                eventCount.incrementAndGet();
                Thread.currentThread().interrupt();
            }

            @Override
            public void onError(Throwable error) {
                listenerError.set(error);
            }
        };

        try {
            RuntimeException exception
                = assertThrows(RuntimeException.class, () -> ServerSentEventStream.process(body, eventId -> {
                    reconnectCount.incrementAndGet();
                    return BinaryData.fromString("data: unexpected\n\n");
                }, (event, data) -> data, event -> false, listener));

            assertTrue(Thread.currentThread().isInterrupted());
            assertSame(exception, listenerError.get());
            assertEquals(1, eventCount.get());
            assertEquals(0, reconnectCount.get());
        } finally {
            Thread.interrupted();
        }
    }
}
