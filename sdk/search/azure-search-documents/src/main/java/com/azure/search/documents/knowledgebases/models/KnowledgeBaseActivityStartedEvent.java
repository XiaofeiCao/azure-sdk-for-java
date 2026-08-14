// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.search.documents.knowledgebases.models;

import com.azure.core.annotation.Immutable;
import com.azure.core.util.CoreUtils;
import com.azure.json.JsonReader;
import com.azure.json.JsonSerializable;
import com.azure.json.JsonToken;
import com.azure.json.JsonWriter;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The event emitted immediately before a knowledge base retrieval activity starts.
 */
@Immutable
public final class KnowledgeBaseActivityStartedEvent implements JsonSerializable<KnowledgeBaseActivityStartedEvent> {
    private final int id;
    private final KnowledgeBaseActivityRecordType type;
    private final OffsetDateTime startedAt;
    private final String knowledgeSourceName;

    private KnowledgeBaseActivityStartedEvent(int id, KnowledgeBaseActivityRecordType type, OffsetDateTime startedAt,
        String knowledgeSourceName) {
        this.id = id;
        this.type = type;
        this.startedAt = startedAt;
        this.knowledgeSourceName = knowledgeSourceName;
    }

    /**
     * Gets the activity identifier.
     *
     * @return the activity identifier.
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the activity type.
     *
     * @return the activity type.
     */
    public KnowledgeBaseActivityRecordType getType() {
        return type;
    }

    /**
     * Gets the time at which the activity started.
     *
     * @return the activity start time.
     */
    public OffsetDateTime getStartedAt() {
        return startedAt;
    }

    /**
     * Gets the knowledge source used by the activity.
     *
     * @return the knowledge source name, or {@code null} when the activity does not target a knowledge source.
     */
    public String getKnowledgeSourceName() {
        return knowledgeSourceName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonWriter toJson(JsonWriter jsonWriter) throws IOException {
        jsonWriter.writeStartObject();
        jsonWriter.writeIntField("id", id);
        jsonWriter.writeStringField("type", type == null ? null : type.toString());
        jsonWriter.writeStringField("startedAt",
            startedAt == null ? null : DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(startedAt));
        jsonWriter.writeStringField("knowledgeSourceName", knowledgeSourceName);
        return jsonWriter.writeEndObject();
    }

    /**
     * Reads an activity-started event from JSON.
     *
     * @param jsonReader the JSON reader.
     * @return the deserialized event.
     * @throws IOException if the event cannot be read.
     */
    public static KnowledgeBaseActivityStartedEvent fromJson(JsonReader jsonReader) throws IOException {
        return jsonReader.readObject(reader -> {
            int id = 0;
            KnowledgeBaseActivityRecordType type = null;
            OffsetDateTime startedAt = null;
            String knowledgeSourceName = null;
            while (reader.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = reader.getFieldName();
                reader.nextToken();
                if ("id".equals(fieldName)) {
                    id = reader.getInt();
                } else if ("type".equals(fieldName)) {
                    type = KnowledgeBaseActivityRecordType.fromString(reader.getString());
                } else if ("startedAt".equals(fieldName)) {
                    startedAt = reader
                        .getNullable(nonNullReader -> CoreUtils.parseBestOffsetDateTime(nonNullReader.getString()));
                } else if ("knowledgeSourceName".equals(fieldName)) {
                    knowledgeSourceName = reader.getString();
                } else {
                    reader.skipChildren();
                }
            }
            return new KnowledgeBaseActivityStartedEvent(id, type, startedAt, knowledgeSourceName);
        });
    }
}
