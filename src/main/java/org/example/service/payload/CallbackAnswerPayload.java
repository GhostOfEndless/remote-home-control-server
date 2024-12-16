package org.example.service.payload;

import java.util.Collections;
import java.util.List;
import org.example.service.enums.CallbackTextCode;
import org.jspecify.annotations.NonNull;

public record CallbackAnswerPayload(
    CallbackTextCode callbackText,
    List<CallbackArgument> callbackArgs
) {

  public static @NonNull CallbackAnswerPayload create(CallbackTextCode callbackTextCode) {
    return new CallbackAnswerPayload(callbackTextCode, Collections.emptyList());
  }

  public static @NonNull CallbackAnswerPayload create(
      CallbackTextCode callbackTextCode,
      List<CallbackArgument> callbackArgs
  ) {
    return new CallbackAnswerPayload(callbackTextCode, callbackArgs);
  }
}
