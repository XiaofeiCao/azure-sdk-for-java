// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.http;

import com.azure.core.implementation.util.ServerSentEventHelper;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ServerSentEventTests {
    @Test
    public void helperPopulatesEventFields() {
        ServerSentEvent event = new ServerSentEvent();
        List<String> data = new ArrayList<>(Arrays.asList("first", "second"));

        ServerSentEventHelper.setId(event, "42");
        ServerSentEventHelper.setEvent(event, "stockUpdate");
        ServerSentEventHelper.setData(event, data);
        ServerSentEventHelper.setComment(event, "comment");
        ServerSentEventHelper.setRetryAfter(event, Duration.ofSeconds(2));

        data.add("third");

        assertEquals("42", event.getId());
        assertEquals("stockUpdate", event.getEvent());
        assertEquals(Arrays.asList("first", "second"), event.getData());
        assertEquals("comment", event.getComment());
        assertEquals(Duration.ofSeconds(2), ServerSentEventHelper.getRetryAfter(event));
        assertThrows(UnsupportedOperationException.class, () -> event.getData().add("third"));
    }
}
