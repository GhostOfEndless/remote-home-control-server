package org.example.service.handlers;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.AppUser;
import org.example.service.AppUserService;
import org.example.service.CallbackAnswerSender;
import org.example.service.MessageSender;
import org.example.service.UserRole;
import org.example.service.enums.ButtonTextCode;
import org.example.service.enums.MessageTextCode;
import org.example.service.enums.UserState;
import org.example.service.payload.CallbackButtonPayload;
import org.example.service.payload.CallbackData;
import org.example.service.payload.MessageArgument;
import org.example.service.payload.MessagePayload;
import org.example.service.payload.ProcessingResult;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Component;

@NullMarked
@Slf4j
@Component
public final class UserStateHandler extends PersonalUpdateHandler {

  private final AppUserService appUserService;

  public UserStateHandler(
      CallbackAnswerSender callbackSender,
      MessageSender messageSender,
      AppUserService appUserService) {
    super(callbackSender, messageSender, UserState.USER);
    this.appUserService = appUserService;
  }

  @Override
  protected MessagePayload buildMessagePayloadForUser(AppUser appUser, Object[] args) {
    Long userId = (Long) args[0];
    return appUserService.findById(userId)
        .map(user -> MessagePayload.create(
            MessageTextCode.USER_MESSAGE,
            List.of(
                MessageArgument.createTextArgument(
                    "%s %s".formatted(user.getFirstName(), user.getLastName())
                )
            ),
            List.of(
                CallbackButtonPayload.create(ButtonTextCode.USER_BUTTON_REMOVE, userId),
                CallbackButtonPayload.create(ButtonTextCode.BUTTON_BACK)
            )))
        .orElseGet(() -> MessagePayload.create(
            MessageTextCode.USER_MESSAGE_NOT_FOUND,
            List.of(CallbackButtonPayload.create(ButtonTextCode.BUTTON_BACK))
        ));
  }

  @Override
  protected ProcessingResult processCallbackButtonUpdate(CallbackData callbackData, AppUser appUser) {
    Integer messageId = callbackData.messageId();
    return switch (callbackData.pressedButton()) {
      case BUTTON_BACK -> ProcessingResult.create(UserState.USERS, messageId);
      case USER_BUTTON_REMOVE -> checkPermissionAndProcess(
          UserRole.ADMIN,
          appUser,
          () -> {
            appUserService.removeTokenFromUser(callbackData.getUserId());
            return ProcessingResult.create(UserState.USERS, messageId);
          },
          callbackData
      );
      default -> ProcessingResult.create(processedUserState, messageId);
    };
  }
}
