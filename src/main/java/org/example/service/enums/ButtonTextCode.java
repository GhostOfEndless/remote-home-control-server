package org.example.service.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ButtonTextCode {
  START_BUTTON_HOME_CONTROL("telegram.start_level.button.home_control"),
  HOME_CONTROL_BUTTON_USERS("telegram.home_control_level.button.users"),
  HOME_CONTROL_BUTTON_DEVICES("telegram.home_control_level.button.devices"),
  HOME_CONTROL_BUTTON_GENERATE_TOKEN("telegram.home_control_level.button.generate_token"),
  HOME_CONTROL_BUTTON_SHOW_TOKEN("telegram.home_control_level.button.show_token"),
  USERS_BUTTON_ADDITION("telegram.users_level.addition_button"),
  USER_BUTTON_REMOVE("telegram.user_level.button.remove"),
  USER_BUTTON_USER(""),
  BUTTON_BACK("telegram.button.back");

  private final String resourceName;

  public boolean isBackButton() {
    return this == BUTTON_BACK;
  }
}
