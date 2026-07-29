// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.util.sse.generated.flat;

import com.azure.json.JsonReader;
import com.azure.json.JsonSerializable;
import com.azure.json.JsonToken;
import com.azure.json.JsonWriter;

import java.io.IOException;

/**
 * Fixture model for the unnamed ({@code message}) SSE event in the flat-envelope variant. A plain stream-style
 * model that is <em>untouched</em> by any union machinery — it neither {@code implements} a marker nor is wrapped.
 */
public final class Info implements JsonSerializable<Info> {
    private String desc;

    /**
     * Gets the {@code desc} property.
     *
     * @return The description value.
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Sets the {@code desc} property.
     *
     * @param desc The description value.
     * @return The updated {@link Info}.
     */
    public Info setDesc(String desc) {
        this.desc = desc;
        return this;
    }

    @Override
    public JsonWriter toJson(JsonWriter jsonWriter) throws IOException {
        jsonWriter.writeStartObject();
        jsonWriter.writeStringField("desc", desc);
        return jsonWriter.writeEndObject();
    }

    /**
     * Reads an instance of {@link Info} from the {@link JsonReader}.
     *
     * @param jsonReader The reader to read from.
     * @return The parsed {@link Info}.
     * @throws IOException If an error occurs while reading.
     */
    public static Info fromJson(JsonReader jsonReader) throws IOException {
        return jsonReader.readObject(reader -> {
            Info info = new Info();
            while (reader.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = reader.getFieldName();
                reader.nextToken();
                if ("desc".equals(fieldName)) {
                    info.desc = reader.getString();
                } else {
                    reader.skipChildren();
                }
            }
            return info;
        });
    }
}
