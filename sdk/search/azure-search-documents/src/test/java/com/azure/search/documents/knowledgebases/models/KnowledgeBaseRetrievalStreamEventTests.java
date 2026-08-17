// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.search.documents.knowledgebases.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KnowledgeBaseRetrievalStreamEventTests {
    @Test
    public void deserializesEveryNamedEventToItsMarkerImplementation() {
        KnowledgeBaseRetrievalStreamEvent retrievalStarted = deserialize("retrieval.started",
            "{\"requestId\":\"request-1\",\"knowledgeBaseName\":\"kb\",\"outputMode\":\"answerSynthesis\","
                + "\"reasoningEffort\":{\"kind\":\"minimal\"}}");
        KnowledgeBaseRetrievalStartedEvent retrievalStartedEvent
            = assertInstanceOf(KnowledgeBaseRetrievalStartedEvent.class, retrievalStarted);
        assertEquals("request-1", retrievalStartedEvent.getRequestId());
        assertEquals(KnowledgeRetrievalOutputMode.ANSWER_SYNTHESIS, retrievalStartedEvent.getOutputMode());
        assertFalse(retrievalStartedEvent.isTerminal());

        KnowledgeBaseRetrievalStreamEvent activityStarted = deserialize("activity.started",
            "{\"id\":1,\"type\":\"searchIndex\",\"startedAt\":\"2026-08-11T00:00:00Z\","
                + "\"knowledgeSourceName\":\"source\"}");
        KnowledgeBaseActivityStartedEvent activityStartedEvent
            = assertInstanceOf(KnowledgeBaseActivityStartedEvent.class, activityStarted);
        assertEquals(KnowledgeBaseActivityRecordType.SEARCH_INDEX, activityStartedEvent.getType());
        assertFalse(activityStartedEvent.isTerminal());

        KnowledgeBaseRetrievalStreamEvent activityCompleted
            = deserialize("activity.completed", "{\"id\":1,\"type\":\"searchIndex\"}");
        KnowledgeBaseActivityRecord activityRecord
            = assertInstanceOf(KnowledgeBaseSearchIndexActivityRecord.class, activityCompleted);
        assertEquals(1, activityRecord.getId());
        assertFalse(activityRecord.isTerminal());

        KnowledgeBaseRetrievalStreamEvent answerCompleted
            = deserialize("answer.completed", "{\"messageIndex\":0,\"message\":{\"content\":[]}}");
        KnowledgeBaseAnswerCompletedEvent answerCompletedEvent
            = assertInstanceOf(KnowledgeBaseAnswerCompletedEvent.class, answerCompleted);
        assertEquals(0, answerCompletedEvent.getMessageIndex());
        assertFalse(answerCompletedEvent.isTerminal());

        KnowledgeBaseRetrievalStreamEvent referencesCompleted = deserialize("references.completed", "[]");
        KnowledgeBaseReferencesCompletedEvent referencesCompletedEvent
            = assertInstanceOf(KnowledgeBaseReferencesCompletedEvent.class, referencesCompleted);
        assertTrue(referencesCompletedEvent.getReferences().isEmpty());
        assertFalse(referencesCompletedEvent.isTerminal());

        KnowledgeBaseRetrievalStreamEvent error
            = deserialize("error", "{\"error\":{\"code\":\"RetrievalFailed\"},\"activity\":[]}");
        KnowledgeBaseStreamErrorEvent errorEvent = assertInstanceOf(KnowledgeBaseStreamErrorEvent.class, error);
        assertEquals("RetrievalFailed", errorEvent.getError().getCode());
        assertTrue(errorEvent.isTerminal());

        KnowledgeBaseRetrievalStreamEvent responseCompleted = deserialize("response.completed",
            "{\"statusCode\":200,\"response\":{\"response\":[],\"activity\":[],\"references\":[]}}");
        KnowledgeBaseResponseCompletedEvent responseCompletedEvent
            = assertInstanceOf(KnowledgeBaseResponseCompletedEvent.class, responseCompleted);
        assertEquals(200, responseCompletedEvent.getStatusCode());
        assertTrue(responseCompletedEvent.isTerminal());
    }

    @Test
    public void unknownEventsRetainEventNameAndRawData() {
        String rawData = "{\"value\":\"future\"}";
        KnowledgeBaseRetrievalStreamEvent event = KnowledgeBaseRetrievalStreamEvent.fromEvent("future.event", rawData);

        UnknownKnowledgeBaseRetrievalStreamEvent unknown
            = assertInstanceOf(UnknownKnowledgeBaseRetrievalStreamEvent.class, event);
        assertEquals("future.event", unknown.getEventName());
        assertEquals(rawData, unknown.getRawData());
        assertFalse(unknown.isTerminal());
    }

    @Test
    public void missingProtocolFieldsDoNotProduceAnEventPayload() {
        assertNull(KnowledgeBaseRetrievalStreamEvent.fromEvent(null, "{}"));
        assertNull(KnowledgeBaseRetrievalStreamEvent.fromEvent("future.event", null));
    }

    private static KnowledgeBaseRetrievalStreamEvent deserialize(String eventName, String data) {
        return KnowledgeBaseRetrievalStreamEvent.fromEvent(eventName, data);
    }
}
