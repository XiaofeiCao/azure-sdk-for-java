// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.analytics.purview.scanning.implementation;

import com.azure.analytics.purview.scanning.PurviewScanningServiceVersion;
import com.azure.core.annotation.BodyParam;
import com.azure.core.annotation.Delete;
import com.azure.core.annotation.ExpectedResponses;
import com.azure.core.annotation.Get;
import com.azure.core.annotation.HeaderParam;
import com.azure.core.annotation.Host;
import com.azure.core.annotation.HostParam;
import com.azure.core.annotation.PathParam;
import com.azure.core.annotation.Put;
import com.azure.core.annotation.QueryParam;
import com.azure.core.annotation.ReturnType;
import com.azure.core.annotation.ServiceInterface;
import com.azure.core.annotation.ServiceMethod;
import com.azure.core.annotation.UnexpectedResponseExceptionType;
import com.azure.core.exception.ClientAuthenticationException;
import com.azure.core.exception.HttpResponseException;
import com.azure.core.exception.ResourceModifiedException;
import com.azure.core.exception.ResourceNotFoundException;
import com.azure.core.http.rest.PagedFlux;
import com.azure.core.http.rest.PagedIterable;
import com.azure.core.http.rest.PagedResponse;
import com.azure.core.http.rest.PagedResponseBase;
import com.azure.core.http.rest.RequestOptions;
import com.azure.core.http.rest.Response;
import com.azure.core.http.rest.RestProxy;
import com.azure.core.util.BinaryData;
import com.azure.core.util.Context;
import com.azure.core.util.FluxUtil;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import reactor.core.publisher.Mono;

/**
 * An instance of this class provides access to all the operations defined in KeyVaultConnections.
 */
public final class KeyVaultConnectionsImpl {
    /**
     * The proxy service used to perform REST calls.
     */
    private final KeyVaultConnectionsService service;

    /**
     * The service client containing this operation class.
     */
    private final PurviewScanningClientImpl client;

    /**
     * Initializes an instance of KeyVaultConnectionsImpl.
     * 
     * @param client the instance of the service client containing this operation class.
     */
    KeyVaultConnectionsImpl(PurviewScanningClientImpl client) {
        this.service = RestProxy.create(KeyVaultConnectionsService.class, client.getHttpPipeline(),
            client.getSerializerAdapter());
        this.client = client;
    }

    /**
     * Gets Service version.
     * 
     * @return the serviceVersion value.
     */
    public PurviewScanningServiceVersion getServiceVersion() {
        return client.getServiceVersion();
    }

    /**
     * The interface defining all the services for PurviewScanningClientKeyVaultConnections to be used by the proxy
     * service to perform REST calls.
     */
    @Host("{Endpoint}")
    @ServiceInterface(name = "PurviewScanningClientKeyVaultConnections")
    public interface KeyVaultConnectionsService {
        @Get("/azureKeyVaults/{keyVaultName}")
        @ExpectedResponses({ 200 })
        @UnexpectedResponseExceptionType(value = ClientAuthenticationException.class, code = { 401 })
        @UnexpectedResponseExceptionType(value = ResourceNotFoundException.class, code = { 404 })
        @UnexpectedResponseExceptionType(value = ResourceModifiedException.class, code = { 409 })
        @UnexpectedResponseExceptionType(HttpResponseException.class)
        Mono<Response<BinaryData>> get(@HostParam("Endpoint") String endpoint,
            @PathParam("keyVaultName") String keyVaultName, @QueryParam("api-version") String apiVersion,
            @HeaderParam("Accept") String accept, RequestOptions requestOptions, Context context);

