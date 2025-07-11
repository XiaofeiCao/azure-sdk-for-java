// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.ai.textanalytics.implementation.models;

import com.azure.core.annotation.Fluent;
import com.azure.core.annotation.Generated;
import com.azure.json.JsonReader;
import com.azure.json.JsonToken;
import com.azure.json.JsonWriter;
import java.io.IOException;

/**
 * An object representing the task definition for an Abstractive Summarization task.
 */
@Fluent
public final class AbstractiveSummarizationLROTask extends AnalyzeTextLROTask {
    /*
     * Enumeration of supported long-running Text Analysis tasks.
     */
    @Generated
    private AnalyzeTextLROTaskKind kind = AnalyzeTextLROTaskKind.ABSTRACTIVE_SUMMARIZATION;

    /*
     * Supported parameters for the pre-build Abstractive Summarization task.
     */
    @Generated
    private AbstractiveSummarizationTaskParameters parameters;

    /**
     * Creates an instance of AbstractiveSummarizationLROTask class.
     */
    @Generated
    public AbstractiveSummarizationLROTask() {
    }

    /**
     * Get the kind property: Enumeration of supported long-running Text Analysis tasks.
     * 
     * @return the kind value.
     */
    @Generated
    @Override
    public AnalyzeTextLROTaskKind getKind() {
        return this.kind;
    }

    /**
     * Get the parameters property: Supported parameters for the pre-build Abstractive Summarization task.
     * 
     * @return the parameters value.
     */
    @Generated
    public AbstractiveSummarizationTaskParameters getParameters() {
        return this.parameters;
    }

    /**
     * Set the parameters property: Supported parameters for the pre-build Abstractive Summarization task.
     * 
     * @param parameters the parameters value to set.
     * @return the AbstractiveSummarizationLROTask object itself.
     */
    @Generated
    public AbstractiveSummarizationLROTask setParameters(AbstractiveSummarizationTaskParameters parameters) {
        this.parameters = parameters;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Generated
    @Override
    public AbstractiveSummarizationLROTask setTaskName(String taskName) {
        super.setTaskName(taskName);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Generated
    @Override
    public JsonWriter toJson(JsonWriter jsonWriter) throws IOException {
        jsonWriter.writeStartObject();
        jsonWriter.writeStringField("taskName", getTaskName());
        jsonWriter.writeJsonField("parameters", this.parameters);
        jsonWriter.writeStringField("kind", this.kind == null ? null : this.kind.toString());
        return jsonWriter.writeEndObject();
    }

    /**
     * Reads an instance of AbstractiveSummarizationLROTask from the JsonReader.
     * 
     * @param jsonReader The JsonReader being read.
     * @return An instance of AbstractiveSummarizationLROTask if the JsonReader was pointing to an instance of it, or
     * null if it was pointing to JSON null.
     * @throws IllegalStateException If the deserialized JSON object was missing any required properties.
     * @throws IOException If an error occurs while reading the AbstractiveSummarizationLROTask.
     */
    @Generated
    public static AbstractiveSummarizationLROTask fromJson(JsonReader jsonReader) throws IOException {
        return jsonReader.readObject(reader -> {
            AbstractiveSummarizationLROTask deserializedAbstractiveSummarizationLROTask
                = new AbstractiveSummarizationLROTask();
            while (reader.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = reader.getFieldName();
                reader.nextToken();

                if ("taskName".equals(fieldName)) {
                    deserializedAbstractiveSummarizationLROTask.setTaskName(reader.getString());
                } else if ("parameters".equals(fieldName)) {
                    deserializedAbstractiveSummarizationLROTask.parameters
                        = AbstractiveSummarizationTaskParameters.fromJson(reader);
                } else if ("kind".equals(fieldName)) {
                    deserializedAbstractiveSummarizationLROTask.kind
                        = AnalyzeTextLROTaskKind.fromString(reader.getString());
                } else {
                    reader.skipChildren();
                }
            }

            return deserializedAbstractiveSummarizationLROTask;
        });
    }
}
