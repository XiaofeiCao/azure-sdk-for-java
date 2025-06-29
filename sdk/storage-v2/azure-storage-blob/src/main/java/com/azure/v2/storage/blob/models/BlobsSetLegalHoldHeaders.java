// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.v2.storage.blob.models;

import io.clientcore.core.annotations.Metadata;
import io.clientcore.core.annotations.MetadataProperties;
import io.clientcore.core.http.models.HttpHeaderName;
import io.clientcore.core.http.models.HttpHeaders;
import io.clientcore.core.utils.DateTimeRfc1123;
import java.time.OffsetDateTime;

/**
 * The BlobsSetLegalHoldHeaders model.
 */
@Metadata(properties = { MetadataProperties.FLUENT })
public final class BlobsSetLegalHoldHeaders {
    /*
     * The x-ms-version property.
     */
    @Metadata(properties = { MetadataProperties.GENERATED })
    private String xMsVersion;

    /*
     * The x-ms-request-id property.
     */
    @Metadata(properties = { MetadataProperties.GENERATED })
    private String xMsRequestId;

    /*
     * The x-ms-legal-hold property.
     */
    @Metadata(properties = { MetadataProperties.GENERATED })
    private Boolean xMsLegalHold;

    /*
     * The x-ms-client-request-id property.
     */
    @Metadata(properties = { MetadataProperties.GENERATED })
    private String xMsClientRequestId;

    /*
     * The Date property.
     */
    @Metadata(properties = { MetadataProperties.GENERATED })
    private DateTimeRfc1123 date;

    private static final HttpHeaderName X_MS_VERSION = HttpHeaderName.fromString("x-ms-version");

    private static final HttpHeaderName X_MS_REQUEST_ID = HttpHeaderName.fromString("x-ms-request-id");

    private static final HttpHeaderName X_MS_LEGAL_HOLD = HttpHeaderName.fromString("x-ms-legal-hold");

    private static final HttpHeaderName X_MS_CLIENT_REQUEST_ID = HttpHeaderName.fromString("x-ms-client-request-id");

    // HttpHeaders containing the raw property values.
    /**
     * Creates an instance of BlobsSetLegalHoldHeaders class.
     * 
     * @param rawHeaders The raw HttpHeaders that will be used to create the property values.
     */
    public BlobsSetLegalHoldHeaders(HttpHeaders rawHeaders) {
        this.xMsVersion = rawHeaders.getValue(X_MS_VERSION);
        this.xMsRequestId = rawHeaders.getValue(X_MS_REQUEST_ID);
        String xMsLegalHold = rawHeaders.getValue(X_MS_LEGAL_HOLD);
        if (xMsLegalHold != null) {
            this.xMsLegalHold = Boolean.parseBoolean(xMsLegalHold);
        } else {
            this.xMsLegalHold = null;
        }
        this.xMsClientRequestId = rawHeaders.getValue(X_MS_CLIENT_REQUEST_ID);
        String date = rawHeaders.getValue(HttpHeaderName.DATE);
        if (date != null) {
            this.date = new DateTimeRfc1123(date);
        } else {
            this.date = null;
        }
    }

    /**
     * Get the xMsVersion property: The x-ms-version property.
     * 
     * @return the xMsVersion value.
     */
    @Metadata(properties = { MetadataProperties.GENERATED })
    public String getXMsVersion() {
        return this.xMsVersion;
    }

    /**
     * Set the xMsVersion property: The x-ms-version property.
     * 
     * @param xMsVersion the xMsVersion value to set.
     * @return the BlobsSetLegalHoldHeaders object itself.
     */
    @Metadata(properties = { MetadataProperties.GENERATED })
    public BlobsSetLegalHoldHeaders setXMsVersion(String xMsVersion) {
        this.xMsVersion = xMsVersion;
        return this;
    }

    /**
     * Get the xMsRequestId property: The x-ms-request-id property.
     * 
     * @return the xMsRequestId value.
     */
    @Metadata(properties = { MetadataProperties.GENERATED })
    public String getXMsRequestId() {
        return this.xMsRequestId;
    }

    /**
     * Set the xMsRequestId property: The x-ms-request-id property.
     * 
     * @param xMsRequestId the xMsRequestId value to set.
     * @return the BlobsSetLegalHoldHeaders object itself.
     */
    @Metadata(properties = { MetadataProperties.GENERATED })
    public BlobsSetLegalHoldHeaders setXMsRequestId(String xMsRequestId) {
        this.xMsRequestId = xMsRequestId;
        return this;
    }

    /**
     * Get the xMsLegalHold property: The x-ms-legal-hold property.
     * 
     * @return the xMsLegalHold value.
     */
    @Metadata(properties = { MetadataProperties.GENERATED })
    public Boolean isXMsLegalHold() {
        return this.xMsLegalHold;
    }

    /**
     * Set the xMsLegalHold property: The x-ms-legal-hold property.
     * 
     * @param xMsLegalHold the xMsLegalHold value to set.
     * @return the BlobsSetLegalHoldHeaders object itself.
     */
    @Metadata(properties = { MetadataProperties.GENERATED })
    public BlobsSetLegalHoldHeaders setXMsLegalHold(Boolean xMsLegalHold) {
        this.xMsLegalHold = xMsLegalHold;
        return this;
    }

    /**
     * Get the xMsClientRequestId property: The x-ms-client-request-id property.
     * 
     * @return the xMsClientRequestId value.
     */
    @Metadata(properties = { MetadataProperties.GENERATED })
    public String getXMsClientRequestId() {
        return this.xMsClientRequestId;
    }

    /**
     * Set the xMsClientRequestId property: The x-ms-client-request-id property.
     * 
     * @param xMsClientRequestId the xMsClientRequestId value to set.
     * @return the BlobsSetLegalHoldHeaders object itself.
     */
    @Metadata(properties = { MetadataProperties.GENERATED })
    public BlobsSetLegalHoldHeaders setXMsClientRequestId(String xMsClientRequestId) {
        this.xMsClientRequestId = xMsClientRequestId;
        return this;
    }

    /**
     * Get the date property: The Date property.
     * 
     * @return the date value.
     */
    @Metadata(properties = { MetadataProperties.GENERATED })
    public OffsetDateTime getDate() {
        if (this.date == null) {
            return null;
        }
        return this.date.getDateTime();
    }

    /**
     * Set the date property: The Date property.
     * 
     * @param date the date value to set.
     * @return the BlobsSetLegalHoldHeaders object itself.
     */
    @Metadata(properties = { MetadataProperties.GENERATED })
    public BlobsSetLegalHoldHeaders setDate(OffsetDateTime date) {
        if (date == null) {
            this.date = null;
        } else {
            this.date = new DateTimeRfc1123(date);
        }
        return this;
    }
}
