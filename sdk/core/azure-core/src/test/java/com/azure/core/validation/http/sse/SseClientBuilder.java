// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.validation.http.sse;

import com.azure.core.http.HttpClient;
import com.azure.core.http.HttpPipeline;
import com.azure.core.http.HttpPipelineBuilder;

/**
 * Test-only builder shaped like a generated client builder.
 */
public final class SseClientBuilder {
    private String endpoint;
    private HttpClient httpClient;

    public SseClientBuilder endpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    public SseClientBuilder httpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
        return this;
    }

    public SseClient buildClient() {
        HttpPipeline pipeline = new HttpPipelineBuilder().httpClient(httpClient).build();
        return new SseClient(new SseClientImpl(pipeline, endpoint));
    }
}
