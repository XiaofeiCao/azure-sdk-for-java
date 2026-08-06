// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.util;

import com.azure.core.http.ServerSentEvent;
import com.azure.core.implementation.util.ServerSentEventHelper;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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
}
