// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.util;

import com.azure.core.http.ServerSentEvent;
import com.azure.core.http.ServerSentEventDeserializer;
import com.azure.core.http.ServerSentEventListener;
import com.azure.core.implementation.util.ServerSentEventStream;
import reactor.core.publisher.Flux;

import java.io.UncheckedIOException;
import java.util.Objects;

/**
 * Utility methods for incrementally decoding one server-sent event response body.
 *
 * <p>Generated clients may layer request-level reconnection over these response-body parsing methods.</p>
 */
public final class ServerSentEventUtils {
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
     * @param body The response body containing a server-sent event stream.
     * @param deserializer The deserializer that converts event data to {@code T}.
     * @param <T> The type of the deserialized event data.
     * @return A flux of decoded server-sent events.
     * @throws NullPointerException If {@code body} or {@code deserializer} is {@code null}.
     */
    public static <T> Flux<ServerSentEvent<T>> decode(BinaryData body, ServerSentEventDeserializer<T> deserializer) {
        Objects.requireNonNull(body, "'body' cannot be null.");
        Objects.requireNonNull(deserializer, "'deserializer' cannot be null.");
        return ServerSentEventStream.decode(body, deserializer);
    }

    /**
     * Incrementally decodes and processes a response body containing server-sent events.
     *
     * <p>Multiple {@code data} fields in an event are joined with a newline in the decoded event data.</p>
     *
     * <p>The response body is closed when it completes or processing fails. Processing failures are delivered to
     * {@link ServerSentEventListener#onError(Throwable)}, followed by {@link ServerSentEventListener#onClose()}, and
     * rethrown to the caller.</p>
     *
     * @param body The response body containing a server-sent event stream.
     * @param listener The listener invoked for each decoded event.
     * @throws UncheckedIOException If an I/O error occurs while decoding the stream.
     * @throws RuntimeException If the listener fails while processing an event.
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
     * <p>The response body is closed when it completes or processing fails. Processing failures are delivered to
     * {@link ServerSentEventListener#onError(Throwable)}, followed by {@link ServerSentEventListener#onClose()}, and
     * rethrown to the caller.</p>
     *
     * @param body The response body containing a server-sent event stream.
     * @param deserializer The deserializer that converts event data to {@code T}.
     * @param listener The listener invoked with each typed event.
     * @param <T> The type of the deserialized event data.
     * @throws UncheckedIOException If an I/O error occurs while decoding the stream.
     * @throws RuntimeException If deserialization or the listener fails while processing an event.
     * @throws NullPointerException If {@code body}, {@code deserializer}, or {@code listener} is {@code null}.
     */
    public static <T> void process(BinaryData body, ServerSentEventDeserializer<T> deserializer,
        ServerSentEventListener<T> listener) {
        Objects.requireNonNull(body, "'body' cannot be null.");
        Objects.requireNonNull(deserializer, "'deserializer' cannot be null.");
        Objects.requireNonNull(listener, "'listener' cannot be null.");
        ServerSentEventStream.process(body, deserializer, listener);
    }
}
