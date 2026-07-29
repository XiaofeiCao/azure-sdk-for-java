// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.util.sse.generated;

import com.azure.json.JsonReader;
import com.azure.json.JsonSerializable;
import com.azure.json.JsonToken;
import com.azure.json.JsonWriter;

import java.io.IOException;

/**
 * Fixture model for the {@code responseDelta} SSE event (scenario 2). Standalone stream-style model.
 */
public final class ResponseDelta implements JsonSerializable<ResponseDelta> {
    private String delta;

    /**
     * Gets the {@code delta} property.
     *
     * @return The delta value.
     */
    public String getDelta() {
        return delta;
    }

    /**
     * Sets the {@code delta} property.
     *
     * @param delta The delta value.
     * @return The updated {@link ResponseDelta}.
     */
    public ResponseDelta setDelta(String delta) {
        this.delta = delta;
        return this;
    }

    @Override
    public JsonWriter toJson(JsonWriter jsonWriter) throws IOException {
        jsonWriter.writeStartObject();
        jsonWriter.writeStringField("delta", delta);
        return jsonWriter.writeEndObject();
    }

    /**
     * Reads an instance of {@link ResponseDelta} from the {@link JsonReader}.
     *
     * @param jsonReader The reader to read from.
     * @return The parsed {@link ResponseDelta}.
     * @throws IOException If an error occurs while reading.
     */
    public static ResponseDelta fromJson(JsonReader jsonReader) throws IOException {
        return jsonReader.readObject(reader -> {
            ResponseDelta model = new ResponseDelta();
            while (reader.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = reader.getFieldName();
                reader.nextToken();
                if ("delta".equals(fieldName)) {
                    model.delta = reader.getString();
                } else {
                    reader.skipChildren();
                }
            }
            return model;
        });
    }
}
