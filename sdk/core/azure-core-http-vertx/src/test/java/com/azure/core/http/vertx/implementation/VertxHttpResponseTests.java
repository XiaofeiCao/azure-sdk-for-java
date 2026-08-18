// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.http.vertx.implementation;

import com.azure.core.http.HttpMethod;
import com.azure.core.http.HttpRequest;
import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.net.NetSocket;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.net.URI;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class VertxHttpResponseTests {
    @Test
    public void bodyCancellationClosesSocketOnce() throws Exception {
        HttpClientResponse response = mock(HttpClientResponse.class);
        NetSocket socket = mock(NetSocket.class);
        when(response.pause()).thenReturn(response);
        when(response.headers()).thenReturn(MultiMap.caseInsensitiveMultiMap());
        when(response.handler(any())).thenReturn(response);
        when(response.endHandler(any())).thenReturn(response);
        when(response.exceptionHandler(any())).thenReturn(response);
        when(response.resume()).thenReturn(response);
        when(response.netSocket()).thenReturn(socket);
        when(socket.close()).thenReturn(Future.succeededFuture());
        HttpRequest request = new HttpRequest(HttpMethod.GET, new URI("https://localhost/stream").toURL());
        VertxHttpResponse azureResponse = new VertxHttpResponse(request, response);

        StepVerifier.create(azureResponse.getBody()).thenCancel().verify();
        azureResponse.close();

        verify(socket, times(1)).close();
    }
}
