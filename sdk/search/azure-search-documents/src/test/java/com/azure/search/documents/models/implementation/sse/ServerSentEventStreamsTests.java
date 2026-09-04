// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.search.documents.models.implementation.sse;

import com.azure.core.http.HttpHeaderName;
import com.azure.core.http.HttpHeaders;
import com.azure.core.http.HttpMethod;
import com.azure.core.http.HttpRequest;
import com.azure.core.http.ServerSentEvent;
import com.azure.core.http.rest.Response;
import com.azure.core.http.rest.SimpleResponse;
import com.azure.core.util.BinaryData;
import com.azure.core.util.CloseableIterableStream;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServerSentEventStreamsTests {
    @Test
    public void iterableUsesCoreEventMetadataAndStopsAtTerminalEvent() {
        String body = ": stream comment\n" + "id: event-1\n" + "retry: 250\n" + "event: value\n" + "data: first\n\n"
            + "event: done\n" + "data: second\n\n" + "event: value\n" + "data: ignored\n\n";

        List<ServerSentEvent<String>> events = new ArrayList<>();
        try (CloseableIterableStream<ServerSentEvent<String>> stream
            = ServerSentEventStreams.toIterableStream(response(200, BinaryData.fromString(body)), (event, data) -> data,
                event -> "done".equals(event.getEvent()))) {
            Iterator<ServerSentEvent<String>> iterator = stream.iterator();
            assertThrows(IllegalStateException.class, stream::iterator);
            iterator.forEachRemaining(events::add);
        }

        assertEquals(2, events.size());
        assertEquals("event-1", events.get(0).getId());
        assertEquals("value", events.get(0).getEvent());
        assertEquals("first", events.get(0).getData());
        assertEquals("stream comment", events.get(0).getComment());
        assertEquals(Duration.ofMillis(250), events.get(0).getRetryAfter());
        assertEquals("done", events.get(1).getEvent());
        assertEquals("second", events.get(1).getData());
    }

    @Test
    public void closingIterableCancelsStreamingBody() {
        AtomicBoolean cancelled = new AtomicBoolean();
        byte[] firstEvent = "event: value\ndata: first\n\n".getBytes(StandardCharsets.UTF_8);
        Flux<ByteBuffer> body = Flux.concat(Flux.just(ByteBuffer.wrap(firstEvent)), Flux.<ByteBuffer>never())
            .doOnCancel(() -> cancelled.set(true));
        BinaryData binaryData = BinaryData.fromFlux(body, null, false).block();

        try (CloseableIterableStream<ServerSentEvent<String>> stream
            = ServerSentEventStreams.toIterableStream(response(200, binaryData), (event, data) -> data)) {
            Iterator<ServerSentEvent<String>> iterator = stream.iterator();
            assertTrue(iterator.hasNext());
            assertEquals("first", iterator.next().getData());
            assertFalse(cancelled.get());
        }

        assertTrue(cancelled.get());
    }

    @Test
    public void closingBeforeIterationCancelsStreamingBody() {
        AtomicBoolean cancelled = new AtomicBoolean();
        Flux<ByteBuffer> body = Flux.<ByteBuffer>never().doOnCancel(() -> cancelled.set(true));
        BinaryData binaryData = BinaryData.fromFlux(body, null, false).block();

        CloseableIterableStream<ServerSentEvent<String>> stream
            = ServerSentEventStreams.toIterableStream(response(200, binaryData), (event, data) -> data);
        stream.close();

        assertTrue(cancelled.get());
    }

    @Test
    public void noContentReturnsEmptyCloseableIterable() {
        try (CloseableIterableStream<ServerSentEvent<String>> stream
            = ServerSentEventStreams.toIterableStream(response(204, null), (event, data) -> data)) {
            assertFalse(stream.iterator().hasNext());
        }
    }

    private static Response<BinaryData> response(int statusCode, BinaryData body) {
        HttpHeaders headers = new HttpHeaders();
        if (statusCode == 200) {
            headers.set(HttpHeaderName.CONTENT_TYPE, "text/event-stream");
        }
        return new SimpleResponse<>(new HttpRequest(HttpMethod.GET, "https://example.search.windows.net"), statusCode,
            headers, body);
    }
}
