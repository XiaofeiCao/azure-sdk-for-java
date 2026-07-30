// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.http;

import com.azure.core.util.Context;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServerSentEventProcessorTests {
    @Test
    public void synchronousReconnectIsIterativeAndPreservesContext() {
        int connectionCount = 500;
        AtomicInteger requests = new AtomicInteger();
        AtomicInteger events = new AtomicInteger();
        Context context = new Context("sse-context", "preserved");

        HttpClient httpClient = new HttpClient() {
            @Override
            public Mono<HttpResponse> send(HttpRequest request) {
                return Mono.just(createResponse(request, requests.incrementAndGet(), connectionCount));
            }

            @Override
            public HttpResponse sendSync(HttpRequest request, Context requestContext) {
                assertEquals("preserved", requestContext.getData("sse-context").orElse(null));
                return createResponse(request, requests.incrementAndGet(), connectionCount);
            }
        };

        HttpPipeline pipeline = new HttpPipelineBuilder().httpClient(httpClient).build();
        HttpRequest request = new HttpRequest(HttpMethod.GET, "https://example.com/events")
            .setServerSentEventListener(event -> events.incrementAndGet());

        try (HttpResponse response = pipeline.sendSync(request, context)) {
            assertEquals(200, response.getStatusCode());
        }

        assertEquals(connectionCount, requests.get());
        assertEquals(connectionCount, events.get());
    }

    private static HttpResponse createResponse(HttpRequest request, int requestNumber, int connectionCount) {
        String retry = requestNumber < connectionCount ? "retry: 0\n" : "";
        byte[] body = ("data: event " + requestNumber + "\nid: " + requestNumber + "\n" + retry + "\n")
            .getBytes(StandardCharsets.UTF_8);
        HttpHeaders headers = new HttpHeaders().set(HttpHeaderName.CONTENT_TYPE, "text/event-stream");
        return new MockFluxHttpResponse(request, 200, headers, Flux.just(ByteBuffer.wrap(body)));
    }
}
