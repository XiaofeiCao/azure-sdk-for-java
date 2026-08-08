// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.implementation.http.rest.sse.generated;

import com.azure.core.http.ServerSentEventListener;
import com.azure.core.http.rest.RequestOptions;
import com.azure.core.http.rest.Response;
import com.azure.core.http.rest.SimpleResponse;
import com.azure.core.util.BinaryData;

import java.util.Objects;

public final class EventsClient {
    private final EventsClientImpl serviceClient;

    EventsClient(EventsClientImpl serviceClient) {
        this.serviceClient = serviceClient;
    }

    public void getEvents(ServerSentEventListener<ServiceStreamEvent> listener) {
        getEventsWithResponse(listener, new RequestOptions());
    }

    public Response<Void> getEventsWithResponse(ServerSentEventListener<ServiceStreamEvent> listener,
        RequestOptions requestOptions) {
        Objects.requireNonNull(listener, "'listener' cannot be null.");
        Response<BinaryData> response = serviceClient.getEventsWithResponse(requestOptions);
        ServiceStreamEvents.listen(response.getValue(),
            lastEventId -> serviceClient.getEventsWithResponse(requestOptions, lastEventId).getValue(), listener);
        return new SimpleResponse<>(response, null);
    }
}
