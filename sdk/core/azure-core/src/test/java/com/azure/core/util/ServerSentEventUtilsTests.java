// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.util;

import com.azure.core.http.ServerSentEvent;
import com.azure.core.http.ServerSentEventListener;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServerSentEventUtilsTests {
    @Test
    public void processParsesSupportedFields() {
        BinaryData body = BinaryData.fromString("\uFEFF: comment\n" + "id: 42\n" + "event: stockUpdate\n"
            + "retry: 2000\n" + "ignored: value\n" + "data: first\n" + "data: second\n\n");
        List<ServerSentEvent<String>> events = new ArrayList<>();

        ServerSentEventUtils.process(body, events::add);

        assertEquals(1, events.size());
        ServerSentEvent<String> event = events.get(0);
        assertEquals("42", event.getId());
        assertEquals("stockUpdate", event.getEvent());
        assertEquals("first\nsecond", event.getData());
        assertEquals("comment", event.getComment());
        assertEquals(Duration.ofSeconds(2), event.getRetryAfter());
    }

    @Test
    public void processSkipsBlocksWithoutDataAndUsesDefaultEvent() {
        BinaryData body = BinaryData.fromString(
            ": keep alive\n" + "retry: invalid\n\n" + "id: contains\0null\n" + "event:\n" + "data: payload\n\n");
        AtomicReference<ServerSentEvent<String>> eventReference = new AtomicReference<>();

        ServerSentEventUtils.process(body, eventReference::set);

        ServerSentEvent<String> event = eventReference.get();
        assertNull(event.getId());
        assertEquals("message", event.getEvent());
        assertEquals("payload", event.getData());
        assertNull(event.getComment());
        assertNull(event.getRetryAfter());
    }

    @Test
    public void metadataBlocksPersistAcrossEvents() {
        BinaryData body = BinaryData.fromString("id: 42\nretry: 2000\n\ndata: first\n\ndata: second\n\n");
        List<ServerSentEvent<String>> syncEvents = new ArrayList<>();

        ServerSentEventUtils.process(body, syncEvents::add);

        assertPersistentMetadata(syncEvents);
        StepVerifier.create(ServerSentEventUtils.decode(body).collectList())
            .assertNext(ServerSentEventUtilsTests::assertPersistentMetadata)
            .verifyComplete();
    }

    @Test
    public void emptyIdResetsPersistentState() {
        BinaryData body = BinaryData.fromString("id: 42\ndata: first\n\nid:\ndata: second\n\n");

        StepVerifier.create(ServerSentEventUtils.decode(body))
            .assertNext(event -> assertEquals("42", event.getId()))
            .assertNext(event -> assertEquals("", event.getId()))
            .verifyComplete();
    }

    @Test
    public void finalMetadataOnlyBlockDoesNotEmitState() {
        BinaryData body = BinaryData.fromString("id: 1\nretry: 1000\ndata: first\n\nid: 2\nretry: 2000\n\n");
        List<ServerSentEvent<String>> syncEvents = new ArrayList<>();

        ServerSentEventUtils.process(body, syncEvents::add);

        assertEquals(1, syncEvents.size());
        assertInitialMetadata(syncEvents.get(0));
        StepVerifier.create(ServerSentEventUtils.decode(body))
            .assertNext(ServerSentEventUtilsTests::assertInitialMetadata)
            .verifyComplete();
    }

    @Test
    public void processDeserializesTypedEventData() {
        BinaryData body = BinaryData.fromString("id: 42\nevent: number\ndata: 123\n\n");
        AtomicReference<ServerSentEvent<Integer>> eventReference = new AtomicReference<>();

        ServerSentEventUtils.process(body, (eventName, data) -> {
            assertEquals("number", eventName);
            return Integer.parseInt(data);
        }, eventReference::set);

        ServerSentEvent<Integer> event = eventReference.get();
        assertEquals("42", event.getId());
        assertEquals("number", event.getEvent());
        assertEquals(123, event.getData());
    }

    @Test
    public void decodeParsesFragmentedUtf8AndLineEndings() {
        byte[] bytes
            = ("\uFEFF: comment\rid: 42\r\nevent: greeting\nretry: 2000\n" + "data: caf\u00e9\r\ndata: second\n\n")
                .getBytes(StandardCharsets.UTF_8);
        List<ByteBuffer> buffers = new ArrayList<>();
        for (byte value : bytes) {
            buffers.add(ByteBuffer.wrap(new byte[] { value }));
        }
        BinaryData body = BinaryData.fromFlux(Flux.fromIterable(buffers), null, false).block();

        StepVerifier.create(ServerSentEventUtils.decode(body)).assertNext(event -> {
            assertEquals("42", event.getId());
            assertEquals("greeting", event.getEvent());
            assertEquals("caf\u00e9\nsecond", event.getData());
            assertEquals("comment", event.getComment());
            assertEquals(Duration.ofSeconds(2), event.getRetryAfter());
        }).verifyComplete();
    }

    @Test
    public void decodeDiscardsFinalEventWhenBodyEndsWithoutDelimiter() {
        BinaryData body
            = BinaryData
                .fromFlux(Flux.just(ByteBuffer.wrap("event: final\ndata: payload".getBytes(StandardCharsets.UTF_8))),
                    null, false)
                .block();

        StepVerifier.create(ServerSentEventUtils.decode(body)).verifyComplete();

        AtomicBoolean eventReceived = new AtomicBoolean();
        ServerSentEventUtils.process(body, event -> eventReceived.set(true));
        assertFalse(eventReceived.get());
    }

    @Test
    public void decodeDoesNotDispatchPartialEventAfterNetworkError() {
        IOException disconnect = new IOException("connection closed");
        Flux<ByteBuffer> content
            = Flux.concat(Flux.just(ByteBuffer.wrap("event: partial\ndata: pay".getBytes(StandardCharsets.UTF_8))),
                Flux.error(disconnect));
        BinaryData body = BinaryData.fromFlux(content, null, false).block();

        StepVerifier.create(ServerSentEventUtils.decode(body))
            .expectErrorMatches(error -> error == disconnect)
            .verify();
    }

    @Test
    public void processNotifiesAndRethrowsNetworkError() {
        IOException disconnect = new IOException("connection closed");
        Flux<ByteBuffer> content = Flux.concat(
            Flux.just(ByteBuffer.wrap("data: first\n\n".getBytes(StandardCharsets.UTF_8))), Flux.error(disconnect));
        BinaryData body = BinaryData.fromFlux(content, null, false).block();
        AtomicReference<Throwable> listenerError = new AtomicReference<>();
        AtomicBoolean closed = new AtomicBoolean();

        UncheckedIOException exception = assertThrows(UncheckedIOException.class,
            () -> ServerSentEventUtils.process(body, new ServerSentEventListener<String>() {
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
            }));

        assertSame(disconnect, exception.getCause());
        assertSame(disconnect, listenerError.get());
        assertTrue(closed.get());
    }

    @Test
    public void processRethrowsDeserializerRuntimeException() {
        UncheckedIOException deserializationError = new UncheckedIOException(new IOException("invalid event"));
        BinaryData body = BinaryData.fromString("data: invalid\n\n");

        UncheckedIOException exception
            = assertThrows(UncheckedIOException.class, () -> ServerSentEventUtils.process(body, (event, data) -> {
                throw deserializationError;
            }, ignored -> {
            }));

        assertSame(deserializationError, exception);
    }

    @Test
    public void processNotifiesAndRethrowsListenerRuntimeException() {
        RuntimeException listenerFailure = new IllegalStateException("listener failed");
        AtomicReference<Throwable> listenerError = new AtomicReference<>();
        AtomicBoolean closed = new AtomicBoolean();
        BinaryData body = BinaryData.fromString("data: event\n\n");

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> ServerSentEventUtils.process(body, new ServerSentEventListener<String>() {
                @Override
                public void onEvent(ServerSentEvent<String> event) {
                    throw listenerFailure;
                }

                @Override
                public void onError(Throwable error) {
                    listenerError.set(error);
                }

                @Override
                public void onClose() {
                    closed.set(true);
                }
            }));

        assertSame(listenerFailure, exception);
        assertSame(listenerFailure, listenerError.get());
        assertTrue(closed.get());
    }

    @Test
    public void cancellingDecodeCancelsFluxBackedBody() {
        AtomicBoolean cancelled = new AtomicBoolean();
        byte[] eventBytes = "data: payload\n\n".getBytes(StandardCharsets.UTF_8);
        Flux<ByteBuffer> content
            = Flux.concat(Flux.just(ByteBuffer.wrap(eventBytes)), Flux.never()).doOnCancel(() -> cancelled.set(true));
        BinaryData body = BinaryData.fromFlux(content, null, false).block();

        StepVerifier.create(ServerSentEventUtils.decode(body))
            .assertNext(event -> assertEquals("payload", event.getData()))
            .thenCancel()
            .verify();

        assertTrue(cancelled.get());
    }

    @Test
    public void syncAndAsyncDecodingHaveMatchingFraming() {
        String content = "event: first\ndata: one\r\n\r\nevent: second\ndata: two\n\n";
        List<ServerSentEvent<String>> syncEvents = new ArrayList<>();
        ServerSentEventUtils.process(BinaryData.fromString(content), syncEvents::add);

        List<ServerSentEvent<String>> asyncEvents
            = ServerSentEventUtils.decode(BinaryData.fromString(content)).collectList().block();

        assertEquals(2, asyncEvents.size());
        assertEquals(Arrays.asList(syncEvents.get(0).getEvent(), syncEvents.get(1).getEvent()),
            Arrays.asList(asyncEvents.get(0).getEvent(), asyncEvents.get(1).getEvent()));
        assertEquals(Arrays.asList(syncEvents.get(0).getData(), syncEvents.get(1).getData()),
            Arrays.asList(asyncEvents.get(0).getData(), asyncEvents.get(1).getData()));
    }

    private static void assertPersistentMetadata(List<ServerSentEvent<String>> events) {
        assertEquals(2, events.size());
        assertEquals(Arrays.asList("first", "second"), Arrays.asList(events.get(0).getData(), events.get(1).getData()));
        for (ServerSentEvent<String> event : events) {
            assertEquals("42", event.getId());
            assertEquals(Duration.ofSeconds(2), event.getRetryAfter());
        }
    }

    private static void assertInitialMetadata(ServerSentEvent<String> event) {
        assertEquals("1", event.getId());
        assertEquals(Duration.ofSeconds(1), event.getRetryAfter());
    }
}
