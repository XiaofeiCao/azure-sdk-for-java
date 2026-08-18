// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.http.okhttp.implementation;

import com.azure.core.http.HttpMethod;
import com.azure.core.http.HttpRequest;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import okio.Okio;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OkHttpAsyncResponseTests {
    @Test
    public void binaryDataCancellationClosesResponseBody() throws Exception {
        AtomicBoolean closed = new AtomicBoolean();
        InputStream bodyStream = new InfiniteInputStream(closed);
        ResponseBody responseBody = new ResponseBody() {
            private final BufferedSource source = Okio.buffer(Okio.source(bodyStream));

            @Override
            public MediaType contentType() {
                return null;
            }

            @Override
            public long contentLength() {
                return -1;
            }

            @Override
            public BufferedSource source() {
                return source;
            }
        };
        okhttp3.Request okhttpRequest = new okhttp3.Request.Builder().url("https://localhost/stream").build();
        Response response = new Response.Builder().request(okhttpRequest)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .body(responseBody)
            .build();
        HttpRequest request = new HttpRequest(HttpMethod.GET, new URI("https://localhost/stream").toURL());
        OkHttpAsyncResponse azureResponse = new OkHttpAsyncResponse(response, request, true);

        assertSame(azureResponse.getBodyAsBinaryData(), azureResponse.getBodyAsBinaryData());
        StepVerifier.create(azureResponse.getBodyAsBinaryData().toFluxByteBuffer().take(1))
            .expectNextCount(1)
            .verifyComplete();

        assertTrue(closed.get());
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
