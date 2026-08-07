// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.http;

import com.azure.core.implementation.util.ServerSentEventHelper;

import java.time.Duration;

/**
 * Represents a server-sent event with a typed data payload.
 *
 * <p>A server-sent event may contain an identifier, event name, data, a comment, and a retry interval. All fields are
 * optional.</p>
 *
 * <p>This type exposes the metadata needed for caller-managed reconnection, but Azure Core doesn't automatically
 * reconnect an event stream. Callers can use {@link #getId()} as the next request's {@code Last-Event-Id} value and
 * {@link #getRetryAfter()} as the reconnect delay.</p>
 *
 * @param <T> The type of the event data.
 * @see <a href="https://html.spec.whatwg.org/multipage/server-sent-events.html#parsing-an-event-stream">
 * Parsing an event stream</a>
 */
public final class ServerSentEvent<T> {
    private String id;
    private String event;
    private T data;
    private String comment;
    private Duration retryAfter;

    static {
        ServerSentEventHelper.setAccessor(new ServerSentEventHelper.ServerSentEventAccessor() {
            @Override
            public void setId(ServerSentEvent<?> serverSentEvent, String id) {
                serverSentEvent.setId(id);
            }

            @Override
            public void setEvent(ServerSentEvent<?> serverSentEvent, String event) {
                serverSentEvent.setEvent(event);
            }

            @Override
            public <U> void setData(ServerSentEvent<U> serverSentEvent, U data) {
                serverSentEvent.setData(data);
            }

            @Override
            public void setComment(ServerSentEvent<?> serverSentEvent, String comment) {
                serverSentEvent.setComment(comment);
            }

            @Override
            public void setRetryAfter(ServerSentEvent<?> serverSentEvent, Duration retryAfter) {
                serverSentEvent.setRetryAfter(retryAfter);
            }
        });
    }

    /**
     * Creates a server-sent event.
     */
    public ServerSentEvent() {
    }

    /**
     * Gets the event identifier.
     *
     * @return The event identifier, or {@code null} if it wasn't specified.
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the event name.
     *
     * @return The event name, or {@code null} if it wasn't specified.
     */
    public String getEvent() {
        return event;
    }

    /**
     * Gets the event data.
     *
     * @return The event data, or {@code null} if event data wasn't specified.
     */
    public T getData() {
        return data;
    }

    /**
     * Gets the event comment.
     *
     * @return The event comment, or {@code null} if it wasn't specified.
     */
    public String getComment() {
        return comment;
    }

    /**
     * Gets the reconnection delay requested by the event source.
     *
     * @return The reconnection delay, or {@code null} if it wasn't specified.
     */
    public Duration getRetryAfter() {
        return retryAfter;
    }

    private void setId(String id) {
        this.id = id;
    }

    private void setEvent(String event) {
        this.event = event;
    }

    private void setData(T data) {
        this.data = data;
    }

    private void setComment(String comment) {
        this.comment = comment;
    }

    private void setRetryAfter(Duration retryAfter) {
        this.retryAfter = retryAfter;
    }
}
