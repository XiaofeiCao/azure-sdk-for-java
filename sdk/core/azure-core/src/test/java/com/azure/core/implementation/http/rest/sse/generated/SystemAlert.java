// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.implementation.http.rest.sse.generated;

import com.azure.json.JsonReader;
import com.azure.json.JsonSerializable;
import com.azure.json.JsonToken;
import com.azure.json.JsonWriter;

import java.io.IOException;

public final class SystemAlert implements JsonSerializable<SystemAlert> {
    private String level;
    private String message;

    public String getLevel() {
        return level;
    }

    public SystemAlert setLevel(String level) {
        this.level = level;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public SystemAlert setMessage(String message) {
        this.message = message;
        return this;
    }

    @Override
    public JsonWriter toJson(JsonWriter jsonWriter) throws IOException {
        return jsonWriter.writeStartObject()
            .writeStringField("level", level)
            .writeStringField("message", message)
            .writeEndObject();
    }

    public static SystemAlert fromJson(JsonReader jsonReader) throws IOException {
        return jsonReader.readObject(reader -> {
            SystemAlert model = new SystemAlert();
            while (reader.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = reader.getFieldName();
                reader.nextToken();
                if ("level".equals(fieldName)) {
                    model.level = reader.getString();
                } else if ("message".equals(fieldName)) {
                    model.message = reader.getString();
                } else {
                    reader.skipChildren();
                }
            }
            return model;
        });
    }
}
