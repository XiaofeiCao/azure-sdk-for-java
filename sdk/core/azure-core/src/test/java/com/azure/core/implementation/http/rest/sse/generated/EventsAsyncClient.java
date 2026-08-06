// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.implementation.http.rest.sse.generated;

import com.azure.core.http.rest.RequestOptions;
import com.azure.core.http.rest.Response;
import com.azure.core.http.rest.SimpleResponse;
import com.azure.core.util.BinaryData;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public final class EventsAsyncClient {
    private final EventsClientImpl serviceClient;

    EventsAsyncClient(EventsClientImpl serviceClient) {
        this.serviceClient = serviceClient;
    }

    public Flux<ServiceStreamEvent> getEvents() {
        return Flux.defer(() -> serviceClient.getEventsWithResponseAsync(new RequestOptions())
            .flatMapMany(response -> ServiceStreamEvents.toFlux(response.getValue())));
    }

    public Mono<Response<Flux<ServiceStreamEvent>>> getEventsWithResponse(RequestOptions requestOptions) {
        return serviceClient.getEventsWithResponseAsync(requestOptions)
            .map(response -> new SimpleResponse<Flux<ServiceStreamEvent>>(response,
                ServiceStreamEvents.toFlux(response.getValue())));
    }
}
