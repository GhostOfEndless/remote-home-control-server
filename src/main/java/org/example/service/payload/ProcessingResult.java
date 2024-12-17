package org.example.service.payload;

import org.example.service.enums.UserState;
import org.jspecify.annotations.NonNull;

public record ProcessingResult(
    UserState newState,
    Integer messageId,
    Object[] args
) {
  public static @NonNull ProcessingResult create(UserState newState, Integer messageId, Object... args) {
    return new ProcessingResult(newState, messageId, args);
  }

  public static @NonNull ProcessingResult create(UserState newState, Object... args) {
    return ProcessingResult.create(newState, 0, args);
  }
}
