// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.util.sse;

import com.azure.core.util.IterableStream;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link ServerSentEventParser} covering WHATWG framing, split-buffer handling and the sync adapter.
 */
public class ServerSentEventParserTests {

    private static Flux<ByteBuffer> asFlux(String text) {
        return Flux.just(ByteBuffer.wrap(text.getBytes(StandardCharsets.UTF_8)));
    }

    private static List<ServerSentEvent> parseAll(String text) {
        return ServerSentEventParser.parse(asFlux(text)).collectList().block();
    }

    @Test
    public void singleUnnamedEventDefaultsToMessage() {
        List<ServerSentEvent> events = parseAll("data: {\"desc\": \"one\"}\n\n");

        assertEquals(1, events.size());
        ServerSentEvent event = events.get(0);
        assertEquals("message", event.getEvent());
        assertEquals("{\"desc\": \"one\"}", event.getDataString());
        assertNull(event.getId());
        assertNull(event.getComment());
        assertNull(event.getRetryAfter());
    }

    @Test
    public void multipleEventsSeparatedByBlankLines() {
        List<ServerSentEvent> events = parseAll("data: one\n\n" + "data: two\n\n" + "data: three\n\n");

        assertEquals(3, events.size());
        assertEquals("one", events.get(0).getDataString());
        assertEquals("two", events.get(1).getDataString());
        assertEquals("three", events.get(2).getDataString());
    }

    @Test
    public void multiLineDataIsConcatenatedWithNewline() {
        List<ServerSentEvent> events = parseAll("data: line one\ndata: line two\n\n");

        assertEquals(1, events.size());
        assertEquals(Arrays.asList("line one", "line two"), events.get(0).getData());
        assertEquals("line one\nline two", events.get(0).getDataString());
    }

    @Test
    public void namedEventWithIdAndRetry() {
        List<ServerSentEvent> events
            = parseAll("event: responseCreated\nid: 42\nretry: 5000\ndata: {\"id\": \"resp_1\"}\n\n");

        assertEquals(1, events.size());
        ServerSentEvent event = events.get(0);
        assertEquals("responseCreated", event.getEvent());
        assertEquals("42", event.getId());
        assertEquals(Duration.ofMillis(5000), event.getRetryAfter());
        assertEquals("{\"id\": \"resp_1\"}", event.getDataString());
    }

    @Test
    public void commentOnlyBlockIsNotEmitted() {
        List<ServerSentEvent> events = parseAll(": keep-alive\n\n" + "data: real\n\n");

        assertEquals(1, events.size());
        assertEquals("real", events.get(0).getDataString());
    }

    @Test
    public void terminalDoneEventIsSurfacedAsData() {
        List<ServerSentEvent> events = parseAll("data: [DONE]\n\n");

        assertEquals(1, events.size());
        assertEquals("[DONE]", events.get(0).getDataString());
    }

    @Test
    public void eventsSplitAcrossBufferBoundariesAreReassembled() {
        String body = "event: responseDelta\ndata: {\"delta\": \"Hello\"}\n\n"
            + "event: responseDelta\ndata: {\"delta\": \" world\"}\n\n";

        // Feed one byte per ByteBuffer to maximise fragmentation across event/line boundaries.
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        List<ByteBuffer> singleByteBuffers = new ArrayList<>();
        for (byte b : bytes) {
            singleByteBuffers.add(ByteBuffer.wrap(new byte[] { b }));
        }

        StepVerifier.create(ServerSentEventParser.parse(Flux.fromIterable(singleByteBuffers))).assertNext(evt -> {
            assertEquals("responseDelta", evt.getEvent());
            assertEquals("{\"delta\": \"Hello\"}", evt.getDataString());
        }).assertNext(evt -> {
            assertEquals("responseDelta", evt.getEvent());
            assertEquals("{\"delta\": \" world\"}", evt.getDataString());
        }).verifyComplete();
    }

    @Test
    public void multiByteUtf8CharacterSplitAcrossBuffers() {
        // "data: café\n\n" - the é is two UTF-8 bytes; split the stream in the middle of it.
        byte[] bytes = "data: café\n\n".getBytes(StandardCharsets.UTF_8);
        int splitAt = new String(bytes, StandardCharsets.UTF_8).indexOf('é') + 1; // between the two é bytes
        Flux<ByteBuffer> source = Flux.just(ByteBuffer.wrap(Arrays.copyOfRange(bytes, 0, splitAt)),
            ByteBuffer.wrap(Arrays.copyOfRange(bytes, splitAt, bytes.length)));

        List<ServerSentEvent> events = ServerSentEventParser.parse(source).collectList().block();
        assertEquals(1, events.size());
        assertEquals("café", events.get(0).getDataString());
    }

    @Test
    public void crlfAndCrLineEndingsAreHandled() {
        List<ServerSentEvent> crlf = parseAll("event: a\r\ndata: one\r\n\r\n");
        assertEquals(1, crlf.size());
        assertEquals("a", crlf.get(0).getEvent());
        assertEquals("one", crlf.get(0).getDataString());

        List<ServerSentEvent> cr = parseAll("event: b\rdata: two\r\r");
        assertEquals(1, cr.size());
        assertEquals("b", cr.get(0).getEvent());
        assertEquals("two", cr.get(0).getDataString());
    }

    @Test
    public void finalEventWithoutTrailingBlankLineIsStillEmitted() {
        List<ServerSentEvent> events = parseAll("data: one\n\ndata: last");

        assertEquals(2, events.size());
        assertEquals("one", events.get(0).getDataString());
        assertEquals("last", events.get(1).getDataString());
    }

    @Test
    public void leadingSpaceAfterColonIsStrippedOnce() {
        // Two spaces after the colon: WHATWG strips exactly one, leaving a single leading space.
        List<ServerSentEvent> events = parseAll("data:  two-spaces\n\n");
        assertEquals(" two-spaces", events.get(0).getDataString());
    }

    @Test
    public void syncInputStreamParsesIdenticallyToAsync() {
        String body = "event: responseCreated\ndata: {\"id\": \"resp_1\"}\n\n" + "data: [DONE]\n\n";
        IterableStream<ServerSentEvent> events
            = ServerSentEventParser.parse(new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8)));

        List<ServerSentEvent> list = new ArrayList<>();
        events.forEach(list::add);

        assertEquals(2, list.size());
        assertEquals("responseCreated", list.get(0).getEvent());
        assertEquals("{\"id\": \"resp_1\"}", list.get(0).getDataString());
        assertEquals("[DONE]", list.get(1).getDataString());
    }

    @Test
    public void dataListIsUnmodifiable() {
        ServerSentEvent event = parseAll("data: one\n\n").get(0);
        assertThrows(UnsupportedOperationException.class, () -> event.getData().add("mutate"));
    }

    @Test
    public void emptyStreamProducesNoEvents() {
        assertTrue(parseAll("").isEmpty());
    }

    @Test
    public void nullSourceThrowsOrErrors() {
        StepVerifier.create(ServerSentEventParser.parse((Flux<ByteBuffer>) null))
            .verifyError(NullPointerException.class);
        assertThrows(NullPointerException.class, () -> ServerSentEventParser.parse((java.io.InputStream) null));
    }
}
