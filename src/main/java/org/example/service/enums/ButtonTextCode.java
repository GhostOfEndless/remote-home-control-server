package org.example.service.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ButtonTextCode {
  BUTTON_BACK("telegram.button.back");

  private final String resourceName;
}
