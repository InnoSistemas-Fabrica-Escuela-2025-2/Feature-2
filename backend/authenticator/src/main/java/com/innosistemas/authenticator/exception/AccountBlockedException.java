package com.innosistemas.authenticator.exception;

import java.time.Instant;

public class AccountBlockedException extends RuntimeException {
    private final Instant blockedUntil;
    private final boolean permanent;

    public AccountBlockedException(String message, Instant blockedUntil, boolean permanent) {
        super(message);
        this.blockedUntil = blockedUntil;
        this.permanent = permanent;
    }

    public Instant getBlockedUntil() {
        return blockedUntil;
    }

    public boolean isPermanent() {
        return permanent;
    }
}
