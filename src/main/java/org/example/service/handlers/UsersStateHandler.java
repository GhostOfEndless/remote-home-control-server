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
import org.example.service.payload.MessagePayload;
import org.example.service.payload.ProcessingResult;
import org.example.utils.TelegramUtils;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Component;

@NullMarked
@Slf4j
@Component
public final class UsersStateHandler extends PersonalUpdateHandler {

  private final AppUserService appUserService;

  public UsersStateHandler(
      CallbackAnswerSender callbackSender,
      MessageSender messageSender,
      AppUserService appUserService
  ) {
    super(callbackSender, messageSender, UserState.USERS);
    this.appUserService = appUserService;
  }

  @Override
  protected MessagePayload buildMessagePayloadForUser(AppUser appUser, Object[] args) {
    List<AppUser> users = appUserService.findAllByTokenWithoutCurrent(appUser.getId(), appUser.getToken());
    var usersButtons = TelegramUtils.buildUserButtons(users);
    usersButtons.add(CallbackButtonPayload.create(ButtonTextCode.USERS_BUTTON_ADDITION));
    usersButtons.add(CallbackButtonPayload.create(ButtonTextCode.BUTTON_BACK));
    return MessagePayload.create(MessageTextCode.USERS_MESSAGE, usersButtons);
  }

  @Override
  protected ProcessingResult processCallbackButtonUpdate(CallbackData callbackData, AppUser appUser) {
    Integer messageId = callbackData.messageId();
    return switch (callbackData.pressedButton()) {
      case BUTTON_BACK -> ProcessingResult.create(UserState.HOME_CONTROL, messageId);
      case USERS_BUTTON_ADDITION -> checkPermissionAndProcess(
          UserRole.ADMIN,
          appUser,
          () -> ProcessingResult.create(UserState.USER_ADDITION, messageId),
          callbackData
      );
      case USER_BUTTON_USER -> checkPermissionAndProcess(
          UserRole.ADMIN,
          appUser,
          () -> ProcessingResult.create(UserState.USER, messageId, callbackData.getUserId()),
          callbackData
      );
      default -> ProcessingResult.create(processedUserState, messageId);
    };
  }
}
