// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.util.sse.generated;

import com.azure.json.JsonReader;

import java.io.IOException;

/**
 * Fixture base type for the scenario 3 {@code @events} union (§5a, wrapper-hierarchy shape). Identical dispatch
 * shape to {@link ResponseEventsBase}; only the variant set differs.
 */
public abstract class RetrievalEventsBase {
    /**
     * Creates a new instance. Only subclasses may be instantiated.
     */
    protected RetrievalEventsBase() {
    }

    /**
     * Deserializes a variant of this union using the SSE {@code event:} frame field as the discriminator.
     *
     * @param jsonReader The reader over the SSE event's {@code data} payload.
     * @param eventName The value of the SSE {@code event:} field.
     * @return The deserialized variant, or {@code null} for an unknown event name.
     * @throws IOException If an error occurs while reading.
     */
    public static RetrievalEventsBase fromJson(JsonReader jsonReader, String eventName) throws IOException {
        switch (eventName) {
            case "partialResult":
                return RetrievalPartialResultEvents.fromJson(jsonReader);

            case "finalResult":
                return RetrievalFinalResultEvents.fromJson(jsonReader);

            default:
                return null;
        }
    }
}
