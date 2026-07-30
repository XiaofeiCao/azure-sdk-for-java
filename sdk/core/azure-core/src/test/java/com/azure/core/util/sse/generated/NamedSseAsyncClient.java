// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.util.sse.generated;

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

/**
 * Fixture async client for scenario 2 (named events + terminal {@code [DONE]}) representing the "expected emitter
 * output" (§5a).
 * <p>
 * The stream is driven by the SSE {@code event:} frame field: each event's {@code data} JSON is dispatched through
 * {@link ResponseEventsBase#fromJson(JsonReader, String)} keyed on {@link ServerSentEvent#getEvent()}. The terminal
 * {@code [DONE]} event is a parser sentinel that ends the stream and is never emitted.
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
     * Streams the named {@link ResponseEventsBase} events, completing on the terminal {@code [DONE]} sentinel.
     *
     * @return A {@link Flux} of {@link ResponseEventsBase} events.
     */
    public Flux<ResponseEventsBase> receive() {
        HttpRequest request = new HttpRequest(HttpMethod.GET, endpoint + "/streaming/sse/named/receive");
        return pipeline.send(request)
            .flatMapMany(response -> ServerSentEventParser.parse(response.getBody()))
            .takeUntil(NamedSseAsyncClient::isTerminal)
            .filter(evt -> !isTerminal(evt))
            .map(NamedSseAsyncClient::toResponseEvent)
            .filter(java.util.Objects::nonNull);
    }

    static boolean isTerminal(ServerSentEvent evt) {
        return evt.getData() != null && TERMINAL_EVENT.equals(String.join("\n", evt.getData()).trim());
    }

    static ResponseEventsBase toResponseEvent(ServerSentEvent evt) {
        String payload = evt.getData() == null ? "" : String.join("\n", evt.getData());
        try (JsonReader reader = JsonProviders.createReader(payload)) {
            return ResponseEventsBase.fromJson(reader, evt.getEvent());
        } catch (IOException e) {
            throw LOGGER.logExceptionAsError(new UncheckedIOException(e));
        }
    }
}
