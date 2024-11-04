package org.example.service.handlers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.service.UserRole;
import org.example.service.UserState;
import org.jspecify.annotations.NonNull;
import org.telegram.telegrambots.meta.api.objects.Update;

@RequiredArgsConstructor
public abstract class PersonalUpdateHandler {

    @Getter
    protected final UserState processedUserState;

    public void handle(@NonNull Update update, Long userId) {

        if (UserRole.getRoleLevel() >= processedUserState.getAllowedRoleLevel())
    }
}
