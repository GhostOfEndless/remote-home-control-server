package org.example.service.enums;

public enum UserAdditionResult {
  SUCCESS,
  USER_IS_BUSY,
  USER_NOT_FOUND;

  public static boolean isAdditionResult(Object object) {
    return object instanceof UserAdditionResult;
  }
}
