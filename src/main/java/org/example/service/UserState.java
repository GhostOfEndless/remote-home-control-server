package org.example.service;


import lombok.Getter;

@Getter
public enum UserState {
    START(UserRole.USER.ordinal()),
    TOKEN_GEN(UserRole.USER.ordinal()),
    GENERATED(UserRole.ADMIN.ordinal()),
    HOME(UserRole.USER.ordinal());

    private final int allowedRoleLevel;

    UserState(int allowedRoleLevel) {
        this.allowedRoleLevel = allowedRoleLevel;
    }
}
