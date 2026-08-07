// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.util;

import com.azure.core.http.ServerSentEvent;
import com.azure.core.http.ServerSentEventDeserializer;
import com.azure.core.http.ServerSentEventListener;
import com.azure.core.implementation.FluxInputStream;
import com.azure.core.implementation.util.ServerSentEventHelper;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Utility methods for incrementally decoding one server-sent event response body.
 *
 * <p>These methods don't automatically reconnect when the response terminates. Retry and last-event identifier
 * metadata are available from {@link ServerSentEvent} for caller-managed reconnection.</p>
 */
public final class ServerSentEventUtils {
    private static final String DEFAULT_EVENT = "message";

    private ServerSentEventUtils() {
    }

    /**
     * Incrementally decodes a response body containing server-sent events.
     *
     * <p>Multiple {@code data} fields in an event are joined with a newline in the decoded event data. Cancelling the
     * returned {@link Flux} cancels the response body subscription.</p>
     *
     * @param body The response body containing a server-sent event stream.
     * @return A flux of decoded server-sent events.
     * @throws NullPointerException If {@code body} is {@code null}.
     */
    public static Flux<ServerSentEvent<String>> decode(BinaryData body) {
        return decode(body, (event, data) -> data);
    }

    /**
     * Incrementally decodes and deserializes a response body containing server-sent events.
     *
     * <p>Multiple {@code data} fields in an event are joined with a newline before deserialization. Cancelling the
     * returned {@link Flux} cancels the response body subscription.</p>
     *
     * <p>This method doesn't reconnect if the response body terminates. Callers that require reconnection can use
     * {@link ServerSentEvent#getId()} and {@link ServerSentEvent#getRetryAfter()} to construct a subsequent
     * request.</p>
     *
     * @param body The response body containing a server-sent event stream.
     * @param deserializer The deserializer that converts event data to {@code T}.
     * @param <T> The type of the deserialized event data.
     * @return A flux of decoded server-sent events.
     * @throws NullPointerException If {@code body} or {@code deserializer} is {@code null}.
     */
    public static <T> Flux<ServerSentEvent<T>> decode(BinaryData body, ServerSentEventDeserializer<T> deserializer) {
        Objects.requireNonNull(body, "'body' cannot be null.");
        Objects.requireNonNull(deserializer, "'deserializer' cannot be null.");

        return Flux.defer(() -> {
            ServerSentEventDecoder decoder = new ServerSentEventDecoder();
            Flux<ServerSentEventFrame> frames = body.toFluxByteBuffer()
                .concatMap(buffer -> decodeBuffer(decoder, buffer), 1)
                .concatWith(Flux.defer(() -> decodeRemaining(decoder)));
            return frames.concatMap(frame -> deserializeFrame(frame, deserializer), 1);
        });
    }

    /**
     * Incrementally decodes and processes a response body containing server-sent events.
     *
     * <p>Multiple {@code data} fields in an event are joined with a newline in the decoded event data.</p>
     *
     * <p>The response body is closed when it completes, processing fails, or the listener returns {@code false}.
     * Processing failures are delivered to {@link ServerSentEventListener#onError(Throwable)}, followed by
     * {@link ServerSentEventListener#onClose()}.</p>
     *
     * @param body The response body containing a server-sent event stream.
     * @param listener The listener invoked for each decoded event.
     * @throws NullPointerException If {@code body} or {@code listener} is {@code null}.
     */
    public static void process(BinaryData body, ServerSentEventListener<String> listener) {
        process(body, (event, data) -> data, listener);
    }

    /**
     * Incrementally decodes, deserializes, and processes a response body containing server-sent events.
     *
     * <p>Multiple {@code data} fields in an event are joined with a newline before deserialization.</p>
     *
     * <p>The response body is closed when it completes, processing fails, or the listener returns {@code false}.
     * Processing failures are delivered to {@link ServerSentEventListener#onError(Throwable)}, followed by
     * {@link ServerSentEventListener#onClose()}.</p>
     *
     * @param body The response body containing a server-sent event stream.
     * @param deserializer The deserializer that converts event data to {@code T}.
     * @param listener The listener invoked with each typed event.
     * @param <T> The type of the deserialized event data.
     * @throws NullPointerException If {@code body}, {@code deserializer}, or {@code listener} is {@code null}.
     */
    public static <T> void process(BinaryData body, ServerSentEventDeserializer<T> deserializer,
        ServerSentEventListener<T> listener) {
        Objects.requireNonNull(body, "'body' cannot be null.");
        Objects.requireNonNull(deserializer, "'deserializer' cannot be null.");
        Objects.requireNonNull(listener, "'listener' cannot be null.");

        ServerSentEventDecoder decoder = new ServerSentEventDecoder();
        byte[] readBuffer = new byte[8192];

        try (InputStream stream = new FluxInputStream(body.toFluxByteBuffer())) {
            int read;
            while ((read = stream.read(readBuffer)) != -1) {
                if (read > 0
                    && !processFrames(decoder.feed(ByteBuffer.wrap(readBuffer, 0, read)), deserializer, listener)) {
                    return;
                }
            }

            processFrames(decoder.finish(), deserializer, listener);
        } catch (IOException | RuntimeException exception) {
            listener.onError(exception);
        } finally {
            listener.onClose();
        }
    }

