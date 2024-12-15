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
}
