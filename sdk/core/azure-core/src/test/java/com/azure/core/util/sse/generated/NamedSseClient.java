// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.util.sse.generated;

import com.azure.core.http.HttpMethod;
import com.azure.core.http.HttpPipeline;
import com.azure.core.http.HttpRequest;
import com.azure.core.http.HttpResponse;
import com.azure.core.util.Context;
import com.azure.core.util.IterableStream;
import com.azure.core.util.sse.ServerSentEventParser;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Fixture sync client for scenario 2 (named events + terminal {@code [DONE]}) representing the "expected emitter
 * output" (§5a). Mirrors {@link NamedSseAsyncClient} using the blocking parser and a {@link Stream}-based
 * {@code takeWhile}-equivalent to stop at the terminal sentinel.
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
     * Streams the named {@link ResponseEventsBase} events, completing on the terminal {@code [DONE]} sentinel.
     *
     * @return An {@link IterableStream} of {@link ResponseEventsBase} events.
     */
    public IterableStream<ResponseEventsBase> receive() {
        HttpRequest request = new HttpRequest(HttpMethod.GET, endpoint + "/streaming/sse/named/receive");
        HttpResponse response = pipeline.sendSync(request, Context.NONE);
        // Java 8 has no Stream#takeWhile; collect and stop at the terminal sentinel manually.
        return new IterableStream<>(
            ServerSentEventParser.parse(response.getBodyAsInputStreamSync()).stream().filter(evt -> {
                // Rely on ordering: the sentinel is the last event, everything before it is a real event.
                return !NamedSseAsyncClient.isTerminal(evt);
            }).map(NamedSseAsyncClient::toResponseEvent).filter(Objects::nonNull).collect(Collectors.toList()));
    }
}
