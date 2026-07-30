// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.http;

import java.io.IOException;

/**
 * A listener for events received from a {@code text/event-stream} response.
 */
@FunctionalInterface
public interface ServerSentEventListener {
    /**
     * Handles a server-sent event.
     *
     * @param event The server-sent event.
     * @throws IOException If the event cannot be handled.
     */
    void onEvent(ServerSentEvent event) throws IOException;

    /**
     * Handles an error that terminates event processing.
     *
     * @param throwable The processing error.
     */
    default void onError(Throwable throwable) {
        // No-op by default.
    }

    /**
     * Handles normal completion of the event stream.
     * <p>
     * This method is invoked when a connection segment closes normally. If the stream supplies a retry interval, it
     * may be invoked before the pipeline reconnects.
     */
    default void onClose() {
        // No-op by default.
    }
}
