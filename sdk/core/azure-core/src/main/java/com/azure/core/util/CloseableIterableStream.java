// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.util;

import reactor.core.publisher.Flux;
import reactor.core.Exceptions;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * An {@link IterableStream} that closes a resource when it is no longer needed.
 *
 * <p>
 * The resource is closed at most once. Closing this stream doesn't cancel consumption that is already in progress and
 * doesn't prevent subsequent calls to {@link #stream()} or {@link #iterator()}. Callers must not consume this stream
 * after its resource has been closed.
 * </p>
 *
 * <p>
 * <strong>Code sample using try-with-resources</strong>
 * </p>
 *
 * <!-- src_embed com.azure.core.util.closeableIterableStream -->
 * <pre>
 * try &#40;CloseableIterableStream&lt;Integer&gt; iterableStream =
 *     new CloseableIterableStream&lt;&gt;&#40;Flux.just&#40;1, 2, 3&#41;, &#40;&#41; -&gt; &#123; &#125;&#41;&#41; &#123;
 *     iterableStream.forEach&#40;System.out::println&#41;;
 * &#125;
 * </pre>
 * <!-- end com.azure.core.util.closeableIterableStream -->
 *
 * @param <T> The type of value in this {@link Iterable}.
 * @see Iterable
 */
public final class CloseableIterableStream<T> extends IterableStream<T> implements AutoCloseable {
    private final AutoCloseable closeable;
    private final AtomicBoolean closed = new AtomicBoolean();

    /**
     * Creates an instance with the given {@link Flux} and resource.
     *
     * @param flux Flux of items to iterate over.
     * @param closeable Resource to close when this stream is closed.
     * @throws NullPointerException If {@code flux} or {@code closeable} is {@code null}.
     */
    public CloseableIterableStream(Flux<T> flux, AutoCloseable closeable) {
        super(flux);
        this.closeable = Objects.requireNonNull(closeable, "'closeable' cannot be null.");
    }

    /**
     * Creates an instance with the given {@link Iterable} and resource.
     *
     * @param iterable Collection of items to iterate over.
     * @param closeable Resource to close when this stream is closed.
     * @throws NullPointerException If {@code iterable} or {@code closeable} is {@code null}.
     */
    public CloseableIterableStream(Iterable<T> iterable, AutoCloseable closeable) {
        super(iterable);
        this.closeable = Objects.requireNonNull(closeable, "'closeable' cannot be null.");
    }

    /**
     * Closes the resource associated with this stream. The resource is closed at most once.
     *
     * @throws RuntimeException If closing the resource fails.
     */
    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            try {
                closeable.close();
            } catch (Exception exception) {
                throw Exceptions.propagate(exception);
            }
        }
    }
}
