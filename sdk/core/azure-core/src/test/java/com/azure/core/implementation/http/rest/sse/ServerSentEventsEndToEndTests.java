// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.implementation.http.rest.sse;

import com.azure.core.http.HttpHeaderName;
import com.azure.core.http.rest.RequestOptions;
import com.azure.core.http.rest.Response;
import com.azure.core.implementation.http.rest.sse.generated.EventsAsyncClient;
import com.azure.core.implementation.http.rest.sse.generated.EventsClient;
import com.azure.core.implementation.http.rest.sse.generated.EventsClientBuilder;
import com.azure.core.implementation.http.rest.sse.generated.ServiceStreamEvent;
import com.azure.core.util.IterableStream;
import com.azure.core.validation.http.LocalTestServer;
import org.eclipse.jetty.server.Request;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.SAME_THREAD)
public class ServerSentEventsEndToEndTests {
    private static final Duration TIMEOUT = Duration.ofSeconds(10);
    private static final String CONTENT_TYPE = "text/event-stream";

    private TrackingUrlConnectionHttpClient httpClient;
    private LocalTestServer server;
    private CountDownLatch sendEvents;

    @BeforeEach
    public void beforeEach() {
        httpClient = new TrackingUrlConnectionHttpClient();
        sendEvents = new CountDownLatch(1);
        server = new LocalTestServer(this::handleRequest);
        server.start();
    }

    @AfterEach
    public void afterEach() {
        if (server != null) {
            server.stop();
        }
    }

    @Test
    public void syncClientStreamsUntilInclusiveTerminalEvent() {
        EventsClient client
            = new EventsClientBuilder().endpoint(server.getHttpUri()).httpClient(httpClient).buildClient();

        Response<IterableStream<ServiceStreamEvent>> response = client.getEventsWithResponse(new RequestOptions());
        assertEquals(200, response.getStatusCode());
        assertTrue(response.getHeaders().getValue(HttpHeaderName.CONTENT_TYPE).startsWith(CONTENT_TYPE));
        sendEvents.countDown();

        List<ServiceStreamEvent> events;
        try (Stream<ServiceStreamEvent> stream = response.getValue().stream()) {
            events = stream.collect(Collectors.toList());
        }

        assertEvents(events);
        assertTrue(httpClient.awaitCancellation(TIMEOUT));
        assertTrue(httpClient.isCancelled());
    }

    @Test
    public void asyncClientStreamsUntilInclusiveTerminalEvent() {
        EventsAsyncClient client
            = new EventsClientBuilder().endpoint(server.getHttpUri()).httpClient(httpClient).buildAsyncClient();

        Flux<ServiceStreamEvent> events = client.getEventsWithResponse(new RequestOptions()).flatMapMany(response -> {
            assertEquals(200, response.getStatusCode());
            assertTrue(response.getHeaders().getValue(HttpHeaderName.CONTENT_TYPE).startsWith(CONTENT_TYPE));
            sendEvents.countDown();
            return response.getValue();
        });

        StepVerifier.create(events)
            .assertNext(this::assertUserLogin)
            .assertNext(this::assertStockUpdate)
            .expectNextMatches(ServiceStreamEvent::isTerminal)
            .expectComplete()
            .verify(TIMEOUT);

        assertTrue(httpClient.awaitCancellation(TIMEOUT));
        assertTrue(httpClient.isCancelled());
    }

    private void handleRequest(Request request, org.eclipse.jetty.server.Response response, byte[] ignored)
        throws IOException, ServletException {
        if (!"GET".equals(request.getMethod()) || !"/events".equals(request.getServletPath())) {
            throw new ServletException("Unexpected request: " + request.getMethod() + " " + request.getServletPath());
        }
        if (!CONTENT_TYPE.equals(request.getHeader("Accept"))) {
            throw new ServletException("Unexpected Accept header: " + request.getHeader("Accept"));
        }

        response.setStatus(200);
        response.setContentType(CONTENT_TYPE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.flushBuffer();

        try {
            if (!sendEvents.await(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS)) {
                throw new IOException("The generated client didn't return after the SSE response headers.");
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IOException(exception);
        }

        OutputStream output = response.getOutputStream();
        write(output, "event: futureEvent\ndata: {\"ignored\":true}\n\n");
        write(output, "event: userLogin\ndata: {\"userId\":\"user-1\",\"loginTime\":\"2026-08-05T21:00:00Z\"}\n\n");
        write(output, "event: stockUpdate\ndata: {\"symbol\":\"MSFT\",\"price\":123.45}\n\n");
        write(output, "event: terminal\ndata: [DONE]\n\n"
            + "event: systemAlert\ndata: {\"level\":\"warning\",\"message\":\"must not be emitted\"}\n\n");
        output.flush();

        if (!httpClient.awaitCancellation(TIMEOUT)) {
            throw new IOException("The client didn't cancel the SSE response after the terminal event.");
        }
    }

    private static void write(OutputStream output, String value) throws IOException {
        output.write(value.getBytes(StandardCharsets.UTF_8));
        output.flush();
    }

    private void assertEvents(List<ServiceStreamEvent> events) {
        assertEquals(3, events.size());
        assertUserLogin(events.get(0));
        assertStockUpdate(events.get(1));
        assertTrue(events.get(2).isTerminal());
    }

    private void assertUserLogin(ServiceStreamEvent event) {
        assertTrue(event.isUserLogin());
        assertEquals("user-1", event.asUserLogin().getUserId());
        assertEquals("2026-08-05T21:00:00Z", event.asUserLogin().getLoginTime());
    }

    private void assertStockUpdate(ServiceStreamEvent event) {
        assertTrue(event.isStockUpdate());
        assertEquals("MSFT", event.asStockUpdate().getSymbol());
        assertEquals(123.45F, event.asStockUpdate().getPrice());
    }
}
