// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.implementation.http.rest.sse.generated;

import com.azure.core.http.ServerSentEvent;
import com.azure.core.http.ServerSentEventListener;
import com.azure.core.implementation.util.ServerSentEventStream;
import com.azure.core.util.BinaryData;
import com.azure.core.util.ServerSentEventUtils;
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
        ServerSentEventUtils.process(body, ServiceStreamEvents::deserialize, listener);
    }

    static void listen(BinaryData body, Function<String, BinaryData> reconnect,
        ServerSentEventListener<ServiceStreamEvent> listener) {
        Objects.requireNonNull(listener, "'listener' cannot be null.");
        ServerSentEventStream.process(body, reconnect, ServiceStreamEvents::deserialize,
            event -> event.getData().isTerminal(), listener);
    }

    static Flux<ServerSentEvent<ServiceStreamEvent>> toFlux(BinaryData body) {
        Objects.requireNonNull(body, "'body' cannot be null.");

        return ServerSentEventUtils.decode(body, ServiceStreamEvents::deserialize)
            .takeUntil(event -> event.getData().isTerminal());
    }

    static Flux<ServerSentEvent<ServiceStreamEvent>> toFlux(BinaryData body,
        Function<String, Mono<BinaryData>> reconnect) {
        Objects.requireNonNull(body, "'body' cannot be null.");
        Objects.requireNonNull(reconnect, "'reconnect' cannot be null.");

        return ServerSentEventStream.decode(body, reconnect, ServiceStreamEvents::deserialize)
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
