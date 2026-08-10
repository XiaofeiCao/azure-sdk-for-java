// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.implementation.http.rest.sse.generated;

import com.azure.core.http.ServerSentEvent;
import com.azure.core.http.rest.RequestOptions;
import com.azure.core.http.rest.Response;
import com.azure.core.http.rest.SimpleResponse;
import com.azure.core.implementation.util.ServerSentEventStreamResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public final class EventsAsyncClient {
    private final EventsClientImpl serviceClient;

    EventsAsyncClient(EventsClientImpl serviceClient) {
        this.serviceClient = serviceClient;
    }

    public Flux<ServerSentEvent<ServiceStreamEvent>> getEvents() {
        return Flux.defer(() -> {
            RequestOptions requestOptions = new RequestOptions();
            return serviceClient.getEventsWithResponseAsync(requestOptions)
                .flatMapMany(
                    response -> ServiceStreamEvents.toFlux(ServerSentEventStreamResponse.fromResponse(response),
                        lastEventId -> serviceClient.getEventsWithResponseAsync(requestOptions, lastEventId)
                            .map(ServerSentEventStreamResponse::fromResponse)));
        });
    }

    public Mono<Response<Flux<ServerSentEvent<ServiceStreamEvent>>>>
        getEventsWithResponse(RequestOptions requestOptions) {
        return serviceClient.getEventsWithResponseAsync(requestOptions)
            .map(response -> new SimpleResponse<Flux<ServerSentEvent<ServiceStreamEvent>>>(response,
                ServiceStreamEvents.toFlux(ServerSentEventStreamResponse.fromResponse(response),
                    lastEventId -> serviceClient.getEventsWithResponseAsync(requestOptions, lastEventId)
                        .map(ServerSentEventStreamResponse::fromResponse))));
    }
}
