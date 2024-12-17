package org.example.service;

import java.util.Map;
import org.jspecify.annotations.NonNull;

public enum UserRole {
    USER,
    ADMIN;

    private static final Map<String, UserRole> roleNameToRole = Map.of(
        "USER", USER,
        "ADMIN", ADMIN
    );

    public static UserRole getRoleByName(String roleName) {
        if (!roleNameToRole.containsKey(roleName)) {
            throw new IllegalArgumentException();
        }

        return roleNameToRole.get(roleName);
    }

    public boolean isEqualOrLowerThan(@NonNull UserRole userRole) {
        return this.ordinal() <= userRole.ordinal();
    }
}
