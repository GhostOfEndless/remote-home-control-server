package org.example.service;

import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TextResourceService {

  private final MessageSource messageSource;

  public String getText(String code, String languageTag, Object... args) {
    return messageSource.getMessage(code, args, code, Locale.forLanguageTag(languageTag));
  }
}
