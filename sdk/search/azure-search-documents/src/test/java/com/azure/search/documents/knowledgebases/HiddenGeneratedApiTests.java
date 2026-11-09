// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.search.documents.knowledgebases;

import com.azure.core.http.rest.RequestOptions;
import com.azure.core.util.BinaryData;
import com.azure.search.documents.knowledgebases.models.KnowledgeBaseRetrievalOptions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HiddenGeneratedApiTests {
    @ParameterizedTest
    @ValueSource(classes = { KnowledgeBaseRetrievalClient.class, KnowledgeBaseRetrievalAsyncClient.class })
    public void rawStreamConvenienceMethodsRemainPackagePrivate(Class<?> clientClass) throws NoSuchMethodException {
        boolean async = clientClass == KnowledgeBaseRetrievalAsyncClient.class;
        String returnType
            = async ? "reactor.core.publisher.Mono<com.azure.core.util.BinaryData>" : "com.azure.core.util.BinaryData";
        for (Class<?>[] parameters : new Class<?>[][] {
            { KnowledgeBaseRetrievalOptions.class },
            { KnowledgeBaseRetrievalOptions.class, String.class } }) {
            Method method = clientClass.getDeclaredMethod("hiddenGeneratedRetrieveStream", parameters);
            assertEquals(0, method.getModifiers() & (Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE));
            assertEquals(async ? Mono.class : BinaryData.class, method.getReturnType());
            assertEquals(returnType, method.getGenericReturnType().getTypeName());
        }
        Method protocolMethod = clientClass.getDeclaredMethod("hiddenGeneratedRetrieveStreamWithResponse",
            BinaryData.class, RequestOptions.class);
        assertEquals(0, protocolMethod.getModifiers() & (Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE));
    }
}