        @Get("/azureKeyVaults/{keyVaultName}")
        @ExpectedResponses({ 200 })
        @UnexpectedResponseExceptionType(value = ClientAuthenticationException.class, code = { 401 })
        @UnexpectedResponseExceptionType(value = ResourceNotFoundException.class, code = { 404 })
        @UnexpectedResponseExceptionType(value = ResourceModifiedException.class, code = { 409 })
        @UnexpectedResponseExceptionType(HttpResponseException.class)
        Response<BinaryData> getSync(@HostParam("Endpoint") String endpoint,
            @PathParam("keyVaultName") String keyVaultName, @QueryParam("api-version") String apiVersion,
            @HeaderParam("Accept") String accept, RequestOptions requestOptions, Context context);

        @Put("/azureKeyVaults/{keyVaultName}")
        @ExpectedResponses({ 200 })
        @UnexpectedResponseExceptionType(value = ClientAuthenticationException.class, code = { 401 })
        @UnexpectedResponseExceptionType(value = ResourceNotFoundException.class, code = { 404 })
        @UnexpectedResponseExceptionType(value = ResourceModifiedException.class, code = { 409 })
        @UnexpectedResponseExceptionType(HttpResponseException.class)
        Mono<Response<BinaryData>> create(@HostParam("Endpoint") String endpoint,
            @PathParam("keyVaultName") String keyVaultName, @QueryParam("api-version") String apiVersion,
            @BodyParam("application/json") BinaryData body, @HeaderParam("Accept") String accept,
            RequestOptions requestOptions, Context context);

        @Put("/azureKeyVaults/{keyVaultName}")
        @ExpectedResponses({ 200 })
        @UnexpectedResponseExceptionType(value = ClientAuthenticationException.class, code = { 401 })
        @UnexpectedResponseExceptionType(value = ResourceNotFoundException.class, code = { 404 })
        @UnexpectedResponseExceptionType(value = ResourceModifiedException.class, code = { 409 })
        @UnexpectedResponseExceptionType(HttpResponseException.class)
        Response<BinaryData> createSync(@HostParam("Endpoint") String endpoint,
            @PathParam("keyVaultName") String keyVaultName, @QueryParam("api-version") String apiVersion,
            @BodyParam("application/json") BinaryData body, @HeaderParam("Accept") String accept,
            RequestOptions requestOptions, Context context);

        @Delete("/azureKeyVaults/{keyVaultName}")
        @ExpectedResponses({ 200, 204 })
        @UnexpectedResponseExceptionType(value = ClientAuthenticationException.class, code = { 401 })
        @UnexpectedResponseExceptionType(value = ResourceNotFoundException.class, code = { 404 })
        @UnexpectedResponseExceptionType(value = ResourceModifiedException.class, code = { 409 })
        @UnexpectedResponseExceptionType(HttpResponseException.class)
        Mono<Response<BinaryData>> delete(@HostParam("Endpoint") String endpoint,
            @PathParam("keyVaultName") String keyVaultName, @QueryParam("api-version") String apiVersion,
            @HeaderParam("Accept") String accept, RequestOptions requestOptions, Context context);

        @Delete("/azureKeyVaults/{keyVaultName}")
        @ExpectedResponses({ 200, 204 })
        @UnexpectedResponseExceptionType(value = ClientAuthenticationException.class, code = { 401 })
        @UnexpectedResponseExceptionType(value = ResourceNotFoundException.class, code = { 404 })
        @UnexpectedResponseExceptionType(value = ResourceModifiedException.class, code = { 409 })
        @UnexpectedResponseExceptionType(HttpResponseException.class)
        Response<BinaryData> deleteSync(@HostParam("Endpoint") String endpoint,
            @PathParam("keyVaultName") String keyVaultName, @QueryParam("api-version") String apiVersion,
            @HeaderParam("Accept") String accept, RequestOptions requestOptions, Context context);

