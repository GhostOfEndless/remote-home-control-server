package org.example.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenGenerator {

  public String generateToken() {
    return RandomStringUtils.secureStrong()
        .nextAlphanumeric(32);
  }
}
