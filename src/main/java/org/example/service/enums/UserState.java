package org.example.service.enums;

import java.util.Arrays;
import java.util.List;

public enum UserState {
  NONE,
  START;

  public static List<String> getBaseNames() {
    return Arrays.stream(UserState.values())
        .map(state -> state.name().toLowerCase().replace('_', '-'))
        .toList();
  }
}
