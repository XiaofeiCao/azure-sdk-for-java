// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.http;

import com.azure.core.implementation.util.ServerSentEventHelper;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServerSentEventTests {
    @Test
    public void helperPopulatesEventFields() {
        ServerSentEvent<String> event = new ServerSentEvent<>();

        ServerSentEventHelper.setId(event, "42");
        ServerSentEventHelper.setEvent(event, "stockUpdate");
        ServerSentEventHelper.setData(event, "payload");
        ServerSentEventHelper.setComment(event, "comment");
        ServerSentEventHelper.setRetryAfter(event, Duration.ofSeconds(2));

        assertEquals("42", event.getId());
        assertEquals("stockUpdate", event.getEvent());
        assertEquals("payload", event.getData());
        assertEquals("comment", event.getComment());
        assertEquals(Duration.ofSeconds(2), event.getRetryAfter());
    }
}
