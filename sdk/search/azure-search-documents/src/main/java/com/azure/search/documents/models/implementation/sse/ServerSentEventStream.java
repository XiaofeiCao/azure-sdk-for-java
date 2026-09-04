// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.search.documents.models.implementation.sse;

import com.azure.core.http.ServerSentEvent;
import com.azure.core.http.rest.Response;
import com.azure.core.util.BinaryData;
import com.azure.core.util.CloseableIterableStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import reactor.core.publisher.Flux;

/**
 * Parses one server-sent event response.
 */
final class ServerSentEventStream {
    private static final String DEFAULT_EVENT = "message";

    private ServerSentEventStream() {
    }

    /**
     * Decodes an SSE response until the response body ends.
     *
     * @param response The streaming response.
     * @param converter Converts an event name and data payload into the event data type.
     * @param <T> The event data type.
     * @return A flux of decoded events.
     */
    static <T> Flux<ServerSentEvent<T>> toFlux(Response<BinaryData> response, BiFunction<String, String, T> converter) {
        Objects.requireNonNull(response, "'response' cannot be null.");
        Objects.requireNonNull(converter, "'converter' cannot be null.");
        return toFluxInternal(response, converter, null);
    }

    /**
     * Decodes an SSE response until an inclusive terminal event is emitted.
     *
     * @param response The streaming response.
     * @param converter Converts an event name and data payload into the event data type.
     * @param terminalEvent Identifies an inclusive terminal event that ends processing early.
     * @param <T> The event data type.
     * @return A flux of decoded events.
     */
    static <T> Flux<ServerSentEvent<T>> toFlux(Response<BinaryData> response, BiFunction<String, String, T> converter,
        Predicate<ServerSentEvent<T>> terminalEvent) {
        Objects.requireNonNull(response, "'response' cannot be null.");
        Objects.requireNonNull(converter, "'converter' cannot be null.");
        Objects.requireNonNull(terminalEvent, "'terminalEvent' cannot be null.");
        return toFluxInternal(response, converter, terminalEvent);
    }

    private static <T> Flux<ServerSentEvent<T>> toFluxInternal(Response<BinaryData> response,
        BiFunction<String, String, T> converter, Predicate<ServerSentEvent<T>> terminalEvent) {
        AtomicBoolean subscribed = new AtomicBoolean();
        return Flux.defer(() -> {
            if (!subscribed.compareAndSet(false, true)) {
                return Flux
                    .error(new IllegalStateException("This server-sent event stream supports only one subscription."));
            }

            ServerSentEventStreamResponse streamResponse = ServerSentEventStreamResponse.fromResponse(response);
            Flux<ServerSentEvent<T>> events
                = streamResponse.getStatusCode() == 204 ? Flux.empty() : decode(streamResponse.getBody(), converter);

            if (terminalEvent != null) {
                events = events.takeUntil(terminalEvent);
            }
            return events;
        });
    }

    /**
     * Decodes an SSE response as a closeable iterable until the response body ends.
     *
     * @param response The streaming response.
     * @param converter Converts an event name and data payload into the event data type.
     * @param <T> The event data type.
     * @return A closeable iterable of decoded events.
     */
    static <T> CloseableIterableStream<ServerSentEvent<T>> toIterableStream(Response<BinaryData> response,
        BiFunction<String, String, T> converter) {
        Objects.requireNonNull(response, "'response' cannot be null.");
        Objects.requireNonNull(converter, "'converter' cannot be null.");
        return toIterableStreamInternal(response, converter, null);
    }

    /**
     * Decodes an SSE response as a closeable iterable until an inclusive terminal event is returned.
     *
     * @param response The streaming response.
     * @param converter Converts an event name and data payload into the event data type.
     * @param terminalEvent Identifies an inclusive terminal event that ends processing early.
     * @param <T> The event data type.
     * @return A closeable iterable of decoded events.
     */
    static <T> CloseableIterableStream<ServerSentEvent<T>> toIterableStream(Response<BinaryData> response,
        BiFunction<String, String, T> converter, Predicate<ServerSentEvent<T>> terminalEvent) {
        Objects.requireNonNull(response, "'response' cannot be null.");
        Objects.requireNonNull(converter, "'converter' cannot be null.");
        Objects.requireNonNull(terminalEvent, "'terminalEvent' cannot be null.");
        return toIterableStreamInternal(response, converter, terminalEvent);
    }

