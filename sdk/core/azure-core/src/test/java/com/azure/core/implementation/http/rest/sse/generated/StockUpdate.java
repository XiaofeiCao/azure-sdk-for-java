// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.implementation.http.rest.sse.generated;

import com.azure.json.JsonReader;
import com.azure.json.JsonSerializable;
import com.azure.json.JsonToken;
import com.azure.json.JsonWriter;

import java.io.IOException;

public final class StockUpdate implements JsonSerializable<StockUpdate> {
    private String symbol;
    private float price;

    public String getSymbol() {
        return symbol;
    }

    public StockUpdate setSymbol(String symbol) {
        this.symbol = symbol;
        return this;
    }

    public float getPrice() {
        return price;
    }

    public StockUpdate setPrice(float price) {
        this.price = price;
        return this;
    }

    @Override
    public JsonWriter toJson(JsonWriter jsonWriter) throws IOException {
        return jsonWriter.writeStartObject()
            .writeStringField("symbol", symbol)
            .writeFloatField("price", price)
            .writeEndObject();
    }

    public static StockUpdate fromJson(JsonReader jsonReader) throws IOException {
        return jsonReader.readObject(reader -> {
            StockUpdate model = new StockUpdate();
            while (reader.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = reader.getFieldName();
                reader.nextToken();
                if ("symbol".equals(fieldName)) {
                    model.symbol = reader.getString();
                } else if ("price".equals(fieldName)) {
                    model.price = reader.getFloat();
                } else {
                    reader.skipChildren();
                }
            }
            return model;
        });
    }
}
