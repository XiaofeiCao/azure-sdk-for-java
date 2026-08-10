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
import com.azure.core.implementation.http.rest.sse.generated.StockUpdate;
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
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.SAME_THREAD)
public class ServerSentEventsEndToEndTests {
    private static final Duration TIMEOUT = Duration.ofSeconds(10);
    private static final String CONTENT_TYPE = "text/event-stream";
    private static final String JSON_CONTENT_TYPE = "application/json";
    private static final HttpHeaderName LAST_EVENT_ID = HttpHeaderName.fromString("Last-Event-Id");

    private TrackingUrlConnectionHttpClient httpClient;
    private LocalTestServer server;
    private ExecutorService executorService;
    private CountDownLatch headersSent;
    private CountDownLatch sendEvents;
    private CountDownLatch firstEventReceived;
    private CountDownLatch sendRemainingEvents;
    private String responseContentType;
    private String expectedAccept;
    private AtomicInteger requestCount;
    private AtomicReference<String> reconnectLastEventId;
    private AtomicBoolean resetLastEventIdOmitted;
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
        expectedAccept = CONTENT_TYPE;
        requestCount = new AtomicInteger();
        reconnectLastEventId = new AtomicReference<>();
        resetLastEventIdOmitted = new AtomicBoolean();
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
    public void syncNormalApiUsesFiniteJsonRepresentation() {
        expectedAccept = JSON_CONTENT_TYPE;
        EventsClient client
            = new EventsClientBuilder().endpoint(server.getHttpUri()).httpClient(httpClient).buildClient();

        Response<StockUpdate> response = client.getEventsWithResponse(new RequestOptions());

        assertEquals(200, response.getStatusCode());
        assertTrue(response.getHeaders().getValue(HttpHeaderName.CONTENT_TYPE).startsWith(JSON_CONTENT_TYPE));
        assertEquals("MSFT", response.getValue().getSymbol());
        assertEquals(420.5F, response.getValue().getPrice());
        assertEquals(1, requestCount.get());
        assertFalse(httpClient.isStreamingResponse());
    }

    @Test
    public void asyncNormalApiUsesFiniteJsonRepresentation() {
        expectedAccept = JSON_CONTENT_TYPE;
        EventsAsyncClient client
            = new EventsClientBuilder().endpoint(server.getHttpUri()).httpClient(httpClient).buildAsyncClient();

        StepVerifier.create(client.getEventsWithResponse(new RequestOptions())).assertNext(response -> {
            assertEquals(200, response.getStatusCode());
            assertTrue(response.getHeaders().getValue(HttpHeaderName.CONTENT_TYPE).startsWith(JSON_CONTENT_TYPE));
            assertEquals("MSFT", response.getValue().getSymbol());
            assertEquals(420.5F, response.getValue().getPrice());
        }).verifyComplete();

        assertEquals(1, requestCount.get());
        assertFalse(httpClient.isStreamingResponse());
    }

    @Test
    public void normalApisAllowNoContentFromSharedProxy() {
        scenario = StreamScenario.NORMAL_NO_CONTENT;
        expectedAccept = JSON_CONTENT_TYPE;
        EventsClient client
            = new EventsClientBuilder().endpoint(server.getHttpUri()).httpClient(httpClient).buildClient();
        EventsAsyncClient asyncClient
            = new EventsClientBuilder().endpoint(server.getHttpUri()).httpClient(httpClient).buildAsyncClient();

        Response<StockUpdate> response = client.getEventsWithResponse(new RequestOptions());

        assertEquals(204, response.getStatusCode());
        assertNull(response.getValue());
        StepVerifier.create(asyncClient.getEvents()).verifyComplete();
        assertEquals(2, requestCount.get());
        assertFalse(httpClient.isStreamingResponse());
    }