        @Get("/azureKeyVaults")
        @ExpectedResponses({ 200 })
        @UnexpectedResponseExceptionType(value = ClientAuthenticationException.class, code = { 401 })
        @UnexpectedResponseExceptionType(value = ResourceNotFoundException.class, code = { 404 })
        @UnexpectedResponseExceptionType(value = ResourceModifiedException.class, code = { 409 })
        @UnexpectedResponseExceptionType(HttpResponseException.class)
        Mono<Response<BinaryData>> listAll(@HostParam("Endpoint") String endpoint,
            @QueryParam("api-version") String apiVersion, @HeaderParam("Accept") String accept,
            RequestOptions requestOptions, Context context);

        @Get("/azureKeyVaults")
        @ExpectedResponses({ 200 })
        @UnexpectedResponseExceptionType(value = ClientAuthenticationException.class, code = { 401 })
        @UnexpectedResponseExceptionType(value = ResourceNotFoundException.class, code = { 404 })
        @UnexpectedResponseExceptionType(value = ResourceModifiedException.class, code = { 409 })
        @UnexpectedResponseExceptionType(HttpResponseException.class)
        Response<BinaryData> listAllSync(@HostParam("Endpoint") String endpoint,
            @QueryParam("api-version") String apiVersion, @HeaderParam("Accept") String accept,
            RequestOptions requestOptions, Context context);

        @Get("{nextLink}")
        @ExpectedResponses({ 200 })
        @UnexpectedResponseExceptionType(value = ClientAuthenticationException.class, code = { 401 })
        @UnexpectedResponseExceptionType(value = ResourceNotFoundException.class, code = { 404 })
        @UnexpectedResponseExceptionType(value = ResourceModifiedException.class, code = { 409 })
        @UnexpectedResponseExceptionType(HttpResponseException.class)
        Mono<Response<BinaryData>> listAllNext(@PathParam(value = "nextLink", encoded = true) String nextLink,
            @HostParam("Endpoint") String endpoint, @HeaderParam("Accept") String accept, RequestOptions requestOptions,
            Context context);

        @Get("{nextLink}")
        @ExpectedResponses({ 200 })
        @UnexpectedResponseExceptionType(value = ClientAuthenticationException.class, code = { 401 })
        @UnexpectedResponseExceptionType(value = ResourceNotFoundException.class, code = { 404 })
        @UnexpectedResponseExceptionType(value = ResourceModifiedException.class, code = { 409 })
        @UnexpectedResponseExceptionType(HttpResponseException.class)
        Response<BinaryData> listAllNextSync(@PathParam(value = "nextLink", encoded = true) String nextLink,
            @HostParam("Endpoint") String endpoint, @HeaderParam("Accept") String accept, RequestOptions requestOptions,
            Context context);
    }

