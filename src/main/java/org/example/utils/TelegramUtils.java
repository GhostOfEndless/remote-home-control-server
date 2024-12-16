package org.example.utils;

import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.AppUser;
import org.example.exception.UnsupportedUpdateType;
import org.example.service.enums.ButtonTextCode;
import org.example.service.payload.CallbackButtonPayload;
import org.example.service.payload.CallbackData;
import org.example.utils.enums.UpdateType;
import org.jspecify.annotations.NullMarked;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

@NullMarked
@Slf4j
public class TelegramUtils {

  public static UpdateType determineUpdateType(Update update) {
    if (update.hasMessage()) {
      return determineMessageType(update);
    } else if (update.hasCallbackQuery()) {
      return UpdateType.CALLBACK;
    } else {
      log.warn("Unknown update type! {}", update);
      return UpdateType.UNKNOWN;
    }
  }

  private static UpdateType determineMessageType(Update update) {
    if (update.getMessage().isUserMessage()) {
      return UpdateType.PERSONAL_MESSAGE;
    } else {
      log.warn("Unhandled type of message! {}", update);
      return UpdateType.UNKNOWN;
    }
  }

  public static User getUserFromUpdate(Update update) {
    return switch (determineUpdateType(update)) {
      case PERSONAL_MESSAGE -> update.getMessage().getFrom();
      case CALLBACK -> update.getCallbackQuery().getFrom();
      case UNKNOWN -> throw new UnsupportedUpdateType("Couldn't get user from update %s".formatted(update));
    };
  }

  public static List<CallbackButtonPayload> buildUserButtons(List<AppUser> appUsers) {
    return appUsers.stream()
        .map(user -> CallbackButtonPayload.createUserButton(
            user.getFirstName(),
            user.getLastName(),
            user.getId()
        ))
        .collect(Collectors.toList());
  }

  public static CallbackData parseCallbackData(CallbackQuery callbackQuery) {
    String[] callbackDataArr = callbackQuery.getData().split(":");
    String[] args = new String[callbackDataArr.length - 1];
    System.arraycopy(callbackDataArr, 1, args, 0, args.length);
    ButtonTextCode pressedButton = ButtonTextCode.valueOf(callbackDataArr[0]);
    return new CallbackData(
        callbackQuery.getMessage().getMessageId(),
        callbackQuery.getId(),
        pressedButton,
        args
    );
  }
}