    private static Flux<ServerSentEventFrame> decodeBuffer(ServerSentEventDecoder decoder, ByteBuffer buffer) {
        return Flux.fromIterable(decoder.feed(buffer));
    }

    private static Flux<ServerSentEventFrame> decodeRemaining(ServerSentEventDecoder decoder) {
        return Flux.fromIterable(decoder.finish());
    }

    private static <T> Flux<ServerSentEvent<T>> deserializeFrame(ServerSentEventFrame frame,
        ServerSentEventDeserializer<T> deserializer) {
        try {
            T data = deserializer.deserialize(frame.event, frame.data);
            return data == null ? Flux.empty() : Flux.just(frame.toEvent(data));
        } catch (IOException exception) {
            return Flux.error(exception);
        }
    }

    private static <T> boolean processFrames(List<ServerSentEventFrame> frames,
        ServerSentEventDeserializer<T> deserializer, ServerSentEventListener<T> listener) throws IOException {
        for (ServerSentEventFrame frame : frames) {
            T data = deserializer.deserialize(frame.event, frame.data);
            if (data != null && !listener.onEvent(frame.toEvent(data))) {
                return false;
            }
        }
        return true;
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
            // Ignore retry values that don't fit in a long.
            return null;
        }
    }

    private static final class ServerSentEventDecoder {
        private byte[] lineBytes = new byte[256];
        private int lineLength;
        private boolean pendingCarriageReturn;
        private boolean firstLine = true;
        private String lastEventId;
        private String event;
        private List<String> data;
        private String comment;
        private Duration retryAfter;

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
            List<ServerSentEventFrame> events = new ArrayList<>();
            if (lineLength > 0) {
                processLine(decodeLine(), events);
            }

            ServerSentEventFrame event = buildEvent();
            if (event != null) {
                events.add(event);
            }
            return events;
        }

        private void appendByte(byte value) {
            if (lineLength == lineBytes.length) {
                lineBytes = Arrays.copyOf(lineBytes, lineBytes.length * 2);
            }
            lineBytes[lineLength++] = value;
        }

        private String decodeLine() {
            String line = new String(lineBytes, 0, lineLength, StandardCharsets.UTF_8);
            lineLength = 0;

            if (firstLine) {
                firstLine = false;
                if (!line.isEmpty() && line.charAt(0) == '\uFEFF') {
                    return line.substring(1);
                }
            }

            return line;
        }

        private void processLine(String line, List<ServerSentEventFrame> events) {
            if (line.isEmpty()) {
                ServerSentEventFrame event = buildEvent();
                if (event != null) {
                    events.add(event);
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
                        lastEventId = value;
                    }
                    break;

                case "retry":
                    Duration parsedRetryAfter = parseRetryAfter(value);
                    if (parsedRetryAfter != null) {
                        retryAfter = parsedRetryAfter;
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
            resetEvent();

            if (currentData == null) {
                return null;
            }

            if (currentEvent == null || currentEvent.isEmpty()) {
                currentEvent = DEFAULT_EVENT;
            }

            return new ServerSentEventFrame(lastEventId, currentEvent, String.join("\n", currentData), currentComment,
                retryAfter);
        }

        private void resetEvent() {
            event = null;
            data = null;
            comment = null;
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
            ServerSentEvent<T> result = new ServerSentEvent<>();
            ServerSentEventHelper.setId(result, id);
            ServerSentEventHelper.setEvent(result, event);
            ServerSentEventHelper.setData(result, data);
            ServerSentEventHelper.setComment(result, comment);
            ServerSentEventHelper.setRetryAfter(result, retryAfter);
            return result;
        }
    }
}
