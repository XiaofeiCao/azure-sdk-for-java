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

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
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

    }

    @Test
    public void toFluxCompletesOnEofWithoutReconnecting() {
        TestResponse response = response(200, BinaryData.fromString("id: 1\nretry: 0\ndata: one\n\n"));

        StepVerifier.create(ServerSentEventStreams.toFlux(response, (event, data) -> data))
            .assertNext(event -> assertEquals("one", event.getData()))
            .verifyComplete();

    }

    @Test
    public void toFluxReturnsEmptyForNoContent() {
        TestResponse response = response(204, null);

        StepVerifier.create(ServerSentEventStreams.toFlux(response, (event, data) -> data)).verifyComplete();

    }

    @Test
    public void toFluxRejectsInvalidContentType() {
        AtomicBoolean cancelled = new AtomicBoolean();
        TestResponse response = response(200, cancellableBody(cancelled), "application/json");

        assertThrows(IllegalStateException.class,
            () -> ServerSentEventStreams.toFlux(response, (event, data) -> data).blockLast());

        assertTrue(cancelled.get());

    }

    @Test
    public void toFluxDecodesFragmentedUtf16Event() {
        byte[] bytes = "data: caf\u00e9\n\n".getBytes(StandardCharsets.UTF_16BE);
        List<ByteBuffer> buffers = new ArrayList<>();
        for (byte value : bytes) {
            buffers.add(ByteBuffer.wrap(new byte[] { value }));
        }
        TestResponse response = response(200, BinaryData.fromFlux(Flux.fromIterable(buffers), null, false).block(),
            "text/event-stream; charset=UTF-16BE");

        StepVerifier.create(ServerSentEventStreams.toFlux(response, (event, data) -> data))
            .assertNext(event -> assertEquals("caf\u00e9", event.getData()))
            .verifyComplete();

    }

    @Test
    public void toFluxUsesBomBeforeDeclaredCharset() {
        byte[] event = "data: caf\u00e9\n\n".getBytes(StandardCharsets.UTF_16BE);
        byte[] bytes = new byte[event.length + 2];
        bytes[0] = (byte) 0xFE;
        bytes[1] = (byte) 0xFF;
        System.arraycopy(event, 0, bytes, 2, event.length);
        List<ByteBuffer> buffers = new ArrayList<>();
        for (byte value : bytes) {
            buffers.add(ByteBuffer.wrap(new byte[] { value }));
        }
        TestResponse response = response(200, BinaryData.fromFlux(Flux.fromIterable(buffers), null, false).block(),
            "text/event-stream; charset=UTF-8");

        StepVerifier.create(ServerSentEventStreams.toFlux(response, (eventName, data) -> data))
            .assertNext(eventResult -> assertEquals("caf\u00e9", eventResult.getData()))
            .verifyComplete();

    }

    @Test
    public void toFluxWaitsForFragmentedUtf32LeBomAfterEmptyBuffer() {
        byte[] event = "data: caf\u00e9\n\n".getBytes(Charset.forName("UTF-32LE"));
        byte[] bytes = new byte[event.length + 4];
        bytes[0] = (byte) 0xFF;
        bytes[1] = (byte) 0xFE;
        System.arraycopy(event, 0, bytes, 4, event.length);
        List<ByteBuffer> buffers = new ArrayList<>();
        buffers.add(ByteBuffer.allocate(0));
        for (byte value : bytes) {
            buffers.add(ByteBuffer.wrap(new byte[] { value }));
        }
        TestResponse response = response(200, BinaryData.fromFlux(Flux.fromIterable(buffers), null, false).block(),
            "text/event-stream; charset=UTF-8");

        StepVerifier.create(ServerSentEventStreams.toFlux(response, (eventName, data) -> data))
            .assertNext(eventResult -> assertEquals("caf\u00e9", eventResult.getData()))
            .verifyComplete();

    }

    @Test
    public void toFluxCompletesForEmptyBody() {
        TestResponse response = response(200, BinaryData.fromBytes(new byte[0]));

        StepVerifier.create(ServerSentEventStreams.toFlux(response, (eventName, data) -> data)).verifyComplete();

    }

    @Test
    public void toFluxCompletesForBomOnlyBody() {
        TestResponse response
            = response(200, BinaryData.fromBytes(new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF }));

        StepVerifier.create(ServerSentEventStreams.toFlux(response, (eventName, data) -> data)).verifyComplete();

    }

    @Test
    public void toFluxCompletesForUtf16LeBomOnlyBody() {
        TestResponse response = response(200, BinaryData.fromBytes(new byte[] { (byte) 0xFF, (byte) 0xFE }));

        StepVerifier.create(ServerSentEventStreams.toFlux(response, (eventName, data) -> data)).verifyComplete();

    }

    @Test
    public void toFluxFailsForTruncatedBomPrefixWithoutNullPointerException() {
        TestResponse response = response(200, BinaryData.fromBytes(new byte[] { (byte) 0xEF }));

        StepVerifier.create(ServerSentEventStreams.toFlux(response, (eventName, data) -> data))
            .expectErrorMatches(
                error -> error instanceof IllegalStateException && !(error instanceof NullPointerException))
            .verify();

    }

    @Test
    public void toFluxRejectsUnsupportedCharset() {
        TestResponse response
            = response(200, BinaryData.fromString("data: one\n\n"), "text/event-stream; charset=not-a-charset");

        assertThrows(IllegalStateException.class,
            () -> ServerSentEventStreams.toFlux(response, (event, data) -> data).blockLast());

    }

    @Test
    public void toFluxAcceptsOrdinaryResponse() {
        ResponseBase<Object, BinaryData> response
            = new ResponseBase<>(null, 200, new HttpHeaders().set(HttpHeaderName.CONTENT_TYPE, "text/event-stream"),
                BinaryData.fromString("data: one\n\n"), null);

        StepVerifier.create(ServerSentEventStreams.toFlux(response, (event, data) -> data))
            .assertNext(event -> assertEquals("one", event.getData()))
            .verifyComplete();
    }

    @Test
    public void toFluxRejectsMissingContentType() {
        TestResponse response = new TestResponse(200, new HttpHeaders(), BinaryData.fromString("data: one\n\n"));

        assertThrows(IllegalStateException.class,
            () -> ServerSentEventStreams.toFlux(response, (event, data) -> data).blockLast());

    }

    @Test
    public void toFluxRejectsNullBody() {
        TestResponse response = response(200, null);

        assertThrows(NullPointerException.class,
            () -> ServerSentEventStreams.toFlux(response, (event, data) -> data).blockLast());

    }

    @Test
    public void toFluxRejectsUnsupportedStatus() {
        AtomicBoolean cancelled = new AtomicBoolean();
        TestResponse response = response(201, cancellableBody(cancelled));

        StepVerifier.create(ServerSentEventStreams.toFlux(response, (event, data) -> data))
            .expectErrorMessage("Expected a server-sent event response to have status code 200 or 204.")
            .verify();

        assertTrue(cancelled.get());

    }

    @Test
    public void toFluxDoesNotClaimBodyBeforeSubscription() {
        AtomicBoolean subscribed = new AtomicBoolean();
        BinaryData body
            = BinaryData.fromFlux(Flux.<ByteBuffer>never().doOnSubscribe(ignored -> subscribed.set(true)), null, false)
                .block();
        TestResponse response = response(200, body);

        ServerSentEventStreams.toFlux(response, (event, data) -> data);

        assertFalse(subscribed.get());
    }

    @Test
    public void toFluxAllowsOnlyOneSubscription() {
        TestResponse response = response(200, BinaryData.fromString("data: one\n\n"));
        Flux<ServerSentEvent<String>> events = ServerSentEventStreams.toFlux(response, (event, data) -> data);

        StepVerifier.create(events).expectNextCount(1).verifyComplete();
        StepVerifier.create(events)
            .expectErrorMessage("This server-sent event stream supports only one subscription.")
            .verify();

    }

    @Test
    public void toFluxCancellationCancelsBody() {
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
    }

    @Test
    public void toFluxCompletionClosesInputStreamBody() {
        AtomicBoolean closed = new AtomicBoolean();
        BinaryData body = inputStreamBody("data: one\n\n", false, closed);
        TestResponse response = response(200, body);

        StepVerifier.create(ServerSentEventStreams.toFlux(response, (event, data) -> data))
            .assertNext(event -> assertEquals("one", event.getData()))
            .verifyComplete();

        assertTrue(closed.get());
    }

    @Test
    public void toFluxCancellationClosesInputStreamBody() {
        AtomicBoolean closed = new AtomicBoolean();
        BinaryData body = inputStreamBody("data: one\n\n", true, closed);
        TestResponse response = response(200, body);

        StepVerifier.create(ServerSentEventStreams.toFlux(response, (event, data) -> data))
            .assertNext(event -> assertEquals("one", event.getData()))
            .thenCancel()
            .verify();

        assertTrue(closed.get());
    }

    @Test
    public void validationFailureClosesInputStreamBody() {
        AtomicBoolean closed = new AtomicBoolean();
        BinaryData body = inputStreamBody("data: one\n\n", true, closed);
        TestResponse response = response(200, body, "application/json");

        StepVerifier.create(ServerSentEventStreams.toFlux(response, (event, data) -> data))
            .expectErrorMessage(
                "Expected a successful server-sent event response to have Content-Type " + "'text/event-stream'.")
            .verify();

        assertTrue(closed.get());
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
    }

    @Test
    public void listenNotifiesError() {
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
    }

    @Test
    public void listenStopsDeliveringBufferedEventsAfterInterruption() {
        AtomicBoolean cancelled = new AtomicBoolean();
        BinaryData body = BinaryData
            .fromFlux(Flux
                .concat(Flux.just(ByteBuffer.wrap("data: one\n\ndata: two\n\n".getBytes(StandardCharsets.UTF_8))),
                    Flux.never())
                .doOnCancel(() -> cancelled.set(true)), null, false)
            .block();
        TestResponse response = response(200, body);
        List<String> events = new ArrayList<>();
        AtomicReference<Throwable> reportedError = new AtomicReference<>();

        try {
            RuntimeException exception = assertThrows(RuntimeException.class, () -> ServerSentEventStreams
                .listen(response, (event, data) -> data, new ServerSentEventListener<String>() {
                    @Override
                    public void onEvent(ServerSentEvent<String> event) {
                        events.add(event.getData());
                        Thread.currentThread().interrupt();
                    }

                    @Override
                    public void onError(Throwable error) {
                        reportedError.set(error);
                    }
                }));

            assertTrue(Thread.currentThread().isInterrupted());
            assertSame(exception, reportedError.get());
            assertEquals(1, events.size());
            assertTrue(cancelled.get());
        } finally {
            Thread.interrupted();
        }
    }

    @Test
    public void idAndRetryAreMetadataOnly() {
        TestResponse response = response(200, BinaryData.fromString("id: 42\nretry: 1000\ndata: one\n\n"));

        StepVerifier.create(ServerSentEventStreams.toFlux(response, (event, data) -> data)).assertNext(event -> {
            assertEquals("42", event.getId());
            assertEquals(Duration.ofSeconds(1), event.getRetryAfter());
        }).verifyComplete();
    }

    @Test
    public void parserSkipsMetadataOnlyBlocksAndPersistsMetadata() {
        TestResponse response
            = response(200, BinaryData.fromString("id: 42\nretry: 1000\n\ndata: one\n\ndata: two\n\n"));

        StepVerifier.create(ServerSentEventStreams.toFlux(response, (event, data) -> data).collectList())
            .assertNext(events -> {
                assertEquals(2, events.size());
                for (ServerSentEvent<String> event : events) {
                    assertEquals("42", event.getId());
                    assertEquals(Duration.ofSeconds(1), event.getRetryAfter());
                }
            })
            .verifyComplete();
    }

    @Test
    public void parserResetsIdAndUsesDefaultEvent() {
        TestResponse response = response(200, BinaryData.fromString("id: 42\ndata: one\n\nid:\nevent:\ndata: two\n\n"));

        StepVerifier.create(ServerSentEventStreams.toFlux(response, (event, data) -> data))
            .assertNext(event -> assertEquals("42", event.getId()))
            .assertNext(event -> {
                assertEquals("", event.getId());
                assertEquals("message", event.getEvent());
            })
            .verifyComplete();
    }

    @Test
    public void parserDiscardsUnterminatedEventAtEof() {
        TestResponse response = response(200, BinaryData.fromString("event: partial\ndata: payload"));

        StepVerifier.create(ServerSentEventStreams.toFlux(response, (event, data) -> data)).verifyComplete();
    }

    @Test
    public void toFluxPropagatesBodyFailure() {
        IOException failure = new IOException("connection closed");
        BinaryData body = BinaryData
            .fromFlux(Flux.concat(Flux.just(ByteBuffer.wrap("data: one\n\n".getBytes(StandardCharsets.UTF_8))),
                Flux.error(failure)), null, false)
            .block();
        TestResponse response = response(200, body);

        StepVerifier.create(ServerSentEventStreams.toFlux(response, (event, data) -> data))
            .assertNext(event -> assertEquals("one", event.getData()))
            .expectErrorMatches(error -> error == failure)
            .verify();
    }

    @Test
    public void toFluxPropagatesConverterFailureAndCancelsBody() {
        RuntimeException failure = new IllegalStateException("invalid event");
        AtomicBoolean cancelled = new AtomicBoolean();
        BinaryData body = BinaryData.fromFlux(
            Flux.concat(Flux.just(ByteBuffer.wrap("data: invalid\n\n".getBytes(StandardCharsets.UTF_8))), Flux.never())
                .doOnCancel(() -> cancelled.set(true)),
            null, false).block();
        TestResponse response = response(200, body);

        StepVerifier.create(ServerSentEventStreams.toFlux(response, (event, data) -> {
            throw failure;
        })).expectErrorMatches(error -> error == failure).verify();

        assertTrue(cancelled.get());
    }

    @Test
    public void toFluxEmitsTerminalEventAndCancelsRemainingBody() {
        AtomicBoolean cancelled = new AtomicBoolean();
        AtomicReference<Integer> conversionCount = new AtomicReference<>(0);
        BinaryData body
            = BinaryData
                .fromFlux(Flux.concat(
                    Flux.just(ByteBuffer
                        .wrap("data: one\n\ndata: [DONE]\n\ndata: ignored\n\n".getBytes(StandardCharsets.UTF_8))),
                    Flux.never()).doOnCancel(() -> cancelled.set(true)), null, false)
                .block();
        TestResponse response = response(200, body);

        StepVerifier.create(ServerSentEventStreams.toFlux(response, (event, data) -> {
            conversionCount.set(conversionCount.get() + 1);
            return data;
        }, event -> "[DONE]".equals(event.getData())))
            .assertNext(event -> assertEquals("one", event.getData()))
            .assertNext(event -> assertEquals("[DONE]", event.getData()))
            .verifyComplete();

        assertEquals(2, conversionCount.get());
        assertTrue(cancelled.get());
    }

    @Test
    public void toFluxFailsOnEofBeforeTerminalEvent() {
        TestResponse response = response(200, BinaryData.fromString("data: one\n\n"));

        StepVerifier.create(ServerSentEventStreams.toFlux(response, (event, data) -> data, event -> false))
            .assertNext(event -> assertEquals("one", event.getData()))
            .expectErrorMessage("The server-sent event stream ended before a terminal event.")
            .verify();

    }

    @Test
    public void toFluxFailsOnMetadataOnlyEofBeforeTerminalEvent() {
        TestResponse response = response(200, BinaryData.fromString("retry: 1000\n\n"));

        StepVerifier.create(ServerSentEventStreams.toFlux(response, (event, data) -> data, event -> false))
            .expectErrorMessage("The server-sent event stream ended before a terminal event.")
            .verify();

    }

    @Test
    public void toFluxCancellationBeforeTerminalDoesNotCreateEofError() {
        AtomicBoolean cancelled = new AtomicBoolean();
        BinaryData body = BinaryData.fromFlux(
            Flux.concat(Flux.just(ByteBuffer.wrap("data: one\n\n".getBytes(StandardCharsets.UTF_8))), Flux.never())
                .doOnCancel(() -> cancelled.set(true)),
            null, false).block();
        TestResponse response = response(200, body);

        StepVerifier.create(ServerSentEventStreams.toFlux(response, (event, data) -> data, event -> false))
            .assertNext(event -> assertEquals("one", event.getData()))
            .thenCancel()
            .verify();

        assertTrue(cancelled.get());
    }

    @Test
    public void toFluxNoContentDoesNotInvokeTerminalPredicate() {
        AtomicBoolean predicateInvoked = new AtomicBoolean();
        TestResponse response = response(204, null);

        StepVerifier.create(ServerSentEventStreams.toFlux(response, (event, data) -> data, event -> {
            predicateInvoked.set(true);
            return false;
        })).expectErrorMessage("The server-sent event stream ended before a terminal event.").verify();

        assertFalse(predicateInvoked.get());
    }

    @Test
    public void toFluxPropagatesTerminalPredicateFailure() {
        RuntimeException failure = new IllegalStateException("predicate failed");
        TestResponse response = response(200, BinaryData.fromString("data: one\n\n"));

        StepVerifier.create(ServerSentEventStreams.toFlux(response, (event, data) -> data, event -> {
            throw failure;
        }))
            .assertNext(event -> assertEquals("one", event.getData()))
            .expectErrorMatches(error -> error == failure)
            .verify();

    }

    @Test
    public void listenDeliversTerminalEventAndSkipsBufferedEventsAfterIt() {
        TestResponse response = response(200, BinaryData.fromString("data: one\n\ndata: [DONE]\n\ndata: ignored\n\n"));
        List<String> events = new ArrayList<>();
        AtomicReference<Integer> conversionCount = new AtomicReference<>(0);

        ServerSentEventStreams.listen(response, (event, data) -> {
            conversionCount.set(conversionCount.get() + 1);
            return data;
        }, event -> "[DONE]".equals(event.getData()), event -> events.add(event.getData()));

        assertEquals(2, events.size());
        assertEquals("[DONE]", events.get(1));
        assertEquals(2, conversionCount.get());
    }

    @Test
    public void listenTerminalEventClosesInputStreamBody() {
        AtomicBoolean closed = new AtomicBoolean();
        BinaryData body = inputStreamBody("data: one\n\ndata: [DONE]\n\n", true, closed);
        TestResponse response = response(200, body);
        List<String> events = new ArrayList<>();

        ServerSentEventStreams.listen(response, (event, data) -> data, event -> "[DONE]".equals(event.getData()),
            event -> events.add(event.getData()));

        assertEquals(2, events.size());
        assertEquals("[DONE]", events.get(1));
        assertTrue(closed.get());
    }

    @Test
    public void listenFailsOnEofBeforeTerminalEventAndNotifiesListener() {
        TestResponse response = response(200, BinaryData.fromString("data: one\n\n"));
        AtomicReference<Throwable> reportedError = new AtomicReference<>();
        List<String> events = new ArrayList<>();

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> ServerSentEventStreams
            .listen(response, (event, data) -> data, event -> false, new ServerSentEventListener<String>() {
                @Override
                public void onEvent(ServerSentEvent<String> event) {
                    events.add(event.getData());
                }

                @Override
                public void onError(Throwable error) {
                    reportedError.set(error);
                }
            }));

        assertEquals(1, events.size());
        assertSame(exception, reportedError.get());
    }

    @Test
    public void listenNoContentDoesNotInvokeTerminalPredicate() {
        AtomicBoolean predicateInvoked = new AtomicBoolean();
        AtomicReference<Throwable> reportedError = new AtomicReference<>();
        TestResponse response = response(204, null);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> ServerSentEventStreams.listen(response, (event, data) -> data, event -> {
                predicateInvoked.set(true);
                return false;
            }, new ServerSentEventListener<String>() {
                @Override
                public void onEvent(ServerSentEvent<String> event) {
                }

                @Override
                public void onError(Throwable error) {
                    reportedError.set(error);
                }
            }));

        assertFalse(predicateInvoked.get());
        assertSame(exception, reportedError.get());
    }

    @Test
    public void listenPropagatesTerminalPredicateFailure() {
        RuntimeException failure = new IllegalStateException("predicate failed");
        AtomicReference<Throwable> reportedError = new AtomicReference<>();
        AtomicBoolean closed = new AtomicBoolean();
        TestResponse response = response(200, BinaryData.fromString("data: one\n\n"));

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> ServerSentEventStreams.listen(response, (event, data) -> data, event -> {
                throw failure;
            }, new ServerSentEventListener<String>() {
                @Override
                public void onEvent(ServerSentEvent<String> event) {
                }

                @Override
                public void onError(Throwable error) {
                    reportedError.set(error);
                }

                @Override
                public void onClose() {
                    closed.set(true);
                }
            }));

        assertSame(failure, exception);
        assertSame(failure, reportedError.get());
        assertTrue(closed.get());
    }

    private static TestResponse response(int statusCode, BinaryData body) {
        return response(statusCode, body, "text/event-stream");
    }

    private static BinaryData cancellableBody(AtomicBoolean cancelled) {
        return BinaryData.fromFlux(Flux.<ByteBuffer>never().doOnCancel(() -> cancelled.set(true)), null, false).block();
    }

    private static BinaryData inputStreamBody(String value, boolean infinite, AtomicBoolean closed) {
        return BinaryData.fromStream(new TrackingInputStream(value.getBytes(StandardCharsets.UTF_8), infinite, closed));
    }

    private static TestResponse response(int statusCode, BinaryData body, String contentType) {
        return new TestResponse(statusCode, new HttpHeaders().set(HttpHeaderName.CONTENT_TYPE, contentType), body);
    }

    private static final class TestResponse extends ResponseBase<Object, BinaryData> {
        private TestResponse(int statusCode, HttpHeaders headers, BinaryData value) {
            super(null, statusCode, headers, value, null);
        }
    }

    private static final class TrackingInputStream extends InputStream {
        private final byte[] bytes;
        private final boolean infinite;
        private final AtomicBoolean closed;
        private int position;

        private TrackingInputStream(byte[] bytes, boolean infinite, AtomicBoolean closed) {
            this.bytes = bytes;
            this.infinite = infinite;
            this.closed = closed;
        }

        @Override
        public int read() {
            if (closed.get()) {
                return -1;
            }
            if (position < bytes.length) {
                return bytes[position++] & 0xFF;
            }
            return infinite ? ' ' : -1;
        }

        @Override
        public int read(byte[] destination, int offset, int length) {
            if (closed.get()) {
                return -1;
            }
            if (position < bytes.length) {
                int count = Math.min(length, bytes.length - position);
                System.arraycopy(bytes, position, destination, offset, count);
                position += count;
                return count;
            }
            if (!infinite) {
                return -1;
            }

            for (int i = offset; i < offset + length; i++) {
                destination[i] = ' ';
            }
            return length;
        }

        @Override
        public void close() {
            closed.set(true);
        }
    }
}
