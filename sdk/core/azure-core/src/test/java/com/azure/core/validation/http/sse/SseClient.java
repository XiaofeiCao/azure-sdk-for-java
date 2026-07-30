// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.validation.http.sse;

import com.azure.core.http.ServerSentEventListener;
import com.azure.core.http.rest.RequestOptions;
import com.azure.core.http.rest.Response;

/**
 * Test-only client shaped like a TypeSpec-generated synchronous client.
 */
public final class SseClient {
    private final SseClientImpl serviceClient;

    SseClient(SseClientImpl serviceClient) {
        this.serviceClient = serviceClient;
    }

    public void receiveUnnamed(ServerSentEventListener listener) {
        receiveUnnamedWithResponse(listener, new RequestOptions()).getValue();
    }

    public Response<Void> receiveUnnamedWithResponse(ServerSentEventListener listener, RequestOptions requestOptions) {
        return serviceClient.receiveUnnamedWithResponse(listener, requestOptions);
    }

    public void receiveNamed(ServerSentEventListener listener) {
        receiveNamedWithResponse(listener, new RequestOptions()).getValue();
    }

    public Response<Void> receiveNamedWithResponse(ServerSentEventListener listener, RequestOptions requestOptions) {
        return serviceClient.receiveNamedWithResponse(listener, requestOptions);
    }

    public void streamRetrieval(RetrievalRequest request, ServerSentEventListener listener) {
        streamRetrievalWithResponse(request, listener, new RequestOptions()).getValue();
    }

    public Response<Void> streamRetrievalWithResponse(RetrievalRequest request, ServerSentEventListener listener,
        RequestOptions requestOptions) {
        return serviceClient.streamRetrievalWithResponse(request, listener, requestOptions);
    }
}
