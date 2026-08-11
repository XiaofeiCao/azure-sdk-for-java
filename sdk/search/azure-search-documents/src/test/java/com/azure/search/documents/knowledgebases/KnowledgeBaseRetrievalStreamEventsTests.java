// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.search.documents.knowledgebases;

import com.azure.search.documents.knowledgebases.models.KnowledgeBaseActivityRecordType;
import com.azure.search.documents.knowledgebases.models.KnowledgeRetrievalOutputMode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KnowledgeBaseRetrievalStreamEventsTests {
    @Test
    public void deserializesEveryEventVariant() {
        KnowledgeBaseRetrievalStreamEvent retrievalStarted = deserialize("retrieval.started",
            "{\"requestId\":\"request-1\",\"knowledgeBaseName\":\"kb\",\"outputMode\":\"answerSynthesis\","
                + "\"reasoningEffort\":{\"kind\":\"minimal\"}}");
        assertTrue(retrievalStarted.isRetrievalStarted());
        assertEquals("request-1", retrievalStarted.asRetrievalStarted().getRequestId());
        assertEquals(KnowledgeRetrievalOutputMode.ANSWER_SYNTHESIS,
            retrievalStarted.asRetrievalStarted().getOutputMode());

        KnowledgeBaseRetrievalStreamEvent activityStarted = deserialize("activity.started",
            "{\"id\":1,\"type\":\"searchIndex\",\"startedAt\":\"2026-08-11T00:00:00Z\","
                + "\"knowledgeSourceName\":\"source\"}");
        assertTrue(activityStarted.isActivityStarted());
        assertEquals(KnowledgeBaseActivityRecordType.SEARCH_INDEX, activityStarted.asActivityStarted().getType());

        KnowledgeBaseRetrievalStreamEvent activityCompleted
            = deserialize("activity.completed", "{\"id\":1,\"type\":\"searchIndex\"}");
        assertTrue(activityCompleted.isActivityCompleted());
        assertEquals(1, activityCompleted.asActivityCompleted().getId());

        KnowledgeBaseRetrievalStreamEvent answerCompleted
            = deserialize("answer.completed", "{\"messageIndex\":0,\"message\":{\"content\":[]}}");
        assertTrue(answerCompleted.isAnswerCompleted());
        assertEquals(0, answerCompleted.asAnswerCompleted().getMessageIndex());

        KnowledgeBaseRetrievalStreamEvent referencesCompleted = deserialize("references.completed", "[]");
        assertTrue(referencesCompleted.isReferencesCompleted());
        assertTrue(referencesCompleted.asReferencesCompleted().isEmpty());

        KnowledgeBaseRetrievalStreamEvent error
            = deserialize("error", "{\"error\":{\"code\":\"RetrievalFailed\"},\"activity\":[]}");
        assertTrue(error.isError());
        assertTrue(error.isTerminal());
        assertEquals("RetrievalFailed", error.asError().getError().getCode());

        KnowledgeBaseRetrievalStreamEvent responseCompleted = deserialize("response.completed",
            "{\"statusCode\":200,\"response\":{\"response\":[],\"activity\":[],\"references\":[]}}");
        assertTrue(responseCompleted.isResponseCompleted());
        assertTrue(responseCompleted.isTerminal());
        assertEquals(200, responseCompleted.asResponseCompleted().getStatusCode());
    }

    @Test
    public void accessorsRejectTheWrongVariant() {
        KnowledgeBaseRetrievalStreamEvent event = deserialize("references.completed", "[]");

        assertFalse(event.isTerminal());
        assertTrue(event.referencesCompleted().isPresent());
        assertThrows(IllegalStateException.class, event::asError);
    }

    @Test
    public void unknownEventsRetainOnlyProtocolMetadata() {
        KnowledgeBaseRetrievalStreamEvent event = KnowledgeBaseRetrievalStreamEvents.deserialize("future.event", "{}");

        assertFalse(event.isRetrievalStarted());
        assertFalse(event.isActivityStarted());
        assertFalse(event.isActivityCompleted());
        assertFalse(event.isAnswerCompleted());
        assertFalse(event.isReferencesCompleted());
        assertFalse(event.isError());
        assertFalse(event.isResponseCompleted());
        assertFalse(event.isTerminal());
    }

    private static KnowledgeBaseRetrievalStreamEvent deserialize(String eventName, String data) {
        return KnowledgeBaseRetrievalStreamEvents.deserialize(eventName, data);
    }
}
