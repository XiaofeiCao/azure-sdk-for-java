// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.http;

import com.azure.core.implementation.util.ServerSentEventHelper;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a server-sent event.
 *
 * <p>A server-sent event may contain an identifier, event name, one or more data lines, a comment, and a retry
 * interval. All fields are optional.</p>
 *
 * @see <a href="https://html.spec.whatwg.org/multipage/server-sent-events.html#parsing-an-event-stream">
 * Parsing an event stream</a>
 */
public final class ServerSentEvent {
    private String id;
    private String event;
    private List<String> data;
    private String comment;
    private Duration retryAfter;

    static {
        ServerSentEventHelper.setAccessor(new ServerSentEventHelper.ServerSentEventAccessor() {
            @Override
            public void setId(ServerSentEvent serverSentEvent, String id) {
                serverSentEvent.setId(id);
            }

            @Override
            public void setEvent(ServerSentEvent serverSentEvent, String event) {
                serverSentEvent.setEvent(event);
            }

            @Override
            public void setData(ServerSentEvent serverSentEvent, List<String> data) {
                serverSentEvent.setData(data);
            }

            @Override
            public void setComment(ServerSentEvent serverSentEvent, String comment) {
                serverSentEvent.setComment(comment);
            }

            @Override
            public void setRetryAfter(ServerSentEvent serverSentEvent, Duration retryAfter) {
                serverSentEvent.setRetryAfter(retryAfter);
            }

            @Override
            public Duration getRetryAfter(ServerSentEvent serverSentEvent) {
                return serverSentEvent.getRetryAfter();
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
     * Gets the event data lines.
     *
     * @return An unmodifiable list of event data lines, or {@code null} if event data wasn't specified.
     */
    public List<String> getData() {
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

    private Duration getRetryAfter() {
        return retryAfter;
    }

    private void setId(String id) {
        this.id = id;
    }

    private void setEvent(String event) {
        this.event = event;
    }

    private void setData(List<String> data) {
        this.data = data == null ? null : Collections.unmodifiableList(new ArrayList<>(data));
    }

    private void setComment(String comment) {
        this.comment = comment;
    }

    private void setRetryAfter(Duration retryAfter) {
        this.retryAfter = retryAfter;
    }
}
