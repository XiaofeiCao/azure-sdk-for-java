// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.http;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServerSentEventParserTests {
    @Test
    public void parsesFieldsAcrossChunksAndLineEndings() throws IOException {
        List<ServerSentEvent> events = new ArrayList<>();
        AtomicBoolean closed = new AtomicBoolean();
        ServerSentEventParser parser = new ServerSentEventParser(listener(events, closed));

        accept(parser, ": note\rdata: h");
        accept(parser, "éllo\r\ndata: world\nid: 42\nretry: 25\n\n");
        parser.complete();

        assertEquals(1, events.size());
        ServerSentEvent event = events.get(0);
        assertEquals("message", event.getEvent());
        assertEquals("42", event.getId());
        assertEquals("note", event.getComment());
        assertEquals(Arrays.asList("héllo", "world"), event.getData());
        assertEquals("42", parser.getLastEventId());
        assertEquals(Duration.ofMillis(25), parser.getRetryAfter());
        assertTrue(closed.get());
    }

    @Test
    public void discardsIncompleteEventAtEndOfStream() throws IOException {
        List<ServerSentEvent> events = new ArrayList<>();
        ServerSentEventParser parser = new ServerSentEventParser(listener(events, new AtomicBoolean()));

        accept(parser, "event: responseDelta\ndata: {\"delta\":\"hello\"}");
        parser.complete();

        assertTrue(events.isEmpty());
    }

    @Test
    public void ignoresLeadingUtf8Bom() throws IOException {
        List<ServerSentEvent> events = new ArrayList<>();
        ServerSentEventParser parser = new ServerSentEventParser(listener(events, new AtomicBoolean()));

        accept(parser, "\uFEFFdata: value\n\n");
        parser.complete();

        assertEquals(1, events.size());
        assertEquals("value", events.get(0).getData().get(0));
    }

    @Test
    public void ignoresCommentOnlyAndUnknownFields() throws IOException {
        List<ServerSentEvent> events = new ArrayList<>();
        ServerSentEventParser parser = new ServerSentEventParser(listener(events, new AtomicBoolean()));

        accept(parser, ": keepalive\nunknown: value\n\n");
        parser.complete();

        assertTrue(events.isEmpty());
    }

    @Test
    public void ignoresNamedEventWithoutData() throws IOException {
        List<ServerSentEvent> events = new ArrayList<>();
        ServerSentEventParser parser = new ServerSentEventParser(listener(events, new AtomicBoolean()));

        accept(parser, "event: ping\n\n");
        parser.complete();

        assertTrue(events.isEmpty());
    }

    @Test
    public void ignoresInvalidRetryAndNullEventId() throws IOException {
        List<ServerSentEvent> events = new ArrayList<>();
        ServerSentEventParser parser = new ServerSentEventParser(listener(events, new AtomicBoolean()));

        accept(parser, "id: valid\nretry: 10\n\nid: invalid\0id\nretry: nope\ndata: value\n\n");
        parser.complete();

        assertEquals("valid", parser.getLastEventId());
        assertEquals(Duration.ofMillis(10), parser.getRetryAfter());
        assertEquals("valid", events.get(0).getId());
    }

    @Test
    public void listenerExceptionStopsParsing() {
        ServerSentEventParser parser = new ServerSentEventParser(event -> {
            throw new IOException("listener failure");
        });

        IOException exception = assertThrows(IOException.class, () -> accept(parser, "data: value\n\n"));
        assertEquals("listener failure", exception.getMessage());
    }

    @Test
    public void emptyIdResetsLastEventId() throws IOException {
        List<ServerSentEvent> events = new ArrayList<>();
        ServerSentEventParser parser = new ServerSentEventParser(listener(events, new AtomicBoolean()));

        accept(parser, "id: first\ndata: one\n\nid:\ndata: two\n\n");
        parser.complete();

        assertEquals(2, events.size());
        assertEquals("first", events.get(0).getId());
        assertEquals("", events.get(1).getId());
        assertEquals("", parser.getLastEventId());
        assertNull(events.get(1).getComment());
        assertFalse(events.get(1).getData().isEmpty());
    }

    @Test
    public void carriesLastEventIdFromPreviousConnection() throws IOException {
        List<ServerSentEvent> events = new ArrayList<>();
        ServerSentEventParser parser = new ServerSentEventParser(listener(events, new AtomicBoolean()), "previous-id");

        accept(parser, "data: value\n\n");
        parser.complete();

        assertEquals("previous-id", events.get(0).getId());
        assertEquals("previous-id", parser.getLastEventId());
    }

    private static ServerSentEventListener listener(List<ServerSentEvent> events, AtomicBoolean closed) {
        AtomicReference<Throwable> error = new AtomicReference<>();
        return new ServerSentEventListener() {
            @Override
            public void onEvent(ServerSentEvent event) {
                events.add(event);
            }

            @Override
            public void onError(Throwable throwable) {
                error.set(throwable);
            }

            @Override
            public void onClose() {
                closed.set(true);
            }
        };
    }

    private static void accept(ServerSentEventParser parser, String value) throws IOException {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        int split = Math.max(1, bytes.length / 2);
        parser.accept(ByteBuffer.wrap(bytes, 0, split));
        parser.accept(ByteBuffer.wrap(bytes, split, bytes.length - split));
    }
}