    /**
     * Gets key vault information.
     * <p><strong>Response Body Schema</strong></p>
     * 
     * <pre>
     * {@code
     * {
     *     id: String (Optional)
     *     name: String (Optional)
     *     properties (Optional): {
     *         baseUrl: String (Optional)
     *         description: String (Optional)
     *     }
     * }
     * }
     * </pre>
     * 
     * @param keyVaultName The keyVaultName parameter.
     * @param requestOptions The options to configure the HTTP request before HTTP client sends it.
     * @throws HttpResponseException thrown if the request is rejected by server.
     * @throws ClientAuthenticationException thrown if the request is rejected by server on status code 401.
     * @throws ResourceNotFoundException thrown if the request is rejected by server on status code 404.
     * @throws ResourceModifiedException thrown if the request is rejected by server on status code 409.
     * @return key vault information along with {@link Response} on successful completion of {@link Mono}.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public Mono<Response<BinaryData>> getWithResponseAsync(String keyVaultName, RequestOptions requestOptions) {
        final String accept = "application/json";
        return FluxUtil.withContext(context -> service.get(this.client.getEndpoint(), keyVaultName,
            this.client.getServiceVersion().getVersion(), accept, requestOptions, context));
    }

    /**
     * Gets key vault information.
     * <p><strong>Response Body Schema</strong></p>
     * 
     * <pre>
     * {@code
     * {
     *     id: String (Optional)
     *     name: String (Optional)
     *     properties (Optional): {
     *         baseUrl: String (Optional)
     *         description: String (Optional)
     *     }
     * }
     * }
     * </pre>
     * 
     * @param keyVaultName The keyVaultName parameter.
     * @param requestOptions The options to configure the HTTP request before HTTP client sends it.
     * @throws HttpResponseException thrown if the request is rejected by server.
     * @throws ClientAuthenticationException thrown if the request is rejected by server on status code 401.
     * @throws ResourceNotFoundException thrown if the request is rejected by server on status code 404.
     * @throws ResourceModifiedException thrown if the request is rejected by server on status code 409.
     * @return key vault information along with {@link Response}.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public Response<BinaryData> getWithResponse(String keyVaultName, RequestOptions requestOptions) {
        final String accept = "application/json";
        return service.getSync(this.client.getEndpoint(), keyVaultName, this.client.getServiceVersion().getVersion(),
            accept, requestOptions, Context.NONE);
    }

    /**
     * Creates an instance of a key vault connection.
     * <p><strong>Request Body Schema</strong></p>
     * 
     * <pre>
     * {@code
     * {
     *     id: String (Optional)
     *     name: String (Optional)
     *     properties (Optional): {
     *         baseUrl: String (Optional)
     *         description: String (Optional)
     *     }
     * }
     * }
     * </pre>
     * 
     * <p><strong>Response Body Schema</strong></p>
     * 
     * <pre>
     * {@code
     * {
     *     id: String (Optional)
     *     name: String (Optional)
     *     properties (Optional): {
     *         baseUrl: String (Optional)
     *         description: String (Optional)
     *     }
     * }
     * }
     * </pre>
     * 
     * @param keyVaultName The keyVaultName parameter.
     * @param body The body parameter.
     * @param requestOptions The options to configure the HTTP request before HTTP client sends it.
     * @throws HttpResponseException thrown if the request is rejected by server.
     * @throws ClientAuthenticationException thrown if the request is rejected by server on status code 401.
     * @throws ResourceNotFoundException thrown if the request is rejected by server on status code 404.
     * @throws ResourceModifiedException thrown if the request is rejected by server on status code 409.
     * @return the response body along with {@link Response} on successful completion of {@link Mono}.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public Mono<Response<BinaryData>> createWithResponseAsync(String keyVaultName, BinaryData body,
        RequestOptions requestOptions) {
        final String accept = "application/json";
        return FluxUtil.withContext(context -> service.create(this.client.getEndpoint(), keyVaultName,
            this.client.getServiceVersion().getVersion(), body, accept, requestOptions, context));
    }

    /**
     * Creates an instance of a key vault connection.
     * <p><strong>Request Body Schema</strong></p>
     * 
     * <pre>
     * {@code
     * {
     *     id: String (Optional)
     *     name: String (Optional)
     *     properties (Optional): {
     *         baseUrl: String (Optional)
     *         description: String (Optional)
     *     }
     * }
     * }
     * </pre>
     * 
     * <p><strong>Response Body Schema</strong></p>
     * 
     * <pre>
     * {@code
     * {
     *     id: String (Optional)
     *     name: String (Optional)
     *     properties (Optional): {
     *         baseUrl: String (Optional)
     *         description: String (Optional)
     *     }
     * }
     * }
     * </pre>
     * 
     * @param keyVaultName The keyVaultName parameter.
     * @param body The body parameter.
     * @param requestOptions The options to configure the HTTP request before HTTP client sends it.
     * @throws HttpResponseException thrown if the request is rejected by server.
     * @throws ClientAuthenticationException thrown if the request is rejected by server on status code 401.
     * @throws ResourceNotFoundException thrown if the request is rejected by server on status code 404.
     * @throws ResourceModifiedException thrown if the request is rejected by server on status code 409.
     * @return the response body along with {@link Response}.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public Response<BinaryData> createWithResponse(String keyVaultName, BinaryData body,
        RequestOptions requestOptions) {
        final String accept = "application/json";
        return service.createSync(this.client.getEndpoint(), keyVaultName, this.client.getServiceVersion().getVersion(),
            body, accept, requestOptions, Context.NONE);
    }

    /**
     * Deletes the key vault connection associated with the account.
     * <p><strong>Response Body Schema</strong></p>
     * 
     * <pre>
     * {@code
     * {
     *     id: String (Optional)
     *     name: String (Optional)
     *     properties (Optional): {
     *         baseUrl: String (Optional)
     *         description: String (Optional)
     *     }
     * }
     * }
     * </pre>
     * 
     * @param keyVaultName The keyVaultName parameter.
     * @param requestOptions The options to configure the HTTP request before HTTP client sends it.
     * @throws HttpResponseException thrown if the request is rejected by server.
     * @throws ClientAuthenticationException thrown if the request is rejected by server on status code 401.
     * @throws ResourceNotFoundException thrown if the request is rejected by server on status code 404.
     * @throws ResourceModifiedException thrown if the request is rejected by server on status code 409.
     * @return the response body along with {@link Response} on successful completion of {@link Mono}.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public Mono<Response<BinaryData>> deleteWithResponseAsync(String keyVaultName, RequestOptions requestOptions) {
        final String accept = "application/json";
        return FluxUtil.withContext(context -> service.delete(this.client.getEndpoint(), keyVaultName,
            this.client.getServiceVersion().getVersion(), accept, requestOptions, context));
    }

    /**
     * Deletes the key vault connection associated with the account.
     * <p><strong>Response Body Schema</strong></p>
     * 
     * <pre>
     * {@code
     * {
     *     id: String (Optional)
     *     name: String (Optional)
     *     properties (Optional): {
     *         baseUrl: String (Optional)
     *         description: String (Optional)
     *     }
     * }
     * }
     * </pre>
     * 
     * @param keyVaultName The keyVaultName parameter.
     * @param requestOptions The options to configure the HTTP request before HTTP client sends it.
     * @throws HttpResponseException thrown if the request is rejected by server.
     * @throws ClientAuthenticationException thrown if the request is rejected by server on status code 401.
     * @throws ResourceNotFoundException thrown if the request is rejected by server on status code 404.
     * @throws ResourceModifiedException thrown if the request is rejected by server on status code 409.
     * @return the response body along with {@link Response}.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public Response<BinaryData> deleteWithResponse(String keyVaultName, RequestOptions requestOptions) {
        final String accept = "application/json";
        return service.deleteSync(this.client.getEndpoint(), keyVaultName, this.client.getServiceVersion().getVersion(),
            accept, requestOptions, Context.NONE);
    }

    /**
     * List key vault connections in account.
     * <p><strong>Response Body Schema</strong></p>
     * 
     * <pre>
     * {@code
     * {
     *     id: String (Optional)
     *     name: String (Optional)
     *     properties (Optional): {
     *         baseUrl: String (Optional)
     *         description: String (Optional)
     *     }
     * }
     * }
     * </pre>
     * 
     * @param requestOptions The options to configure the HTTP request before HTTP client sends it.
     * @throws HttpResponseException thrown if the request is rejected by server.
     * @throws ClientAuthenticationException thrown if the request is rejected by server on status code 401.
     * @throws ResourceNotFoundException thrown if the request is rejected by server on status code 404.
     * @throws ResourceModifiedException thrown if the request is rejected by server on status code 409.
     * @return the response body along with {@link PagedResponse} on successful completion of {@link Mono}.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    private Mono<PagedResponse<BinaryData>> listAllSinglePageAsync(RequestOptions requestOptions) {
        final String accept = "application/json";
        return FluxUtil
            .withContext(context -> service.listAll(this.client.getEndpoint(),
                this.client.getServiceVersion().getVersion(), accept, requestOptions, context))
            .map(res -> new PagedResponseBase<>(res.getRequest(), res.getStatusCode(), res.getHeaders(),
                getValues(res.getValue(), "value"), getNextLink(res.getValue(), "nextLink"), null));
    }

    /**
     * List key vault connections in account.
     * <p><strong>Response Body Schema</strong></p>
     * 
     * <pre>
     * {@code
     * {
     *     id: String (Optional)
     *     name: String (Optional)
     *     properties (Optional): {
     *         baseUrl: String (Optional)
     *         description: String (Optional)
     *     }
     * }
     * }
     * </pre>
     * 
     * @param requestOptions The options to configure the HTTP request before HTTP client sends it.
     * @throws HttpResponseException thrown if the request is rejected by server.
     * @throws ClientAuthenticationException thrown if the request is rejected by server on status code 401.
     * @throws ResourceNotFoundException thrown if the request is rejected by server on status code 404.
     * @throws ResourceModifiedException thrown if the request is rejected by server on status code 409.
     * @return the paginated response with {@link PagedFlux}.
     */
    @ServiceMethod(returns = ReturnType.COLLECTION)
    public PagedFlux<BinaryData> listAllAsync(RequestOptions requestOptions) {
        RequestOptions requestOptionsForNextPage = new RequestOptions();
        requestOptionsForNextPage.setContext(
            requestOptions != null && requestOptions.getContext() != null ? requestOptions.getContext() : Context.NONE);
        return new PagedFlux<>(() -> listAllSinglePageAsync(requestOptions),
            nextLink -> listAllNextSinglePageAsync(nextLink, requestOptionsForNextPage));
    }

