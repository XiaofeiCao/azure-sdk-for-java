// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.implementation.util;

import com.azure.core.http.ServerSentEvent;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Helper class that accesses non-public members of {@link ServerSentEvent}.
 */
public final class ServerSentEventHelper {
    private static final AtomicReference<ServerSentEventAccessor> ACCESSOR = new AtomicReference<>();

    private ServerSentEventHelper() {
    }

    /**
     * Defines access to non-public members of {@link ServerSentEvent}.
     */
    public interface ServerSentEventAccessor {
        /**
         * Sets the event identifier.
         *
         * @param serverSentEvent The event.
         * @param id The event identifier.
         */
        void setId(ServerSentEvent serverSentEvent, String id);

        /**
         * Sets the event name.
         *
         * @param serverSentEvent The event.
         * @param event The event name.
         */
        void setEvent(ServerSentEvent serverSentEvent, String event);

        /**
         * Sets the event data lines.
         *
         * @param serverSentEvent The event.
         * @param data The event data lines.
         */
        void setData(ServerSentEvent serverSentEvent, List<String> data);

        /**
         * Sets the event comment.
         *
         * @param serverSentEvent The event.
         * @param comment The event comment.
         */
        void setComment(ServerSentEvent serverSentEvent, String comment);

        /**
         * Sets the reconnection delay.
         *
         * @param serverSentEvent The event.
         * @param retryAfter The reconnection delay.
         */
        void setRetryAfter(ServerSentEvent serverSentEvent, Duration retryAfter);

        /**
         * Gets the reconnection delay.
         *
         * @param serverSentEvent The event.
         * @return The reconnection delay.
         */
        Duration getRetryAfter(ServerSentEvent serverSentEvent);
    }

    /**
     * Sets the accessor.
     *
     * @param serverSentEventAccessor The accessor.
     */
    public static void setAccessor(final ServerSentEventAccessor serverSentEventAccessor) {
        ACCESSOR.set(Objects.requireNonNull(serverSentEventAccessor, "'serverSentEventAccessor' cannot be null."));
    }

    /**
     * Sets the event identifier.
     *
     * @param serverSentEvent The event.
     * @param id The event identifier.
     */
    public static void setId(ServerSentEvent serverSentEvent, String id) {
        getAccessor().setId(serverSentEvent, id);
    }

    /**
     * Sets the event name.
     *
     * @param serverSentEvent The event.
     * @param event The event name.
     */
    public static void setEvent(ServerSentEvent serverSentEvent, String event) {
        getAccessor().setEvent(serverSentEvent, event);
    }

    /**
     * Sets the event data lines.
     *
     * @param serverSentEvent The event.
     * @param data The event data lines.
     */
    public static void setData(ServerSentEvent serverSentEvent, List<String> data) {
        getAccessor().setData(serverSentEvent, data);
    }

    /**
     * Sets the event comment.
     *
     * @param serverSentEvent The event.
     * @param comment The event comment.
     */
    public static void setComment(ServerSentEvent serverSentEvent, String comment) {
        getAccessor().setComment(serverSentEvent, comment);
    }

    /**
     * Sets the reconnection delay.
     *
     * @param serverSentEvent The event.
     * @param retryAfter The reconnection delay.
     */
    public static void setRetryAfter(ServerSentEvent serverSentEvent, Duration retryAfter) {
        getAccessor().setRetryAfter(serverSentEvent, retryAfter);
    }

    /**
     * Gets the reconnection delay.
     *
     * @param serverSentEvent The event.
     * @return The reconnection delay.
     */
    public static Duration getRetryAfter(ServerSentEvent serverSentEvent) {
        return getAccessor().getRetryAfter(serverSentEvent);
    }

    private static ServerSentEventAccessor getAccessor() {
        ServerSentEventAccessor accessor = ACCESSOR.get();
        if (accessor == null) {
            new ServerSentEvent();
            accessor = ACCESSOR.get();
        }
        return accessor;
    }
}
