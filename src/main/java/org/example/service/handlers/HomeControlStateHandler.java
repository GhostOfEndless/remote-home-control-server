package org.example.service.handlers;

import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.AppUser;
import org.example.service.CallbackAnswerSender;
import org.example.service.MessageSender;
import org.example.service.UserRole;
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
public final class HomeControlStateHandler extends PersonalUpdateHandler {

  public HomeControlStateHandler(
      CallbackAnswerSender callbackSender,
      MessageSender messageSender
  ) {
    super(callbackSender, messageSender, UserState.HOME_CONTROL);
  }

  @Override
  protected MessagePayload buildMessagePayloadForUser(AppUser appUser, Object[] args) {
    if (Optional.ofNullable(appUser.getToken()).isEmpty()) {
      return createTokenNotFoundPayload();
    }
    if (appUser.getRole().equals(UserRole.USER)) {
      return createUserPayload();
    }
    return createAdminPayload();
  }

  @Override
  protected ProcessingResult processCallbackButtonUpdate(CallbackData callbackData, AppUser appUser) {
    Integer messageId = callbackData.messageId();
    return switch (callbackData.pressedButton()) {
      case BUTTON_BACK -> ProcessingResult.create(UserState.START, messageId);
      case HOME_CONTROL_BUTTON_GENERATE_TOKEN,
           HOME_CONTROL_BUTTON_SHOW_TOKEN -> ProcessingResult.create(UserState.TOKEN, messageId);
      case HOME_CONTROL_BUTTON_USERS -> ProcessingResult.create(UserState.USERS, messageId);
      default -> ProcessingResult.create(processedUserState, messageId);
    };
  }

  private MessagePayload createTokenNotFoundPayload() {
    return MessagePayload.create(
        MessageTextCode.HOME_CONTROL_MESSAGE_TOKEN_NOT_FOUND,
        List.of(
            CallbackButtonPayload.create(ButtonTextCode.HOME_CONTROL_BUTTON_GENERATE_TOKEN),
            CallbackButtonPayload.create(ButtonTextCode.BUTTON_BACK)
        )
    );
  }

  private MessagePayload createAdminPayload() {
    return MessagePayload.create(
        MessageTextCode.HOME_CONTROL_MESSAGE_ADMIN,
        List.of(
            CallbackButtonPayload.create(ButtonTextCode.HOME_CONTROL_BUTTON_USERS),
            CallbackButtonPayload.create(ButtonTextCode.HOME_CONTROL_BUTTON_DEVICES),
            CallbackButtonPayload.create(ButtonTextCode.HOME_CONTROL_BUTTON_SHOW_TOKEN),
            CallbackButtonPayload.create(ButtonTextCode.BUTTON_BACK)
        )
    );
  }

  private MessagePayload createUserPayload() {
    return MessagePayload.create(
        MessageTextCode.HOME_CONTROL_MESSAGE_USER,
        List.of(
            CallbackButtonPayload.create(ButtonTextCode.HOME_CONTROL_BUTTON_DEVICES),
            CallbackButtonPayload.create(ButtonTextCode.BUTTON_BACK)
        )
    );
  }
}
