package com.sponsorando.app.models;

public enum Role {
    USER,
    ADMIN;

    public static Role[] getRoles() {
        return Role.values();
    }
}