    /**
     * List key vault connections in account.
     * <p><strong>Response Body Schema</strong></p>
     * 
     * <pre>
     * {@code
     * {
     *     id: String (Optional)
     *     name: String (Optional)
     *     properties (Optional): {
     *         baseUrl: String (Optional)
     *         description: String (Optional)
     *     }
     * }
     * }
     * </pre>
     * 
     * @param requestOptions The options to configure the HTTP request before HTTP client sends it.
     * @throws HttpResponseException thrown if the request is rejected by server.
     * @throws ClientAuthenticationException thrown if the request is rejected by server on status code 401.
     * @throws ResourceNotFoundException thrown if the request is rejected by server on status code 404.
     * @throws ResourceModifiedException thrown if the request is rejected by server on status code 409.
     * @return the response body along with {@link PagedResponse}.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    private PagedResponse<BinaryData> listAllSinglePage(RequestOptions requestOptions) {
        final String accept = "application/json";
        Response<BinaryData> res = service.listAllSync(this.client.getEndpoint(),
            this.client.getServiceVersion().getVersion(), accept, requestOptions, Context.NONE);
        return new PagedResponseBase<>(res.getRequest(), res.getStatusCode(), res.getHeaders(),
            getValues(res.getValue(), "value"), getNextLink(res.getValue(), "nextLink"), null);
    }

    /**
     * List key vault connections in account.
     * <p><strong>Response Body Schema</strong></p>
     * 
     * <pre>
     * {@code
     * {
     *     id: String (Optional)
     *     name: String (Optional)
     *     properties (Optional): {
     *         baseUrl: String (Optional)
     *         description: String (Optional)
     *     }
     * }
     * }
     * </pre>
     * 
     * @param requestOptions The options to configure the HTTP request before HTTP client sends it.
     * @throws HttpResponseException thrown if the request is rejected by server.
     * @throws ClientAuthenticationException thrown if the request is rejected by server on status code 401.
     * @throws ResourceNotFoundException thrown if the request is rejected by server on status code 404.
     * @throws ResourceModifiedException thrown if the request is rejected by server on status code 409.
     * @return the paginated response with {@link PagedIterable}.
     */
    @ServiceMethod(returns = ReturnType.COLLECTION)
    public PagedIterable<BinaryData> listAll(RequestOptions requestOptions) {
        RequestOptions requestOptionsForNextPage = new RequestOptions();
        requestOptionsForNextPage.setContext(
            requestOptions != null && requestOptions.getContext() != null ? requestOptions.getContext() : Context.NONE);
        return new PagedIterable<>(() -> listAllSinglePage(requestOptions),
            nextLink -> listAllNextSinglePage(nextLink, requestOptionsForNextPage));
    }

