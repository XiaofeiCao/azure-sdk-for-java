// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.implementation.http.rest.sse.generated;

import com.azure.json.JsonReader;
import com.azure.json.JsonSerializable;
import com.azure.json.JsonWriter;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public final class ServiceStreamEvent implements JsonSerializable<ServiceStreamEvent> {
    private final UserLogin userLogin;
    private final UserLogout userLogout;
    private final StockUpdate stockUpdate;
    private final SystemAlert systemAlert;
    private final boolean terminal;

    private ServiceStreamEvent(UserLogin userLogin, UserLogout userLogout, StockUpdate stockUpdate,
        SystemAlert systemAlert, boolean terminal) {
        this.userLogin = userLogin;
        this.userLogout = userLogout;
        this.stockUpdate = stockUpdate;
        this.systemAlert = systemAlert;
        this.terminal = terminal;
    }

    static ServiceStreamEvent ofUserLogin(UserLogin value) {
        return new ServiceStreamEvent(Objects.requireNonNull(value, "'value' cannot be null."), null, null, null,
            false);
    }

    static ServiceStreamEvent ofUserLogout(UserLogout value) {
        return new ServiceStreamEvent(null, Objects.requireNonNull(value, "'value' cannot be null."), null, null,
            false);
    }

    static ServiceStreamEvent ofStockUpdate(StockUpdate value) {
        return new ServiceStreamEvent(null, null, Objects.requireNonNull(value, "'value' cannot be null."), null,
            false);
    }

    static ServiceStreamEvent ofSystemAlert(SystemAlert value) {
        return new ServiceStreamEvent(null, null, null, Objects.requireNonNull(value, "'value' cannot be null."),
            false);
    }

    static ServiceStreamEvent terminal() {
        return new ServiceStreamEvent(null, null, null, null, true);
    }

    public Optional<UserLogin> userLogin() {
        return Optional.ofNullable(userLogin);
    }

    public Optional<UserLogout> userLogout() {
        return Optional.ofNullable(userLogout);
    }

    public Optional<StockUpdate> stockUpdate() {
        return Optional.ofNullable(stockUpdate);
    }

    public Optional<SystemAlert> systemAlert() {
        return Optional.ofNullable(systemAlert);
    }

    public boolean isUserLogin() {
        return userLogin != null;
    }

    public boolean isUserLogout() {
        return userLogout != null;
    }

    public boolean isStockUpdate() {
        return stockUpdate != null;
    }

    public boolean isSystemAlert() {
        return systemAlert != null;
    }

    public boolean isTerminal() {
        return terminal;
    }

    public UserLogin asUserLogin() {
        if (userLogin == null) {
            throw new IllegalStateException("Event data is not userLogin.");
        }
        return userLogin;
    }

    public UserLogout asUserLogout() {
        if (userLogout == null) {
            throw new IllegalStateException("Event data is not userLogout.");
        }
        return userLogout;
    }

    public StockUpdate asStockUpdate() {
        if (stockUpdate == null) {
            throw new IllegalStateException("Event data is not stockUpdate.");
        }
        return stockUpdate;
    }

    public SystemAlert asSystemAlert() {
        if (systemAlert == null) {
            throw new IllegalStateException("Event data is not systemAlert.");
        }
        return systemAlert;
    }

    @Override
    public JsonWriter toJson(JsonWriter jsonWriter) throws IOException {
        if (terminal) {
            return jsonWriter.writeString("[DONE]");
        } else if (userLogin != null) {
            return userLogin.toJson(jsonWriter);
        } else if (userLogout != null) {
            return userLogout.toJson(jsonWriter);
        } else if (stockUpdate != null) {
            return stockUpdate.toJson(jsonWriter);
        } else if (systemAlert != null) {
            return systemAlert.toJson(jsonWriter);
        }
        throw new IllegalStateException("No event data is set.");
    }

    public static ServiceStreamEvent fromJson(JsonReader jsonReader, String eventName) throws IOException {
        switch (eventName) {
            case "userLogin":
                return ofUserLogin(UserLogin.fromJson(jsonReader));

            case "userLogout":
                return ofUserLogout(UserLogout.fromJson(jsonReader));

            case "stockUpdate":
                return ofStockUpdate(StockUpdate.fromJson(jsonReader));

            case "systemAlert":
                return ofSystemAlert(SystemAlert.fromJson(jsonReader));

            default:
                return null;
        }
    }
}