    @Test
    public void syncClientListensUntilInclusiveTerminalEvent() throws Exception {
        EventsClient client
            = new EventsClientBuilder().endpoint(server.getHttpUri()).httpClient(httpClient).buildClient();
        RecordingListener listener = new RecordingListener();

        CompletableFuture<Response<Void>> responseFuture = CompletableFuture
            .supplyAsync(() -> client.getEventsStreamWithResponse(listener, new RequestOptions()), executorService);

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
    public void syncStreamingApiRejectsNonEventStreamContentTypeBeforeReadingBody() {
        responseContentType = "application/octet-stream";
        EventsClient client
            = new EventsClientBuilder().endpoint(server.getHttpUri()).httpClient(httpClient).buildClient();
        RecordingListener listener = new RecordingListener();
        sendEvents.countDown();

        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> client.getEventsStreamWithResponse(listener, new RequestOptions()));

        assertTrue(exception.getMessage().contains("Content-Type 'text/event-stream'"));
        assertTrue(listener.events.isEmpty());
        assertEquals(0, listener.closeCount.get());
        assertEquals(1, httpClient.getClosedResponseCount());
        assertTrue(httpClient.isStreamingResponse());
    }

    @Test
    public void syncClientReconnectsWithMetadataOnlyState() throws Exception {
        scenario = StreamScenario.RECONNECT;
        EventsClient client
            = new EventsClientBuilder().endpoint(server.getHttpUri()).httpClient(httpClient).buildClient();
        RecordingListener listener = new RecordingListener();
        RequestOptions requestOptions = new RequestOptions().setHeader(LAST_EVENT_ID, "stale");

        CompletableFuture<Response<Void>> responseFuture = CompletableFuture
            .supplyAsync(() -> client.getEventsStreamWithResponse(listener, requestOptions), executorService);

        assertTrue(headersSent.await(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS));
        sendEvents.countDown();
        Response<Void> response = responseFuture.get(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);

        assertEquals(200, response.getStatusCode());
        assertEquals(3, listener.events.size());
        assertUserLogin(listener.events.get(0));
        assertStockUpdate(listener.events.get(1));
        assertTerminal(listener.events.get(2));
        assertEquals("", listener.events.get(2).getId());
        assertEquals(Duration.ofMillis(1), listener.events.get(2).getRetryAfter());
        assertEquals(3, requestCount.get());
        assertEquals("reconnect-id", reconnectLastEventId.get());
        assertTrue(resetLastEventIdOmitted.get());
        assertNull(listener.error.get());
        assertEquals(1, listener.closeCount.get());
        assertTrue(httpClient.awaitCancellation(TIMEOUT));
        assertTrue(httpClient.isCancelled());
    }

