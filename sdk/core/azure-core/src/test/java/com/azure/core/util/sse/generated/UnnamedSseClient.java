// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.util.sse.generated;

import com.azure.core.http.HttpMethod;
import com.azure.core.http.HttpPipeline;
import com.azure.core.http.HttpRequest;
import com.azure.core.http.HttpResponse;
import com.azure.core.util.BinaryData;
import com.azure.core.util.Context;
import com.azure.core.util.IterableStream;
import com.azure.core.util.sse.ServerSentEventParser;

import java.util.stream.Collectors;

/**
 * Fixture sync client for scenario 1 (unnamed events) representing the "expected emitter output" (§4a).
 * <p>
 * Mirrors {@link UnnamedSseAsyncClient} but exposes a blocking {@link IterableStream} fed by the same
 * {@link ServerSentEventParser}.
 */
public final class UnnamedSseClient {
    private final HttpPipeline pipeline;
    private final String endpoint;

    /**
     * Creates the client.
     *
     * @param pipeline The HTTP pipeline.
     * @param endpoint The service endpoint.
     */
    public UnnamedSseClient(HttpPipeline pipeline, String endpoint) {
        this.pipeline = pipeline;
        this.endpoint = endpoint;
    }

    /**
     * Streams the unnamed {@link Info} events.
     *
     * @return An {@link IterableStream} of {@link Info} events.
     */
    public IterableStream<Info> receive() {
        HttpRequest request = new HttpRequest(HttpMethod.GET, endpoint + "/streaming/sse/unnamed/receive");
        HttpResponse response = pipeline.sendSync(request, Context.NONE);
        return new IterableStream<>(ServerSentEventParser.parse(response.getBodyAsInputStreamSync())
            .stream()
            .filter(evt -> evt.getData() != null && !evt.getData().isEmpty())
            .map(evt -> BinaryData.fromString(String.join("\n", evt.getData())).toObject(Info.class))
            .collect(Collectors.toList()));
    }
}
