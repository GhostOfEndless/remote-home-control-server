package org.example.service.payload;

import org.example.service.enums.MessageTextCode;
import org.jspecify.annotations.NonNull;

public record MessageArgument(
    String text,
    boolean isResource
) {
  public static @NonNull MessageArgument createTextArgument(String text) {
    return new MessageArgument(text, false);
  }

  public static @NonNull MessageArgument createResourceArgument(@NonNull MessageTextCode messageTextCode) {
    return new MessageArgument(messageTextCode.getResourceName(), true);
  }
}
