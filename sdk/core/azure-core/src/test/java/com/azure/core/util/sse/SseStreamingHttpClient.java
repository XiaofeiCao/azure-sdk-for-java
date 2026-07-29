// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.util.sse;

import com.azure.core.http.HttpClient;
import com.azure.core.http.HttpHeaderName;
import com.azure.core.http.HttpHeaders;
import com.azure.core.http.HttpRequest;
import com.azure.core.http.HttpResponse;
import com.azure.core.util.BinaryData;
import com.azure.core.util.Context;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Minimal blocking {@link HttpClient} backed by {@link HttpURLConnection} that streams the response body in small
 * chunks. It exists because {@code azure-core} unit tests have no concrete {@link HttpClient} provider on the
 * classpath, yet we need a <em>real</em> HTTP round-trip against {@link SseMockServer} to test the SSE runtime.
 * <p>
 * The response body is emitted in deliberately small {@link ByteBuffer} slices so that individual SSE events are
 * split across buffer boundaries, exercising the parser's incremental framing.
 */
final class SseStreamingHttpClient implements HttpClient {
    private static final int CHUNK_SIZE = 24;

    @Override
    public Mono<HttpResponse> send(HttpRequest request) {
        return Mono.fromCallable(() -> connect(request)).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public HttpResponse sendSync(HttpRequest request, Context context) {
        try {
            return connect(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpResponse connect(HttpRequest request) throws IOException {
        URL url = request.getUrl();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(request.getHttpMethod().name());
        for (com.azure.core.http.HttpHeader header : request.getHeaders()) {
            connection.setRequestProperty(header.getName(), header.getValue());
        }

        BinaryData requestBody = request.getBodyAsBinaryData();
        if (requestBody != null) {
            connection.setDoOutput(true);
            byte[] bytes = requestBody.toBytes();
            connection.setFixedLengthStreamingMode(bytes.length);
            try (OutputStream os = connection.getOutputStream()) {
                os.write(bytes);
            }
        }

        connection.connect();
        int statusCode = connection.getResponseCode();
        HttpHeaders headers = new HttpHeaders();
        for (Map.Entry<String, List<String>> entry : connection.getHeaderFields().entrySet()) {
            if (entry.getKey() != null) {
                headers.set(HttpHeaderName.fromString(entry.getKey()), entry.getValue());
            }
        }

        InputStream stream = statusCode >= 400 ? connection.getErrorStream() : connection.getInputStream();
        return new StreamingHttpResponse(request, statusCode, headers, stream);
    }

    private static final class StreamingHttpResponse extends HttpResponse {
        private final int statusCode;
        private final HttpHeaders headers;
        private final InputStream body;

        StreamingHttpResponse(HttpRequest request, int statusCode, HttpHeaders headers, InputStream body) {
            super(request);
            this.statusCode = statusCode;
            this.headers = headers;
            this.body = body;
        }

        @Override
        public int getStatusCode() {
            return statusCode;
        }

        @Override
        @SuppressWarnings("deprecation")
        public String getHeaderValue(String name) {
            return headers.getValue(HttpHeaderName.fromString(name));
        }

        @Override
        public HttpHeaders getHeaders() {
            return headers;
        }

        @Override
        public Flux<ByteBuffer> getBody() {
            if (body == null) {
                return Flux.empty();
            }
            return Flux.<ByteBuffer, InputStream>generate(() -> body, (stream, sink) -> {
                try {
                    byte[] buffer = new byte[CHUNK_SIZE];
                    int read = stream.read(buffer);
                    if (read == -1) {
                        sink.complete();
                    } else {
                        sink.next(ByteBuffer.wrap(buffer, 0, read));
                    }
                } catch (IOException e) {
                    sink.error(e);
                }
                return stream;
            }, stream -> {
                try {
                    stream.close();
                } catch (IOException ignored) {
                    // ignore close failures in tests
                }
            });
        }

        @Override
        public Mono<byte[]> getBodyAsByteArray() {
            return Mono.fromCallable(this::readAll);
        }

        @Override
        public Mono<String> getBodyAsString() {
            return getBodyAsString(StandardCharsets.UTF_8);
        }

        @Override
        public Mono<String> getBodyAsString(Charset charset) {
            return getBodyAsByteArray().map(bytes -> new String(bytes, charset));
        }

        private byte[] readAll() throws IOException {
            if (body == null) {
                return new byte[0];
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[CHUNK_SIZE];
            int read;
            try (InputStream stream = body) {
                while ((read = stream.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
            }
            return out.toByteArray();
        }

        @Override
        public void close() {
            try {
                if (body != null) {
                    body.close();
                }
            } catch (IOException ignored) {
                // ignore
            }
        }
    }
}
