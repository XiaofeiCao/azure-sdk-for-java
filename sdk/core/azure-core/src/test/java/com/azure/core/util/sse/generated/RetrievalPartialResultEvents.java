// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.util.sse.generated;

import com.azure.json.JsonReader;
import com.azure.json.JsonSerializable;
import com.azure.json.JsonWriter;

import java.io.IOException;

/**
 * Fixture wrapper subtype for the {@code partialResult} variant of the scenario 3 union (§5a).
 */
public final class RetrievalPartialResultEvents extends RetrievalEventsBase
    implements JsonSerializable<RetrievalPartialResultEvents> {
    private final PartialResult value;

    /**
     * Creates the wrapper.
     *
     * @param value The wrapped {@link PartialResult} model.
     */
    public RetrievalPartialResultEvents(PartialResult value) {
        this.value = value;
    }

    /**
     * Gets the wrapped model.
     *
     * @return The {@link PartialResult} value.
     */
    public PartialResult getValue() {
        return value;
    }

    @Override
    public JsonWriter toJson(JsonWriter jsonWriter) throws IOException {
        return value.toJson(jsonWriter);
    }

    /**
     * Reads an instance of {@link RetrievalPartialResultEvents} from the {@link JsonReader}.
     *
     * @param jsonReader The reader to read from.
     * @return The parsed wrapper.
     * @throws IOException If an error occurs while reading.
     */
    public static RetrievalPartialResultEvents fromJson(JsonReader jsonReader) throws IOException {
        return new RetrievalPartialResultEvents(PartialResult.fromJson(jsonReader));
    }
}
