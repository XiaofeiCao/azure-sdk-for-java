// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.http;

import java.io.IOException;

/**
 * A listener for receiving server-sent events.
 *
 * <p>Returning {@code false} from {@link #onEvent(ServerSentEvent)} stops normal processing and closes the response
 * body without invoking {@link #onError(Throwable)}. {@link #onClose()} is invoked when processing ends.</p>
 *
 * <p>Generated Azure Core clients consume this listener above the HTTP transport after receiving a streaming response
 * body. The listener isn't attached to the underlying {@link HttpRequest}.</p>
 *
 * @param <T> The type of the event data.
 */
@FunctionalInterface
public interface ServerSentEventListener<T> {
    /**
     * Handles a server-sent event.
     *
     * @param event The server-sent event.
     * @return {@code true} to continue receiving events, or {@code false} to stop and close the response body.
     * @throws IOException If an I/O error occurs while handling the event.
     */
    boolean onEvent(ServerSentEvent<T> event) throws IOException;

    /**
     * Handles an error that terminates event processing.
     *
     * @param error The error that terminated event processing.
     */
    default void onError(Throwable error) {
        // No-op by default.
    }

    /**
     * Handles closure of the event stream, including closure requested by returning {@code false} from
     * {@link #onEvent(ServerSentEvent)}.
     */
    default void onClose() {
        // No-op by default.
    }
}
