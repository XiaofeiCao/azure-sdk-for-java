// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.util.sse.generated;

import com.azure.json.JsonReader;
import com.azure.json.JsonSerializable;
import com.azure.json.JsonToken;
import com.azure.json.JsonWriter;

import java.io.IOException;

/**
 * Fixture model for the {@code responseCreated} SSE event (scenario 2). Standalone stream-style model, untouched
  * by the union hierarchy (§5a).
 */
public final class ResponseCreated implements JsonSerializable<ResponseCreated> {
    private String id;

    /**
     * Gets the {@code id} property.
     *
     * @return The response id.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the {@code id} property.
     *
     * @param id The response id.
     * @return The updated {@link ResponseCreated}.
     */
    public ResponseCreated setId(String id) {
        this.id = id;
        return this;
    }

    @Override
    public JsonWriter toJson(JsonWriter jsonWriter) throws IOException {
        jsonWriter.writeStartObject();
        jsonWriter.writeStringField("id", id);
        return jsonWriter.writeEndObject();
    }

    /**
     * Reads an instance of {@link ResponseCreated} from the {@link JsonReader}.
     *
     * @param jsonReader The reader to read from.
     * @return The parsed {@link ResponseCreated}.
     * @throws IOException If an error occurs while reading.
     */
    public static ResponseCreated fromJson(JsonReader jsonReader) throws IOException {
        return jsonReader.readObject(reader -> {
            ResponseCreated model = new ResponseCreated();
            while (reader.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = reader.getFieldName();
                reader.nextToken();
                if ("id".equals(fieldName)) {
                    model.id = reader.getString();
                } else {
                    reader.skipChildren();
                }
            }
            return model;
        });
    }
}
