// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.util.sse.generated;

import com.azure.json.JsonReader;
import com.azure.json.JsonSerializable;
import com.azure.json.JsonWriter;

import java.io.IOException;

/**
 * Fixture wrapper subtype for the {@code finalResult} variant of the scenario 3 union (§6c).
 */
public final class RetrievalFinalResultEvents extends RetrievalEventsBase
    implements JsonSerializable<RetrievalFinalResultEvents> {
    private final FinalResult value;

    /**
     * Creates the wrapper.
     *
     * @param value The wrapped {@link FinalResult} model.
     */
    public RetrievalFinalResultEvents(FinalResult value) {
        this.value = value;
    }

    /**
     * Gets the wrapped model.
     *
     * @return The {@link FinalResult} value.
     */
    public FinalResult getValue() {
        return value;
    }

    @Override
    public JsonWriter toJson(JsonWriter jsonWriter) throws IOException {
        return value.toJson(jsonWriter);
    }

    /**
     * Reads an instance of {@link RetrievalFinalResultEvents} from the {@link JsonReader}.
     *
     * @param jsonReader The reader to read from.
     * @return The parsed wrapper.
     * @throws IOException If an error occurs while reading.
     */
    public static RetrievalFinalResultEvents fromJson(JsonReader jsonReader) throws IOException {
        return new RetrievalFinalResultEvents(FinalResult.fromJson(jsonReader));
    }
}
