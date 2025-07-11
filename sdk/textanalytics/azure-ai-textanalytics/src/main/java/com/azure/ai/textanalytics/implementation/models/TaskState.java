// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.ai.textanalytics.implementation.models;

import com.azure.core.annotation.Fluent;
import com.azure.core.annotation.Generated;
import com.azure.core.util.CoreUtils;
import com.azure.json.JsonReader;
import com.azure.json.JsonSerializable;
import com.azure.json.JsonToken;
import com.azure.json.JsonWriter;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The TaskState model.
 */
@Fluent
public class TaskState implements JsonSerializable<TaskState> {
    /*
     * The lastUpdateDateTime property.
     */
    @Generated
    private OffsetDateTime lastUpdateDateTime;

    /*
     * The status property.
     */
    @Generated
    private State status;

    /**
     * Creates an instance of TaskState class.
     */
    @Generated
    public TaskState() {
    }

    /**
     * Get the lastUpdateDateTime property: The lastUpdateDateTime property.
     * 
     * @return the lastUpdateDateTime value.
     */
    @Generated
    public OffsetDateTime getLastUpdateDateTime() {
        return this.lastUpdateDateTime;
    }

    /**
     * Set the lastUpdateDateTime property: The lastUpdateDateTime property.
     * 
     * @param lastUpdateDateTime the lastUpdateDateTime value to set.
     * @return the TaskState object itself.
     */
    @Generated
    public TaskState setLastUpdateDateTime(OffsetDateTime lastUpdateDateTime) {
        this.lastUpdateDateTime = lastUpdateDateTime;
        return this;
    }

    /**
     * Get the status property: The status property.
     * 
     * @return the status value.
     */
    @Generated
    public State getStatus() {
        return this.status;
    }

    /**
     * Set the status property: The status property.
     * 
     * @param status the status value to set.
     * @return the TaskState object itself.
     */
    @Generated
    public TaskState setStatus(State status) {
        this.status = status;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Generated
    @Override
    public JsonWriter toJson(JsonWriter jsonWriter) throws IOException {
        jsonWriter.writeStartObject();
        jsonWriter.writeStringField("lastUpdateDateTime",
            this.lastUpdateDateTime == null
                ? null
                : DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(this.lastUpdateDateTime));
        jsonWriter.writeStringField("status", this.status == null ? null : this.status.toString());
        return jsonWriter.writeEndObject();
    }

    /**
     * Reads an instance of TaskState from the JsonReader.
     * 
     * @param jsonReader The JsonReader being read.
     * @return An instance of TaskState if the JsonReader was pointing to an instance of it, or null if it was pointing
     * to JSON null.
     * @throws IllegalStateException If the deserialized JSON object was missing any required properties.
     * @throws IOException If an error occurs while reading the TaskState.
     */
    @Generated
    public static TaskState fromJson(JsonReader jsonReader) throws IOException {
        return jsonReader.readObject(reader -> {
            TaskState deserializedTaskState = new TaskState();
            while (reader.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = reader.getFieldName();
                reader.nextToken();

                if ("lastUpdateDateTime".equals(fieldName)) {
                    deserializedTaskState.lastUpdateDateTime = reader
                        .getNullable(nonNullReader -> CoreUtils.parseBestOffsetDateTime(nonNullReader.getString()));
                } else if ("status".equals(fieldName)) {
                    deserializedTaskState.status = State.fromString(reader.getString());
                } else {
                    reader.skipChildren();
                }
            }

            return deserializedTaskState;
        });
    }
}
