// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.implementation.http.rest.sse.generated;

import com.azure.core.http.rest.RequestOptions;
import com.azure.core.http.rest.Response;
import com.azure.core.http.rest.SimpleResponse;
import com.azure.core.util.BinaryData;
import com.azure.core.util.IterableStream;

public final class EventsClient {
    private final EventsClientImpl serviceClient;

    EventsClient(EventsClientImpl serviceClient) {
        this.serviceClient = serviceClient;
    }

    public IterableStream<ServiceStreamEvent> getEvents() {
        return getEventsWithResponse(new RequestOptions()).getValue();
    }

    public Response<IterableStream<ServiceStreamEvent>> getEventsWithResponse(RequestOptions requestOptions) {
        Response<BinaryData> response = serviceClient.getEventsWithResponse(requestOptions);
        IterableStream<ServiceStreamEvent> events = ServiceStreamEvents.toIterableStream(response.getValue());
        return new SimpleResponse<>(response, events);
    }
}
