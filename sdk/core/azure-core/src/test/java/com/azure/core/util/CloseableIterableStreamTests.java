// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Tests for {@link CloseableIterableStream}.
 */
public class CloseableIterableStreamTests {
    @Test
    public void requiresCloseableWithFlux() {
        Assertions.assertThrows(NullPointerException.class,
            () -> new CloseableIterableStream<>(Flux.just("item"), null));
    }

    @Test
    public void requiresCloseableWithIterable() {
        Assertions.assertThrows(NullPointerException.class,
            () -> new CloseableIterableStream<>(Arrays.asList("item"), null));
    }

    @Test
    public void closesResourceOnlyOnce() throws Exception {
        AtomicInteger closeCount = new AtomicInteger();
        CloseableIterableStream<String> iterableStream
            = new CloseableIterableStream<>(Flux.just("item"), closeCount::incrementAndGet);

        iterableStream.close();
        iterableStream.close();

        Assertions.assertEquals(1, closeCount.get());
    }

    @Test
    public void closesIterableResource() throws Exception {
        AtomicInteger closeCount = new AtomicInteger();
        CloseableIterableStream<String> iterableStream
            = new CloseableIterableStream<>(Arrays.asList("one", "two"), closeCount::incrementAndGet);

        iterableStream.close();

        Assertions.assertEquals(1, closeCount.get());
    }

    @Test
    public void propagatesCloseExceptionOnlyOnce() {
        AtomicInteger closeCount = new AtomicInteger();
        IllegalStateException closeException = new IllegalStateException("close failed");
        CloseableIterableStream<String> iterableStream = new CloseableIterableStream<>(Flux.just("item"), () -> {
            closeCount.incrementAndGet();
            throw closeException;
        });

        IllegalStateException thrown = Assertions.assertThrows(IllegalStateException.class, iterableStream::close);
        Assertions.assertSame(closeException, thrown);
        Assertions.assertDoesNotThrow(iterableStream::close);
        Assertions.assertEquals(1, closeCount.get());
    }

    @Test
    public void preservesConsumptionAfterClose() throws Exception {
        AtomicInteger closeCount = new AtomicInteger();
        CloseableIterableStream<String> iterableStream
            = new CloseableIterableStream<>(Flux.just("one", "two"), closeCount::incrementAndGet);

        iterableStream.close();

        Assertions.assertEquals(Arrays.asList("one", "two"), iterableStream.stream().collect(Collectors.toList()));
        Assertions.assertEquals(1, closeCount.get());
    }
}
