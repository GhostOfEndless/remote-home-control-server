package org.example.service.handlers;

import java.util.Objects;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.AppUser;
import org.example.service.CallbackAnswerSender;
import org.example.service.MessageSender;
import org.example.service.UserRole;
import org.example.service.enums.UserState;
import org.example.service.payload.CallbackData;
import org.example.service.payload.MessagePayload;
import org.example.service.payload.ProcessingResult;
import org.example.utils.TelegramUtils;
import org.example.utils.enums.UpdateType;
import org.jspecify.annotations.NullMarked;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@Slf4j
@NullMarked
@RequiredArgsConstructor
public abstract class PersonalUpdateHandler {

  private static final String START_COMMAND = "/start";

  protected final CallbackAnswerSender callbackSender;
  protected final MessageSender messageSender;

  @Getter
  protected final UserState processedUserState;

  public final ProcessingResult handle(UpdateType updateType, Update update, AppUser appUser) {
    return processUpdate(updateType, update, appUser);
  }

  public final void goToState(AppUser appUser, Integer messageId, Object... args) {
    var messagePayload = buildMessagePayloadForUser(appUser, args);
    messageSender.updateUserMessage(appUser, messageId, messagePayload, processedUserState);
  }

  protected final ProcessingResult processUpdate(UpdateType updateType, Update update, AppUser appUser) {
    return switch (updateType) {
      case PERSONAL_MESSAGE -> processTextMessageUpdate(update.getMessage(), appUser);
      case CALLBACK -> processCallbackUpdate(update.getCallbackQuery(), appUser);
      default -> ProcessingResult.create(processedUserState);
    };
  }

  private ProcessingResult processCallbackUpdate(CallbackQuery callbackQuery, AppUser appUser) {
    var callbackMessageId = callbackQuery.getMessage().getMessageId();
    Integer lastMessageId = appUser.getLastMessageId();

    if (!Objects.equals(callbackMessageId, lastMessageId)) {
      callbackSender.showMessageExpiredCallback(appUser.getLocale(), callbackQuery.getId());
      return ProcessingResult.create(UserState.NONE);
    }
    var callbackData = TelegramUtils.parseCallbackData(callbackQuery);
    return processCallbackButtonUpdate(callbackData, appUser);
  }

  protected ProcessingResult processCallbackButtonUpdate(CallbackData callbackData, AppUser appUser) {
    return ProcessingResult.create(processedUserState);
  }

  protected ProcessingResult processTextMessageUpdate(Message message, AppUser appUser) {
    if (message.hasText() && message.getText().startsWith(START_COMMAND)) {
      return ProcessingResult.create(UserState.START);
    }
    return ProcessingResult.create(processedUserState);
  }

  protected abstract MessagePayload buildMessagePayloadForUser(AppUser appUser, Object[] args);

  protected final ProcessingResult checkPermissionAndProcess(
      UserRole requiredRole,
      AppUser appUser,
      Supplier<ProcessingResult> supplier,
      CallbackData callbackData
  ) {
    UserRole userRole = appUser.getRole();
    if (!requiredRole.isEqualOrLowerThan(userRole)) {
      callbackSender.showPermissionDeniedCallback(appUser.getLocale(), callbackData.callbackId());
      return ProcessingResult.create(UserState.START, callbackData.messageId());
    }
    return supplier.get();
  }
}
