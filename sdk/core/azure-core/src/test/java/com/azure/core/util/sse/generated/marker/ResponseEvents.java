// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.util.sse.generated.marker;

import com.azure.json.JsonReader;

import java.io.IOException;

/**
 * Fixture marker interface for the scenario 2 {@code @events} union (§5b, marker-interface shape).
 * <p>
 * Unlike the wrapper-hierarchy shape, the union base is a plain marker interface (<em>not</em>
 * {@code JsonSerializable}) that each real event model {@code implements}, so the streamed element type is the
 * marker itself and consumers switch directly on the concrete models
 * ({@code if (evt instanceof ResponseCreated c) ...}) without a wrapper. The SSE {@code event:} frame field is the
 * external discriminator, threaded into the {@code static} {@link #fromJson(JsonReader, String)} dispatch.
 * <p>
 * The interface static {@code fromJson(reader, eventName)} and each variant's own {@code fromJson(reader)} never
 * collide: interface statics are not inherited, so they are always invoked qualified by type name.
 */
public interface ResponseEvents {
    /**
     * Deserializes a variant of this union using the SSE {@code event:} frame field as the discriminator.
     *
     * @param jsonReader The reader over the SSE event's {@code data} payload.
     * @param eventName The value of the SSE {@code event:} field.
     * @return The deserialized variant, or {@code null} for an unknown event name (forward-compatibility: skip rather
     * than throw).
     * @throws IOException If an error occurs while reading.
     */
    static ResponseEvents fromJson(JsonReader jsonReader, String eventName) throws IOException {
        switch (eventName) {
            case "responseCreated":
                return ResponseCreated.fromJson(jsonReader);

            case "responseDelta":
                return ResponseDelta.fromJson(jsonReader);

            default:
                return null;
        }
    }
}
