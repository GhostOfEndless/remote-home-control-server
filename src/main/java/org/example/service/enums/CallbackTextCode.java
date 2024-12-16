package org.example.service.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CallbackTextCode {
  PERMISSION_DENIED("telegram.callback.permission_denied"),
  MESSAGE_EXPIRED("telegram.callback.message_expired");

  private final String resourceName;
}
