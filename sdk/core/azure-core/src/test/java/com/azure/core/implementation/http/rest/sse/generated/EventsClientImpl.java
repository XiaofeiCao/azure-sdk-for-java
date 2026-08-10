// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.implementation.http.rest.sse.generated;

import com.azure.core.annotation.ExpectedResponses;
import com.azure.core.annotation.Get;
import com.azure.core.annotation.HeaderParam;
import com.azure.core.annotation.Host;
import com.azure.core.annotation.HostParam;
import com.azure.core.annotation.ServiceInterface;
import com.azure.core.annotation.UnexpectedResponseExceptionType;
import com.azure.core.exception.HttpResponseException;
import com.azure.core.http.HttpPipeline;
import com.azure.core.http.rest.RequestOptions;
import com.azure.core.http.rest.Response;
import com.azure.core.http.rest.RestProxy;
import com.azure.core.http.rest.SimpleResponse;
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
        @ExpectedResponses({ 200, 204 })
        @UnexpectedResponseExceptionType(HttpResponseException.class)
        Mono<Response<BinaryData>> getEvents(@HostParam("endpoint") String endpoint,
            @HeaderParam("Accept") String accept, RequestOptions requestOptions, Context context);

        @Get("/events")
        @ExpectedResponses({ 200, 204 })
        @UnexpectedResponseExceptionType(HttpResponseException.class)
        Response<BinaryData> getEventsSync(@HostParam("endpoint") String endpoint, @HeaderParam("Accept") String accept,
            RequestOptions requestOptions, Context context);
    }

    private final EventsClientService service;
    private final String endpoint;

    EventsClientImpl(HttpPipeline pipeline, String endpoint) {
        this.service = RestProxy.create(EventsClientService.class, pipeline);
        this.endpoint = endpoint;
    }

    public StockUpdate getEvents() {
        return getEventsWithResponse(new RequestOptions()).getValue();
    }

    public Response<StockUpdate> getEventsWithResponse(RequestOptions requestOptions) {
        Response<BinaryData> response
            = service.getEventsSync(endpoint, "application/json", requestOptions, Context.NONE);
        return new SimpleResponse<>(response, deserializeStockUpdate(response));
    }

    public Mono<StockUpdate> getEventsAsync() {
        return getEventsWithResponseAsync(new RequestOptions())
            .flatMap(response -> Mono.justOrEmpty(response.getValue()));
    }

    public Mono<Response<StockUpdate>> getEventsWithResponseAsync(RequestOptions requestOptions) {
        return FluxUtil.withContext(context -> service.getEvents(endpoint, "application/json", requestOptions, context))
            .map(response -> new SimpleResponse<>(response, deserializeStockUpdate(response)));
    }

    public Response<BinaryData> getEventsStreamWithResponse(RequestOptions requestOptions) {
        return service.getEventsSync(endpoint, "text/event-stream", requestOptions, Context.NONE);
    }

    Response<BinaryData> getEventsStreamWithResponse(RequestOptions requestOptions, String lastEventId) {
        Context context = ServerSentEventStream.addReconnectContext(Context.NONE, lastEventId);
        return service.getEventsSync(endpoint, "text/event-stream", requestOptions, context);
    }

    public Mono<Response<BinaryData>> getEventsStreamWithResponseAsync(RequestOptions requestOptions) {
        return FluxUtil
            .withContext(context -> service.getEvents(endpoint, "text/event-stream", requestOptions, context));
    }

    Mono<Response<BinaryData>> getEventsStreamWithResponseAsync(RequestOptions requestOptions, String lastEventId) {
        return FluxUtil.withContext(context -> service.getEvents(endpoint, "text/event-stream", requestOptions,
            ServerSentEventStream.addReconnectContext(context, lastEventId)));
    }

    private static StockUpdate deserializeStockUpdate(Response<BinaryData> response) {
        return response.getStatusCode() == 204 ? null : response.getValue().toObject(StockUpdate.class);
    }
}
