// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.implementation.http.rest.sse;

import com.azure.core.http.HttpHeaderName;
import com.azure.core.http.ServerSentEvent;
import com.azure.core.http.ServerSentEventListener;
import com.azure.core.http.rest.RequestOptions;
import com.azure.core.http.rest.Response;
import com.azure.core.implementation.http.rest.sse.generated.EventsAsyncClient;
import com.azure.core.implementation.http.rest.sse.generated.EventsClient;
import com.azure.core.implementation.http.rest.sse.generated.EventsClientBuilder;
import com.azure.core.implementation.http.rest.sse.generated.ServiceStreamEvent;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.SAME_THREAD)
public class ServerSentEventsEndToEndTests {
    private static final Duration TIMEOUT = Duration.ofSeconds(10);
    private static final String CONTENT_TYPE = "text/event-stream";

    private TrackingUrlConnectionHttpClient httpClient;
    private LocalTestServer server;
    private ExecutorService executorService;
    private CountDownLatch headersSent;
    private CountDownLatch sendEvents;
    private CountDownLatch firstEventReceived;
    private CountDownLatch sendRemainingEvents;
    private String responseContentType;
    private volatile StreamScenario scenario;

    @BeforeEach
    public void beforeEach() {
        httpClient = new TrackingUrlConnectionHttpClient();
        executorService = Executors.newSingleThreadExecutor();
        headersSent = new CountDownLatch(1);
        sendEvents = new CountDownLatch(1);
        firstEventReceived = new CountDownLatch(1);
        sendRemainingEvents = new CountDownLatch(1);
        responseContentType = CONTENT_TYPE;
        scenario = StreamScenario.TERMINAL;
        server = new LocalTestServer(this::handleRequest);
        server.start();
    }

    @AfterEach
    public void afterEach() {
        sendEvents.countDown();
        sendRemainingEvents.countDown();
        if (executorService != null) {
            executorService.shutdownNow();
        }
        if (server != null) {
            server.stop();
        }
    }

