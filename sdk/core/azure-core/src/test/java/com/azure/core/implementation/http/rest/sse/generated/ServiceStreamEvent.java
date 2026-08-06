// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.implementation.http.rest.sse.generated;

import java.util.Objects;
import java.util.Optional;

public final class ServiceStreamEvent {
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
            throw new IllegalStateException("Event is not a userLogin event.");
        }
        return userLogin;
    }

    public UserLogout asUserLogout() {
        if (userLogout == null) {
            throw new IllegalStateException("Event is not a userLogout event.");
        }
        return userLogout;
    }

    public StockUpdate asStockUpdate() {
        if (stockUpdate == null) {
            throw new IllegalStateException("Event is not a stockUpdate event.");
        }
        return stockUpdate;
    }

    public SystemAlert asSystemAlert() {
        if (systemAlert == null) {
            throw new IllegalStateException("Event is not a systemAlert event.");
        }
        return systemAlert;
    }
}
