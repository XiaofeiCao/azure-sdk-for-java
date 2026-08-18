// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.implementation.util;

import com.azure.core.http.HttpHeaderName;
import com.azure.core.http.rest.Response;
import com.azure.core.util.BinaryData;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Objects;

/**
 * Validates and exposes the fields used by a logical server-sent event stream.
 */
final class ServerSentEventStreamResponse {
    private final int statusCode;
    private final BinaryData body;
    private final Charset charset;

    ServerSentEventStreamResponse(int statusCode, BinaryData body, Charset charset) {
        this.statusCode = statusCode;
        this.body = body;
        this.charset = charset;
    }

    /**
     * Creates a stream response from a REST response.
     *
     * @param response The REST response.
     * @return The stream response.
     */
    static ServerSentEventStreamResponse fromResponse(Response<BinaryData> response) {
        Objects.requireNonNull(response, "'response' cannot be null.");
        if (response.getStatusCode() != 200 && response.getStatusCode() != 204) {
            cancelBody(response);
            throw new IllegalStateException("Expected a server-sent event response to have status code 200 or 204.");
        }
        String contentType = response.getHeaders().getValue(HttpHeaderName.CONTENT_TYPE);
        Charset charset = response.getStatusCode() == 200 ? HttpUtils.getTextEventStreamCharset(contentType) : null;
        if (response.getStatusCode() == 200
            && (!HttpUtils.isTextEventStreamContentType(contentType) || charset == null)) {
            cancelBody(response);
            throw new IllegalStateException(
                "Expected a successful server-sent event response to have Content-Type 'text/event-stream'.");
        }

        BinaryData body = response.getValue();
        if (response.getStatusCode() == 200) {
            if (body == null) {
                throw new NullPointerException("'response.getValue()' cannot be null unless the status code is 204.");
            }
        }
        return new ServerSentEventStreamResponse(response.getStatusCode(), body, charset);
    }

    private static void cancelBody(Response<BinaryData> response) {
        BinaryData body = response.getValue();
        if (body == null) {
            return;
        }

        body.toFluxByteBuffer().subscribe(new BaseSubscriber<ByteBuffer>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                cancel();
            }
        });
    }

    int getStatusCode() {
        return statusCode;
    }

    BinaryData getBody() {
        return body;
    }

    Charset getCharset() {
        return charset;
    }
}
