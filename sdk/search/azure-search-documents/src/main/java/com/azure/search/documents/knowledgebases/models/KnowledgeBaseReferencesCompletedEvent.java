// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.search.documents.knowledgebases.models;

import com.azure.core.annotation.Immutable;
import com.azure.json.JsonReader;
import com.azure.json.JsonSerializable;
import com.azure.json.JsonWriter;

import java.io.IOException;
import java.util.List;

/**
 * The event emitted after all knowledge base references have been streamed.
 */
@Immutable
public final class KnowledgeBaseReferencesCompletedEvent
    implements KnowledgeBaseRetrievalStreamEvent, JsonSerializable<KnowledgeBaseReferencesCompletedEvent> {
    private final List<KnowledgeBaseReference> references;

    private KnowledgeBaseReferencesCompletedEvent(List<KnowledgeBaseReference> references) {
        this.references = references;
    }

    /**
     * Gets the completed references.
     *
     * @return the completed references.
     */
    public List<KnowledgeBaseReference> getReferences() {
        return references;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonWriter toJson(JsonWriter jsonWriter) throws IOException {
        jsonWriter.writeStartArray();
        if (references != null) {
            for (KnowledgeBaseReference reference : references) {
                jsonWriter.writeJson(reference);
            }
        }
        return jsonWriter.writeEndArray();
    }

    /**
     * Reads a references-completed event from JSON.
     *
     * @param jsonReader the JSON reader.
     * @return the deserialized event.
     * @throws IOException if the event cannot be read.
     */
    public static KnowledgeBaseReferencesCompletedEvent fromJson(JsonReader jsonReader) throws IOException {
        return new KnowledgeBaseReferencesCompletedEvent(jsonReader.readArray(KnowledgeBaseReference::fromJson));
    }
}
