// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.util;

import com.azure.core.http.ServerSentEvent;
import com.azure.core.implementation.util.ServerSentEventHelper;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServerSentEventUtilsTests {
    @Test
    public void toStreamParsesSupportedFields() {
        BinaryData body = BinaryData.fromString("\uFEFF: comment\n" + "id: 42\n" + "event: stockUpdate\n"
            + "retry: 2000\n" + "ignored: value\n" + "data: first\n" + "data: second\n\n");

        List<ServerSentEvent> events;
        try (Stream<ServerSentEvent> stream = ServerSentEventUtils.toStream(body)) {
            events = stream.collect(Collectors.toList());
        }

        assertEquals(1, events.size());
        ServerSentEvent event = events.get(0);
        assertEquals("42", event.getId());
        assertEquals("stockUpdate", event.getEvent());
        assertEquals(Arrays.asList("first", "second"), event.getData());
        assertEquals("comment", event.getComment());
        assertEquals(Duration.ofSeconds(2), ServerSentEventHelper.getRetryAfter(event));
    }

    @Test
    public void toFluxSkipsBlocksWithoutDataAndUsesDefaultEvent() {
        BinaryData body = BinaryData.fromString(
            ": keep alive\n" + "retry: invalid\n\n" + "id: contains\0null\n" + "event:\n" + "data: payload");

        StepVerifier.create(ServerSentEventUtils.toFlux(body)).assertNext(event -> {
            assertNull(event.getId());
            assertEquals("message", event.getEvent());
            assertEquals(Collections.singletonList("payload"), event.getData());
            assertNull(event.getComment());
            assertNull(ServerSentEventHelper.getRetryAfter(event));
        }).verifyComplete();
    }

    @Test
    public void toStreamIsLazyAndCloseCancelsBody() {
        AtomicBoolean subscribed = new AtomicBoolean();
        AtomicBoolean cancelled = new AtomicBoolean();
        Flux<ByteBuffer> content = Flux
            .concat(Flux.just(ByteBuffer.wrap("data: payload\n\n".getBytes(StandardCharsets.UTF_8))), Flux.never())
            .doOnSubscribe(ignored -> subscribed.set(true))
            .doOnCancel(() -> cancelled.set(true));
        BinaryData body = BinaryData.fromFlux(content, null, false).block();

        assertFalse(subscribed.get());
        try (Stream<ServerSentEvent> stream = ServerSentEventUtils.toStream(body)) {
            assertFalse(subscribed.get());
            Iterator<ServerSentEvent> iterator = stream.iterator();
            assertTrue(iterator.hasNext());
            assertEquals(Collections.singletonList("payload"), iterator.next().getData());
            assertTrue(subscribed.get());
        }

        assertTrue(cancelled.get());
    }
}
