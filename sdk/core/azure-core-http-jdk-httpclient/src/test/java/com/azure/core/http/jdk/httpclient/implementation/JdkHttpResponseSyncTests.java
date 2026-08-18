// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.http.jdk.httpclient.implementation;

import com.azure.core.http.HttpMethod;
import com.azure.core.http.HttpRequest;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.time.Duration;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JdkHttpResponseSyncTests {
    @Test
    public void binaryDataCancellationClosesResponseBody() throws Exception {
        AtomicBoolean closed = new AtomicBoolean();
        InputStream bodyStream = new InfiniteInputStream(closed);
        java.net.http.HttpResponse<InputStream> response = createResponse(bodyStream);
        HttpRequest request = new HttpRequest(HttpMethod.GET, new URI("https://localhost/stream").toURL());
        JdkHttpResponseSync azureResponse = new JdkHttpResponseSync(request, response, Duration.ofSeconds(30));

        assertSame(azureResponse.getBodyAsBinaryData(), azureResponse.getBodyAsBinaryData());
        StepVerifier.create(azureResponse.getBodyAsBinaryData().toFluxByteBuffer().take(1))
            .expectNextCount(1)
            .verifyComplete();

        assertTrue(closed.get());
    }

    private static java.net.http.HttpResponse<InputStream> createResponse(InputStream bodyStream) {
        return new java.net.http.HttpResponse<InputStream>() {
            @Override
            public int statusCode() {
                return 200;
            }

            @Override
            public java.net.http.HttpRequest request() {
                return java.net.http.HttpRequest.newBuilder(URI.create("https://localhost/stream")).build();
            }

            @Override
            public Optional<java.net.http.HttpResponse<InputStream>> previousResponse() {
                return Optional.empty();
            }

            @Override
            public HttpHeaders headers() {
                return HttpHeaders.of(Collections.emptyMap(), (name, value) -> true);
            }

            @Override
            public InputStream body() {
                return bodyStream;
            }

            @Override
            public Optional<SSLSession> sslSession() {
                return Optional.empty();
            }

            @Override
            public URI uri() {
                return URI.create("https://localhost/stream");
            }

            @Override
            public HttpClient.Version version() {
                return HttpClient.Version.HTTP_1_1;
            }
        };
    }

    private static final class InfiniteInputStream extends InputStream {
        private final AtomicBoolean closed;

        private InfiniteInputStream(AtomicBoolean closed) {
            this.closed = closed;
        }

        @Override
        public int read() {
            return closed.get() ? -1 : 1;
        }

        @Override
        public int read(byte[] bytes, int offset, int length) {
            if (closed.get()) {
                return -1;
            }

            bytes[offset] = 1;
            return 1;
        }

        @Override
        public void close() throws IOException {
            closed.set(true);
        }
    }
}
