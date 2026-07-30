// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.util.sse.generated;

import com.azure.core.http.HttpMethod;
import com.azure.core.http.HttpPipeline;
import com.azure.core.http.HttpRequest;
import com.azure.core.util.sse.ServerSentEventParser;
import reactor.core.publisher.Flux;

/**
 * Fixture async client for scenario 1 (unnamed events) representing the "expected emitter output" (§4a).
 * <p>
 * The single unnamed union variant means there is no {@code event:} discriminator: every event is a default
 * {@code message} event carrying a JSON {@link Info} payload, so the streaming convenience deserializes the
 * {@code data} payload directly.
 */
public final class UnnamedSseAsyncClient {
    private final HttpPipeline pipeline;
    private final String endpoint;

    /**
     * Creates the client.
     *
     * @param pipeline The HTTP pipeline.
     * @param endpoint The service endpoint.
     */
    public UnnamedSseAsyncClient(HttpPipeline pipeline, String endpoint) {
        this.pipeline = pipeline;
        this.endpoint = endpoint;
    }

    /**
     * Streams the unnamed {@link Info} events.
     *
     * @return A {@link Flux} of {@link Info} events.
     */
    public Flux<Info> receive() {
        HttpRequest request = new HttpRequest(HttpMethod.GET, endpoint + "/streaming/sse/unnamed/receive");
        return pipeline.send(request)
            .flatMapMany(response -> ServerSentEventParser.parse(response.getBody()))
            .filter(evt -> evt.getData() != null && !evt.getData().isEmpty())
            .map(evt -> com.azure.core.util.BinaryData.fromString(String.join("\n", evt.getData()))
                .toObject(Info.class));
    }
}
