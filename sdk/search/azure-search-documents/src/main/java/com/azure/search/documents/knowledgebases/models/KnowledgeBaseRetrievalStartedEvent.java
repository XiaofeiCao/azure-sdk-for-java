// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.search.documents.knowledgebases.models;

import com.azure.core.annotation.Immutable;
import com.azure.json.JsonReader;
import com.azure.json.JsonSerializable;
import com.azure.json.JsonToken;
import com.azure.json.JsonWriter;

import java.io.IOException;

/**
 * The event emitted when knowledge base retrieval starts.
 */
@Immutable
public final class KnowledgeBaseRetrievalStartedEvent
    implements KnowledgeBaseRetrievalStreamEvent, JsonSerializable<KnowledgeBaseRetrievalStartedEvent> {
    private final String requestId;
    private final String knowledgeBaseName;
    private final KnowledgeRetrievalOutputMode outputMode;
    private final KnowledgeRetrievalReasoningEffort reasoningEffort;

    private KnowledgeBaseRetrievalStartedEvent(String requestId, String knowledgeBaseName,
        KnowledgeRetrievalOutputMode outputMode, KnowledgeRetrievalReasoningEffort reasoningEffort) {
        this.requestId = requestId;
        this.knowledgeBaseName = knowledgeBaseName;
        this.outputMode = outputMode;
        this.reasoningEffort = reasoningEffort;
    }

    /**
     * Gets the identifier that correlates the events in this retrieval stream.
     *
     * @return the request identifier.
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * Gets the name of the knowledge base being queried.
     *
     * @return the knowledge base name.
     */
    public String getKnowledgeBaseName() {
        return knowledgeBaseName;
    }

    /**
     * Gets the effective retrieval output mode.
     *
     * @return the retrieval output mode.
     */
    public KnowledgeRetrievalOutputMode getOutputMode() {
        return outputMode;
    }

    /**
     * Gets the effective retrieval reasoning effort.
     *
     * @return the retrieval reasoning effort.
     */
    public KnowledgeRetrievalReasoningEffort getReasoningEffort() {
        return reasoningEffort;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonWriter toJson(JsonWriter jsonWriter) throws IOException {
        jsonWriter.writeStartObject();
        jsonWriter.writeStringField("requestId", requestId);
        jsonWriter.writeStringField("knowledgeBaseName", knowledgeBaseName);
        jsonWriter.writeStringField("outputMode", outputMode == null ? null : outputMode.toString());
        jsonWriter.writeJsonField("reasoningEffort", reasoningEffort);
        return jsonWriter.writeEndObject();
    }

    /**
     * Reads a retrieval-started event from JSON.
     *
     * @param jsonReader the JSON reader.
     * @return the deserialized event.
     * @throws IOException if the event cannot be read.
     */
    public static KnowledgeBaseRetrievalStartedEvent fromJson(JsonReader jsonReader) throws IOException {
        return jsonReader.readObject(reader -> {
            String requestId = null;
            String knowledgeBaseName = null;
            KnowledgeRetrievalOutputMode outputMode = null;
            KnowledgeRetrievalReasoningEffort reasoningEffort = null;
            while (reader.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = reader.getFieldName();
                reader.nextToken();
                if ("requestId".equals(fieldName)) {
                    requestId = reader.getString();
                } else if ("knowledgeBaseName".equals(fieldName)) {
                    knowledgeBaseName = reader.getString();
                } else if ("outputMode".equals(fieldName)) {
                    outputMode = KnowledgeRetrievalOutputMode.fromString(reader.getString());
                } else if ("reasoningEffort".equals(fieldName)) {
                    reasoningEffort = KnowledgeRetrievalReasoningEffort.fromJson(reader);
                } else {
                    reader.skipChildren();
                }
            }
            return new KnowledgeBaseRetrievalStartedEvent(requestId, knowledgeBaseName, outputMode, reasoningEffort);
        });
    }
}
