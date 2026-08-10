// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.implementation.http.rest.sse.generated;

import com.azure.core.http.HttpClient;
import com.azure.core.http.HttpPipeline;
import com.azure.core.http.HttpPipelineBuilder;

import java.util.Objects;

public final class EventsClientBuilder {
    private String endpoint;
    private HttpClient httpClient;

    public EventsClientBuilder endpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    public EventsClientBuilder httpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
        return this;
    }

    public EventsClient buildClient() {
        return new EventsClient(buildImpl());
    }

    public EventsAsyncClient buildAsyncClient() {
        return new EventsAsyncClient(buildImpl());
    }

    private EventsClientImpl buildImpl() {
        Objects.requireNonNull(endpoint, "'endpoint' cannot be null.");
        Objects.requireNonNull(httpClient, "'httpClient' cannot be null.");
        HttpPipeline pipeline = new HttpPipelineBuilder().httpClient(httpClient).build();
        return new EventsClientImpl(pipeline, endpoint);
    }
}
