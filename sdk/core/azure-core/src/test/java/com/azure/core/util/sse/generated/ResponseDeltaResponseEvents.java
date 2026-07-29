// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.util.sse.generated;

import com.azure.json.JsonReader;
import com.azure.json.JsonSerializable;
import com.azure.json.JsonWriter;

import java.io.IOException;

/**
 * Fixture wrapper subtype for the {@code responseDelta} variant of the scenario 2 union (§6b).
 */
public final class ResponseDeltaResponseEvents extends ResponseEventsBase
    implements JsonSerializable<ResponseDeltaResponseEvents> {
    private final ResponseDelta value;

    /**
     * Creates the wrapper.
     *
     * @param value The wrapped {@link ResponseDelta} model.
     */
    public ResponseDeltaResponseEvents(ResponseDelta value) {
        this.value = value;
    }

    /**
     * Gets the wrapped model.
     *
     * @return The {@link ResponseDelta} value.
     */
    public ResponseDelta getValue() {
        return value;
    }

    @Override
    public JsonWriter toJson(JsonWriter jsonWriter) throws IOException {
        return value.toJson(jsonWriter);
    }

    /**
     * Reads an instance of {@link ResponseDeltaResponseEvents} from the {@link JsonReader}.
     *
     * @param jsonReader The reader to read from.
     * @return The parsed wrapper.
     * @throws IOException If an error occurs while reading.
     */
    public static ResponseDeltaResponseEvents fromJson(JsonReader jsonReader) throws IOException {
        return new ResponseDeltaResponseEvents(ResponseDelta.fromJson(jsonReader));
    }
}
