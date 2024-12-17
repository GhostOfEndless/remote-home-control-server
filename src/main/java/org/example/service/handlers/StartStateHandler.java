package org.example.service.handlers;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.AppUser;
import org.example.service.CallbackAnswerSender;
import org.example.service.MessageSender;
import org.example.service.enums.ButtonTextCode;
import org.example.service.enums.MessageTextCode;
import org.example.service.enums.UserState;
import org.example.service.payload.CallbackButtonPayload;
import org.example.service.payload.CallbackData;
import org.example.service.payload.MessagePayload;
import org.example.service.payload.ProcessingResult;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Component;

@NullMarked
@Slf4j
@Component
public final class StartStateHandler extends PersonalUpdateHandler {

  public StartStateHandler(
      CallbackAnswerSender callbackSender,
      MessageSender messageSender
  ) {
    super(callbackSender, messageSender, UserState.START);
  }

  @Override
  protected MessagePayload buildMessagePayloadForUser(AppUser appUser, Object[] args) {
    return MessagePayload.create(
        MessageTextCode.START_MESSAGE,
        List.of(
            CallbackButtonPayload.create(ButtonTextCode.START_BUTTON_HOME_CONTROL)
        )
    );
  }

  @Override
  protected ProcessingResult processCallbackButtonUpdate(CallbackData callbackData, AppUser appUser) {
    Integer messageId = callbackData.messageId();
    if (callbackData.pressedButton() != ButtonTextCode.START_BUTTON_HOME_CONTROL) {
      return ProcessingResult.create(processedUserState, messageId);
    }
    return ProcessingResult.create(UserState.HOME_CONTROL, messageId);
  }
}
