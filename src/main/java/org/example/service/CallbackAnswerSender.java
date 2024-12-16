package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.service.enums.CallbackTextCode;
import org.example.service.payload.CallbackAnswerPayload;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CallbackAnswerSender {

  private final TelegramClientService telegramClientService;
  private final TextResourceService textResourceService;

  public final void showAnswerCallback(
      @NonNull CallbackAnswerPayload callbackAnswerPayload,
      String userLocale,
      String callbackQueryId,
      boolean isAlert
  ) {
    String callbackResourceName = callbackAnswerPayload.callbackText().getResourceName();
    Object[] callbackArgs = callbackAnswerPayload.callbackArgs()
        .stream()
        .map(argument -> argument.isResource()
            ? textResourceService.getText(argument.text(), userLocale)
            : argument.text()
        )
        .toArray();

    telegramClientService.sendCallbackAnswer(
        textResourceService.getText(callbackResourceName, userLocale, callbackArgs),
        callbackQueryId,
        isAlert
    );
  }

  public final void showAnswerCallback(
      CallbackAnswerPayload callbackAnswerPayload,
      String userLocale,
      String callbackQueryId
  ) {
    showAnswerCallback(callbackAnswerPayload, userLocale, callbackQueryId, false);
  }

  public void showMessageExpiredCallback(String userLocale, String callbackId) {
    showAnswerCallback(
        CallbackAnswerPayload.create(
            CallbackTextCode.MESSAGE_EXPIRED
        ),
        userLocale,
        callbackId,
        true
    );
  }

  public void showPermissionDeniedCallback(String userLocale, String callbackId) {
    showAnswerCallback(
        CallbackAnswerPayload.create(
            CallbackTextCode.PERMISSION_DENIED
        ),
        userLocale,
        callbackId,
        true
    );
  }
}
