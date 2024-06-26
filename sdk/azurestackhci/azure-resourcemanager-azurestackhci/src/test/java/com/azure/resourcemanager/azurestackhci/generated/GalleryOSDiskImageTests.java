// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.azurestackhci.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.azurestackhci.models.GalleryOSDiskImage;

public final class GalleryOSDiskImageTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        GalleryOSDiskImage model =
            BinaryData.fromString("{\"sizeInMB\":8523359422088474782}").toObject(GalleryOSDiskImage.class);
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        GalleryOSDiskImage model = new GalleryOSDiskImage();
        model = BinaryData.fromObject(model).toObject(GalleryOSDiskImage.class);
    }
}