    @Test
    public void asyncClientStreamsUntilInclusiveTerminalEvent() {
        EventsAsyncClient client
            = new EventsClientBuilder().endpoint(server.getHttpUri()).httpClient(httpClient).buildAsyncClient();

        Flux<ServerSentEvent<ServiceStreamEvent>> events = getEventsStream(client);

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
    public void asyncClientReconnectsWithMetadataOnlyState() {
        scenario = StreamScenario.RECONNECT;
        EventsAsyncClient client
            = new EventsClientBuilder().endpoint(server.getHttpUri()).httpClient(httpClient).buildAsyncClient();
        RequestOptions requestOptions = new RequestOptions().setHeader(LAST_EVENT_ID, "stale");

        StepVerifier.create(getEventsStream(client, requestOptions))
            .assertNext(this::assertUserLogin)
            .assertNext(this::assertStockUpdate)
            .assertNext(event -> {
                assertTerminal(event);
                assertEquals("", event.getId());
                assertEquals(Duration.ofMillis(1), event.getRetryAfter());
            })
            .verifyComplete();

        assertEquals(3, requestCount.get());
        assertEquals("reconnect-id", reconnectLastEventId.get());
        assertTrue(resetLastEventIdOmitted.get());
        assertTrue(httpClient.awaitCancellation(TIMEOUT));
        assertTrue(httpClient.isCancelled());
    }

    @Test
    public void syncClientStopsReconnectingOnNoContent() throws Exception {
        scenario = StreamScenario.NO_CONTENT;
        EventsClient client
            = new EventsClientBuilder().endpoint(server.getHttpUri()).httpClient(httpClient).buildClient();
        RecordingListener listener = new RecordingListener();

        CompletableFuture<Response<Void>> responseFuture = CompletableFuture
            .supplyAsync(() -> client.getEventsStreamWithResponse(listener, new RequestOptions()), executorService);

        assertTrue(headersSent.await(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS));
        sendEvents.countDown();
        Response<Void> response = responseFuture.get(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);

        assertEquals(200, response.getStatusCode());
        assertEquals(1, listener.events.size());
        assertUserLogin(listener.events.get(0));
        assertNull(listener.error.get());
        assertEquals(1, listener.closeCount.get());
        assertEquals(2, requestCount.get());
        assertEquals(2, httpClient.getClosedResponseCount());
    }

    @Test
    public void asyncClientStopsReconnectingOnNoContent() {
        scenario = StreamScenario.NO_CONTENT;
        EventsAsyncClient client
            = new EventsClientBuilder().endpoint(server.getHttpUri()).httpClient(httpClient).buildAsyncClient();
        sendEvents.countDown();

        StepVerifier.create(getEventsStream(client)).assertNext(this::assertUserLogin).verifyComplete();

        assertEquals(2, requestCount.get());
        assertEquals(2, httpClient.getClosedResponseCount());
    }

    @Test
    public void asyncClientAcceptsMixedCaseEventStreamContentType() {
        responseContentType = "Text/Event-Stream";
        EventsAsyncClient client
            = new EventsClientBuilder().endpoint(server.getHttpUri()).httpClient(httpClient).buildAsyncClient();

        StepVerifier.create(client.getEventsStreamWithResponse(new RequestOptions()).flatMapMany(response -> {
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
    public void eventStreamResponsePreservesStreamingWhenAcceptIsReplaced() {
        scenario = StreamScenario.OPEN;
        expectedAccept = "*/*";
        EventsAsyncClient client
            = new EventsClientBuilder().endpoint(server.getHttpUri()).httpClient(httpClient).buildAsyncClient();
        RequestOptions options = new RequestOptions()
            .addRequestCallback(request -> request.setHeader(HttpHeaderName.ACCEPT, expectedAccept));

        StepVerifier.create(client.getEventsStreamWithResponse(options).flatMapMany(response -> {
            assertEquals(200, response.getStatusCode());
            sendEvents.countDown();
            return response.getValue();
        })).assertNext(this::assertUserLogin).thenCancel().verify(TIMEOUT);

        assertTrue(httpClient.awaitCancellation(TIMEOUT));
        assertTrue(httpClient.isCancelled());
        assertFalse(httpClient.isStreamingResponse());
    }

    @Test
    public void asyncStreamingApiRejectsNonEventStreamContentTypeBeforeReadingBody() {
        responseContentType = "application/octet-stream";
        EventsAsyncClient client
            = new EventsClientBuilder().endpoint(server.getHttpUri()).httpClient(httpClient).buildAsyncClient();
        sendEvents.countDown();

        StepVerifier.create(client.getEventsStream())
            .expectErrorMatches(error -> error instanceof IllegalStateException
                && error.getMessage().contains("Content-Type 'text/event-stream'"))
            .verify(TIMEOUT);

        assertEquals(1, httpClient.getClosedResponseCount());
        assertTrue(httpClient.isStreamingResponse());
    }

    @Test
    public void asyncStreamingApiRejectsMissingContentTypeBeforeReadingBody() {
        responseContentType = null;
        EventsAsyncClient client
            = new EventsClientBuilder().endpoint(server.getHttpUri()).httpClient(httpClient).buildAsyncClient();
        sendEvents.countDown();

        StepVerifier.create(client.getEventsStream())
            .expectErrorMatches(error -> error instanceof IllegalStateException
                && error.getMessage().contains("Content-Type 'text/event-stream'"))
            .verify(TIMEOUT);

        assertEquals(1, httpClient.getClosedResponseCount());
    }

    @Test
    public void asyncStreamingApiRejectsNonEventStreamReconnectResponse() {
        scenario = StreamScenario.INVALID_RECONNECT_CONTENT_TYPE;
        EventsAsyncClient client
            = new EventsClientBuilder().endpoint(server.getHttpUri()).httpClient(httpClient).buildAsyncClient();
        sendEvents.countDown();

        StepVerifier.create(client.getEventsStream())
            .assertNext(this::assertUserLogin)
            .expectErrorMatches(error -> error instanceof IllegalStateException
                && error.getMessage().contains("Content-Type 'text/event-stream'"))
            .verify(TIMEOUT);

        assertEquals(2, requestCount.get());
        assertEquals(2, httpClient.getClosedResponseCount());
    }

    @Test
    public void syncListenerReceivesErrorThenCallFailsAndCloses() throws Exception {
        scenario = StreamScenario.MALFORMED;
        EventsClient client
            = new EventsClientBuilder().endpoint(server.getHttpUri()).httpClient(httpClient).buildClient();
        RecordingListener listener = new RecordingListener();

        CompletableFuture<Response<Void>> responseFuture = CompletableFuture
            .supplyAsync(() -> client.getEventsStreamWithResponse(listener, new RequestOptions()), executorService);

        assertTrue(headersSent.await(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS));
        sendEvents.countDown();

        ExecutionException exception = assertThrows(ExecutionException.class,
            () -> responseFuture.get(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS));
        assertTrue(exception.getCause() instanceof UncheckedIOException);
        assertTrue(listener.events.isEmpty());
        assertSame(exception.getCause(), listener.error.get());
        assertEquals(1, listener.closeCount.get());
        assertTrue(httpClient.awaitCancellation(TIMEOUT));
    }

    @Test
    public void asyncErrorIsNotReplacedByCloseCompletion() {
        scenario = StreamScenario.MALFORMED;
        EventsAsyncClient client
            = new EventsClientBuilder().endpoint(server.getHttpUri()).httpClient(httpClient).buildAsyncClient();

        StepVerifier.create(getEventsStream(client)).expectError().verify(TIMEOUT);

        assertTrue(httpClient.awaitCancellation(TIMEOUT));
        assertTrue(httpClient.isCancelled());
    }

    @Test
    public void asyncCancellationClosesHttpConnection() {
        scenario = StreamScenario.OPEN;
        EventsAsyncClient client
            = new EventsClientBuilder().endpoint(server.getHttpUri()).httpClient(httpClient).buildAsyncClient();

        StepVerifier.create(getEventsStream(client)).assertNext(this::assertUserLogin).thenCancel().verify(TIMEOUT);

        assertTrue(httpClient.awaitCancellation(TIMEOUT));
        assertTrue(httpClient.isCancelled());
    }

    private Flux<ServerSentEvent<ServiceStreamEvent>> getEventsStream(EventsAsyncClient client) {
        return getEventsStream(client, new RequestOptions());
    }

    private Flux<ServerSentEvent<ServiceStreamEvent>> getEventsStream(EventsAsyncClient client,
        RequestOptions requestOptions) {
        return client.getEventsStreamWithResponse(requestOptions).flatMapMany(response -> {
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
        if (!expectedAccept.equals(request.getHeader("Accept"))) {
            throw new ServletException("Unexpected Accept header: " + request.getHeader("Accept"));
        }
        int currentRequest = requestCount.incrementAndGet();
        if (JSON_CONTENT_TYPE.equals(expectedAccept)) {
            if (scenario == StreamScenario.NORMAL_NO_CONTENT) {
                response.setStatus(204);
                response.flushBuffer();
                return;
            }
            byte[] body = "{\"symbol\":\"MSFT\",\"price\":420.5}".getBytes(StandardCharsets.UTF_8);
            response.setStatus(200);
            response.setContentType(JSON_CONTENT_TYPE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentLength(body.length);
            response.getOutputStream().write(body);
            return;
        }

        if (scenario == StreamScenario.NO_CONTENT && currentRequest == 2) {
            response.setStatus(204);
            response.flushBuffer();
            return;
        }

        if (scenario == StreamScenario.RECONNECT) {
            String lastEventId = request.getHeader("Last-Event-Id");
            if (currentRequest == 1 && !"stale".equals(lastEventId)) {
                throw new ServletException("The initial request didn't preserve the caller's Last-Event-Id.");
            }
            if (currentRequest == 2) {
                reconnectLastEventId.set(lastEventId);
                if (!"reconnect-id".equals(lastEventId)) {
                    throw new ServletException("The reconnect request didn't use the retained Last-Event-Id.");
                }
            }
            if (currentRequest == 3) {
                resetLastEventIdOmitted.set(lastEventId == null);
                if (lastEventId != null) {
                    throw new ServletException("The reset Last-Event-Id must be omitted.");
                }
            }
        }

        response.setStatus(200);
        String currentResponseContentType
            = scenario == StreamScenario.INVALID_RECONNECT_CONTENT_TYPE && currentRequest == 2
                ? JSON_CONTENT_TYPE
                : responseContentType;
        if (currentResponseContentType != null) {
            response.setContentType(currentResponseContentType);
        }
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

            case RECONNECT:
                if (currentRequest == 1) {
                    write(output,
                        ": login event\nretry: 1\nid: login-1\nevent: userLogin\n"
                            + "data: {\"userId\":\"user-1\",\"loginTime\":\"2026-08-05T21:00:00Z\"}\n\n"
                            + "id: reconnect-id\nretry: invalid\n\n");
                    return;
                }
                if (currentRequest == 2) {
                    write(output, "event: stockUpdate\ndata: {\"symbol\":\"MSFT\",\"price\":123.45}\n\nid:\n\n");
                    return;
                }
                if (currentRequest == 3) {
                    write(output, "event: terminal\ndata: [DONE]\n\n"
                        + "event: systemAlert\ndata: {\"level\":\"warning\",\"message\":\"must not be emitted\"}\n\n");
                    break;
                }
                throw new ServletException("Unexpected SSE reconnect attempt: " + currentRequest);

            case NO_CONTENT:
                write(output, ": login event\nretry: 1\nid: login-1\nevent: userLogin\n"
                    + "data: {\"userId\":\"user-1\",\"loginTime\":\"2026-08-05T21:00:00Z\"}\n\n");
                return;

            case INVALID_RECONNECT_CONTENT_TYPE:
                if (currentRequest == 1) {
                    write(output, ": login event\nretry: 1\nid: login-1\nevent: userLogin\n"
                        + "data: {\"userId\":\"user-1\",\"loginTime\":\"2026-08-05T21:00:00Z\"}\n\n");
                    return;
                }
                write(output, "{\"symbol\":\"MSFT\",\"price\":420.5}");
                return;

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
        TERMINAL, MALFORMED, OPEN, INTERLEAVED, RECONNECT, NO_CONTENT, NORMAL_NO_CONTENT, INVALID_RECONNECT_CONTENT_TYPE
    }

    private static final class RecordingListener implements ServerSentEventListener<ServiceStreamEvent> {
        private final List<ServerSentEvent<ServiceStreamEvent>> events = new ArrayList<>();
        private final AtomicReference<Throwable> error = new AtomicReference<>();
        private final AtomicInteger closeCount = new AtomicInteger();
        private final CountDownLatch firstEventReceived;

        private RecordingListener() {
            this(null);
        }

        private RecordingListener(CountDownLatch firstEventReceived) {
            this.firstEventReceived = firstEventReceived;
        }

        @Override
        public void onEvent(ServerSentEvent<ServiceStreamEvent> event) {
            events.add(event);
            if (firstEventReceived != null) {
                firstEventReceived.countDown();
            }
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
