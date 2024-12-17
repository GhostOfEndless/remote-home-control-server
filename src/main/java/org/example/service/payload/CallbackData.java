package org.example.service.payload;

import java.util.NoSuchElementException;
import org.example.service.enums.ButtonTextCode;
import org.jspecify.annotations.NonNull;

public record CallbackData(
    Integer messageId,
    String callbackId,
    ButtonTextCode pressedButton,
    String[] args
) {

  public @NonNull Long getUserId() {
    if (args.length == 0) {
      throw new NoSuchElementException("Callback data doesn't contains admin id");
    }
    return Long.parseLong(args[0]);
  }
}
