// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.search.documents.models.implementation.sse;

import com.azure.core.http.ServerSentEvent;
import com.azure.core.http.rest.Response;
import com.azure.core.util.BinaryData;
import com.azure.core.util.CloseableIterableStream;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import reactor.core.publisher.Flux;

/**
 * Consumes a single HTTP response as a server-sent event stream.
 */
public final class ServerSentEventStreams {
    private ServerSentEventStreams() {
    }

    /**
     * Decodes one response until the response body ends.
     *
     * @param response The streaming response.
     * @param converter Converts an event name and data payload into the event data type.
     * @param <T> The event data type.
     * @return A flux of decoded server-sent events.
     */
    public static <T> Flux<ServerSentEvent<T>> toFlux(Response<BinaryData> response,
        BiFunction<String, String, T> converter) {
        return ServerSentEventStream.toFlux(response, converter);
    }

    /**
     * Decodes one response until an inclusive terminal event is emitted.
     *
     * <p>HTTP 204 and response-body EOF complete normally without requiring a terminal event.</p>
     *
     * @param response The streaming response.
     * @param converter Converts an event name and data payload into the event data type.
     * @param terminalEvent Identifies an inclusive terminal event that ends processing early.
     * @param <T> The event data type.
     * @return A flux of decoded server-sent events.
     */
    public static <T> Flux<ServerSentEvent<T>> toFlux(Response<BinaryData> response,
        BiFunction<String, String, T> converter, Predicate<ServerSentEvent<T>> terminalEvent) {
        return ServerSentEventStream.toFlux(response, converter, terminalEvent);
    }

    /**
     * Decodes one response as a closeable iterable until the response body ends.
     *
     * @param response The streaming response.
     * @param converter Converts an event name and data payload into the event data type.
     * @param <T> The event data type.
     * @return A closeable iterable of decoded server-sent events.
     * Callers must close the returned stream to release the response body.
     */
    public static <T> CloseableIterableStream<ServerSentEvent<T>> toIterableStream(Response<BinaryData> response,
        BiFunction<String, String, T> converter) {
        return ServerSentEventStream.toIterableStream(response, converter);
    }

    /**
     * Decodes one response as a closeable iterable until an inclusive terminal event is returned.
     *
     * <p>HTTP 204 and response-body EOF complete normally without requiring a terminal event.</p>
     *
     * @param response The streaming response.
     * @param converter Converts an event name and data payload into the event data type.
     * @param terminalEvent Identifies an inclusive terminal event that ends processing early.
     * @param <T> The event data type.
     * @return A closeable iterable of decoded server-sent events.
     * Callers must close the returned stream to release the response body.
     */
    public static <T> CloseableIterableStream<ServerSentEvent<T>> toIterableStream(Response<BinaryData> response,
        BiFunction<String, String, T> converter, Predicate<ServerSentEvent<T>> terminalEvent) {
        return ServerSentEventStream.toIterableStream(response, converter, terminalEvent);
    }
}
