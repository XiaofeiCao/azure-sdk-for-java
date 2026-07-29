// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.util.sse.generated;

import com.azure.core.http.HttpHeaderName;
import com.azure.core.http.HttpMethod;
import com.azure.core.http.HttpPipeline;
import com.azure.core.http.HttpRequest;
import com.azure.core.util.BinaryData;
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
 * Fixture async client for scenario 3 (retrieve, POST with a JSON body) representing the "expected emitter output"
 * (§5a). Identical streaming/dispatch shape to {@link NamedSseAsyncClient}; only the protocol method changes
 * (POST + {@code @body}).
 */
public final class RetrieveSseAsyncClient {
    private static final ClientLogger LOGGER = new ClientLogger(RetrieveSseAsyncClient.class);
    static final String TERMINAL_EVENT = "[DONE]";

    private final HttpPipeline pipeline;
    private final String endpoint;

    /**
     * Creates the client.
     *
     * @param pipeline The HTTP pipeline.
     * @param endpoint The service endpoint.
     */
    public RetrieveSseAsyncClient(HttpPipeline pipeline, String endpoint) {
        this.pipeline = pipeline;
        this.endpoint = endpoint;
    }

    /**
     * Streams the {@link RetrievalEventsBase} events for the given request, completing on the terminal
     * {@code [DONE]} sentinel.
     *
     * @param request The retrieval request body.
     * @return A {@link Flux} of {@link RetrievalEventsBase} events.
     */
    public Flux<RetrievalEventsBase> stream(RetrievalRequest request) {
        HttpRequest httpRequest = new HttpRequest(HttpMethod.POST, endpoint + "/streaming/sse/retrieve/stream")
            .setBody(BinaryData.fromObject(request));
        httpRequest.getHeaders().set(HttpHeaderName.CONTENT_TYPE, "application/json");
        return pipeline.send(httpRequest)
            .flatMapMany(response -> ServerSentEventParser.parse(response.getBody()))
            .takeUntil(RetrieveSseAsyncClient::isTerminal)
            .filter(evt -> !isTerminal(evt))
            .map(RetrieveSseAsyncClient::toRetrievalEvent)
            .filter(Objects::nonNull);
    }

    static boolean isTerminal(ServerSentEvent evt) {
        return TERMINAL_EVENT.equals(evt.getDataString().trim());
    }

    static RetrievalEventsBase toRetrievalEvent(ServerSentEvent evt) {
        try (JsonReader reader = JsonProviders.createReader(evt.getDataString())) {
            return RetrievalEventsBase.fromJson(reader, evt.getEvent());
        } catch (IOException e) {
            throw LOGGER.logExceptionAsError(new UncheckedIOException(e));
        }
    }
}