    private static <T> CloseableIterableStream<ServerSentEvent<T>> toIterableStreamInternal(
        Response<BinaryData> response, BiFunction<String, String, T> converter,
        Predicate<ServerSentEvent<T>> terminalEvent) {
        ServerSentEventStreamResponse streamResponse = ServerSentEventStreamResponse.fromResponse(response);
        InputStream inputStream = streamResponse.getStatusCode() == 204
            ? new ByteArrayInputStream(new byte[0])
            : streamResponse.getBody().toStream();
        Iterable<ServerSentEvent<T>> iterable = new ServerSentEventIterable<>(inputStream, converter, terminalEvent);
        return new CloseableIterableStream<>(iterable, inputStream);
    }

    private static <T> Flux<ServerSentEvent<T>> decode(BinaryData body, BiFunction<String, String, T> converter) {
        ServerSentEventDecoder decoder = new ServerSentEventDecoder();
        Flux<ServerSentEventFrame> frames = body.toFluxByteBuffer()
            .hide()
            .concatMap(buffer -> Flux.fromIterable(decoder.feed(buffer)), 1)
            .concatWith(Flux.defer(() -> Flux.fromIterable(decoder.finish())));
        return frames.concatMap(frame -> {
            T data = converter.apply(frame.event, frame.data);
            return data == null ? Flux.empty() : Flux.just(frame.toEvent(data));
        }, 1);
    }

    private static void checkInterrupted() {
        if (Thread.currentThread().isInterrupted()) {
            throw new RuntimeException("Interrupted while processing the server-sent event stream.",
                new InterruptedException());
        }
    }

    private static String removeOptionalSpace(String value) {
        return value.startsWith(" ") ? value.substring(1) : value;
    }

