// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

module com.azure.v2.storage.blob {
    requires transitive io.clientcore.core;
    requires transitive com.azure.v2.core;

    exports com.azure.v2.storage.blob;
    exports com.azure.v2.storage.blob.models;

    opens com.azure.v2.storage.blob.models to io.clientcore.core;
}
