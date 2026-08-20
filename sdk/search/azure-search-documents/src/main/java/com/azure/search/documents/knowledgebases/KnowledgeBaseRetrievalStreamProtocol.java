// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.search.documents.knowledgebases;

import com.azure.core.exception.ClientAuthenticationException;
import com.azure.core.exception.HttpResponseException;
import com.azure.core.exception.ResourceModifiedException;
import com.azure.core.exception.ResourceNotFoundException;
import com.azure.core.annotation.BodyParam;
import com.azure.core.annotation.ExpectedResponses;
import com.azure.core.annotation.HeaderParam;
import com.azure.core.annotation.Host;
import com.azure.core.annotation.HostParam;
import com.azure.core.annotation.PathParam;
import com.azure.core.annotation.Post;
import com.azure.core.annotation.QueryParam;
import com.azure.core.annotation.ServiceInterface;
import com.azure.core.annotation.UnexpectedResponseExceptionType;
import com.azure.core.http.rest.RequestOptions;
import com.azure.core.http.rest.RestProxy;
import com.azure.core.http.rest.StreamResponse;
import com.azure.core.util.BinaryData;
import com.azure.core.util.Context;
import com.azure.core.util.FluxUtil;
import com.azure.search.documents.SearchServiceVersion;
import com.azure.search.documents.implementation.KnowledgeBaseRetrievalClientImpl;
import reactor.core.publisher.Mono;

final class KnowledgeBaseRetrievalStreamProtocol {
    private static final String ACCEPT = "text/event-stream";
    private static final String CONTENT_TYPE = "application/json";
    private static final String API_VERSION = SearchServiceVersion.V2026_08_01_PREVIEW.getVersion();

    private final KnowledgeBaseRetrievalClientImpl client;
    private final Service service;

    KnowledgeBaseRetrievalStreamProtocol(KnowledgeBaseRetrievalClientImpl client) {
        this.client = client;
        this.service = RestProxy.create(Service.class, client.getHttpPipeline(), client.getSerializerAdapter());
    }

    Mono<StreamResponse> retrieveAsync(BinaryData retrievalRequest, RequestOptions requestOptions) {
        return FluxUtil.withContext(context -> service.retrieve(client.getEndpoint(), API_VERSION, ACCEPT,
            client.getKnowledgeBaseName(), CONTENT_TYPE, retrievalRequest, requestOptions, context));
    }

    StreamResponse retrieve(BinaryData retrievalRequest, RequestOptions requestOptions) {
        return service.retrieveSync(client.getEndpoint(), API_VERSION, ACCEPT, client.getKnowledgeBaseName(),
            CONTENT_TYPE, retrievalRequest, requestOptions, Context.NONE);
    }

    @Host("{endpoint}")
    @ServiceInterface(name = "KnowledgeBaseRetrievalStream")
    private interface Service {
        @Post("/knowledgebases('{knowledgeBaseName}')/retrieve")
        @ExpectedResponses({ 200, 204, 206 })
        @UnexpectedResponseExceptionType(value = ClientAuthenticationException.class, code = { 401 })
        @UnexpectedResponseExceptionType(value = ResourceNotFoundException.class, code = { 404 })
        @UnexpectedResponseExceptionType(value = ResourceModifiedException.class, code = { 409 })
        @UnexpectedResponseExceptionType(HttpResponseException.class)
        Mono<StreamResponse> retrieve(@HostParam("endpoint") String endpoint,
            @QueryParam("api-version") String apiVersion, @HeaderParam("Accept") String accept,
            @PathParam("knowledgeBaseName") String knowledgeBaseName, @HeaderParam("Content-Type") String contentType,
            @BodyParam("application/json") BinaryData retrievalRequest, RequestOptions requestOptions, Context context);

        @Post("/knowledgebases('{knowledgeBaseName}')/retrieve")
        @ExpectedResponses({ 200, 204, 206 })
        @UnexpectedResponseExceptionType(value = ClientAuthenticationException.class, code = { 401 })
        @UnexpectedResponseExceptionType(value = ResourceNotFoundException.class, code = { 404 })
        @UnexpectedResponseExceptionType(value = ResourceModifiedException.class, code = { 409 })
        @UnexpectedResponseExceptionType(HttpResponseException.class)
        StreamResponse retrieveSync(@HostParam("endpoint") String endpoint,
            @QueryParam("api-version") String apiVersion, @HeaderParam("Accept") String accept,
            @PathParam("knowledgeBaseName") String knowledgeBaseName, @HeaderParam("Content-Type") String contentType,
            @BodyParam("application/json") BinaryData retrievalRequest, RequestOptions requestOptions, Context context);
    }
}
