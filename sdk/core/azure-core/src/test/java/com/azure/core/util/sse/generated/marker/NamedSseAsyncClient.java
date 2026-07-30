// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.util.sse.generated.marker;

import com.azure.core.http.HttpMethod;
import com.azure.core.http.HttpPipeline;
import com.azure.core.http.HttpRequest;
import com.azure.core.util.logging.ClientLogger;
import com.azure.core.util.sse.ServerSentEvent;
import com.azure.core.util.sse.ServerSentEventParser;
import com.azure.json.JsonProviders;
import com.azure.json.JsonReader;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;

/**
 * Fixture async client for scenario 2 (named events + terminal {@code [DONE]}) representing the "expected emitter
 * output" in the marker-interface shape (§5b).
 * <p>
 * The streamed element type is the marker {@link ResponseEvents} itself, so consumers switch directly on the real
 * models. Each event's {@code data} JSON is dispatched through {@link ResponseEvents#fromJson(JsonReader, String)}
 * keyed on the SSE {@code event:} frame field; the terminal {@code [DONE]} event is a parser sentinel.
 */
public final class NamedSseAsyncClient {
    private static final ClientLogger LOGGER = new ClientLogger(NamedSseAsyncClient.class);
    static final String TERMINAL_EVENT = "[DONE]";

    private final HttpPipeline pipeline;
    private final String endpoint;

    /**
     * Creates the client.
     *
     * @param pipeline The HTTP pipeline.
     * @param endpoint The service endpoint.
     */
    public NamedSseAsyncClient(HttpPipeline pipeline, String endpoint) {
        this.pipeline = pipeline;
        this.endpoint = endpoint;
    }

    /**
     * Streams the named {@link ResponseEvents} events, completing on the terminal {@code [DONE]} sentinel.
     *
     * @return A {@link Flux} of {@link ResponseEvents} events.
     */
    public Flux<ResponseEvents> receive() {
        HttpRequest request = new HttpRequest(HttpMethod.GET, endpoint + "/streaming/sse/named/receive");
        return pipeline.send(request)
            .flatMapMany(response -> ServerSentEventParser.parse(response.getBody()))
            .takeUntil(NamedSseAsyncClient::isTerminal)
            .filter(evt -> !isTerminal(evt))
            .map(NamedSseAsyncClient::toResponseEvent)
            .filter(Objects::nonNull);
    }

    static boolean isTerminal(ServerSentEvent evt) {
        return evt.getData() != null && TERMINAL_EVENT.equals(String.join("\n", evt.getData()).trim());
    }

    static ResponseEvents toResponseEvent(ServerSentEvent evt) {
        String payload = evt.getData() == null ? "" : String.join("\n", evt.getData());
        try (JsonReader reader = JsonProviders.createReader(payload)) {
            return ResponseEvents.fromJson(reader, evt.getEvent());
        } catch (IOException e) {
            throw LOGGER.logExceptionAsError(new UncheckedIOException(e));
        }
    }
}
