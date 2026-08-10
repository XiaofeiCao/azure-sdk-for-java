// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.implementation.http.rest.sse.generated;

import com.azure.core.http.ServerSentEvent;
import com.azure.core.http.ServerSentEventListener;
import com.azure.core.implementation.util.ServerSentEventStream;
import com.azure.core.implementation.util.ServerSentEventStreamResponse;
import com.azure.core.util.BinaryData;
import com.azure.json.JsonProviders;
import com.azure.json.JsonReader;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Function;

final class ServiceStreamEvents {
    private ServiceStreamEvents() {
    }

    static void listen(BinaryData body, ServerSentEventListener<ServiceStreamEvent> listener) {
        Objects.requireNonNull(listener, "'listener' cannot be null.");
        ServerSentEventStream.process(body, ServiceStreamEvents::deserialize, listener);
    }

    static void listen(ServerSentEventStreamResponse response,
        Function<String, ServerSentEventStreamResponse> reconnect,
        ServerSentEventListener<ServiceStreamEvent> listener) {
        Objects.requireNonNull(listener, "'listener' cannot be null.");
        ServerSentEventStream.process(response, reconnect, ServiceStreamEvents::deserialize,
            event -> event.getData().isTerminal(), listener);
    }

    static Flux<ServerSentEvent<ServiceStreamEvent>> toFlux(BinaryData body) {
        Objects.requireNonNull(body, "'body' cannot be null.");

        return ServerSentEventStream.decode(body, ServiceStreamEvents::deserialize)
            .takeUntil(event -> event.getData().isTerminal());
    }

    static Flux<ServerSentEvent<ServiceStreamEvent>> toFlux(ServerSentEventStreamResponse response,
        Function<String, Mono<ServerSentEventStreamResponse>> reconnect) {
        Objects.requireNonNull(response, "'response' cannot be null.");
        Objects.requireNonNull(reconnect, "'reconnect' cannot be null.");

        return ServerSentEventStream.decode(response, reconnect, ServiceStreamEvents::deserialize)
            .takeUntil(event -> event.getData().isTerminal());
    }

    private static ServiceStreamEvent deserialize(String eventName, String data) {
        if ("[DONE]".equals(data)) {
            return ServiceStreamEvent.terminal();
        }

        try (JsonReader reader = JsonProviders.createReader(data.getBytes(StandardCharsets.UTF_8))) {
            return ServiceStreamEvent.fromJson(reader, eventName);
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }
}
