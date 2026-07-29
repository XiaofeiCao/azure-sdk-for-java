// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.util.sse.generated;

import com.azure.json.JsonReader;
import com.azure.json.JsonSerializable;
import com.azure.json.JsonToken;
import com.azure.json.JsonWriter;

import java.io.IOException;

/**
 * Fixture model for the {@code partialResult} SSE event (scenario 3).
 */
public final class PartialResult implements JsonSerializable<PartialResult> {
    private String text;

    /**
     * Gets the {@code text} property.
     *
     * @return The partial text.
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the {@code text} property.
     *
     * @param text The partial text.
     * @return The updated {@link PartialResult}.
     */
    public PartialResult setText(String text) {
        this.text = text;
        return this;
    }

    @Override
    public JsonWriter toJson(JsonWriter jsonWriter) throws IOException {
        jsonWriter.writeStartObject();
        jsonWriter.writeStringField("text", text);
        return jsonWriter.writeEndObject();
    }

    /**
     * Reads an instance of {@link PartialResult} from the {@link JsonReader}.
     *
     * @param jsonReader The reader to read from.
     * @return The parsed {@link PartialResult}.
     * @throws IOException If an error occurs while reading.
     */
    public static PartialResult fromJson(JsonReader jsonReader) throws IOException {
        return jsonReader.readObject(reader -> {
            PartialResult model = new PartialResult();
            while (reader.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = reader.getFieldName();
                reader.nextToken();
                if ("text".equals(fieldName)) {
                    model.text = reader.getString();
                } else {
                    reader.skipChildren();
                }
            }
            return model;
        });
    }
}
