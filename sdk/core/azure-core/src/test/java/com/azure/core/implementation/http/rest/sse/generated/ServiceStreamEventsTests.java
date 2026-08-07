// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.implementation.http.rest.sse.generated;

import com.azure.core.http.ServerSentEvent;
import com.azure.core.util.BinaryData;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServiceStreamEventsTests {
    private static final byte[] USER_LOGIN_EVENT
        = ("event: userLogin\n" + "data: {\"userId\":\"user-1\",\"loginTime\":\"2026-08-05T21:00:00Z\"}\n\n")
            .getBytes(StandardCharsets.UTF_8);

    @Test
    public void asyncDecodingDoesNotSwitchToBlockingWorker() {
        Thread subscriptionThread = Thread.currentThread();
        AtomicInteger cancellationCount = new AtomicInteger();
        BinaryData body = createOpenBody(cancellationCount);

        StepVerifier.create(ServiceStreamEvents.toFlux(body)).assertNext(event -> {
            assertSame(subscriptionThread, Thread.currentThread());
            assertUserLogin(event);
        }).thenCancel().verify();

        assertEquals(1, cancellationCount.get());
    }

    @Test
    public void concurrentLongLivedStreamsDoNotOccupyWorkerThreads() {
        int streamCount = 64;
        Thread subscriptionThread = Thread.currentThread();
        AtomicInteger cancellationCount = new AtomicInteger();
        Set<Thread> eventThreads = ConcurrentHashMap.newKeySet();
        List<Flux<ServerSentEvent<ServiceStreamEvent>>> streams = new ArrayList<>();

        for (int i = 0; i < streamCount; i++) {
            streams.add(ServiceStreamEvents.toFlux(createOpenBody(cancellationCount))
                .doOnNext(ignored -> eventThreads.add(Thread.currentThread())));
        }

        StepVerifier.create(Flux.merge(streams).take(streamCount)).expectNextCount(streamCount).verifyComplete();

        assertEquals(1, eventThreads.size());
        assertTrue(eventThreads.contains(subscriptionThread));
        assertEquals(streamCount, cancellationCount.get());
    }

    @Test
    public void terminalEventStopsBeforeFollowingFrameIsDeserialized() {
        String content = "event: terminal\ndata: [DONE]\n\n" + "event: userLogin\ndata: not-json\n\n";
        BinaryData body = BinaryData.fromString(content);

        StepVerifier.create(ServiceStreamEvents.toFlux(body))
            .assertNext(event -> assertTrue(event.getData().isTerminal()))
            .verifyComplete();
    }

    private static BinaryData createOpenBody(AtomicInteger cancellationCount) {
        Flux<ByteBuffer> content = Flux.concat(Flux.just(ByteBuffer.wrap(USER_LOGIN_EVENT)), Flux.never())
            .doOnCancel(cancellationCount::incrementAndGet);
        return BinaryData.fromFlux(content, null, false).block();
    }

    private static void assertUserLogin(ServerSentEvent<ServiceStreamEvent> event) {
        assertTrue(event.getData().isUserLogin());
        assertEquals("user-1", event.getData().asUserLogin().getUserId());
    }
}
