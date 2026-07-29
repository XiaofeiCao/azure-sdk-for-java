// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.util.sse.generated;

import com.azure.json.JsonReader;
import com.azure.json.JsonSerializable;
import com.azure.json.JsonWriter;

import java.io.IOException;

/**
 * Fixture wrapper subtype for the {@code responseCreated} variant of the scenario 2 union (§5a).
 * <p>
 * The wrapper {@code extends} the union base and holds the real {@link ResponseCreated} model in a {@code value}
 * field, delegating {@code toJson}/{@code fromJson} to it. The wrapper never touches the variant model, which keeps
 * the shape non-breaking across the union edge cases (§5a).
 */
public final class ResponseCreatedResponseEvents extends ResponseEventsBase
    implements JsonSerializable<ResponseCreatedResponseEvents> {
    private final ResponseCreated value;

    /**
     * Creates the wrapper.
     *
     * @param value The wrapped {@link ResponseCreated} model.
     */
    public ResponseCreatedResponseEvents(ResponseCreated value) {
        this.value = value;
    }

    /**
     * Gets the wrapped model.
     *
     * @return The {@link ResponseCreated} value.
     */
    public ResponseCreated getValue() {
        return value;
    }

    @Override
    public JsonWriter toJson(JsonWriter jsonWriter) throws IOException {
        return value.toJson(jsonWriter);
    }

    /**
     * Reads an instance of {@link ResponseCreatedResponseEvents} from the {@link JsonReader}.
     *
     * @param jsonReader The reader to read from.
     * @return The parsed wrapper.
     * @throws IOException If an error occurs while reading.
     */
    public static ResponseCreatedResponseEvents fromJson(JsonReader jsonReader) throws IOException {
        return new ResponseCreatedResponseEvents(ResponseCreated.fromJson(jsonReader));
    }
}
