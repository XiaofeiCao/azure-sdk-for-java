// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.util.sse.generated.flat;

import com.azure.core.http.HttpMethod;
import com.azure.core.http.HttpPipeline;
import com.azure.core.http.HttpRequest;
import com.azure.core.util.sse.ServerSentEvent;
import com.azure.core.util.sse.ServerSentEventParser;
import reactor.core.publisher.Flux;

/**
 * Fixture async client for the mixed union in the flat/raw shape (TCGC/clientcore-style, no union).
 * <p>
 * The client returns the runtime {@link ServerSentEvent} envelope directly ({@code Flux<ServerSentEvent>}) and does
 * <strong>no</strong> typed deserialization: the consumer inspects {@link ServerSentEvent#getEvent()} and
 * deserializes {@link ServerSentEvent#getDataString()} into the model of its choice ({@link Info} /
 * {@link ResponseDelta}). This mirrors how {@code io.clientcore} surfaces SSE — a flat, untyped event stream.
 * <p>
 * The terminal {@code [DONE]} event is a content-matched sentinel that ends the stream and is not emitted.
 */
public final class MixedSseAsyncClient {
    static final String TERMINAL_EVENT = "[DONE]";

    private final HttpPipeline pipeline;
    private final String endpoint;

    /**
     * Creates the client.
     *
     * @param pipeline The HTTP pipeline.
     * @param endpoint The service endpoint.
     */
    public MixedSseAsyncClient(HttpPipeline pipeline, String endpoint) {
        this.pipeline = pipeline;
        this.endpoint = endpoint;
    }

    /**
     * Streams the raw {@link ServerSentEvent events}, completing on the terminal {@code [DONE]} sentinel.
     *
     * @return A {@link Flux} of {@link ServerSentEvent}.
     */
    public Flux<ServerSentEvent> receive() {
        HttpRequest request = new HttpRequest(HttpMethod.GET, endpoint + "/streaming/sse/mixed/receive");
        return pipeline.send(request)
            .flatMapMany(response -> ServerSentEventParser.parse(response.getBody()))
            .takeUntil(MixedSseAsyncClient::isTerminal)
            .filter(evt -> !isTerminal(evt));
    }

    static boolean isTerminal(ServerSentEvent evt) {
        return TERMINAL_EVENT.equals(evt.getDataString().trim());
    }
}
