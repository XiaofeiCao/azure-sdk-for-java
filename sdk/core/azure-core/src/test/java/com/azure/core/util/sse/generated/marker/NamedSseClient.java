// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.util.sse.generated.marker;

import com.azure.core.http.HttpMethod;
import com.azure.core.http.HttpPipeline;
import com.azure.core.http.HttpRequest;
import com.azure.core.http.HttpResponse;
import com.azure.core.util.Context;
import com.azure.core.util.IterableStream;
import com.azure.core.util.sse.ServerSentEventParser;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Fixture sync client for scenario 2 (named events + terminal {@code [DONE]}) in the marker-interface shape (§6f).
 * Mirrors {@link NamedSseAsyncClient} using the blocking parser.
 */
public final class NamedSseClient {
    private final HttpPipeline pipeline;
    private final String endpoint;

    /**
     * Creates the client.
     *
     * @param pipeline The HTTP pipeline.
     * @param endpoint The service endpoint.
     */
    public NamedSseClient(HttpPipeline pipeline, String endpoint) {
        this.pipeline = pipeline;
        this.endpoint = endpoint;
    }

    /**
     * Streams the named {@link ResponseEvents} events, completing on the terminal {@code [DONE]} sentinel.
     *
     * @return An {@link IterableStream} of {@link ResponseEvents} events.
     */
    public IterableStream<ResponseEvents> receive() {
        HttpRequest request = new HttpRequest(HttpMethod.GET, endpoint + "/streaming/sse/named/receive");
        HttpResponse response = pipeline.sendSync(request, Context.NONE);
        return new IterableStream<>(ServerSentEventParser.parse(response.getBodyAsInputStreamSync())
            .stream()
            .filter(evt -> !NamedSseAsyncClient.isTerminal(evt))
            .map(NamedSseAsyncClient::toResponseEvent)
            .filter(Objects::nonNull)
            .collect(Collectors.toList()));
    }
}
