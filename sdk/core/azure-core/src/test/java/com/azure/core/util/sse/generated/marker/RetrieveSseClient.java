// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.util.sse.generated.marker;

import com.azure.core.http.HttpHeaderName;
import com.azure.core.http.HttpMethod;
import com.azure.core.http.HttpPipeline;
import com.azure.core.http.HttpRequest;
import com.azure.core.http.HttpResponse;
import com.azure.core.util.BinaryData;
import com.azure.core.util.Context;
import com.azure.core.util.IterableStream;
import com.azure.core.util.sse.ServerSentEventParser;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Fixture sync client for scenario 3 (retrieve, POST with a JSON body) in the marker-interface shape (§6f).
 * Mirrors {@link RetrieveSseAsyncClient} using the blocking parser.
 */
public final class RetrieveSseClient {
    private final HttpPipeline pipeline;
    private final String endpoint;

    /**
     * Creates the client.
     *
     * @param pipeline The HTTP pipeline.
     * @param endpoint The service endpoint.
     */
    public RetrieveSseClient(HttpPipeline pipeline, String endpoint) {
        this.pipeline = pipeline;
        this.endpoint = endpoint;
    }

    /**
     * Streams the {@link RetrievalEvents} events for the given request, completing on the terminal {@code [DONE]}
     * sentinel.
     *
     * @param request The retrieval request body.
     * @return An {@link IterableStream} of {@link RetrievalEvents} events.
     */
    public IterableStream<RetrievalEvents> stream(RetrievalRequest request) {
        HttpRequest httpRequest = new HttpRequest(HttpMethod.POST, endpoint + "/streaming/sse/retrieve/stream")
            .setBody(BinaryData.fromObject(request));
        httpRequest.getHeaders().set(HttpHeaderName.CONTENT_TYPE, "application/json");
        HttpResponse response = pipeline.sendSync(httpRequest, Context.NONE);
        return new IterableStream<>(ServerSentEventParser.parse(response.getBodyAsInputStreamSync())
            .stream()
            .filter(evt -> !RetrieveSseAsyncClient.isTerminal(evt))
            .map(RetrieveSseAsyncClient::toRetrievalEvent)
            .filter(Objects::nonNull)
            .collect(Collectors.toList()));
    }
}
