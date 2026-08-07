// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.implementation.http.rest.sse.generated;

import com.azure.core.http.ServerSentEvent;
import com.azure.core.http.ServerSentEventListener;
import com.azure.core.util.BinaryData;
import com.azure.core.util.ServerSentEventUtils;
import com.azure.json.JsonProviders;
import com.azure.json.JsonReader;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

final class ServiceStreamEvents {
    private ServiceStreamEvents() {
    }

    static void listen(BinaryData body, ServerSentEventListener<ServiceStreamEvent> listener) {
        Objects.requireNonNull(listener, "'listener' cannot be null.");

        ServerSentEventUtils.process(body, ServiceStreamEvents::deserialize,
            new ServerSentEventListener<ServiceStreamEvent>() {
                @Override
                public boolean onEvent(ServerSentEvent<ServiceStreamEvent> event) throws IOException {
                    boolean shouldContinue = listener.onEvent(event);
                    return shouldContinue && !event.getData().isTerminal();
                }

                @Override
                public void onError(Throwable error) {
                    listener.onError(error);
                }

                @Override
                public void onClose() {
                    listener.onClose();
                }
            });
    }

    static Flux<ServerSentEvent<ServiceStreamEvent>> toFlux(BinaryData body) {
        Objects.requireNonNull(body, "'body' cannot be null.");

        return ServerSentEventUtils.decode(body, ServiceStreamEvents::deserialize)
            .takeUntil(event -> event.getData().isTerminal());
    }

    private static ServiceStreamEvent deserialize(String eventName, String data) throws IOException {
        if ("[DONE]".equals(data)) {
            return ServiceStreamEvent.terminal();
        }

        try (JsonReader reader = JsonProviders.createReader(data.getBytes(StandardCharsets.UTF_8))) {
            return ServiceStreamEvent.fromJson(reader, eventName);
        }
    }
}
