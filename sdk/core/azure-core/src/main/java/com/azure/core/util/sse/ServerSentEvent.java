// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.util.sse;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

/**
 * Represents a single Server-Sent Event (SSE) parsed from a {@code text/event-stream} response body.
 * <p>
 * The field semantics mirror the
 * <a href="https://html.spec.whatwg.org/multipage/server-sent-events.html#parsing-an-event-stream">WHATWG
 * event stream parsing algorithm</a>:
 * </p>
 * <ul>
 *     <li>{@code id} - the last event identifier (echoed back via {@code Last-Event-Id} on reconnect).</li>
 *     <li>{@code event} - the event type name; defaults to {@code "message"} when not specified.</li>
 *     <li>{@code data} - the payload, split into the individual {@code data:} lines that composed the event.</li>
 *     <li>{@code comment} - the value of a leading colon ({@code :}) line, typically a keep-alive.</li>
 *     <li>{@code retry} - the reconnection delay the client should wait before attempting to reconnect.</li>
 * </ul>
 * <p>
 * Instances are immutable and are produced by {@link ServerSentEventParser}. Typed deserialization of the
 * {@code data} payload (which is usually JSON) is left to the caller, typically generated client code.
 * </p>
 *
 * @see ServerSentEventParser
 */
public final class ServerSentEvent {
    private final String id;
    private final String event;
    private final List<String> data;
    private final String comment;
    private final Duration retryAfter;

    ServerSentEvent(String id, String event, List<String> data, String comment, Duration retryAfter) {
        this.id = id;
        this.event = event;
        this.data = data == null ? null : Collections.unmodifiableList(data);
        this.comment = comment;
        this.retryAfter = retryAfter;
    }

    /**
     * Gets the event identifier.
     * <p>
     * Contains the value of the SSE {@code "id"} field. This field is optional and may return {@code null} if the
     * event identifier is not specified.
     *
     * @return The event id, or {@code null} if not set.
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the event type name.
     * <p>
     * Contains the value of the SSE {@code "event"} field. When the event stream does not specify an event type this
     * defaults to {@code "message"}.
     *
     * @return The event type name.
     */
    public String getEvent() {
        return event;
    }

    /**
     * Gets the event data lines.
     * <p>
     * Contains the values of the SSE {@code "data"} fields. A single event may carry multiple {@code data:} lines; the
     * WHATWG specification concatenates them with a newline to form the payload (see {@link #getDataString()}). This
     * field is optional and may return {@code null} if the event carries no data.
     *
     * @return An unmodifiable list of the raw {@code data:} lines, or {@code null} if the event has no data.
     */
    public List<String> getData() {
        return data;
    }

    /**
     * Gets the comment associated with the event.
     * <p>
     * Contains the value of a leading colon ({@code :}) line. This field is optional and may return {@code null} if the
     * event has no comment.
     *
     * @return The comment associated with the event, or {@code null} if not set.
     */
    public String getComment() {
        return comment;
    }

    /**
     * Gets the reconnection delay the client should wait before attempting to reconnect after the connection to the
     * event source is lost.
     * <p>
     * Contains the value of the SSE {@code "retry"} field. This field is optional and may return {@code null} if no
     * value has been set.
     *
     * @return The reconnection delay, or {@code null} if not set.
     */
    public Duration getRetryAfter() {
        return retryAfter;
    }

    /**
     * Gets the event data as a single string.
     * <p>
     * Convenience method that joins the individual {@link #getData() data lines} with a newline ({@code "\n"}), as
     * described by the WHATWG event stream parsing algorithm. Returns an empty string if the event carries no data.
     *
     * @return The event data lines joined with {@code "\n"}, or an empty string if the event has no data.
     */
    public String getDataString() {
        if (data == null || data.isEmpty()) {
            return "";
        }
        return String.join("\n", data);
    }
}
