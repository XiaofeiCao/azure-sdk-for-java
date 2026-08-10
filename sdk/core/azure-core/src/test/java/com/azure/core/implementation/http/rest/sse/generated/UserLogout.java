// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.implementation.http.rest.sse.generated;

import com.azure.json.JsonReader;
import com.azure.json.JsonSerializable;
import com.azure.json.JsonToken;
import com.azure.json.JsonWriter;

import java.io.IOException;

public final class UserLogout implements JsonSerializable<UserLogout> {
    private String userId;
    private int sessionDurationInSeconds;

    public String getUserId() {
        return userId;
    }

    public UserLogout setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public int getSessionDurationInSeconds() {
        return sessionDurationInSeconds;
    }

    public UserLogout setSessionDurationInSeconds(int sessionDurationInSeconds) {
        this.sessionDurationInSeconds = sessionDurationInSeconds;
        return this;
    }

    @Override
    public JsonWriter toJson(JsonWriter jsonWriter) throws IOException {
        return jsonWriter.writeStartObject()
            .writeStringField("userId", userId)
            .writeIntField("sessionDurationInSeconds", sessionDurationInSeconds)
            .writeEndObject();
    }

    public static UserLogout fromJson(JsonReader jsonReader) throws IOException {
        return jsonReader.readObject(reader -> {
            UserLogout model = new UserLogout();
            while (reader.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = reader.getFieldName();
                reader.nextToken();
                if ("userId".equals(fieldName)) {
                    model.userId = reader.getString();
                } else if ("sessionDurationInSeconds".equals(fieldName)) {
                    model.sessionDurationInSeconds = reader.getInt();
                } else {
                    reader.skipChildren();
                }
            }
            return model;
        });
    }
}
