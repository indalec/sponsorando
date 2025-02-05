package com.sponsorando.app.models;

public enum Role {
    USER,
    ADMIN,
    MODERATOR;

    public static Role[] getRoles() {
        return Role.values();
    }
}
