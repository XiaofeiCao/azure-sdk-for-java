// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.util;

import com.azure.core.http.ServerSentEvent;
import com.azure.core.implementation.util.ServerSentEventHelper;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Utility methods for lazily decoding server-sent event streams.
 */
public final class ServerSentEventUtils {
    private static final String DEFAULT_EVENT = "message";

    private ServerSentEventUtils() {
    }

    /**
     * Lazily decodes a response body into server-sent events.
     *
     * <p>The returned stream must be closed to release the response body if event iteration stops before the response
     * body completes.</p>
     *
     * @param body The response body containing a server-sent event stream.
     * @return A sequential stream of server-sent events.
     * @throws NullPointerException If {@code body} is {@code null}.
     */
    public static Stream<ServerSentEvent> toStream(BinaryData body) {
        ServerSentEventIterator iterator = new ServerSentEventIterator(body);
        return StreamSupport
            .stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED | Spliterator.NONNULL), false)
            .onClose(iterator::close);
    }

    /**
     * Lazily decodes a response body into server-sent events.
     *
     * <p>Completion, cancellation, or an error closes the response body. Reading is performed on the bounded elastic
     * scheduler because response body streams may block.</p>
     *
     * @param body The response body containing a server-sent event stream.
     * @return A flux of server-sent events.
     * @throws NullPointerException If {@code body} is {@code null}.
     */
    public static Flux<ServerSentEvent> toFlux(BinaryData body) {
        Objects.requireNonNull(body, "'body' cannot be null.");

        return Flux.using(() -> new ServerSentEventIterator(body), iterator -> Flux.<ServerSentEvent>generate(sink -> {
            if (iterator.hasNext()) {
                sink.next(iterator.next());
            } else {
                sink.complete();
            }
        }), ServerSentEventIterator::close).subscribeOn(Schedulers.boundedElastic());
    }

    private static final class ServerSentEventIterator implements Iterator<ServerSentEvent> {
        private final BufferedReader reader;
        private boolean firstLine = true;
        private boolean closed;
        private boolean nextLoaded;
        private ServerSentEvent next;

        private ServerSentEventIterator(BinaryData body) {
            Objects.requireNonNull(body, "'body' cannot be null.");
            this.reader = new BufferedReader(new InputStreamReader(body.toStream(), StandardCharsets.UTF_8));
        }

        @Override
        public boolean hasNext() {
            if (closed && !nextLoaded) {
                return false;
            }

            if (!nextLoaded) {
                try {
                    next = readNext();
                    nextLoaded = true;
                    if (next == null) {
                        close();
                    }
                } catch (IOException exception) {
                    closeAfterFailure(exception);
                    throw new UncheckedIOException(exception);
                }
            }

            return next != null;
        }

        @Override
        public ServerSentEvent next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            ServerSentEvent current = next;
            next = null;
            nextLoaded = false;
            return current;
        }

        private ServerSentEvent readNext() throws IOException {
            while (!closed) {
                ServerSentEvent event = new ServerSentEvent();
                List<String> data = null;
                String line;

                while ((line = reader.readLine()) != null) {
                    if (firstLine) {
                        firstLine = false;
                        if (!line.isEmpty() && line.charAt(0) == '\uFEFF') {
                            line = line.substring(1);
                        }
                    }

                    if (line.isEmpty()) {
                        break;
                    }

                    if (line.charAt(0) == ':') {
                        ServerSentEventHelper.setComment(event, removeOptionalSpace(line.substring(1)));
                        continue;
                    }

                    int colonIndex = line.indexOf(':');
                    String field = colonIndex < 0 ? line : line.substring(0, colonIndex);
                    String value = colonIndex < 0 ? "" : removeOptionalSpace(line.substring(colonIndex + 1));

                    switch (field) {
                        case "event":
                            ServerSentEventHelper.setEvent(event, value);
                            break;

                        case "data":
                            if (data == null) {
                                data = new ArrayList<>();
                            }
                            data.add(value);
                            break;

                        case "id":
                            if (value.indexOf('\0') < 0) {
                                ServerSentEventHelper.setId(event, value);
                            }
                            break;

                        case "retry":
                            setRetryAfter(event, value);
                            break;

                        default:
                            break;
                    }
                }

                if (data != null) {
                    if (event.getEvent() == null || event.getEvent().isEmpty()) {
                        ServerSentEventHelper.setEvent(event, DEFAULT_EVENT);
                    }
                    ServerSentEventHelper.setData(event, data);
                    if (line == null) {
                        close();
                    }
                    return event;
                }

                if (line == null) {
                    return null;
                }
            }

            return null;
        }

        private void closeAfterFailure(IOException exception) {
            try {
                close();
            } catch (UncheckedIOException closeException) {
                exception.addSuppressed(closeException.getCause());
            }
        }

        private void close() {
            if (closed) {
                return;
            }

            closed = true;
            try {
                reader.close();
            } catch (IOException exception) {
                throw new UncheckedIOException(exception);
            }
        }
    }

    private static String removeOptionalSpace(String value) {
        return value.startsWith(" ") ? value.substring(1) : value;
    }

    private static void setRetryAfter(ServerSentEvent event, String value) {
        if (value.isEmpty()) {
            return;
        }

        for (int i = 0; i < value.length(); i++) {
            char character = value.charAt(i);
            if (character < '0' || character > '9') {
                return;
            }
        }

        try {
            ServerSentEventHelper.setRetryAfter(event, Duration.ofMillis(Long.parseLong(value)));
        } catch (NumberFormatException ignored) {
            // Ignore retry values that don't fit in a long.
        }
    }
}
