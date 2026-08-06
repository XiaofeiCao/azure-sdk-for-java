// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.implementation.http.rest.sse;

import com.azure.core.http.HttpClient;
import com.azure.core.http.HttpHeader;
import com.azure.core.http.HttpHeaderName;
import com.azure.core.http.HttpHeaders;
import com.azure.core.http.HttpRequest;
import com.azure.core.http.HttpResponse;
import com.azure.core.util.Context;
import com.azure.core.util.FluxUtil;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

final class TrackingUrlConnectionHttpClient implements HttpClient {
    private final CountDownLatch cancellationLatch = new CountDownLatch(1);
    private final AtomicBoolean cancelled = new AtomicBoolean();

    @Override
    public Mono<HttpResponse> send(HttpRequest request) {
        return Mono.fromCallable(() -> openResponse(request)).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public HttpResponse sendSync(HttpRequest request, Context context) {
        try {
            return openResponse(request);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    boolean awaitCancellation(Duration timeout) {
        try {
            return cancellationLatch.await(timeout.toMillis(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(exception);
        }
    }

    boolean isCancelled() {
        return cancelled.get();
    }

    private HttpResponse openResponse(HttpRequest request) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) request.getUrl().openConnection();
        connection.setRequestMethod(request.getHttpMethod().toString());
        connection.setDoInput(true);

        for (HttpHeader header : request.getHeaders()) {
            for (String value : header.getValuesList()) {
                connection.addRequestProperty(header.getName(), value);
            }
        }

        int statusCode = connection.getResponseCode();
        InputStream responseStream = statusCode >= 400 ? connection.getErrorStream() : connection.getInputStream();
        if (responseStream == null) {
            responseStream = new ByteArrayInputStream(new byte[0]);
        }

        HttpHeaders headers = new HttpHeaders();
        for (Map.Entry<String, List<String>> header : connection.getHeaderFields().entrySet()) {
            if (header.getKey() != null) {
                headers.set(HttpHeaderName.fromString(header.getKey()), String.join(",", header.getValue()));
            }
        }

        return new UrlConnectionHttpResponse(request, statusCode, headers, connection, responseStream, this);
    }

    private void markCancelled() {
        if (cancelled.compareAndSet(false, true)) {
            cancellationLatch.countDown();
        }
    }

    private static final class UrlConnectionHttpResponse extends HttpResponse {
        private final int statusCode;
        private final HttpHeaders headers;
        private final HttpURLConnection connection;
        private final InputStream responseStream;
        private final AtomicBoolean closed = new AtomicBoolean();
        private final Flux<ByteBuffer> body;

        private UrlConnectionHttpResponse(HttpRequest request, int statusCode, HttpHeaders headers,
            HttpURLConnection connection, InputStream responseStream, TrackingUrlConnectionHttpClient owner) {
            super(request);
            this.statusCode = statusCode;
            this.headers = headers;
            this.connection = connection;
            this.responseStream = responseStream;
            this.body = Flux.<ByteBuffer>generate(sink -> {
                byte[] bytes = new byte[32];
                try {
                    int read = responseStream.read(bytes);
                    if (read < 0) {
                        sink.complete();
                    } else {
                        sink.next(ByteBuffer.wrap(Arrays.copyOf(bytes, read)));
                    }
                } catch (IOException exception) {
                    sink.error(exception);
                }
            }).doOnCancel(owner::markCancelled).doFinally(ignored -> close()).subscribeOn(Schedulers.boundedElastic());
        }

        @Override
        public int getStatusCode() {
            return statusCode;
        }

        @Override
        @Deprecated
        public String getHeaderValue(String name) {
            return headers.getValue(name);
        }

        @Override
        public HttpHeaders getHeaders() {
            return headers;
        }

        @Override
        public Flux<ByteBuffer> getBody() {
            return body;
        }

        @Override
        public Mono<byte[]> getBodyAsByteArray() {
            return FluxUtil.collectBytesInByteBufferStream(body);
        }

        @Override
        public Mono<String> getBodyAsString() {
            return getBodyAsString(StandardCharsets.UTF_8);
        }

        @Override
        public Mono<String> getBodyAsString(Charset charset) {
            return getBodyAsByteArray().map(bytes -> new String(bytes, charset));
        }

        @Override
        public void close() {
            if (!closed.compareAndSet(false, true)) {
                return;
            }

            try {
                responseStream.close();
            } catch (IOException ignored) {
                // The connection is disconnected below.
            } finally {
                connection.disconnect();
            }
        }
    }
}
