// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.search.documents.knowledgebases.models;

import com.azure.core.annotation.Immutable;

/**
 * A knowledge base retrieval stream event whose event name isn't known by this SDK version.
 */
@Immutable
public final class UnknownKnowledgeBaseRetrievalStreamEvent implements KnowledgeBaseRetrievalStreamEvent {
    private final String eventName;
    private final String rawData;

    UnknownKnowledgeBaseRetrievalStreamEvent(String eventName, String rawData) {
        this.eventName = eventName;
        this.rawData = rawData;
    }

    /**
     * Gets the server-sent event name.
     *
     * @return the event name.
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * Gets the event data exactly as it appeared in the server-sent event.
     *
     * @return the raw event data.
     */
    public String getRawData() {
        return rawData;
    }
}
