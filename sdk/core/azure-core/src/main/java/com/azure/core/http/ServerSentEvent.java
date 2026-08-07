// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.http;

import com.azure.core.implementation.util.ServerSentEventHelper;

import java.time.Duration;

/**
 * Represents a server-sent event with a typed data payload.
 *
 * <p>An emitted server-sent event contains data and may expose an identifier, event name, comment, and retry interval.
 * The identifier and retry interval represent the effective stream state when the event was dispatched, including
 * values inherited from earlier metadata-only blocks.</p>
 *
 * <p>This type exposes the metadata needed for caller-managed reconnection, but Azure Core doesn't automatically
 * reconnect an event stream. Callers can use the latest emitted event's {@link #getId()} as the next request's
 * {@code Last-Event-Id} value and {@link #getRetryAfter()} as the reconnect delay. Metadata-only updates received
 * after the latest emitted event aren't exposed when the stream terminates.</p>
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
     * Gets the effective last-event identifier when this event was dispatched.
     *
     * @return The effective last-event identifier, {@code null} if no valid {@code id} field was received before this
     * event, or an empty string if an empty {@code id} field reset the identifier. An empty identifier should not be
     * sent as a {@code Last-Event-Id} request header.
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the event name.
     *
     * @return The event name, or {@code message} if no non-empty {@code event} field was specified.
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
     * Gets the effective reconnection delay when this event was dispatched.
     *
     * @return The latest valid reconnection delay received before this event, or {@code null} if no valid
     * {@code retry} field was received.
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
