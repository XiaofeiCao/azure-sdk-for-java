// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.util.sse;

import com.azure.core.validation.http.LocalTestServer;

import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

/**
 * Provides a {@link LocalTestServer} that serves the three canonical {@code http-specs/streaming/sse} scenarios as
 * real {@code text/event-stream} responses, used to test the {@link ServerSentEventParser} runtime end-to-end.
 * <ul>
 *     <li>{@code GET /streaming/sse/unnamed/receive} - unnamed (default {@code message}) events.</li>
 *     <li>{@code GET /streaming/sse/named/receive} - named events keyed on {@code event:} + terminal {@code [DONE]}.</li>
 *     <li>{@code POST /streaming/sse/retrieve/stream} - named events with a request body + terminal {@code [DONE]}.</li>
 * </ul>
 */
final class SseMockServer {
    // Every event is terminated by a blank line, as real SSE servers do.
    static final String UNNAMED_BODY
        = "data: {\"desc\": \"one\"}\n\n" + "data: {\"desc\": \"two\"}\n\n" + "data: {\"desc\": \"three\"}\n\n";

    static final String NAMED_BODY = "event: responseCreated\n" + "data: {\"id\": \"resp_1\"}\n\n"
        + "event: responseDelta\n" + "data: {\"delta\": \"Hello\"}\n\n" + "event: responseDelta\n"
        + "data: {\"delta\": \" world\"}\n\n" + "data: [DONE]\n\n";

    static final String RETRIEVE_BODY = "event: partialResult\n" + "data: {\"text\": \"partial one\"}\n\n"
        + "event: partialResult\n" + "data: {\"text\": \"partial two\"}\n\n" + "event: finalResult\n"
        + "data: {\"references\": [\"ref-a\", \"ref-b\"]}\n\n" + "data: [DONE]\n\n";

    static LocalTestServer create() {
        return new LocalTestServer((req, resp, requestBody) -> {
            String path = req.getServletPath();
            boolean get = "GET".equalsIgnoreCase(req.getMethod());
            boolean post = "POST".equalsIgnoreCase(req.getMethod());

            if (get && "/streaming/sse/unnamed/receive".equals(path)) {
                writeEventStream(resp, UNNAMED_BODY);
            } else if (get && "/streaming/sse/named/receive".equals(path)) {
                writeEventStream(resp, NAMED_BODY);
            } else if (post && "/streaming/sse/retrieve/stream".equals(path)) {
                writeEventStream(resp, RETRIEVE_BODY);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getHttpOutput().flush();
            }
        });
    }

    private static void writeEventStream(org.eclipse.jetty.server.Response resp, String body)
        throws java.io.IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("text/event-stream");
        resp.setContentLength(bytes.length);
        resp.getHttpOutput().write(bytes);
        resp.getHttpOutput().flush();
    }

    private SseMockServer() {
    }
}
