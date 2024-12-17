package org.example.service.payload;

import org.example.service.enums.ButtonTextCode;
import org.jspecify.annotations.NonNull;

public record CallbackButtonPayload(
    String text,
    String code
) {
  public static @NonNull CallbackButtonPayload create(@NonNull ButtonTextCode textCode) {
    return new CallbackButtonPayload(textCode.getResourceName(), textCode.name());
  }

  public static @NonNull CallbackButtonPayload create(@NonNull ButtonTextCode textCode, Long userId) {
    return new CallbackButtonPayload(textCode.getResourceName(), "%s:%d".formatted(textCode.name(), userId));
  }

  public static @NonNull CallbackButtonPayload createUserButton(String name, String surname, Long userId) {
    return new CallbackButtonPayload(
        "%s %s".formatted(name, surname),
        "%s:%d".formatted(ButtonTextCode.USER_BUTTON_USER, userId)
    );
  }
}
