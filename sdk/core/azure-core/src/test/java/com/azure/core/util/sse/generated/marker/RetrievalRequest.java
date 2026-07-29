// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.util.sse.generated.marker;

import com.azure.json.JsonReader;
import com.azure.json.JsonSerializable;
import com.azure.json.JsonToken;
import com.azure.json.JsonWriter;

import java.io.IOException;

/**
 * Fixture request body model for scenario 3 (retrieve, POST with a JSON body), marker-interface shape.
 */
public final class RetrievalRequest implements JsonSerializable<RetrievalRequest> {
    private String query;

    /**
     * Gets the {@code query} property.
     *
     * @return The query value.
     */
    public String getQuery() {
        return query;
    }

    /**
     * Sets the {@code query} property.
     *
     * @param query The query value.
     * @return The updated {@link RetrievalRequest}.
     */
    public RetrievalRequest setQuery(String query) {
        this.query = query;
        return this;
    }

    @Override
    public JsonWriter toJson(JsonWriter jsonWriter) throws IOException {
        jsonWriter.writeStartObject();
        jsonWriter.writeStringField("query", query);
        return jsonWriter.writeEndObject();
    }

    /**
     * Reads an instance of {@link RetrievalRequest} from the {@link JsonReader}.
     *
     * @param jsonReader The reader to read from.
     * @return The parsed {@link RetrievalRequest}.
     * @throws IOException If an error occurs while reading.
     */
    public static RetrievalRequest fromJson(JsonReader jsonReader) throws IOException {
        return jsonReader.readObject(reader -> {
            RetrievalRequest model = new RetrievalRequest();
            while (reader.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = reader.getFieldName();
                reader.nextToken();
                if ("query".equals(fieldName)) {
                    model.query = reader.getString();
                } else {
                    reader.skipChildren();
                }
            }
            return model;
        });
    }
}
