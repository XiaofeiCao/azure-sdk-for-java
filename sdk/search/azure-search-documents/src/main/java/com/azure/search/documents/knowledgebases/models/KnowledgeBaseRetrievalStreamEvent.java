// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.search.documents.knowledgebases.models;

import com.azure.json.JsonProviders;
import com.azure.json.JsonReader;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

/**
 * Marker interface for events emitted by a streaming knowledge base retrieval operation.
 *
 * <p>Use {@code instanceof} to identify and access each event payload. The
 * {@link KnowledgeBaseStreamErrorEvent} and {@link KnowledgeBaseResponseCompletedEvent} payloads are terminal events.
 * Event names introduced after this SDK version are represented by
 * {@link UnknownKnowledgeBaseRetrievalStreamEvent}.</p>
 */
public interface KnowledgeBaseRetrievalStreamEvent {
    /**
     * Returns whether this event terminates the stream.
     *
     * @return {@code true} when this event terminates the stream.
     */
    default boolean isTerminal() {
        return false;
    }

    /**
     * Deserializes a knowledge base retrieval stream event according to its server-sent event name.
     *
     * @param eventName the server-sent event name.
     * @param data the JSON event data.
     * @return the typed stream event, or {@code null} when the event name or data is {@code null}.
     * @throws UncheckedIOException if known event data cannot be deserialized.
     */
    static KnowledgeBaseRetrievalStreamEvent fromEvent(String eventName, String data) {
        if (eventName == null || data == null) {
            return null;
        }

        boolean knownEvent = "retrieval.started".equals(eventName)
            || "activity.started".equals(eventName)
            || "activity.completed".equals(eventName)
            || "answer.completed".equals(eventName)
            || "references.completed".equals(eventName)
            || "error".equals(eventName)
            || "response.completed".equals(eventName);
        if (!knownEvent) {
            return new UnknownKnowledgeBaseRetrievalStreamEvent(eventName, data);
        }

        try (JsonReader reader = JsonProviders.createReader(data.getBytes(StandardCharsets.UTF_8))) {
            switch (eventName) {
                case "retrieval.started":
                    return KnowledgeBaseRetrievalStartedEvent.fromJson(reader);

                case "activity.started":
                    return KnowledgeBaseActivityStartedEvent.fromJson(reader);

                case "activity.completed":
                    return KnowledgeBaseActivityRecord.fromJson(reader);

                case "answer.completed":
                    return KnowledgeBaseAnswerCompletedEvent.fromJson(reader);

                case "references.completed":
                    return KnowledgeBaseReferencesCompletedEvent.fromJson(reader);

                case "error":
                    return KnowledgeBaseStreamErrorEvent.fromJson(reader);

                case "response.completed":
                    return KnowledgeBaseResponseCompletedEvent.fromJson(reader);

                default:
                    throw new IllegalStateException("Unhandled known event name: " + eventName);
            }
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

}
