// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.http;

import java.time.Duration;
import java.util.List;

/**
 * Represents an event received from a {@code text/event-stream} response.
 *
 * <p>The event follows the Server-Sent Events processing model defined by the
 * <a href="https://html.spec.whatwg.org/multipage/server-sent-events.html#parsing-an-event-stream">WHATWG HTML
 * specification</a>.</p>
 */
public final class ServerSentEvent {
    private String id;
    private String event;
    private List<String> data;
    private String comment;
    private Duration retryAfter;

    /**
     * Creates an empty server-sent event.
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
     * Gets the event type.
     *
     * @return The event type. Unnamed events use {@code message}.
     */
    public String getEvent() {
        return event;
    }

    /**
     * Gets the event data lines.
     *
     * @return The event data lines, or {@code null} if the event didn't contain data.
     */
    public List<String> getData() {
        return data;
    }

    /**
     * Gets the last comment in the event block.
     *
     * @return The comment, or {@code null} if the event didn't contain a comment.
     */
    public String getComment() {
        return comment;
    }

    Duration getRetryAfter() {
        return retryAfter;
    }

    void setId(String id) {
        this.id = id;
    }

    void setEvent(String event) {
        this.event = event;
    }

    void setData(List<String> data) {
        this.data = data;
    }

    void setComment(String comment) {
        this.comment = comment;
    }

    void setRetryAfter(Duration retryAfter) {
        this.retryAfter = retryAfter;
    }
}
