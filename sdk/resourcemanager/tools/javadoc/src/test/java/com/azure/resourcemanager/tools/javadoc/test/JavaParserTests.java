// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.resourcemanager.tools.javadoc.test;

import com.github.chhorz.javadoc.JavaDoc;
import com.github.chhorz.javadoc.JavaDocParser;
import com.github.chhorz.javadoc.JavaDocParserBuilder;
import org.junit.jupiter.api.Test;

/**
 * @author xiaofeicao
 * @createdAt 2023-09-07 4:57 PM
 */
public class JavaParserTests {

    private final JavaDocParser parser = JavaDocParserBuilder.withStandardJavadocTags().build();

    @Test
    public void testParseDescription() {
        String javadocString = "@return the operational state of the application gateway";
        JavaDoc javaDoc = parser.parse(javadocString);
        String description = javaDoc.getDescription();
        System.out.println(description);
        System.out.println(javaDoc.getTags());
    }

    @Test
    public void testParseReturnTag() {

    }
}
