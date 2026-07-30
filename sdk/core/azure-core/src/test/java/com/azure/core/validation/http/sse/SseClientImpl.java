// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.validation.http.sse;

import com.azure.core.annotation.BodyParam;
import com.azure.core.annotation.ExpectedResponses;
import com.azure.core.annotation.Get;
import com.azure.core.annotation.HeaderParam;
import com.azure.core.annotation.Host;
import com.azure.core.annotation.HostParam;
import com.azure.core.annotation.Post;
import com.azure.core.annotation.ServiceInterface;
import com.azure.core.http.HttpPipeline;
import com.azure.core.http.ServerSentEventListener;
import com.azure.core.http.rest.RequestOptions;
import com.azure.core.http.rest.Response;
import com.azure.core.http.rest.RestProxy;
import com.azure.core.util.BinaryData;
import com.azure.core.util.Context;

import java.util.Objects;

/**
 * Test-only implementation shaped like TypeSpec-generated azure-core code.
 */
final class SseClientImpl {
    private static final String EVENT_STREAM = "text/event-stream";

    private final SseService service;
    private final String endpoint;

    SseClientImpl(HttpPipeline pipeline, String endpoint) {
        this.service = RestProxy.create(SseService.class, pipeline);
        this.endpoint = endpoint;
    }

    @Host("{endpoint}")
    @ServiceInterface(name = "SseTestClient")
    interface SseService {
        @Get("/streaming/sse/unnamed/receive")
        @ExpectedResponses({ 200 })
        Response<Void> receiveUnnamed(@HostParam("endpoint") String endpoint, @HeaderParam("Accept") String accept,
            RequestOptions requestOptions, Context context);

        @Get("/streaming/sse/named/receive")
        @ExpectedResponses({ 200 })
        Response<Void> receiveNamed(@HostParam("endpoint") String endpoint, @HeaderParam("Accept") String accept,
            RequestOptions requestOptions, Context context);

        @Post("/streaming/sse/retrieve/stream")
        @ExpectedResponses({ 200 })
        Response<Void> streamRetrieval(@HostParam("endpoint") String endpoint, @HeaderParam("Accept") String accept,
            @BodyParam("application/json") BinaryData request, RequestOptions requestOptions, Context context);
    }

    Response<Void> receiveUnnamedWithResponse(ServerSentEventListener listener, RequestOptions requestOptions) {
        return service.receiveUnnamed(endpoint, EVENT_STREAM, withListener(listener, requestOptions), Context.NONE);
    }

    Response<Void> receiveNamedWithResponse(ServerSentEventListener listener, RequestOptions requestOptions) {
        return service.receiveNamed(endpoint, EVENT_STREAM, withListener(listener, requestOptions), Context.NONE);
    }

    Response<Void> streamRetrievalWithResponse(RetrievalRequest request, ServerSentEventListener listener,
        RequestOptions requestOptions) {
        Objects.requireNonNull(request, "'request' cannot be null.");
        BinaryData requestBody = BinaryData.fromString("{\"query\":\"" + request.getQuery() + "\"}");
        return service.streamRetrieval(endpoint, EVENT_STREAM, requestBody, withListener(listener, requestOptions),
            Context.NONE);
    }

    private static RequestOptions withListener(ServerSentEventListener listener, RequestOptions requestOptions) {
        Objects.requireNonNull(listener, "'listener' cannot be null.");
        RequestOptions options = requestOptions == null ? new RequestOptions() : requestOptions;
        return options.addRequestCallback(request -> request.setServerSentEventListener(listener));
    }
}
