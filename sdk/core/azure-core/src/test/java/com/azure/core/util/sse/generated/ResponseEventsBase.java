// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.util.sse.generated;

import com.azure.json.JsonReader;

import java.io.IOException;

/**
 * Fixture base type for the scenario 2 {@code @events} union (§6b, wrapper-hierarchy shape).
 * <p>
 * The discriminator for an SSE union is the SSE {@code event:} <em>frame</em> field, not a JSON payload property, so
 * dispatch is done via an <em>externally-supplied</em> discriminator ({@link #fromJson(JsonReader, String)}) rather
 * than the usual payload-driven polymorphic {@code fromJson(JsonReader)}. Each named variant is a thin wrapper
 * subtype that delegates serialization to the real, untouched event model.
 */
public abstract class ResponseEventsBase {
    /**
     * Creates a new instance. Only subclasses may be instantiated.
     */
    protected ResponseEventsBase() {
    }

    /**
     * Deserializes a variant of this union using the SSE {@code event:} frame field as the discriminator.
     *
     * @param jsonReader The reader over the SSE event's {@code data} payload.
     * @param eventName The value of the SSE {@code event:} field.
     * @return The deserialized variant, or {@code null} for an unknown event name (forward-compatibility: skip rather
     * than throw).
     * @throws IOException If an error occurs while reading.
     */
    public static ResponseEventsBase fromJson(JsonReader jsonReader, String eventName) throws IOException {
        switch (eventName) {
            case "responseCreated":
                return ResponseCreatedResponseEvents.fromJson(jsonReader);

            case "responseDelta":
                return ResponseDeltaResponseEvents.fromJson(jsonReader);

            default:
                return null;
        }
    }
}
