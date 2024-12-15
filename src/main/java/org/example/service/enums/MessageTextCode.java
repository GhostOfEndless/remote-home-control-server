package org.example.service.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageTextCode {
  START_MESSAGE_USER("telegram.start_level.message.user");

  private final String resourceName;
}
