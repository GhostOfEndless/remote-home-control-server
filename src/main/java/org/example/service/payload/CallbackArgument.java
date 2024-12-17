package org.example.service.payload;

import org.jspecify.annotations.NonNull;

public record CallbackArgument(
    String text,
    boolean isResource
) {
  public static @NonNull CallbackArgument createResourceArgument(@NonNull String resourceCode) {
    return new CallbackArgument(resourceCode, true);
  }
}
