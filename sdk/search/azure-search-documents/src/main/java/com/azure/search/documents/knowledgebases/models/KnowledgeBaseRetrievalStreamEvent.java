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
 * <p>Use the {@code is*} methods to identify the event payload and the corresponding {@code as*} method to access it.
 * For event names introduced after this SDK version, none of the {@code is*} methods return {@code true}; inspect
 * {@link com.azure.core.http.ServerSentEvent#getEvent()} for the event name. The {@code error} and
 * {@code response.completed} variants are terminal events.</p>
 */
public final class KnowledgeBaseRetrievalStreamEvent {
    private final KnowledgeBaseRetrievalStartedEvent retrievalStarted;
    private final KnowledgeBaseActivityStartedEvent activityStarted;
    private final KnowledgeBaseActivityRecord activityCompleted;
    private final KnowledgeBaseAnswerCompletedEvent answerCompleted;
    private final List<KnowledgeBaseReference> referencesCompleted;
    private final KnowledgeBaseStreamErrorEvent error;
    private final KnowledgeBaseResponseCompletedEvent responseCompleted;

    private KnowledgeBaseRetrievalStreamEvent(KnowledgeBaseRetrievalStartedEvent retrievalStarted,
        KnowledgeBaseActivityStartedEvent activityStarted, KnowledgeBaseActivityRecord activityCompleted,
        KnowledgeBaseAnswerCompletedEvent answerCompleted, List<KnowledgeBaseReference> referencesCompleted,
        KnowledgeBaseStreamErrorEvent error, KnowledgeBaseResponseCompletedEvent responseCompleted) {
        this.retrievalStarted = retrievalStarted;
        this.activityStarted = activityStarted;
        this.activityCompleted = activityCompleted;
        this.answerCompleted = answerCompleted;
        this.referencesCompleted = referencesCompleted;
        this.error = error;
        this.responseCompleted = responseCompleted;
    }

    private static KnowledgeBaseRetrievalStreamEvent ofRetrievalStarted(KnowledgeBaseRetrievalStartedEvent value) {
        return new KnowledgeBaseRetrievalStreamEvent(Objects.requireNonNull(value, "'value' cannot be null."), null,
            null, null, null, null, null);
    }

    private static KnowledgeBaseRetrievalStreamEvent ofActivityStarted(KnowledgeBaseActivityStartedEvent value) {
        return new KnowledgeBaseRetrievalStreamEvent(null, Objects.requireNonNull(value, "'value' cannot be null."),
            null, null, null, null, null);
    }

    private static KnowledgeBaseRetrievalStreamEvent ofActivityCompleted(KnowledgeBaseActivityRecord value) {
        return new KnowledgeBaseRetrievalStreamEvent(null, null,
            Objects.requireNonNull(value, "'value' cannot be null."), null, null, null, null);
    }

    private static KnowledgeBaseRetrievalStreamEvent ofAnswerCompleted(KnowledgeBaseAnswerCompletedEvent value) {
        return new KnowledgeBaseRetrievalStreamEvent(null, null, null,
            Objects.requireNonNull(value, "'value' cannot be null."), null, null, null);
    }

    private static KnowledgeBaseRetrievalStreamEvent ofReferencesCompleted(List<KnowledgeBaseReference> value) {
        return new KnowledgeBaseRetrievalStreamEvent(null, null, null, null,
            Objects.requireNonNull(value, "'value' cannot be null."), null, null);
    }

    private static KnowledgeBaseRetrievalStreamEvent ofError(KnowledgeBaseStreamErrorEvent value) {
        return new KnowledgeBaseRetrievalStreamEvent(null, null, null, null, null,
            Objects.requireNonNull(value, "'value' cannot be null."), null);
    }

    private static KnowledgeBaseRetrievalStreamEvent ofResponseCompleted(KnowledgeBaseResponseCompletedEvent value) {
        return new KnowledgeBaseRetrievalStreamEvent(null, null, null, null, null, null,
            Objects.requireNonNull(value, "'value' cannot be null."));
    }

    private static KnowledgeBaseRetrievalStreamEvent ofUnknown() {
        return new KnowledgeBaseRetrievalStreamEvent(null, null, null, null, null, null, null);
    }

    /**
     * Deserializes a knowledge base retrieval stream event.
     *
     * @param eventName the server-sent event name.
     * @param data the JSON event data.
     * @return the typed stream event, or {@code null} when the event name or data is {@code null}.
     * @throws UncheckedIOException if the event data cannot be deserialized.
     */
    public static KnowledgeBaseRetrievalStreamEvent fromEvent(String eventName, String data) {
        if (eventName == null || data == null) {
            return null;
        }

        try (JsonReader reader = JsonProviders.createReader(data.getBytes(StandardCharsets.UTF_8))) {
            switch (eventName) {
                case "retrieval.started":
                    return ofRetrievalStarted(KnowledgeBaseRetrievalStartedEvent.fromJson(reader));

                case "activity.started":
                    return ofActivityStarted(KnowledgeBaseActivityStartedEvent.fromJson(reader));

                case "activity.completed":
                    return ofActivityCompleted(KnowledgeBaseActivityRecord.fromJson(reader));

                case "answer.completed":
                    return ofAnswerCompleted(KnowledgeBaseAnswerCompletedEvent.fromJson(reader));

                case "references.completed":
                    return ofReferencesCompleted(reader.readArray(KnowledgeBaseReference::fromJson));

                case "error":
                    return ofError(KnowledgeBaseStreamErrorEvent.fromJson(reader));

                case "response.completed":
                    return ofResponseCompleted(KnowledgeBaseResponseCompletedEvent.fromJson(reader));

                default:
                    return ofUnknown();
            }
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
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
