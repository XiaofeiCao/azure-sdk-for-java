// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.search.documents.knowledgebases;

import com.azure.core.http.HttpHeaderName;
import com.azure.core.http.rest.StreamResponse;
import com.azure.core.util.logging.ClientLogger;
import com.azure.search.documents.knowledgebases.models.KnowledgeBaseRetrievalStreamEvent;
import reactor.core.publisher.Flux;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class KnowledgeBaseRetrievalSseParser {
    private static final String EVENT_STREAM_CONTENT_TYPE = "text/event-stream";
    private static final ClientLogger LOGGER = new ClientLogger(KnowledgeBaseRetrievalSseParser.class);

    private final java.nio.charset.CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder()
        .onMalformedInput(CodingErrorAction.REPORT)
        .onUnmappableCharacter(CodingErrorAction.REPORT);
    private final StringBuilder line = new StringBuilder();
    private final StringBuilder data = new StringBuilder();
    private byte[] pendingBytes = new byte[0];
    private String lastEventId;
    private String eventName;
    private Long retry;
    private boolean skipLineFeed;
    private boolean firstCharacter = true;
    private boolean terminalSeen;

    static Flux<KnowledgeBaseRetrievalStreamEvent> parse(StreamResponse response) {
        return Flux.defer(() -> {
            if (response.getStatusCode() == 204) {
                response.close();
                return Flux.empty();
            }

            String contentType = response.getHeaders().getValue(HttpHeaderName.CONTENT_TYPE);
            if (!isEventStream(contentType)) {
                response.close();
                return Flux.error(new IllegalStateException(
                    "Expected response Content-Type 'text/event-stream' but received '" + contentType + "'."));
            }

            KnowledgeBaseRetrievalSseParser parser = new KnowledgeBaseRetrievalSseParser();
            return response.getValue()
                .concatMapIterable(parser::accept)
                .concatWith(Flux.defer(() -> Flux.fromIterable(parser.finish())))
                .takeUntil(KnowledgeBaseRetrievalStreamEvent::isTerminal);
        });
    }

    List<KnowledgeBaseRetrievalStreamEvent> accept(ByteBuffer bytes) {
        ByteBuffer input = ByteBuffer.allocate(pendingBytes.length + bytes.remaining());
        input.put(pendingBytes);
        input.put(bytes.duplicate());
        input.flip();

        CharBuffer chars
            = CharBuffer.allocate(Math.max(1, (int) Math.ceil(input.remaining() * decoder.maxCharsPerByte()) + 1));
        try {
            java.nio.charset.CoderResult result = decoder.decode(input, chars, false);
            if (result.isError()) {
                chars.flip();
                List<KnowledgeBaseRetrievalStreamEvent> events = process(chars);
                if (terminalSeen) {
                    pendingBytes = new byte[0];
                    return events;
                }
                result.throwException();
            }
        } catch (CharacterCodingException exception) {
            throw LOGGER.logExceptionAsError(
                new IllegalArgumentException("The server-sent event stream contains invalid UTF-8.", exception));
        }

        pendingBytes = new byte[input.remaining()];
        input.get(pendingBytes);
        chars.flip();
        return process(chars);
    }

    List<KnowledgeBaseRetrievalStreamEvent> finish() {
        if (terminalSeen) {
            return Collections.emptyList();
        }

        CharBuffer chars
            = CharBuffer.allocate(Math.max(1, (int) Math.ceil(pendingBytes.length * decoder.maxCharsPerByte()) + 1));
        try {
            java.nio.charset.CoderResult result = decoder.decode(ByteBuffer.wrap(pendingBytes), chars, true);
            if (result.isError()) {
                result.throwException();
            }
            result = decoder.flush(chars);
            if (result.isError()) {
                result.throwException();
            }
        } catch (CharacterCodingException exception) {
            throw LOGGER.logExceptionAsError(
                new IllegalArgumentException("The server-sent event stream contains invalid UTF-8.", exception));
        }
        pendingBytes = new byte[0];
        chars.flip();
        return process(chars);
    }

    private List<KnowledgeBaseRetrievalStreamEvent> process(CharBuffer chars) {
        if (!chars.hasRemaining()) {
            return Collections.emptyList();
        }

        List<KnowledgeBaseRetrievalStreamEvent> events = new ArrayList<>();
        while (chars.hasRemaining()) {
            char character = chars.get();
            if (firstCharacter) {
                firstCharacter = false;
                if (character == '\uFEFF') {
                    continue;
                }
            }
            if (skipLineFeed) {
                skipLineFeed = false;
                if (character == '\n') {
                    continue;
                }
            }

            if (character == '\r') {
                processLine(events);
                skipLineFeed = true;
            } else if (character == '\n') {
                processLine(events);
            } else {
                line.append(character);
            }
            if (terminalSeen) {
                break;
            }
        }
        return events;
    }

    private void processLine(List<KnowledgeBaseRetrievalStreamEvent> events) {
        String value = line.toString();
        line.setLength(0);
        if (value.isEmpty()) {
            dispatch(events);
            return;
        }
        if (value.charAt(0) == ':') {
            return;
        }

        int separator = value.indexOf(':');
        String field = separator < 0 ? value : value.substring(0, separator);
        String fieldValue = separator < 0 ? "" : value.substring(separator + 1);
        if (!fieldValue.isEmpty() && fieldValue.charAt(0) == ' ') {
            fieldValue = fieldValue.substring(1);
        }

        switch (field) {
            case "event":
                eventName = fieldValue;
                break;

            case "data":
                data.append(fieldValue).append('\n');
                break;

            case "id":
                if (fieldValue.indexOf('\0') < 0) {
                    lastEventId = fieldValue;
                }
                break;

            case "retry":
                if (isDigits(fieldValue)) {
                    try {
                        retry = Long.parseLong(fieldValue);
                    } catch (NumberFormatException ignored) {
                        // Values outside the Long range are invalid retry fields.
                    }
                }
                break;

            default:
                break;
        }
    }

    private void dispatch(List<KnowledgeBaseRetrievalStreamEvent> events) {
        if (data.length() == 0) {
            eventName = null;
            return;
        }

        data.setLength(data.length() - 1);
        String resolvedEventName = eventName == null || eventName.isEmpty() ? "message" : eventName;
        KnowledgeBaseRetrievalStreamEvent event
            = KnowledgeBaseRetrievalStreamEvent.fromEvent(lastEventId, resolvedEventName, retry, data.toString());
        events.add(event);
        terminalSeen = event.isTerminal();
        data.setLength(0);
        eventName = null;
    }

    private static boolean isEventStream(String contentType) {
        if (contentType == null) {
            return false;
        }
        int parameter = contentType.indexOf(';');
        String mediaType = parameter < 0 ? contentType : contentType.substring(0, parameter);
        return EVENT_STREAM_CONTENT_TYPE.equalsIgnoreCase(mediaType.trim());
    }

    private static boolean isDigits(String value) {
        if (value.isEmpty()) {
            return false;
        }
        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) < '0' || value.charAt(i) > '9') {
                return false;
            }
        }
        return true;
    }
}
