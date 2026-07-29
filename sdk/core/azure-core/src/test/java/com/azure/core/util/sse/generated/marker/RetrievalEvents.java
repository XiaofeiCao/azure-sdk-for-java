// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.util.sse.generated.marker;

import com.azure.json.JsonReader;

import java.io.IOException;

/**
 * Fixture marker interface for the scenario 3 {@code @events} union (§5b). Identical shape to
 * {@link ResponseEvents}; only the variant set differs.
 */
public interface RetrievalEvents {
    /**
     * Deserializes a variant of this union using the SSE {@code event:} frame field as the discriminator.
     *
     * @param jsonReader The reader over the SSE event's {@code data} payload.
     * @param eventName The value of the SSE {@code event:} field.
     * @return The deserialized variant, or {@code null} for an unknown event name.
     * @throws IOException If an error occurs while reading.
     */
    static RetrievalEvents fromJson(JsonReader jsonReader, String eventName) throws IOException {
        switch (eventName) {
            case "partialResult":
                return PartialResult.fromJson(jsonReader);

            case "finalResult":
                return FinalResult.fromJson(jsonReader);

            default:
                return null;
        }
    }
}
