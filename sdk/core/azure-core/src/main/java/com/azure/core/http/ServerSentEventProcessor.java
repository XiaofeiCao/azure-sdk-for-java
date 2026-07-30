// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.http;

import com.azure.core.util.Context;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Duration;

/**
 * Processes listener-enabled server-sent event responses.
 */
final class ServerSentEventProcessor {
    private static final String TEXT_EVENT_STREAM = "text/event-stream";
    private static final HttpHeaderName LAST_EVENT_ID = HttpHeaderName.fromString("Last-Event-Id");

    /**
     * Processes an asynchronous response when it is a listener-enabled event stream.
     *
     * @param pipeline The HTTP pipeline.
     * @param response The HTTP response.
     * @param context The request context.
     * @return A response publisher.
     */
    static Mono<HttpResponse> processAsync(HttpPipeline pipeline, HttpResponse response, Context context) {
        if (response == null || response.getRequest() == null) {
            return Mono.justOrEmpty(response);
        }
        HttpRequest request = response.getRequest();
        ServerSentEventListener listener = request.getServerSentEventListener();
        if (listener == null || !isTextEventStream(response)) {
            return Mono.just(response);
        }

        ServerSentEventParser parser
            = new ServerSentEventParser(listener, request.getHeaders().getValue(LAST_EVENT_ID));
        return response.getBody().doOnNext(buffer -> accept(parser, buffer)).then(Mono.fromCallable(() -> {
            parser.complete();
            return parser;
        }))
            .flatMap(completedParser -> retryAsync(pipeline, response, completedParser, context))
            .onErrorResume(error -> {
                listener.onError(unwrap(error));
                response.close();
                return Mono.just(new ServerSentEventHttpResponse(response));
            });
    }

    /**
     * Processes a synchronous response when it is a listener-enabled event stream.
     *
     * @param pipeline The HTTP pipeline.
     * @param response The HTTP response.
     * @param context The request context.
     * @return The processed response.
     */
    static HttpResponse processSync(HttpPipeline pipeline, HttpResponse response, Context context) {
        if (response == null || response.getRequest() == null) {
            return response;
        }

        HttpResponse currentResponse = response;
        while (true) {
            HttpRequest request = currentResponse.getRequest();
            ServerSentEventListener listener = request.getServerSentEventListener();
            if (listener == null || !isTextEventStream(currentResponse)) {
                return currentResponse;
            }

            ServerSentEventParser parser
                = new ServerSentEventParser(listener, request.getHeaders().getValue(LAST_EVENT_ID));
            try {
                currentResponse.getBody().toIterable().forEach(buffer -> accept(parser, buffer));
                parser.complete();
            } catch (RuntimeException | IOException error) {
                listener.onError(unwrap(error));
                currentResponse.close();
                return new ServerSentEventHttpResponse(currentResponse);
            }

            Duration retryAfter = parser.getRetryAfter();
            if (retryAfter == null || Thread.currentThread().isInterrupted()) {
                currentResponse.close();
                return new ServerSentEventHttpResponse(currentResponse);
            }

            HttpRequest retryRequest = prepareRetry(currentResponse, parser);
            currentResponse.close();
            try {
                Thread.sleep(retryAfter.toMillis());
            } catch (InterruptedException interruptedException) {
                Thread.currentThread().interrupt();
                return new ServerSentEventHttpResponse(currentResponse);
            }
            currentResponse = pipeline.sendSync(retryRequest, context, false);
        }
    }

    private static Mono<HttpResponse> retryAsync(HttpPipeline pipeline, HttpResponse response,
        ServerSentEventParser parser, Context context) {
        Duration retryAfter = parser.getRetryAfter();
        if (retryAfter == null) {
            response.close();
            return Mono.just(new ServerSentEventHttpResponse(response));
        }

        HttpRequest retryRequest = prepareRetry(response, parser);
        response.close();
        return Mono.delay(retryAfter).flatMap(ignored -> pipeline.send(retryRequest, context));
    }

    private static HttpRequest prepareRetry(HttpResponse response, ServerSentEventParser parser) {
        HttpRequest retryRequest = response.getRequest().copy();
        if (parser.getLastEventId() == null || parser.getLastEventId().isEmpty()) {
            retryRequest.getHeaders().remove(LAST_EVENT_ID);
        } else {
            retryRequest.getHeaders().set(LAST_EVENT_ID, parser.getLastEventId());
        }
        return retryRequest;
    }

    private static boolean isTextEventStream(HttpResponse response) {
        String contentType = response.getHeaderValue(HttpHeaderName.CONTENT_TYPE);
        if (contentType == null) {
            return false;
        }
        int parameterIndex = contentType.indexOf(';');
        String mediaType = parameterIndex < 0 ? contentType : contentType.substring(0, parameterIndex);
        return TEXT_EVENT_STREAM.equalsIgnoreCase(mediaType.trim());
    }

    private static void accept(ServerSentEventParser parser, java.nio.ByteBuffer buffer) {
        try {
            parser.accept(buffer);
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    private static Throwable unwrap(Throwable throwable) {
        return throwable instanceof UncheckedIOException ? throwable.getCause() : throwable;
    }

    private ServerSentEventProcessor() {
    }
}
