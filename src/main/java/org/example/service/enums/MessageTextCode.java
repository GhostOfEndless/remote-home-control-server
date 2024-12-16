package org.example.service.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageTextCode {
  START_MESSAGE("telegram.start_level.message"),
  HOME_CONTROL_MESSAGE_ADMIN("telegram.home_control_level.message.admin"),
  HOME_CONTROL_MESSAGE_USER("telegram.home_control_level.message.user"),
  HOME_CONTROL_MESSAGE_TOKEN_NOT_FOUND("telegram.home_control_level.message.token_not_found"),
  TOKEN_MESSAGE_NEW_TOKEN("telegram.generate_token_level.message"),
  TOKEN_MESSAGE_EXISTED_TOKEN("telegram.generate_token_level.message"),
  USER_MESSAGE("telegram.user_level.message"),
  USER_MESSAGE_NOT_FOUND("telegram.user_level.message.not_found"),
  USERS_MESSAGE("telegram.users_level.message"),
  USER_ADDITION_MESSAGE("telegram.user_addition_level.message"),
  USER_ADDITION_MESSAGE_SUCCESS("telegram.user_addition_level.message.success"),
  USER_ADDITION_MESSAGE_USER_NOT_FOUND("telegram.user_addition_level.message.user_not_found"),
  USER_ADDITION_MESSAGE_USER_IS_BUSY("telegram.user_addition_level.message.user_is_busy");

  private final String resourceName;
}
