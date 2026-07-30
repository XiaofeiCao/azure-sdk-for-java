// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.validation.http.sse;

/**
 * Request model for the retrieval SSE scenario.
 */
public final class RetrievalRequest {
    private final String query;

    public RetrievalRequest(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }
}
