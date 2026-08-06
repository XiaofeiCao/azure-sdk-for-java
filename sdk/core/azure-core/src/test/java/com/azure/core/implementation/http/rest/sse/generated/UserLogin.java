// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.implementation.http.rest.sse.generated;

import com.azure.json.JsonReader;
import com.azure.json.JsonSerializable;
import com.azure.json.JsonToken;
import com.azure.json.JsonWriter;

import java.io.IOException;

public final class UserLogin implements JsonSerializable<UserLogin> {
    private String userId;
    private String loginTime;

    public String getUserId() {
        return userId;
    }

    public UserLogin setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getLoginTime() {
        return loginTime;
    }

    public UserLogin setLoginTime(String loginTime) {
        this.loginTime = loginTime;
        return this;
    }

    @Override
    public JsonWriter toJson(JsonWriter jsonWriter) throws IOException {
        return jsonWriter.writeStartObject()
            .writeStringField("userId", userId)
            .writeStringField("loginTime", loginTime)
            .writeEndObject();
    }

    public static UserLogin fromJson(JsonReader jsonReader) throws IOException {
        return jsonReader.readObject(reader -> {
            UserLogin model = new UserLogin();
            while (reader.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = reader.getFieldName();
                reader.nextToken();
                if ("userId".equals(fieldName)) {
                    model.userId = reader.getString();
                } else if ("loginTime".equals(fieldName)) {
                    model.loginTime = reader.getString();
                } else {
                    reader.skipChildren();
                }
            }
            return model;
        });
    }
}
