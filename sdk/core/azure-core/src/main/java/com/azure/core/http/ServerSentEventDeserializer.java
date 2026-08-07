// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.http;

/**
 * Deserializes server-sent event data.
 *
 * @param <T> The type of the deserialized event data.
 */
@FunctionalInterface
public interface ServerSentEventDeserializer<T> {
    /**
     * Deserializes server-sent event data.
     *
     * @param event The event name.
     * @param data The event data.
     * @return The deserialized event data, or {@code null} if the event isn't supported.
     * @throws RuntimeException If the event data cannot be deserialized.
     */
    T deserialize(String event, String data);
}
