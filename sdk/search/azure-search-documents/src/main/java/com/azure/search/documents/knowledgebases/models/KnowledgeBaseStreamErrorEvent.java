// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.search.documents.knowledgebases.models;

import com.azure.core.annotation.Immutable;
import com.azure.json.JsonReader;
import com.azure.json.JsonSerializable;
import com.azure.json.JsonToken;
import com.azure.json.JsonWriter;

import java.io.IOException;
import java.util.List;

/**
 * The event emitted when knowledge base retrieval fails after streaming starts.
 */
@Immutable
public final class KnowledgeBaseStreamErrorEvent
    implements KnowledgeBaseRetrievalStreamEvent, JsonSerializable<KnowledgeBaseStreamErrorEvent> {
    private final KnowledgeBaseErrorDetail error;
    private final List<KnowledgeBaseActivityRecord> activity;

    private KnowledgeBaseStreamErrorEvent(KnowledgeBaseErrorDetail error, List<KnowledgeBaseActivityRecord> activity) {
        this.error = error;
        this.activity = activity;
    }

    /**
     * Gets the error detail.
     *
     * @return the error detail.
     */
    public KnowledgeBaseErrorDetail getError() {
        return error;
    }

    /**
     * Gets the activity records completed before the failure.
     *
     * @return the completed activity records.
     */
    public List<KnowledgeBaseActivityRecord> getActivity() {
        return activity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTerminal() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonWriter toJson(JsonWriter jsonWriter) throws IOException {
        jsonWriter.writeStartObject();
        jsonWriter.writeJsonField("error", error);
        jsonWriter.writeArrayField("activity", activity, (writer, element) -> writer.writeJson(element));
        return jsonWriter.writeEndObject();
    }

    /**
     * Reads a stream-error event from JSON.
     *
     * @param jsonReader the JSON reader.
     * @return the deserialized event.
     * @throws IOException if the event cannot be read.
     */
    public static KnowledgeBaseStreamErrorEvent fromJson(JsonReader jsonReader) throws IOException {
        return jsonReader.readObject(reader -> {
            KnowledgeBaseErrorDetail error = null;
            List<KnowledgeBaseActivityRecord> activity = null;
            while (reader.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = reader.getFieldName();
                reader.nextToken();
                if ("error".equals(fieldName)) {
                    error = KnowledgeBaseErrorDetail.fromJson(reader);
                } else if ("activity".equals(fieldName)) {
                    activity = reader.readArray(KnowledgeBaseActivityRecord::fromJson);
                } else {
                    reader.skipChildren();
                }
            }
            return new KnowledgeBaseStreamErrorEvent(error, activity);
        });
    }
}
