// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.implementation.http.rest.sse.generated;

import com.azure.core.http.ServerSentEvent;
import com.azure.core.util.BinaryData;
import com.azure.core.util.IterableStream;
import com.azure.core.util.ServerSentEventUtils;
import com.azure.json.JsonProviders;
import com.azure.json.JsonReader;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SynchronousSink;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

final class ServiceStreamEvents {
    private ServiceStreamEvents() {
    }

    static IterableStream<ServiceStreamEvent> toIterableStream(BinaryData body) {
        ServiceStreamEventIterable iterable = new ServiceStreamEventIterable(body);
        return new CloseableServiceStreamEventIterableStream(iterable);
    }

    static Flux<ServiceStreamEvent> toFlux(BinaryData body) {
        return ServerSentEventUtils.toFlux(body)
            .handle(
                (ServerSentEvent event, SynchronousSink<ServiceStreamEvent> sink) -> map(event).ifPresent(sink::next))
            .takeUntil(ServiceStreamEvent::isTerminal);
    }

    private static Optional<ServiceStreamEvent> map(ServerSentEvent event) {
        if (event.getData() == null) {
            return Optional.empty();
        }

        String data = String.join("\n", event.getData());
        if ("[DONE]".equals(data)) {
            return Optional.of(ServiceStreamEvent.terminal());
        }

        switch (event.getEvent()) {
            case "userLogin":
                return Optional.of(ServiceStreamEvent.ofUserLogin(deserialize(data, UserLogin::fromJson)));

            case "userLogout":
                return Optional.of(ServiceStreamEvent.ofUserLogout(deserialize(data, UserLogout::fromJson)));

            case "stockUpdate":
                return Optional.of(ServiceStreamEvent.ofStockUpdate(deserialize(data, StockUpdate::fromJson)));

            case "systemAlert":
                return Optional.of(ServiceStreamEvent.ofSystemAlert(deserialize(data, SystemAlert::fromJson)));

            default:
                return Optional.empty();
        }
    }

    private static <T> T deserialize(String data, JsonDeserializer<T> deserializer) {
        try (JsonReader reader = JsonProviders.createReader(data.getBytes(StandardCharsets.UTF_8))) {
            return deserializer.deserialize(reader);
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    @FunctionalInterface
    private interface JsonDeserializer<T> {
        T deserialize(JsonReader reader) throws IOException;
    }

    private static final class ServiceStreamEventIterable implements Iterable<ServiceStreamEvent>, Closeable {
        private final Stream<ServerSentEvent> serverSentEvents;
        private final Iterator<ServerSentEvent> serverSentEventIterator;
        private final AtomicBoolean iteratorCreated = new AtomicBoolean();
        private final AtomicBoolean closed = new AtomicBoolean();

        private ServiceStreamEventIterable(BinaryData body) {
            this.serverSentEvents = ServerSentEventUtils.toStream(body);
            this.serverSentEventIterator = serverSentEvents.iterator();
        }

        @Override
        public Iterator<ServiceStreamEvent> iterator() {
            if (!iteratorCreated.compareAndSet(false, true)) {
                throw new IllegalStateException("The SSE stream can only be consumed once.");
            }
            return new EventIterator();
        }

        @Override
        public void close() {
            if (!closed.compareAndSet(false, true)) {
                return;
            }
            serverSentEvents.close();
        }

        private final class EventIterator implements Iterator<ServiceStreamEvent> {
            private ServiceStreamEvent next;
            private boolean nextLoaded;

            @Override
            public boolean hasNext() {
                if (!nextLoaded) {
                    next = readNext();
                    nextLoaded = true;
                }
                return next != null;
            }

            @Override
            public ServiceStreamEvent next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                ServiceStreamEvent current = next;
                next = null;
                nextLoaded = false;
                if (current.isTerminal()) {
                    close();
                }
                return current;
            }

            private ServiceStreamEvent readNext() {
                try {
                    while (!closed.get() && serverSentEventIterator.hasNext()) {
                        ServerSentEvent event = serverSentEventIterator.next();
                        Optional<ServiceStreamEvent> mappedEvent = map(event);
                        if (mappedEvent.isPresent()) {
                            return mappedEvent.get();
                        }
                    }
                    close();
                    return null;
                } catch (RuntimeException exception) {
                    close();
                    throw exception;
                }
            }
        }
    }

    private static final class CloseableServiceStreamEventIterableStream extends IterableStream<ServiceStreamEvent>
        implements Closeable {

        private final ServiceStreamEventIterable iterable;

        private CloseableServiceStreamEventIterableStream(ServiceStreamEventIterable iterable) {
            super(iterable);
            this.iterable = iterable;
        }

        @Override
        public Stream<ServiceStreamEvent> stream() {
            return super.stream().onClose(this::close);
        }

        @Override
        public void close() {
            iterable.close();
        }
    }
}
