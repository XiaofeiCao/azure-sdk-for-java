// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.util.sse;

import com.azure.core.http.HttpPipeline;
import com.azure.core.http.HttpPipelineBuilder;
import com.azure.core.util.IterableStream;
import com.azure.core.util.sse.generated.FinalResult;
import com.azure.core.util.sse.generated.Info;
import com.azure.core.util.sse.generated.NamedSseAsyncClient;
import com.azure.core.util.sse.generated.NamedSseClient;
import com.azure.core.util.sse.generated.PartialResult;
import com.azure.core.util.sse.generated.ResponseCreatedResponseEvents;
import com.azure.core.util.sse.generated.ResponseDeltaResponseEvents;
import com.azure.core.util.sse.generated.ResponseEventsBase;
import com.azure.core.util.sse.generated.RetrievalEventsBase;
import com.azure.core.util.sse.generated.RetrievalFinalResultEvents;
import com.azure.core.util.sse.generated.RetrievalPartialResultEvents;
import com.azure.core.util.sse.generated.RetrievalRequest;
import com.azure.core.util.sse.generated.RetrieveSseAsyncClient;
import com.azure.core.util.sse.generated.RetrieveSseClient;
import com.azure.core.util.sse.generated.UnnamedSseAsyncClient;
import com.azure.core.util.sse.generated.UnnamedSseClient;
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
 * End-to-end runtime tests that drive the "expected emitter output" fixture clients through a real HTTP round-trip
 * against {@link SseMockServer}, validating the {@link ServerSentEventParser} runtime for all three
 * {@code http-specs/streaming/sse} scenarios (async {@code Flux} + sync {@code IterableStream}).
 */
public class ServerSentEventRuntimeIntegrationTests {
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

    // ---- Scenario 1: unnamed events ----

    @Test
    public void scenario1UnnamedAsync() {
        StepVerifier.create(new UnnamedSseAsyncClient(pipeline, endpoint).receive())
            .assertNext(info -> assertEquals("one", info.getDesc()))
            .assertNext(info -> assertEquals("two", info.getDesc()))
            .assertNext(info -> assertEquals("three", info.getDesc()))
            .verifyComplete();
    }

    @Test
    public void scenario1UnnamedSync() {
        IterableStream<Info> stream = new UnnamedSseClient(pipeline, endpoint).receive();
        List<Info> infos = new ArrayList<>();
        stream.forEach(infos::add);

        assertEquals(3, infos.size());
        assertEquals("one", infos.get(0).getDesc());
        assertEquals("two", infos.get(1).getDesc());
        assertEquals("three", infos.get(2).getDesc());
    }

    // ---- Scenario 2: named events + terminal [DONE] ----

    @Test
    public void scenario2NamedAsync() {
        StepVerifier.create(new NamedSseAsyncClient(pipeline, endpoint).receive()).assertNext(evt -> {
            ResponseCreatedResponseEvents wrapper = assertInstanceOf(ResponseCreatedResponseEvents.class, evt);
            assertEquals("resp_1", wrapper.getValue().getId());
        })
            .assertNext(evt -> assertEquals("Hello", deltaValue(evt)))
            .assertNext(evt -> assertEquals(" world", deltaValue(evt)))
            .verifyComplete();
    }

    @Test
    public void scenario2NamedSync() {
        IterableStream<ResponseEventsBase> stream = new NamedSseClient(pipeline, endpoint).receive();
        List<ResponseEventsBase> events = new ArrayList<>();
        stream.forEach(events::add);

        assertEquals(3, events.size());
        assertEquals("resp_1", assertInstanceOf(ResponseCreatedResponseEvents.class, events.get(0)).getValue().getId());
        assertEquals("Hello", deltaValue(events.get(1)));
        assertEquals(" world", deltaValue(events.get(2)));
    }

    private static String deltaValue(ResponseEventsBase evt) {
        return assertInstanceOf(ResponseDeltaResponseEvents.class, evt).getValue().getDelta();
    }

    // ---- Scenario 3: retrieve (POST body) + terminal [DONE] ----

    @Test
    public void scenario3RetrieveAsync() {
        RetrievalRequest request = new RetrievalRequest().setQuery("hello");
        StepVerifier.create(new RetrieveSseAsyncClient(pipeline, endpoint).stream(request))
            .assertNext(evt -> assertEquals("partial one", partialText(evt)))
            .assertNext(evt -> assertEquals("partial two", partialText(evt)))
            .assertNext(evt -> assertEquals(Arrays.asList("ref-a", "ref-b"), finalRefs(evt)))
            .verifyComplete();
    }

    @Test
    public void scenario3RetrieveSync() {
        RetrievalRequest request = new RetrievalRequest().setQuery("hello");
        IterableStream<RetrievalEventsBase> stream = new RetrieveSseClient(pipeline, endpoint).stream(request);
        List<RetrievalEventsBase> events = new ArrayList<>();
        stream.forEach(events::add);

        assertEquals(3, events.size());
        assertEquals("partial one", partialText(events.get(0)));
        assertEquals("partial two", partialText(events.get(1)));
        assertEquals(Arrays.asList("ref-a", "ref-b"), finalRefs(events.get(2)));
    }

    private static String partialText(RetrievalEventsBase evt) {
        PartialResult value = assertInstanceOf(RetrievalPartialResultEvents.class, evt).getValue();
        return value.getText();
    }

    private static List<String> finalRefs(RetrievalEventsBase evt) {
        FinalResult value = assertInstanceOf(RetrievalFinalResultEvents.class, evt).getValue();
        return value.getReferences();
    }
}