    /**
     * Get the next page of items.
     * <p><strong>Response Body Schema</strong></p>
     * 
     * <pre>
     * {@code
     * {
     *     id: String (Optional)
     *     name: String (Optional)
     *     properties (Optional): {
     *         baseUrl: String (Optional)
     *         description: String (Optional)
     *     }
     * }
     * }
     * </pre>
     * 
     * @param nextLink The URL to get the next list of items.
     * @param requestOptions The options to configure the HTTP request before HTTP client sends it.
     * @throws HttpResponseException thrown if the request is rejected by server.
     * @throws ClientAuthenticationException thrown if the request is rejected by server on status code 401.
     * @throws ResourceNotFoundException thrown if the request is rejected by server on status code 404.
     * @throws ResourceModifiedException thrown if the request is rejected by server on status code 409.
     * @return the response body along with {@link PagedResponse} on successful completion of {@link Mono}.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    private Mono<PagedResponse<BinaryData>> listAllNextSinglePageAsync(String nextLink, RequestOptions requestOptions) {
        final String accept = "application/json";
        return FluxUtil
            .withContext(
                context -> service.listAllNext(nextLink, this.client.getEndpoint(), accept, requestOptions, context))
            .map(res -> new PagedResponseBase<>(res.getRequest(), res.getStatusCode(), res.getHeaders(),
                getValues(res.getValue(), "value"), getNextLink(res.getValue(), "nextLink"), null));
    }

    /**
     * Get the next page of items.
     * <p><strong>Response Body Schema</strong></p>
     * 
     * <pre>
     * {@code
     * {
     *     id: String (Optional)
     *     name: String (Optional)
     *     properties (Optional): {
     *         baseUrl: String (Optional)
     *         description: String (Optional)
     *     }
     * }
     * }
     * </pre>
     * 
     * @param nextLink The URL to get the next list of items.
     * @param requestOptions The options to configure the HTTP request before HTTP client sends it.
     * @throws HttpResponseException thrown if the request is rejected by server.
     * @throws ClientAuthenticationException thrown if the request is rejected by server on status code 401.
     * @throws ResourceNotFoundException thrown if the request is rejected by server on status code 404.
     * @throws ResourceModifiedException thrown if the request is rejected by server on status code 409.
     * @return the response body along with {@link PagedResponse}.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    private PagedResponse<BinaryData> listAllNextSinglePage(String nextLink, RequestOptions requestOptions) {
        final String accept = "application/json";
        Response<BinaryData> res
            = service.listAllNextSync(nextLink, this.client.getEndpoint(), accept, requestOptions, Context.NONE);
        return new PagedResponseBase<>(res.getRequest(), res.getStatusCode(), res.getHeaders(),
            getValues(res.getValue(), "value"), getNextLink(res.getValue(), "nextLink"), null);
    }

    private List<BinaryData> getValues(BinaryData binaryData, String path) {
        try {
            Map<?, ?> obj = binaryData.toObject(Map.class);
            List<?> values = (List<?>) obj.get(path);
            return values.stream().map(BinaryData::fromObject).collect(Collectors.toList());
        } catch (RuntimeException e) {
            return null;
        }
    }

    private String getNextLink(BinaryData binaryData, String path) {
        try {
            Map<?, ?> obj = binaryData.toObject(Map.class);
            return (String) obj.get(path);
        } catch (RuntimeException e) {
            return null;
        }
    }
}
