// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.http;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * Response returned after its server-sent event body has been consumed.
 */
final class ServerSentEventHttpResponse extends HttpResponse {
    private final HttpResponse response;

    ServerSentEventHttpResponse(HttpResponse response) {
        super(response.getRequest());
        this.response = response;
    }

    @Override
    public int getStatusCode() {
        return response.getStatusCode();
    }

    @Override
    @Deprecated
    public String getHeaderValue(String name) {
        return response.getHeaderValue(name);
    }

    @Override
    public HttpHeaders getHeaders() {
        return response.getHeaders();
    }

    @Override
    public Flux<ByteBuffer> getBody() {
        return Flux.empty();
    }

    @Override
    public Mono<byte[]> getBodyAsByteArray() {
        return Mono.just(new byte[0]);
    }

    @Override
    public Mono<String> getBodyAsString() {
        return Mono.just("");
    }

    @Override
    public Mono<String> getBodyAsString(Charset charset) {
        return Mono.just("");
    }

    @Override
    public void close() {
        response.close();
    }
}
