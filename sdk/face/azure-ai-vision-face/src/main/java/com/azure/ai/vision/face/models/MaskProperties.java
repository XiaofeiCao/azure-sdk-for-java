// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) TypeSpec Code Generator.
package com.azure.ai.vision.face.models;

import com.azure.core.annotation.Generated;
import com.azure.core.annotation.Immutable;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Properties describing the presence of a mask on a given face.
 */
@Immutable
public final class MaskProperties {

    /*
     * A boolean value indicating whether nose and mouth are covered.
     */
    @Generated
    @JsonProperty(value = "noseAndMouthCovered")
    private final boolean noseAndMouthCovered;

    /*
     * Type of the mask.
     */
    @Generated
    @JsonProperty(value = "type")
    private final MaskType type;

    /**
     * Creates an instance of MaskProperties class.
     *
     * @param noseAndMouthCovered the noseAndMouthCovered value to set.
     * @param type the type value to set.
     */
    @Generated
    @JsonCreator
    private MaskProperties(@JsonProperty(value = "noseAndMouthCovered") boolean noseAndMouthCovered,
        @JsonProperty(value = "type") MaskType type) {
        this.noseAndMouthCovered = noseAndMouthCovered;
        this.type = type;
    }

    /**
     * Get the noseAndMouthCovered property: A boolean value indicating whether nose and mouth are covered.
     *
     * @return the noseAndMouthCovered value.
     */
    @Generated
    public boolean isNoseAndMouthCovered() {
        return this.noseAndMouthCovered;
    }

    /**
     * Get the type property: Type of the mask.
     *
     * @return the type value.
     */
    @Generated
    public MaskType getType() {
        return this.type;
    }
}
