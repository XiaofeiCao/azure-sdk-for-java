// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.implementation.http.rest.sse.generated;

import com.azure.core.annotation.ExpectedResponses;
import com.azure.core.annotation.Get;
import com.azure.core.annotation.HeaderParam;
import com.azure.core.annotation.Host;
import com.azure.core.annotation.HostParam;
import com.azure.core.annotation.ResponseBodyStreaming;
import com.azure.core.annotation.ServiceInterface;
import com.azure.core.annotation.UnexpectedResponseExceptionType;
import com.azure.core.exception.HttpResponseException;
import com.azure.core.http.HttpPipeline;
import com.azure.core.http.rest.RequestOptions;
import com.azure.core.http.rest.Response;
import com.azure.core.http.rest.RestProxy;
import com.azure.core.implementation.util.ServerSentEventStream;
import com.azure.core.util.BinaryData;
import com.azure.core.util.Context;
import com.azure.core.util.FluxUtil;
import reactor.core.publisher.Mono;

public final class EventsClientImpl {
    @Host("{endpoint}")
    @ServiceInterface(name = "Events")
    public interface EventsClientService {
        @Get("/events")
        @ResponseBodyStreaming
        @ExpectedResponses({ 200, 204 })
        @UnexpectedResponseExceptionType(HttpResponseException.class)
        Mono<Response<BinaryData>> getEvents(@HostParam("endpoint") String endpoint,
            @HeaderParam("Accept") String accept, @HeaderParam("Last-Event-Id") String lastEventId,
            RequestOptions requestOptions, Context context);

        @Get("/events")
        @ResponseBodyStreaming
        @ExpectedResponses({ 200, 204 })
        @UnexpectedResponseExceptionType(HttpResponseException.class)
        Response<BinaryData> getEventsSync(@HostParam("endpoint") String endpoint, @HeaderParam("Accept") String accept,
            @HeaderParam("Last-Event-Id") String lastEventId, RequestOptions requestOptions, Context context);
    }

    private final EventsClientService service;
    private final String endpoint;

    EventsClientImpl(HttpPipeline pipeline, String endpoint) {
        this.service = RestProxy.create(EventsClientService.class, pipeline);
        this.endpoint = endpoint;
    }

    public Response<BinaryData> getEventsWithResponse(RequestOptions requestOptions) {
        return service.getEventsSync(endpoint, "text/event-stream", null, requestOptions, Context.NONE);
    }

    Response<BinaryData> getEventsWithResponse(RequestOptions requestOptions, String lastEventId) {
        Context context = ServerSentEventStream.addReconnectContext(Context.NONE, lastEventId);
        return service.getEventsSync(endpoint, "text/event-stream", lastEventId, requestOptions, context);
    }

    public Mono<Response<BinaryData>> getEventsWithResponseAsync(RequestOptions requestOptions) {
        return FluxUtil
            .withContext(context -> service.getEvents(endpoint, "text/event-stream", null, requestOptions, context));
    }

    Mono<Response<BinaryData>> getEventsWithResponseAsync(RequestOptions requestOptions, String lastEventId) {
        return FluxUtil.withContext(context -> service.getEvents(endpoint, "text/event-stream", lastEventId,
            requestOptions, ServerSentEventStream.addReconnectContext(context, lastEventId)));
    }
}
