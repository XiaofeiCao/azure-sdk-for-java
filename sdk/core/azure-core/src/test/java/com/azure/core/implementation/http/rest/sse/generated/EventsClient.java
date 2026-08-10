// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.implementation.http.rest.sse.generated;

import com.azure.core.http.ServerSentEventListener;
import com.azure.core.http.rest.RequestOptions;
import com.azure.core.http.rest.Response;
import com.azure.core.http.rest.SimpleResponse;
import com.azure.core.implementation.util.ServerSentEventStreamResponse;
import com.azure.core.util.BinaryData;

import java.util.Objects;

public final class EventsClient {
    private final EventsClientImpl serviceClient;

    EventsClient(EventsClientImpl serviceClient) {
        this.serviceClient = serviceClient;
    }

    public StockUpdate getEvents() {
        return serviceClient.getEvents();
    }

    public Response<StockUpdate> getEventsWithResponse(RequestOptions requestOptions) {
        return serviceClient.getEventsWithResponse(requestOptions);
    }

    public void getEventsStream(ServerSentEventListener<ServiceStreamEvent> listener) {
        getEventsStreamWithResponse(listener, new RequestOptions());
    }

    public Response<Void> getEventsStreamWithResponse(ServerSentEventListener<ServiceStreamEvent> listener,
        RequestOptions requestOptions) {
        Objects.requireNonNull(listener, "'listener' cannot be null.");
        Response<BinaryData> response = serviceClient.getEventsStreamWithResponse(requestOptions);
        ServiceStreamEvents.listen(ServerSentEventStreamResponse.fromResponse(response),
            lastEventId -> ServerSentEventStreamResponse
                .fromResponse(serviceClient.getEventsStreamWithResponse(requestOptions, lastEventId)),
            listener);
        return new SimpleResponse<>(response, null);
    }
}
