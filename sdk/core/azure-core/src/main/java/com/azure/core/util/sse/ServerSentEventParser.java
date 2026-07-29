// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.util.sse;

import com.azure.core.util.IterableStream;
import com.azure.core.util.logging.ClientLogger;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Parser that frames a {@code text/event-stream} (Server-Sent Events) byte stream into individual
 * {@link ServerSentEvent} instances.
 * <p>
 * The parser implements the
 * <a href="https://html.spec.whatwg.org/multipage/server-sent-events.html#parsing-an-event-stream">WHATWG event
 * stream parsing algorithm</a>: events are separated by a blank line, {@code data:} lines are collected, the
 * {@code event:} type defaults to {@code "message"}, and the {@code id:}, {@code retry:} and comment ({@code :})
 * fields are recognized. It is the single source of truth for SSE framing, feeding both the reactive
 * ({@link #parse(Flux)}) and blocking ({@link #parse(InputStream)}) consumers so their behavior stays identical.
 * <p>
 * Typed deserialization of the {@code data} payload (which is usually JSON) is intentionally left to the caller,
 * typically generated client code, so this class carries no service-specific knowledge.
 *
 * @see ServerSentEvent
 */
public final class ServerSentEventParser {
    private static final ClientLogger LOGGER = new ClientLogger(ServerSentEventParser.class);

    private ServerSentEventParser() {
    }

    /**
     * Frames an asynchronous byte stream into a stream of {@link ServerSentEvent events}.
     * <p>
     * The source {@link Flux} may split individual events across {@link ByteBuffer} boundaries; the parser buffers
     * incomplete lines and events until they are complete. Events are emitted in the order they appear in the stream.
     *
     * @param source The source of {@code text/event-stream} bytes, typically
     * {@code response.getValue().toFluxByteBuffer()}.
     * @return A {@link Flux} that emits each parsed {@link ServerSentEvent}.
     * @throws NullPointerException If {@code source} is {@code null}.
     */
    public static Flux<ServerSentEvent> parse(Flux<ByteBuffer> source) {
        if (source == null) {
            return Flux.error(new NullPointerException("'source' cannot be null."));
        }

        return Flux.defer(() -> {
            // 'concatMap' processes the source sequentially, so a single mutable state instance is safe.
            SseParserState state = new SseParserState();
            return source.concatMap(buffer -> Flux.fromIterable(state.feed(buffer)))
                .concatWith(Flux.defer(() -> Flux.fromIterable(state.flush())));
        });
    }

    /**
     * Frames a blocking byte stream into an {@link IterableStream} of {@link ServerSentEvent events}.
     * <p>
     * This is the synchronous adapter used by blocking clients; it shares the exact framing semantics of
     * {@link #parse(Flux)}. The passed {@link InputStream} is fully read and closed by this method.
     *
     * @param source The source of {@code text/event-stream} bytes, typically {@code response.getValue().toStream()}.
     * @return An {@link IterableStream} over the parsed {@link ServerSentEvent events}.
     * @throws NullPointerException If {@code source} is {@code null}.
     * @throws UncheckedIOException If an error occurs while reading the stream.
     */
    public static IterableStream<ServerSentEvent> parse(InputStream source) {
        if (source == null) {
            throw LOGGER.logExceptionAsError(new NullPointerException("'source' cannot be null."));
        }

        SseParserState state = new SseParserState();
        List<ServerSentEvent> events = new ArrayList<>();
        byte[] readBuffer = new byte[8192];
        try (InputStream stream = source) {
            int read;
            while ((read = stream.read(readBuffer)) != -1) {
                if (read > 0) {
                    events.addAll(state.feed(ByteBuffer.wrap(readBuffer, 0, read)));
                }
            }
            events.addAll(state.flush());
        } catch (IOException e) {
            throw LOGGER.logExceptionAsError(new UncheckedIOException(e));
        }
        return new IterableStream<>(events);
    }

    /**
     * Mutable, single-threaded state machine that accumulates bytes and dispatches {@link ServerSentEvent events} on
     * blank-line boundaries. A single instance must be fed sequentially (never concurrently).
     */
    private static final class SseParserState {
        private static final String DEFAULT_EVENT = "message";

        // Bytes of the current, not-yet-terminated line. Decoding is deferred until a full line is available so a
        // multi-byte UTF-8 character split across buffers is never decoded partially.
        private byte[] lineBytes = new byte[256];
        private int lineLength = 0;
        // If the previous buffer ended on a lone CR, a following LF must be swallowed (CRLF terminator).
        private boolean pendingCr = false;

        // Fields of the event currently being assembled.
        private String id;
        private String event;
        private List<String> data;
        private String comment;
        private Duration retryAfter;
        private boolean hasFields;

        List<ServerSentEvent> feed(ByteBuffer buffer) {
            List<ServerSentEvent> dispatched = new ArrayList<>();
            while (buffer.hasRemaining()) {
                byte b = buffer.get();

                if (pendingCr) {
                    pendingCr = false;
                    if (b == '\n') {
                        // Swallow the LF that completes a CRLF terminator; the line was already processed.
                        continue;
                    }
                }

                if (b == '\n') {
                    processLine(decodeLine(), dispatched);
                } else if (b == '\r') {
                    processLine(decodeLine(), dispatched);
                    pendingCr = true;
                } else {
                    appendByte(b);
                }
            }
            return dispatched;
        }

        List<ServerSentEvent> flush() {
            List<ServerSentEvent> dispatched = new ArrayList<>();
            // Process any trailing partial line (a stream may end without a final line terminator).
            if (lineLength > 0) {
                processLine(decodeLine(), dispatched);
            }
            // Dispatch a final event that was not terminated by a blank line so no data is silently dropped.
            ServerSentEvent pending = buildEvent();
            if (pending != null) {
                dispatched.add(pending);
            }
            return dispatched;
        }

        private void appendByte(byte b) {
            if (lineLength == lineBytes.length) {
                byte[] grown = new byte[lineBytes.length * 2];
                System.arraycopy(lineBytes, 0, grown, 0, lineLength);
                lineBytes = grown;
            }
            lineBytes[lineLength++] = b;
        }

        private String decodeLine() {
            String line = new String(lineBytes, 0, lineLength, java.nio.charset.StandardCharsets.UTF_8);
            lineLength = 0;
            return line;
        }

        private void processLine(String line, List<ServerSentEvent> dispatched) {
            if (line.isEmpty()) {
                ServerSentEvent completed = buildEvent();
                if (completed != null) {
                    dispatched.add(completed);
                }
                return;
            }

            int colonIdx = line.indexOf(':');
            if (colonIdx == 0) {
                comment = stripLeadingSpace(line.substring(1));
                hasFields = true;
                return;
            }

            String field;
            String value;
            if (colonIdx < 0) {
                field = line;
                value = "";
            } else {
                field = line.substring(0, colonIdx);
                value = stripLeadingSpace(line.substring(colonIdx + 1));
            }

            switch (field) {
                case "event":
                    event = value;
                    hasFields = true;
                    break;

                case "data":
                    if (data == null) {
                        data = new ArrayList<>();
                    }
                    data.add(value);
                    hasFields = true;
                    break;

                case "id":
                    // Per WHATWG the id is ignored if it contains a NUL character.
                    if (value.indexOf('\u0000') < 0) {
                        id = value;
                        hasFields = true;
                    }
                    break;

                case "retry":
                    if (isDigitsOnly(value)) {
                        retryAfter = Duration.ofMillis(Long.parseLong(value));
                        hasFields = true;
                    }
                    break;

                default:
                    // Ignore unknown fields.
                    break;
            }
        }

        /**
         * Builds and resets the current event. Returns {@code null} (dispatching nothing) when the block carried no
         * meaningful content, mirroring the reference behavior where a bare keep-alive block is not surfaced.
         */
        private ServerSentEvent buildEvent() {
            if (!hasFields) {
                return null;
            }

            String eventType = event == null ? DEFAULT_EVENT : event;
            boolean meaningful = data != null || !DEFAULT_EVENT.equals(eventType);
            ServerSentEvent result = meaningful ? new ServerSentEvent(id, eventType, data, comment, retryAfter) : null;

            id = null;
            event = null;
            data = null;
            comment = null;
            retryAfter = null;
            hasFields = false;
            return result;
        }

        private static String stripLeadingSpace(String value) {
            return (!value.isEmpty() && value.charAt(0) == ' ') ? value.substring(1) : value;
        }

        private static boolean isDigitsOnly(String str) {
            if (str.isEmpty()) {
                return false;
            }
            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                if (c < '0' || c > '9') {
                    return false;
                }
            }
            return true;
        }
    }
}
