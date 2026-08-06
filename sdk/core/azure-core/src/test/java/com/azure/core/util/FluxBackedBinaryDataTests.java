// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.util;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FluxBackedBinaryDataTests {
    @Test
    public void nonReplayableToStreamIsLazyAndCloseCancelsUpstream() throws IOException {
        AtomicBoolean subscribed = new AtomicBoolean();
        AtomicBoolean cancelled = new AtomicBoolean();
        byte[] expected = "event".getBytes(StandardCharsets.UTF_8);
        Flux<ByteBuffer> content = Flux.concat(Flux.just(ByteBuffer.wrap(expected)), Flux.never())
            .doOnSubscribe(ignored -> subscribed.set(true))
            .doOnCancel(() -> cancelled.set(true));

        BinaryData binaryData = BinaryData.fromFlux(content, null, false).block();
        assertFalse(subscribed.get());

        byte[] actual = new byte[expected.length];
        try (InputStream stream = binaryData.toStream()) {
            assertFalse(subscribed.get());
            assertEquals(expected.length, stream.read(actual));
            assertTrue(subscribed.get());
        }

        assertArrayEquals(expected, actual);
        assertTrue(cancelled.get());
    }
}