    private static Duration parseRetryAfter(String value) {
        if (value.isEmpty()) {
            return null;
        }
        for (int i = 0; i < value.length(); i++) {
            char character = value.charAt(i);
            if (character < '0' || character > '9') {
                return null;
            }
        }
        try {
            return Duration.ofMillis(Long.parseLong(value));
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private static final class ServerSentEventIterable<T> implements Iterable<ServerSentEvent<T>> {
        private final InputStream inputStream;
        private final BiFunction<String, String, T> converter;
        private final Predicate<ServerSentEvent<T>> terminalEvent;
        private final AtomicBoolean iteratorCreated = new AtomicBoolean();

        private ServerSentEventIterable(InputStream inputStream, BiFunction<String, String, T> converter,
            Predicate<ServerSentEvent<T>> terminalEvent) {
            this.inputStream = inputStream;
            this.converter = converter;
            this.terminalEvent = terminalEvent;
        }

        @Override
        public Iterator<ServerSentEvent<T>> iterator() {
            if (!iteratorCreated.compareAndSet(false, true)) {
                throw new IllegalStateException("This server-sent event stream supports only one iterator.");
            }
            return new ServerSentEventIterator<>(inputStream, converter, terminalEvent);
        }
    }

    private static final class ServerSentEventIterator<T> implements Iterator<ServerSentEvent<T>> {
        private final InputStream inputStream;
        private final BiFunction<String, String, T> converter;
        private final Predicate<ServerSentEvent<T>> terminalEvent;
        private final ServerSentEventDecoder decoder = new ServerSentEventDecoder();
        private final byte[] readBuffer = new byte[8192];
        private final Deque<ServerSentEvent<T>> pendingEvents = new ArrayDeque<>();
        private boolean complete;

        private ServerSentEventIterator(InputStream inputStream, BiFunction<String, String, T> converter,
            Predicate<ServerSentEvent<T>> terminalEvent) {
            this.inputStream = inputStream;
            this.converter = converter;
            this.terminalEvent = terminalEvent;
        }

        @Override
        public boolean hasNext() {
            while (pendingEvents.isEmpty() && !complete) {
                readEvents();
            }
            return !pendingEvents.isEmpty();
        }

        @Override
        public ServerSentEvent<T> next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return pendingEvents.removeFirst();
        }

        private void readEvents() {
            checkInterrupted();
            List<ServerSentEventFrame> frames;
            try {
                int read = inputStream.read(readBuffer);
                if (read == -1) {
                    complete = true;
                    frames = decoder.finish();
                } else if (read == 0) {
                    return;
                } else {
                    frames = decoder.feed(ByteBuffer.wrap(readBuffer, 0, read));
                }
            } catch (IOException exception) {
                complete = true;
                throw new UncheckedIOException(exception);
            }

            for (ServerSentEventFrame frame : frames) {
                checkInterrupted();
                T data = converter.apply(frame.event, frame.data);
                if (data == null) {
                    continue;
                }

                ServerSentEvent<T> event = frame.toEvent(data);
                pendingEvents.addLast(event);
                if (terminalEvent != null && terminalEvent.test(event)) {
                    complete = true;
                    break;
                }
            }
        }
    }

    private static final class ServerSentEventDecoder {
        private final StreamState state = new StreamState();
        private byte[] lineBytes = new byte[256];
        private int lineLength;
        private boolean pendingCarriageReturn;
        private boolean firstLine = true;
        private String event;
        private List<String> data;
        private String comment;

        private List<ServerSentEventFrame> feed(ByteBuffer source) {
            ByteBuffer buffer = source.duplicate();
            List<ServerSentEventFrame> events = new ArrayList<>();
            while (buffer.hasRemaining()) {
                byte value = buffer.get();
                if (pendingCarriageReturn) {
                    pendingCarriageReturn = false;
                    if (value == '\n') {
                        continue;
                    }
                }
                if (value == '\n') {
                    processLine(decodeLine(), events);
                } else if (value == '\r') {
                    processLine(decodeLine(), events);
                    pendingCarriageReturn = true;
                } else {
                    appendByte(value);
                }
            }
            return events;
        }

        private List<ServerSentEventFrame> finish() {
            if (lineLength > 0) {
                // Validate trailing bytes even though an unterminated SSE event is discarded.
                decodeLine();
            }
            return Collections.emptyList();
        }

        private void appendByte(byte value) {
            if (lineLength == lineBytes.length) {
                int expandedLength
                    = lineBytes.length > Integer.MAX_VALUE / 2 ? Integer.MAX_VALUE : lineBytes.length * 2;
                lineBytes = Arrays.copyOf(lineBytes, expandedLength);
            }
            lineBytes[lineLength++] = value;
        }

        private String decodeLine() {
            String line;
            try {
                line = StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)
                    .decode(ByteBuffer.wrap(lineBytes, 0, lineLength))
                    .toString();
            } catch (CharacterCodingException exception) {
                throw new IllegalStateException("Failed to decode the server-sent event stream.", exception);
            }
            lineLength = 0;
            if (firstLine) {
                firstLine = false;
                if (!line.isEmpty() && line.charAt(0) == (char) 0xFEFF) {
                    return line.substring(1);
                }
            }
            return line;
        }

        private void processLine(String line, List<ServerSentEventFrame> events) {
            if (line.isEmpty()) {
                ServerSentEventFrame parsedEvent = buildEvent();
                if (parsedEvent != null) {
                    events.add(parsedEvent);
                }
                return;
            }
            if (line.charAt(0) == ':') {
                comment = removeOptionalSpace(line.substring(1));
                return;
            }

            int colonIndex = line.indexOf(':');
            String field = colonIndex < 0 ? line : line.substring(0, colonIndex);
            String value = colonIndex < 0 ? "" : removeOptionalSpace(line.substring(colonIndex + 1));
            switch (field) {
                case "event":
                    event = value;
                    break;

                case "data":
                    if (data == null) {
                        data = new ArrayList<>();
                    }
                    data.add(value);
                    break;

                case "id":
                    if (value.indexOf('\0') < 0) {
                        state.setLastEventId(value);
                    }
                    break;

                case "retry":
                    Duration parsedRetryAfter = parseRetryAfter(value);
                    if (parsedRetryAfter != null) {
                        state.setRetryAfter(parsedRetryAfter);
                    }
                    break;

                default:
                    break;
            }
        }

        private ServerSentEventFrame buildEvent() {
            String currentEvent = event;
            List<String> currentData = data;
            String currentComment = comment;
            event = null;
            data = null;
            comment = null;

            if (currentData == null) {
                return null;
            }
            if (currentEvent == null || currentEvent.isEmpty()) {
                currentEvent = DEFAULT_EVENT;
            }
            return new ServerSentEventFrame(state.lastEventId, currentEvent, String.join("\n", currentData),
                currentComment, state.retryAfter);
        }
    }

    private static final class StreamState {
        private String lastEventId;
        private Duration retryAfter;

        private void setLastEventId(String lastEventId) {
            this.lastEventId = lastEventId;
        }

        private void setRetryAfter(Duration retryAfter) {
            this.retryAfter = retryAfter;
        }
    }

    private static final class ServerSentEventFrame {
        private final String id;
        private final String event;
        private final String data;
        private final String comment;
        private final Duration retryAfter;

        private ServerSentEventFrame(String id, String event, String data, String comment, Duration retryAfter) {
            this.id = id;
            this.event = event;
            this.data = data;
            this.comment = comment;
            this.retryAfter = retryAfter;
        }

        private <T> ServerSentEvent<T> toEvent(T data) {
            return new ServerSentEvent<>(id, event, data, comment, retryAfter);
        }
    }
}
