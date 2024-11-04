package org.example.service;

public enum UserRole {
    USER,
    ADMIN;

    public static int getRoleLevel(String roleName) {
        try {
            return valueOf(roleName).ordinal();
        } catch (IllegalArgumentException e) {
            return 0;
        }
    }
}
