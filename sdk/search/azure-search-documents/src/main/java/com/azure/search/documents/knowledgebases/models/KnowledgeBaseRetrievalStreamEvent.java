// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.search.documents.knowledgebases.models;

import com.azure.json.JsonProviders;
import com.azure.json.JsonReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A typed event emitted by a streaming knowledge base retrieval operation.
 *
 * <p>This type includes the server-sent event metadata and raw data together with the deserialized payload. Use the
 * {@code is*} methods to identify a known event payload and the corresponding {@code as*} method to access it. For
 * event names introduced after this SDK version, none of the {@code is*} methods return {@code true}; use
 * {@link #getEvent()} and {@link #getData()} to inspect the event. The {@code error} and {@code response.completed}
 * variants are terminal events.</p>
 */
public final class KnowledgeBaseRetrievalStreamEvent {
    private final String id;
    private final String event;
    private final Long retry;
    private final String data;
    private final KnowledgeBaseRetrievalStartedEvent retrievalStarted;
    private final KnowledgeBaseActivityStartedEvent activityStarted;
    private final KnowledgeBaseActivityRecord activityCompleted;
    private final KnowledgeBaseAnswerCompletedEvent answerCompleted;
    private final List<KnowledgeBaseReference> referencesCompleted;
    private final KnowledgeBaseStreamErrorEvent error;
    private final KnowledgeBaseResponseCompletedEvent responseCompleted;

    private KnowledgeBaseRetrievalStreamEvent(String id, String event, Long retry, String data,
        KnowledgeBaseRetrievalStartedEvent retrievalStarted, KnowledgeBaseActivityStartedEvent activityStarted,
        KnowledgeBaseActivityRecord activityCompleted, KnowledgeBaseAnswerCompletedEvent answerCompleted,
        List<KnowledgeBaseReference> referencesCompleted, KnowledgeBaseStreamErrorEvent error,
        KnowledgeBaseResponseCompletedEvent responseCompleted) {
        this.id = id;
        this.event = event;
        this.retry = retry;
        this.data = data;
        this.retrievalStarted = retrievalStarted;
        this.activityStarted = activityStarted;
        this.activityCompleted = activityCompleted;
        this.answerCompleted = answerCompleted;
        this.referencesCompleted = referencesCompleted;
        this.error = error;
        this.responseCompleted = responseCompleted;
    }

    private static KnowledgeBaseRetrievalStreamEvent ofRetrievalStarted(String id, String event, Long retry,
        String data, KnowledgeBaseRetrievalStartedEvent value) {
        return new KnowledgeBaseRetrievalStreamEvent(id, event, retry, data,
            Objects.requireNonNull(value, "'value' cannot be null."), null, null, null, null, null, null);
    }

    private static KnowledgeBaseRetrievalStreamEvent ofActivityStarted(String id, String event, Long retry, String data,
        KnowledgeBaseActivityStartedEvent value) {
        return new KnowledgeBaseRetrievalStreamEvent(id, event, retry, data, null,
            Objects.requireNonNull(value, "'value' cannot be null."), null, null, null, null, null);
    }

    private static KnowledgeBaseRetrievalStreamEvent ofActivityCompleted(String id, String event, Long retry,
        String data, KnowledgeBaseActivityRecord value) {
        return new KnowledgeBaseRetrievalStreamEvent(id, event, retry, data, null, null,
            Objects.requireNonNull(value, "'value' cannot be null."), null, null, null, null);
    }

    private static KnowledgeBaseRetrievalStreamEvent ofAnswerCompleted(String id, String event, Long retry, String data,
        KnowledgeBaseAnswerCompletedEvent value) {
        return new KnowledgeBaseRetrievalStreamEvent(id, event, retry, data, null, null, null,
            Objects.requireNonNull(value, "'value' cannot be null."), null, null, null);
    }

    private static KnowledgeBaseRetrievalStreamEvent ofReferencesCompleted(String id, String event, Long retry,
        String data, List<KnowledgeBaseReference> value) {
        return new KnowledgeBaseRetrievalStreamEvent(id, event, retry, data, null, null, null, null,
            Objects.requireNonNull(value, "'value' cannot be null."), null, null);
    }

    private static KnowledgeBaseRetrievalStreamEvent ofError(String id, String event, Long retry, String data,
        KnowledgeBaseStreamErrorEvent value) {
        return new KnowledgeBaseRetrievalStreamEvent(id, event, retry, data, null, null, null, null, null,
            Objects.requireNonNull(value, "'value' cannot be null."), null);
    }

    private static KnowledgeBaseRetrievalStreamEvent ofResponseCompleted(String id, String event, Long retry,
        String data, KnowledgeBaseResponseCompletedEvent value) {
        return new KnowledgeBaseRetrievalStreamEvent(id, event, retry, data, null, null, null, null, null, null,
            Objects.requireNonNull(value, "'value' cannot be null."));
    }

    private static KnowledgeBaseRetrievalStreamEvent ofUnknown(String id, String event, Long retry, String data) {
        return new KnowledgeBaseRetrievalStreamEvent(id, event, retry, data, null, null, null, null, null, null, null);
    }

    /**
     * Deserializes a knowledge base retrieval stream event.
     *
     * @param eventName The server-sent event name.
     * @param data The JSON event data.
     * @return The typed stream event, or {@code null} when the event name or data is {@code null}.
     */
    public static KnowledgeBaseRetrievalStreamEvent fromEvent(String eventName, String data) {
        return fromEvent(null, eventName, null, data);
    }

    /**
     * Deserializes a knowledge base retrieval stream event and its protocol metadata.
     *
     * @param id The server-sent event identifier.
     * @param eventName The server-sent event name.
     * @param retry The server-sent event reconnection delay, which is retained as metadata but not acted upon.
     * @param data The JSON event data.
     * @return The typed stream event, or {@code null} when the event name or data is {@code null}.
     * @throws UncheckedIOException if the event data cannot be deserialized.
     */
    public static KnowledgeBaseRetrievalStreamEvent fromEvent(String id, String eventName, Long retry, String data) {
        if (eventName == null || data == null) {
            return null;
        }

        try (JsonReader reader = JsonProviders.createReader(data.getBytes(StandardCharsets.UTF_8))) {
            switch (eventName) {
                case "retrieval.started":
                    return ofRetrievalStarted(id, eventName, retry, data,
                        KnowledgeBaseRetrievalStartedEvent.fromJson(reader));

                case "activity.started":
                    return ofActivityStarted(id, eventName, retry, data,
                        KnowledgeBaseActivityStartedEvent.fromJson(reader));

                case "activity.completed":
                    return ofActivityCompleted(id, eventName, retry, data,
                        KnowledgeBaseActivityRecord.fromJson(reader));

                case "answer.completed":
                    return ofAnswerCompleted(id, eventName, retry, data,
                        KnowledgeBaseAnswerCompletedEvent.fromJson(reader));

                case "references.completed":
                    return ofReferencesCompleted(id, eventName, retry, data,
                        reader.readArray(KnowledgeBaseReference::fromJson));

                case "error":
                    return ofError(id, eventName, retry, data, KnowledgeBaseStreamErrorEvent.fromJson(reader));

                case "response.completed":
                    return ofResponseCompleted(id, eventName, retry, data,
                        KnowledgeBaseResponseCompletedEvent.fromJson(reader));

                default:
                    return ofUnknown(id, eventName, retry, data);
            }

        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    /**
     * Gets the server-sent event identifier.
     *
     * @return the event identifier, or {@code null} when none has been received.
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the server-sent event name.
     *
     * @return the event name.
     */
    public String getEvent() {
        return event;
    }

    /**
     * Gets the server-provided reconnection delay.
     *
     * <p>The value is exposed as metadata only. The client does not reconnect automatically.</p>
     *
     * @return the reconnection delay in milliseconds, or {@code null} when none has been received.
     */
    public Long getRetry() {
        return retry;
    }

    /**
     * Gets the raw event data.
     *
     * @return the raw event data.
     */
    public String getData() {
        return data;
    }

    /**
     * Gets the retrieval-started payload.
     *
     * @return the payload when this is a {@code retrieval.started} event.
     */
    public Optional<KnowledgeBaseRetrievalStartedEvent> retrievalStarted() {
        return Optional.ofNullable(retrievalStarted);
    }

    /**
     * Gets the activity-started payload.
     *
     * @return the payload when this is an {@code activity.started} event.
     */
    public Optional<KnowledgeBaseActivityStartedEvent> activityStarted() {
        return Optional.ofNullable(activityStarted);
    }

    /**
     * Gets the activity-completed payload.
     *
     * @return the payload when this is an {@code activity.completed} event.
     */
    public Optional<KnowledgeBaseActivityRecord> activityCompleted() {
        return Optional.ofNullable(activityCompleted);
    }

    /**
     * Gets the answer-completed payload.
     *
     * @return the payload when this is an {@code answer.completed} event.
     */
    public Optional<KnowledgeBaseAnswerCompletedEvent> answerCompleted() {
        return Optional.ofNullable(answerCompleted);
    }

    /**
     * Gets the references-completed payload.
     *
     * @return the payload when this is a {@code references.completed} event.
     */
    public Optional<List<KnowledgeBaseReference>> referencesCompleted() {
        return Optional.ofNullable(referencesCompleted);
    }

    /**
     * Gets the stream-error payload.
     *
     * @return the payload when this is an {@code error} event.
     */
    public Optional<KnowledgeBaseStreamErrorEvent> error() {
        return Optional.ofNullable(error);
    }

    /**
     * Gets the response-completed payload.
     *
     * @return the payload when this is a {@code response.completed} event.
     */
    public Optional<KnowledgeBaseResponseCompletedEvent> responseCompleted() {
        return Optional.ofNullable(responseCompleted);
    }

    /**
     * Returns whether this is a {@code retrieval.started} event.
     *
     * @return {@code true} when this is a retrieval-started event.
     */
    public boolean isRetrievalStarted() {
        return retrievalStarted != null;
    }

    /**
     * Returns whether this is an {@code activity.started} event.
     *
     * @return {@code true} when this is an activity-started event.
     */
    public boolean isActivityStarted() {
        return activityStarted != null;
    }

    /**
     * Returns whether this is an {@code activity.completed} event.
     *
     * @return {@code true} when this is an activity-completed event.
     */
    public boolean isActivityCompleted() {
        return activityCompleted != null;
    }

    /**
     * Returns whether this is an {@code answer.completed} event.
     *
     * @return {@code true} when this is an answer-completed event.
     */
    public boolean isAnswerCompleted() {
        return answerCompleted != null;
    }

    /**
     * Returns whether this is a {@code references.completed} event.
     *
     * @return {@code true} when this is a references-completed event.
     */
    public boolean isReferencesCompleted() {
        return referencesCompleted != null;
    }

    /**
     * Returns whether this is an {@code error} event.
     *
     * @return {@code true} when this is an error event.
     */
    public boolean isError() {
        return error != null;
    }

    /**
     * Returns whether this is a {@code response.completed} event.
     *
     * @return {@code true} when this is a response-completed event.
     */
    public boolean isResponseCompleted() {
        return responseCompleted != null;
    }

    /**
     * Returns whether this is a terminal stream event.
     *
     * @return {@code true} for {@code error} and {@code response.completed} events.
     */
    public boolean isTerminal() {
        return isError() || isResponseCompleted();
    }

    /**
     * Gets this event as a retrieval-started event.
     *
     * @return the retrieval-started payload.
     * @throws IllegalStateException if this is not a retrieval-started event.
     */
    public KnowledgeBaseRetrievalStartedEvent asRetrievalStarted() {
        return require(retrievalStarted, "retrieval.started");
    }

    /**
     * Gets this event as an activity-started event.
     *
     * @return the activity-started payload.
     * @throws IllegalStateException if this is not an activity-started event.
     */
    public KnowledgeBaseActivityStartedEvent asActivityStarted() {
        return require(activityStarted, "activity.started");
    }

    /**
     * Gets this event as an activity-completed event.
     *
     * @return the activity-completed payload.
     * @throws IllegalStateException if this is not an activity-completed event.
     */
    public KnowledgeBaseActivityRecord asActivityCompleted() {
        return require(activityCompleted, "activity.completed");
    }

    /**
     * Gets this event as an answer-completed event.
     *
     * @return the answer-completed payload.
     * @throws IllegalStateException if this is not an answer-completed event.
     */
    public KnowledgeBaseAnswerCompletedEvent asAnswerCompleted() {
        return require(answerCompleted, "answer.completed");
    }

    /**
     * Gets this event as a references-completed event.
     *
     * @return the references-completed payload.
     * @throws IllegalStateException if this is not a references-completed event.
     */
    public List<KnowledgeBaseReference> asReferencesCompleted() {
        return require(referencesCompleted, "references.completed");
    }

    /**
     * Gets this event as a stream-error event.
     *
     * @return the stream-error payload.
     * @throws IllegalStateException if this is not an error event.
     */
    public KnowledgeBaseStreamErrorEvent asError() {
        return require(error, "error");
    }

    /**
     * Gets this event as a response-completed event.
     *
     * @return the response-completed payload.
     * @throws IllegalStateException if this is not a response-completed event.
     */
    public KnowledgeBaseResponseCompletedEvent asResponseCompleted() {
        return require(responseCompleted, "response.completed");
    }

    private static <T> T require(T value, String eventName) {
        if (value == null) {
            throw new IllegalStateException("Event data is not " + eventName + ".");
        }
        return value;
    }
}
