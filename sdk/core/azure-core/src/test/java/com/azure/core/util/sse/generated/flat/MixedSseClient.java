// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.util.sse.generated.flat;

import com.azure.core.http.HttpMethod;
import com.azure.core.http.HttpPipeline;
import com.azure.core.http.HttpRequest;
import com.azure.core.http.HttpResponse;
import com.azure.core.util.Context;
import com.azure.core.util.IterableStream;
import com.azure.core.util.sse.ServerSentEvent;
import com.azure.core.util.sse.ServerSentEventParser;

import java.util.stream.Collectors;

/**
 * Fixture sync client for the mixed union in the flat/raw shape. Mirrors {@link MixedSseAsyncClient} using the
 * blocking parser and returns an {@link IterableStream} of the runtime {@link ServerSentEvent} envelope directly.
 */
public final class MixedSseClient {
    private final HttpPipeline pipeline;
    private final String endpoint;

    /**
     * Creates the client.
     *
     * @param pipeline The HTTP pipeline.
     * @param endpoint The service endpoint.
     */
    public MixedSseClient(HttpPipeline pipeline, String endpoint) {
        this.pipeline = pipeline;
        this.endpoint = endpoint;
    }

    /**
     * Streams the raw {@link ServerSentEvent events}, completing on the terminal {@code [DONE]} sentinel.
     *
     * @return An {@link IterableStream} of {@link ServerSentEvent}.
     */
    public IterableStream<ServerSentEvent> receive() {
        HttpRequest request = new HttpRequest(HttpMethod.GET, endpoint + "/streaming/sse/mixed/receive");
        HttpResponse response = pipeline.sendSync(request, Context.NONE);
        return new IterableStream<>(ServerSentEventParser.parse(response.getBodyAsInputStreamSync())
            .stream()
            .filter(evt -> !MixedSseAsyncClient.isTerminal(evt))
            .collect(Collectors.toList()));
    }
}
