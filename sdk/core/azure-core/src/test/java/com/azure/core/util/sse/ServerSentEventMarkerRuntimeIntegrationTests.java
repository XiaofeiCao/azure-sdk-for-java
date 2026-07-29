// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.util.sse;

import com.azure.core.http.HttpPipeline;
import com.azure.core.http.HttpPipelineBuilder;
import com.azure.core.util.IterableStream;
import com.azure.core.util.sse.generated.marker.FinalResult;
import com.azure.core.util.sse.generated.marker.NamedSseAsyncClient;
import com.azure.core.util.sse.generated.marker.NamedSseClient;
import com.azure.core.util.sse.generated.marker.PartialResult;
import com.azure.core.util.sse.generated.marker.ResponseCreated;
import com.azure.core.util.sse.generated.marker.ResponseDelta;
import com.azure.core.util.sse.generated.marker.ResponseEvents;
import com.azure.core.util.sse.generated.marker.RetrievalEvents;
import com.azure.core.util.sse.generated.marker.RetrievalRequest;
import com.azure.core.util.sse.generated.marker.RetrieveSseAsyncClient;
import com.azure.core.util.sse.generated.marker.RetrieveSseClient;
import com.azure.core.validation.http.LocalTestServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * End-to-end runtime tests for the <strong>marker-interface</strong> SSE fixtures (§5b), driving the fixture clients
 * through a real HTTP round-trip against {@link SseMockServer}. Unlike the wrapper-hierarchy variant, consumers
 * switch directly on the concrete event models (no wrapper {@code getValue()}), which these tests assert for
 * scenarios 2 and 3 (async {@code Flux} + sync {@code IterableStream}). Scenario 1 (unnamed) is identical to the
 * wrapper variant and is covered by {@link ServerSentEventRuntimeIntegrationTests}.
 */
public class ServerSentEventMarkerRuntimeIntegrationTests {
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

    // ---- Scenario 2: named events + terminal [DONE] ----

    @Test
    public void scenario2NamedAsync() {
        StepVerifier.create(new NamedSseAsyncClient(pipeline, endpoint).receive())
            .assertNext(evt -> assertEquals("resp_1", assertInstanceOf(ResponseCreated.class, evt).getId()))
            .assertNext(evt -> assertEquals("Hello", assertInstanceOf(ResponseDelta.class, evt).getDelta()))
            .assertNext(evt -> assertEquals(" world", assertInstanceOf(ResponseDelta.class, evt).getDelta()))
            .verifyComplete();
    }

    @Test
    public void scenario2NamedSync() {
        IterableStream<ResponseEvents> stream = new NamedSseClient(pipeline, endpoint).receive();
        List<ResponseEvents> events = new ArrayList<>();
        stream.forEach(events::add);

        assertEquals(3, events.size());
        assertEquals("resp_1", assertInstanceOf(ResponseCreated.class, events.get(0)).getId());
        assertEquals("Hello", assertInstanceOf(ResponseDelta.class, events.get(1)).getDelta());
        assertEquals(" world", assertInstanceOf(ResponseDelta.class, events.get(2)).getDelta());
    }

    // ---- Scenario 3: retrieve (POST body) + terminal [DONE] ----

    @Test
    public void scenario3RetrieveAsync() {
        RetrievalRequest request = new RetrievalRequest().setQuery("hello");
        StepVerifier.create(new RetrieveSseAsyncClient(pipeline, endpoint).stream(request))
            .assertNext(evt -> assertEquals("partial one", assertInstanceOf(PartialResult.class, evt).getText()))
            .assertNext(evt -> assertEquals("partial two", assertInstanceOf(PartialResult.class, evt).getText()))
            .assertNext(evt -> assertEquals(Arrays.asList("ref-a", "ref-b"),
                assertInstanceOf(FinalResult.class, evt).getReferences()))
            .verifyComplete();
    }

    @Test
    public void scenario3RetrieveSync() {
        RetrievalRequest request = new RetrievalRequest().setQuery("hello");
        IterableStream<RetrievalEvents> stream = new RetrieveSseClient(pipeline, endpoint).stream(request);
        List<RetrievalEvents> events = new ArrayList<>();
        stream.forEach(events::add);

        assertEquals(3, events.size());
        assertEquals("partial one", assertInstanceOf(PartialResult.class, events.get(0)).getText());
        assertEquals("partial two", assertInstanceOf(PartialResult.class, events.get(1)).getText());
        assertEquals(Arrays.asList("ref-a", "ref-b"),
            assertInstanceOf(FinalResult.class, events.get(2)).getReferences());
    }
}
