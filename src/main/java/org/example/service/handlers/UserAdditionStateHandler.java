package org.example.service.handlers;

import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.AppUser;
import org.example.service.AppUserService;
import org.example.service.CallbackAnswerSender;
import org.example.service.MessageSender;
import org.example.service.enums.ButtonTextCode;
import org.example.service.enums.MessageTextCode;
import org.example.service.enums.UserAdditionResult;
import org.example.service.enums.UserState;
import org.example.service.payload.CallbackButtonPayload;
import org.example.service.payload.CallbackData;
import org.example.service.payload.MessagePayload;
import org.example.service.payload.ProcessingResult;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@NullMarked
@Slf4j
@Component
public final class UserAdditionStateHandler extends PersonalUpdateHandler {

  private final AppUserService appUserService;

  public UserAdditionStateHandler(
      CallbackAnswerSender callbackSender,
      MessageSender messageSender,
      AppUserService appUserService
  ) {
    super(callbackSender, messageSender, UserState.USER_ADDITION);
    this.appUserService = appUserService;
  }

  @Override
  protected MessagePayload buildMessagePayloadForUser(AppUser appUser, Object[] args) {
    if (args.length > 0 && UserAdditionResult.isAdditionResult(args[0])) {
      return switch ((UserAdditionResult) args[0]) {
        case SUCCESS -> createSuccessAdditionPayload();
        case USER_NOT_FOUND -> createUserNotFoundPayload();
        case USER_IS_BUSY -> createUserAlreadyRegister();
      };
    }
    return createUserAdditionPayload();
  }

  @Override
  protected ProcessingResult processTextMessageUpdate(Message message, AppUser appUser) {
    if (message.hasEntities()) {
      var messageEntity = message.getEntities().stream()
          .filter(entity -> entity.getType().equals("mention"))
          .findFirst();

      if (messageEntity.isPresent()) {
        String username = messageEntity.get().getText().substring(1);
        var addedUser = appUserService.findByUsername(username);

        if (addedUser.isEmpty()) {
          goToState(appUser, 0, UserAdditionResult.USER_NOT_FOUND);
          return ProcessingResult.create(processedUserState);
        }
        if (Optional.ofNullable(addedUser.get().getToken()).isPresent()) {
          goToState(appUser, 0, UserAdditionResult.USER_IS_BUSY);
          return ProcessingResult.create(processedUserState);
        }
        appUserService.update(addedUser.get(), appUser.getToken());
        goToState(appUser, 0, UserAdditionResult.SUCCESS);
        return ProcessingResult.create(processedUserState);
      }
    }
    return super.processTextMessageUpdate(message, appUser);
  }

  @Override
  protected ProcessingResult processCallbackButtonUpdate(CallbackData callbackData, AppUser appUser) {
    Integer messageId = callbackData.messageId();

    if (!callbackData.pressedButton().isBackButton()) {
      return ProcessingResult.create(processedUserState, messageId);
    }
    return ProcessingResult.create(UserState.USERS, messageId);
  }

  private MessagePayload createUserAdditionPayload() {
    return MessagePayload.create(
        MessageTextCode.USER_ADDITION_MESSAGE,
        List.of(
            CallbackButtonPayload.create(ButtonTextCode.BUTTON_BACK)
        )
    );
  }

  private MessagePayload createSuccessAdditionPayload() {
    return MessagePayload.create(
        MessageTextCode.USER_ADDITION_MESSAGE_SUCCESS,
        List.of(
            CallbackButtonPayload.create(ButtonTextCode.BUTTON_BACK)
        )
    );
  }

  private MessagePayload createUserNotFoundPayload() {
    return MessagePayload.create(
        MessageTextCode.USER_ADDITION_MESSAGE_USER_NOT_FOUND,
        List.of(
            CallbackButtonPayload.create(ButtonTextCode.BUTTON_BACK)
        )
    );
  }

  private MessagePayload createUserAlreadyRegister() {
    return MessagePayload.create(
        MessageTextCode.USER_ADDITION_MESSAGE_USER_IS_BUSY,
        List.of(
            CallbackButtonPayload.create(ButtonTextCode.BUTTON_BACK)
        )
    );
  }
}
