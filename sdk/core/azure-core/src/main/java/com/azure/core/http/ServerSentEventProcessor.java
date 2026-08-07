// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.http;

import java.io.IOException;

/**
 * Processes decoded server-sent events.
 *
 * @param <T> The type of the event data.
 */
@FunctionalInterface
public interface ServerSentEventProcessor<T> {
    /**
     * Processes a server-sent event.
     *
     * @param event The server-sent event.
     * @return {@code true} to continue processing events, or {@code false} to stop and close the event stream.
     * @throws IOException If an I/O error occurs while processing the event.
     */
    boolean process(ServerSentEvent<T> event) throws IOException;
}
