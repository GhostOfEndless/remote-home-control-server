package org.example.utils;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

@Slf4j
public class TelegramUtils {

    public static UpdateType determineUpdateType(@NonNull Update update) {
        if (update.hasMessage()) {
            if (update.getMessage().isUserMessage()) {
                return UpdateType.PERSONAL_MESSAGE;
            } else {
                log.warn("Unhandled type of message! {}", update);
                return UpdateType.UNKNOWN;
            }
        } else if (update.hasCallbackQuery()) {
            return UpdateType.PERSONAL_MESSAGE;
        } else {
            log.warn("Unknown update type! {}", update);
            return UpdateType.UNKNOWN;
        }
    }

    public static User getUserFromUpdate(Update update) {
        return switch (determineUpdateType(update)) {
            case PERSONAL_MESSAGE -> update.getMessage().getFrom();
            case CALLBACK -> update.getCallbackQuery().getFrom();
            case UNKNOWN -> throw new ("Couldn't get user from update %s".formatted(update));
        };
    }
}
