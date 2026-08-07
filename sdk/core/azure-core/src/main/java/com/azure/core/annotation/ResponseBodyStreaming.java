// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Indicates that a REST operation returns a response body that must be streamed instead of eagerly buffered.
 *
 * <p>This annotation is used by generated service interfaces for operations with unbounded or incrementally consumed
 * response bodies, such as server-sent event streams.</p>
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface ResponseBodyStreaming {
}
