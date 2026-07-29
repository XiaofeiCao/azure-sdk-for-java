// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

/**
 * <p>Package containing utilities for parsing
 * <a href="https://html.spec.whatwg.org/multipage/server-sent-events.html">Server-Sent Events (SSE)</a>
 * ({@code text/event-stream}) response bodies in the Azure SDK for Java.</p>
 *
 * <p>{@link com.azure.core.util.sse.ServerSentEventParser} frames a byte stream into
 * {@link com.azure.core.util.sse.ServerSentEvent} instances, feeding both reactive
 * ({@link reactor.core.publisher.Flux}) and blocking ({@link com.azure.core.util.IterableStream}) consumers from a
 * single parser implementation.</p>
 */
package com.azure.core.util.sse;
