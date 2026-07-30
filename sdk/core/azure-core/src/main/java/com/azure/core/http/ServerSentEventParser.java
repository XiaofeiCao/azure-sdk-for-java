// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Incrementally parses a UTF-8 server-sent event stream.
 */
final class ServerSentEventParser {
    private static final String DEFAULT_EVENT = "message";

    private final ServerSentEventListener listener;
    private final ByteArrayOutputStream line = new ByteArrayOutputStream();
    private final List<String> data = new ArrayList<>();

    private String eventType;
    private String comment;
    private String lastEventId;
    private Duration retryAfter;
    private boolean previousCarriageReturn;
    private boolean hasFields;
    private boolean firstLine = true;

    ServerSentEventParser(ServerSentEventListener listener) {
        this(listener, null);
    }

    ServerSentEventParser(ServerSentEventListener listener, String lastEventId) {
        this.listener = listener;
        this.lastEventId = lastEventId;
    }

    void accept(ByteBuffer buffer) throws IOException {
        while (buffer.hasRemaining()) {
            int current = buffer.get() & 0xFF;
            if (previousCarriageReturn) {
                previousCarriageReturn = false;
                if (current == '\n') {
                    continue;
                }
            }

            if (current == '\r') {
                processLine();
                previousCarriageReturn = true;
            } else if (current == '\n') {
                processLine();
            } else {
                line.write(current);
            }
        }
    }

    void complete() throws IOException {
        line.reset();
        resetEvent();
        listener.onClose();
    }

    String getLastEventId() {
        return lastEventId;
    }

    Duration getRetryAfter() {
        return retryAfter;
    }

    private void processLine() throws IOException {
        String value = new String(line.toByteArray(), StandardCharsets.UTF_8);
        line.reset();
        if (firstLine) {
            firstLine = false;
            if (!value.isEmpty() && value.charAt(0) == '\uFEFF') {
                value = value.substring(1);
            }
        }

        if (value.isEmpty()) {
            dispatchEvent();
            return;
        }

        hasFields = true;
        if (value.charAt(0) == ':') {
            comment = removeOptionalSpace(value.substring(1));
            return;
        }

        int colonIndex = value.indexOf(':');
        String field = colonIndex < 0 ? value : value.substring(0, colonIndex);
        String fieldValue = colonIndex < 0 ? "" : removeOptionalSpace(value.substring(colonIndex + 1));

        switch (field) {
            case "event":
                eventType = fieldValue;
                break;

            case "data":
                data.add(fieldValue);
                break;

            case "id":
                if (fieldValue.indexOf('\0') < 0) {
                    lastEventId = fieldValue;
                }
                break;

            case "retry":
                if (isDigitsOnly(fieldValue)) {
                    try {
                        retryAfter = Duration.ofMillis(Long.parseLong(fieldValue));
                    } catch (NumberFormatException ignored) {
                        // Values outside the long range are invalid retry values.
                    }
                }
                break;

            default:
                break;
        }
    }

    private void dispatchEvent() throws IOException {
        if (!hasFields) {
            return;
        }

        ServerSentEvent event = new ServerSentEvent();
        event.setId(lastEventId);
        event.setEvent(eventType == null || eventType.isEmpty() ? DEFAULT_EVENT : eventType);
        event.setComment(comment);
        event.setRetryAfter(retryAfter);

        if (!data.isEmpty()) {
            event.setData(new ArrayList<>(data));
        }

        if (event.getData() != null) {
            listener.onEvent(event);
        }

        resetEvent();
    }

    private void resetEvent() {
        eventType = null;
        comment = null;
        data.clear();
        hasFields = false;
    }

    private static String removeOptionalSpace(String value) {
        return !value.isEmpty() && value.charAt(0) == ' ' ? value.substring(1) : value;
    }

    private static boolean isDigitsOnly(String value) {
        if (value.isEmpty()) {
            return false;
        }
        for (int i = 0; i < value.length(); i++) {
            char character = value.charAt(i);
            if (character < '0' || character > '9') {
                return false;
            }
        }
        return true;
    }
}
