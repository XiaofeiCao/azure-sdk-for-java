// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.search.documents.knowledgebases;

import com.azure.search.documents.knowledgebases.models.KnowledgeBaseRetrievalStreamEvent;

/**
 * A listener for events emitted by a streaming knowledge base retrieval operation.
 */
@FunctionalInterface
public interface KnowledgeBaseRetrievalStreamEventListener {
    /**
     * Handles the next knowledge base retrieval stream event.
     *
     * @param event The stream event.
     */
    void onEvent(KnowledgeBaseRetrievalStreamEvent event);
}
