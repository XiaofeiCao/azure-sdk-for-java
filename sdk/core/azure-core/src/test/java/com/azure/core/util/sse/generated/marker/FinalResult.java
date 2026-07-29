// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.util.sse.generated.marker;

import com.azure.json.JsonReader;
import com.azure.json.JsonSerializable;
import com.azure.json.JsonToken;
import com.azure.json.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Fixture model for the {@code finalResult} SSE event (scenario 3, marker-interface shape).
 */
public final class FinalResult implements JsonSerializable<FinalResult>, RetrievalEvents {
    private List<String> references;

    /**
     * Gets the {@code references} property.
     *
     * @return The list of references.
     */
    public List<String> getReferences() {
        return references;
    }

    /**
     * Sets the {@code references} property.
     *
     * @param references The list of references.
     * @return The updated {@link FinalResult}.
     */
    public FinalResult setReferences(List<String> references) {
        this.references = references;
        return this;
    }

    @Override
    public JsonWriter toJson(JsonWriter jsonWriter) throws IOException {
        jsonWriter.writeStartObject();
        jsonWriter.writeArrayField("references", references, JsonWriter::writeString);
        return jsonWriter.writeEndObject();
    }

    /**
     * Reads an instance of {@link FinalResult} from the {@link JsonReader}.
     *
     * @param jsonReader The reader to read from.
     * @return The parsed {@link FinalResult}.
     * @throws IOException If an error occurs while reading.
     */
    public static FinalResult fromJson(JsonReader jsonReader) throws IOException {
        return jsonReader.readObject(reader -> {
            FinalResult model = new FinalResult();
            while (reader.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = reader.getFieldName();
                reader.nextToken();
                if ("references".equals(fieldName)) {
                    List<String> refs = reader.readArray(JsonReader::getString);
                    model.references = refs == null ? null : new ArrayList<>(refs);
                } else {
                    reader.skipChildren();
                }
            }
            return model;
        });
    }
}
