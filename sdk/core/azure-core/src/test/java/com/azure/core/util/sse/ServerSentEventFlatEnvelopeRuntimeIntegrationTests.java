// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.util.sse;

import com.azure.core.http.HttpPipeline;
import com.azure.core.http.HttpPipelineBuilder;
import com.azure.core.util.BinaryData;
import com.azure.core.util.IterableStream;
import com.azure.core.util.sse.generated.flat.Info;
import com.azure.core.util.sse.generated.flat.MixedSseAsyncClient;
import com.azure.core.util.sse.generated.flat.MixedSseClient;
import com.azure.core.util.sse.generated.flat.ResponseDelta;
import com.azure.core.validation.http.LocalTestServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * End-to-end runtime tests for the <strong>flat/raw</strong> SSE fixtures (TCGC/clientcore-style, no union), driving
 * the fixture clients through a real HTTP round-trip against {@link SseMockServer}'s mixed stream (unnamed
 * {@code Info} {@code message} events, named {@code responseDelta} events, terminal {@code [DONE]}).
 * <p>
 * The clients expose the runtime {@link ServerSentEvent} envelope directly; these tests act as the consumer,
 * dispatching on {@link ServerSentEvent#getEvent()} and deserializing {@link ServerSentEvent#getData()} into
 * the model of choice.
 */
public class ServerSentEventFlatEnvelopeRuntimeIntegrationTests {
    private static LocalTestServer server;
    private static HttpPipeline pipeline;
    private static String endpoint;

    @BeforeAll
    public static void startServer() {
        server = SseMockServer.create();
        server.start();
        endpoint = server.getHttpUri();
        pipeline = new HttpPipelineBuilder().httpClient(new SseStreamingHttpClient()).build();
    }

    @AfterAll
    public static void stopServer() {
        if (server != null) {
            server.stop();
        }
    }

    @Test
    public void mixedRawAsync() {
        StepVerifier.create(new MixedSseAsyncClient(pipeline, endpoint).receive())
            .assertNext(evt -> assertInfo(evt, "one"))
            .assertNext(evt -> assertDelta(evt, "Hello"))
            .assertNext(evt -> assertDelta(evt, " world"))
            .assertNext(evt -> assertInfo(evt, "two"))
            .verifyComplete();
    }

    @Test
    public void mixedRawSync() {
        IterableStream<ServerSentEvent> stream = new MixedSseClient(pipeline, endpoint).receive();
        List<ServerSentEvent> events = new ArrayList<>();
        stream.forEach(events::add);

        assertEquals(4, events.size());
        assertInfo(events.get(0), "one");
        assertDelta(events.get(1), "Hello");
        assertDelta(events.get(2), " world");
        assertInfo(events.get(3), "two");
    }

    // Consumer-side dispatch: key on the SSE event name, deserialize the data payload into the chosen model.
    private static void assertInfo(ServerSentEvent evt, String expectedDesc) {
        assertEquals("message", evt.getEvent());
        Info info = BinaryData.fromString(String.join("\n", evt.getData())).toObject(Info.class);
        assertEquals(expectedDesc, info.getDesc());
    }

    private static void assertDelta(ServerSentEvent evt, String expectedDelta) {
        assertEquals("responseDelta", evt.getEvent());
        ResponseDelta delta = BinaryData.fromString(String.join("\n", evt.getData())).toObject(ResponseDelta.class);
        assertEquals(expectedDelta, delta.getDelta());
    }
}