    @Test
    public void syncClientListensUntilInclusiveTerminalEvent() throws Exception {
        EventsClient client
            = new EventsClientBuilder().endpoint(server.getHttpUri()).httpClient(httpClient).buildClient();
        RecordingListener listener = new RecordingListener();

        CompletableFuture<Response<Void>> responseFuture = CompletableFuture
            .supplyAsync(() -> client.getEventsWithResponse(listener, new RequestOptions()), executorService);

        assertTrue(headersSent.await(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS));
        assertFalse(responseFuture.isDone());
        sendEvents.countDown();

        Response<Void> response = responseFuture.get(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
        assertEquals(200, response.getStatusCode());
        assertEventStreamContentType(response);
        assertNull(response.getValue());
        assertNull(listener.error.get());
        assertEquals(1, listener.closeCount.get());
        assertEvents(listener.events);
        assertTrue(httpClient.awaitCancellation(TIMEOUT));
        assertTrue(httpClient.isCancelled());
        assertTrue(httpClient.isStreamingResponse());
    }

    @Test
    public void syncListenerProcessesEventBeforeResponseBodyCompletes() throws Exception {
        scenario = StreamScenario.INTERLEAVED;
        EventsClient client
            = new EventsClientBuilder().endpoint(server.getHttpUri()).httpClient(httpClient).buildClient();
        RecordingListener listener = new RecordingListener(firstEventReceived);

        CompletableFuture<Response<Void>> responseFuture = CompletableFuture
            .supplyAsync(() -> client.getEventsWithResponse(listener, new RequestOptions()), executorService);

        assertTrue(headersSent.await(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS));
        sendEvents.countDown();
        assertTrue(firstEventReceived.await(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS));
        assertFalse(responseFuture.isDone());

        sendRemainingEvents.countDown();
        Response<Void> response = responseFuture.get(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);

        assertEquals(200, response.getStatusCode());
        assertEquals(2, listener.events.size());
        assertUserLogin(listener.events.get(0));
        assertTerminal(listener.events.get(1));
        assertNull(listener.error.get());
        assertEquals(1, listener.closeCount.get());
        assertTrue(httpClient.awaitCancellation(TIMEOUT));
        assertTrue(httpClient.isStreamingResponse());
    }

    @Test
    public void syncListenerCanStopBeforeTerminalEvent() throws Exception {
        scenario = StreamScenario.OPEN;
        EventsClient client
            = new EventsClientBuilder().endpoint(server.getHttpUri()).httpClient(httpClient).buildClient();
        RecordingListener listener = new RecordingListener(null, 1);

        CompletableFuture<Response<Void>> responseFuture = CompletableFuture
            .supplyAsync(() -> client.getEventsWithResponse(listener, new RequestOptions()), executorService);

        assertTrue(headersSent.await(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS));
        sendEvents.countDown();
        Response<Void> response = responseFuture.get(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);

        assertEquals(200, response.getStatusCode());
        assertEquals(1, listener.events.size());
        assertUserLogin(listener.events.get(0));
        assertNull(listener.error.get());
        assertEquals(1, listener.closeCount.get());
        assertTrue(httpClient.awaitCancellation(TIMEOUT));
        assertTrue(httpClient.isCancelled());
    }

    @Test
    public void asyncClientStreamsUntilInclusiveTerminalEvent() {
        EventsAsyncClient client
            = new EventsClientBuilder().endpoint(server.getHttpUri()).httpClient(httpClient).buildAsyncClient();

        Flux<ServerSentEvent<ServiceStreamEvent>> events = getEvents(client);

        StepVerifier.create(events)
            .assertNext(this::assertUserLogin)
            .assertNext(this::assertStockUpdate)
            .assertNext(this::assertTerminal)
            .expectComplete()
            .verify(TIMEOUT);

        assertTrue(httpClient.awaitCancellation(TIMEOUT));
        assertTrue(httpClient.isCancelled());
        assertTrue(httpClient.isStreamingResponse());
    }

    @Test
    public void asyncClientAcceptsMixedCaseEventStreamContentType() {
        responseContentType = "Text/Event-Stream";
        EventsAsyncClient client
            = new EventsClientBuilder().endpoint(server.getHttpUri()).httpClient(httpClient).buildAsyncClient();

        StepVerifier.create(client.getEventsWithResponse(new RequestOptions()).flatMapMany(response -> {
            assertTrue(response.getHeaders().getValue(HttpHeaderName.CONTENT_TYPE).startsWith(responseContentType));
            sendEvents.countDown();
            return response.getValue();
        }))
            .assertNext(this::assertUserLogin)
            .assertNext(this::assertStockUpdate)
            .assertNext(this::assertTerminal)
            .verifyComplete();

        assertTrue(httpClient.awaitCancellation(TIMEOUT));
        assertTrue(httpClient.isCancelled());
    }

    @Test
    public void syncListenerReceivesErrorThenClose() throws Exception {
        scenario = StreamScenario.MALFORMED;
        EventsClient client
            = new EventsClientBuilder().endpoint(server.getHttpUri()).httpClient(httpClient).buildClient();
        RecordingListener listener = new RecordingListener();

        CompletableFuture<Response<Void>> responseFuture = CompletableFuture
            .supplyAsync(() -> client.getEventsWithResponse(listener, new RequestOptions()), executorService);

        assertTrue(headersSent.await(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS));
        sendEvents.countDown();

        Response<Void> response = responseFuture.get(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
        assertEquals(200, response.getStatusCode());
        assertTrue(listener.events.isEmpty());
        assertNotNull(listener.error.get());
        assertEquals(1, listener.closeCount.get());
        assertTrue(httpClient.awaitCancellation(TIMEOUT));
    }

    @Test
    public void asyncErrorIsNotReplacedByCloseCompletion() {
        scenario = StreamScenario.MALFORMED;
        EventsAsyncClient client
            = new EventsClientBuilder().endpoint(server.getHttpUri()).httpClient(httpClient).buildAsyncClient();

        StepVerifier.create(getEvents(client)).expectError().verify(TIMEOUT);

        assertTrue(httpClient.awaitCancellation(TIMEOUT));
        assertTrue(httpClient.isCancelled());
    }

    @Test
    public void asyncCancellationClosesHttpConnection() {
        scenario = StreamScenario.OPEN;
        EventsAsyncClient client
            = new EventsClientBuilder().endpoint(server.getHttpUri()).httpClient(httpClient).buildAsyncClient();

        StepVerifier.create(getEvents(client)).assertNext(this::assertUserLogin).thenCancel().verify(TIMEOUT);

        assertTrue(httpClient.awaitCancellation(TIMEOUT));
        assertTrue(httpClient.isCancelled());
    }

    private Flux<ServerSentEvent<ServiceStreamEvent>> getEvents(EventsAsyncClient client) {
        return client.getEventsWithResponse(new RequestOptions()).flatMapMany(response -> {
            assertEquals(200, response.getStatusCode());
            assertEventStreamContentType(response);
            sendEvents.countDown();
            return response.getValue();
        });
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
        response.setContentType(responseContentType);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.flushBuffer();
        headersSent.countDown();

        try {
            if (!sendEvents.await(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS)) {
                throw new IOException("The test didn't allow the server-sent events to be written.");
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IOException(exception);
        }

        OutputStream output = response.getOutputStream();
        switch (scenario) {
            case TERMINAL:
                write(output, "event: futureEvent\ndata: {\"ignored\":true}\n\n");
                write(output, ": login event\nid: login-1\nevent: userLogin\n"
                    + "data: {\"userId\":\"user-1\",\"loginTime\":\"2026-08-05T21:00:00Z\"}\n\n");
                write(output, "event: stockUpdate\ndata: {\"symbol\":\"MSFT\",\"price\":123.45}\n\n");
                write(output, "event: terminal\ndata: [DONE]\n\n"
                    + "event: systemAlert\ndata: {\"level\":\"warning\",\"message\":\"must not be emitted\"}\n\n");
                break;

            case MALFORMED:
                write(output, "event: userLogin\ndata: not-json\n\n");
                break;

            case OPEN:
                write(output, ": login event\nid: login-1\nevent: userLogin\n"
                    + "data: {\"userId\":\"user-1\",\"loginTime\":\"2026-08-05T21:00:00Z\"}\n\n");
                break;

            case INTERLEAVED:
                write(output, ": login event\nid: login-1\nevent: userLogin\n"
                    + "data: {\"userId\":\"user-1\",\"loginTime\":\"2026-08-05T21:00:00Z\"}\n\n");
                await(firstEventReceived, "The sync listener didn't receive the first event.");
                await(sendRemainingEvents, "The test didn't allow the remaining events to be written.");
                write(output, "event: terminal\ndata: [DONE]\n\n");
                break;

            default:
                throw new IllegalStateException("Unknown stream scenario: " + scenario);
        }

        if (!httpClient.awaitCancellation(TIMEOUT)) {
            throw new IOException("The client didn't cancel the SSE response.");
        }
    }

    private static void write(OutputStream output, String value) throws IOException {
        output.write(value.getBytes(StandardCharsets.UTF_8));
        output.flush();
    }

    private static void await(CountDownLatch latch, String timeoutMessage) throws IOException {
        try {
            if (!latch.await(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS)) {
                throw new IOException(timeoutMessage);
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IOException(exception);
        }
    }

    private static void assertEventStreamContentType(Response<?> response) {
        assertTrue(response.getHeaders()
            .getValue(HttpHeaderName.CONTENT_TYPE)
            .toLowerCase(Locale.ROOT)
            .startsWith(CONTENT_TYPE));
    }

    private void assertEvents(List<ServerSentEvent<ServiceStreamEvent>> events) {
        assertEquals(3, events.size());
        assertUserLogin(events.get(0));
        assertStockUpdate(events.get(1));
        assertTerminal(events.get(2));
    }

    private void assertUserLogin(ServerSentEvent<ServiceStreamEvent> event) {
        assertEquals("login-1", event.getId());
        assertEquals("userLogin", event.getEvent());
        assertEquals("login event", event.getComment());
        assertTrue(event.getData().isUserLogin());
        assertEquals("user-1", event.getData().asUserLogin().getUserId());
        assertEquals("2026-08-05T21:00:00Z", event.getData().asUserLogin().getLoginTime());
    }

    private void assertStockUpdate(ServerSentEvent<ServiceStreamEvent> event) {
        assertEquals("stockUpdate", event.getEvent());
        assertTrue(event.getData().isStockUpdate());
        assertEquals("MSFT", event.getData().asStockUpdate().getSymbol());
        assertEquals(123.45F, event.getData().asStockUpdate().getPrice());
    }

    private void assertTerminal(ServerSentEvent<ServiceStreamEvent> event) {
        assertEquals("terminal", event.getEvent());
        assertTrue(event.getData().isTerminal());
    }

    private enum StreamScenario {
        TERMINAL, MALFORMED, OPEN, INTERLEAVED
    }

    private static final class RecordingListener implements ServerSentEventListener<ServiceStreamEvent> {
        private final List<ServerSentEvent<ServiceStreamEvent>> events = new ArrayList<>();
        private final AtomicReference<Throwable> error = new AtomicReference<>();
        private final AtomicInteger closeCount = new AtomicInteger();
        private final CountDownLatch firstEventReceived;
        private final int maximumEvents;

        private RecordingListener() {
            this(null, Integer.MAX_VALUE);
        }

        private RecordingListener(CountDownLatch firstEventReceived) {
            this(firstEventReceived, Integer.MAX_VALUE);
        }

        private RecordingListener(CountDownLatch firstEventReceived, int maximumEvents) {
            this.firstEventReceived = firstEventReceived;
            this.maximumEvents = maximumEvents;
        }

        @Override
        public boolean onEvent(ServerSentEvent<ServiceStreamEvent> event) {
            events.add(event);
            if (firstEventReceived != null) {
                firstEventReceived.countDown();
            }
            return events.size() < maximumEvents;
        }

        @Override
        public void onError(Throwable error) {
            this.error.set(error);
        }

        @Override
        public void onClose() {
            closeCount.incrementAndGet();
        }
    }
}
