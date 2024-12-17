package org.example.service.payload;

import java.util.Collections;
import java.util.List;
import org.example.service.enums.MessageTextCode;
import org.jspecify.annotations.NonNull;

public record MessagePayload(
    MessageTextCode messageText,
    List<MessageArgument> messageArgs,
    List<CallbackButtonPayload> buttons
) {
  public static @NonNull MessagePayload create(MessageTextCode messageText, List<CallbackButtonPayload> buttons) {
    return new MessagePayload(messageText, Collections.emptyList(), buttons);
  }

  public static @NonNull MessagePayload create(
      MessageTextCode messageText,
      List<MessageArgument> messageArgs,
      List<CallbackButtonPayload> buttons
  ) {
    return new MessagePayload(messageText, messageArgs, buttons);
  }
}
