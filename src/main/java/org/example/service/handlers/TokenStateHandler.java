package org.example.service.handlers;

import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.AppUser;
import org.example.service.AppUserService;
import org.example.service.CallbackAnswerSender;
import org.example.service.MessageSender;
import org.example.service.TokenGenerator;
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
public final class TokenStateHandler extends PersonalUpdateHandler {

  private final AppUserService appUserService;
  private final TokenGenerator tokenGenerator;

  public TokenStateHandler(
      CallbackAnswerSender callbackSender,
      MessageSender messageSender,
      AppUserService appUserService,
      TokenGenerator tokenGenerator
  ) {
    super(callbackSender, messageSender, UserState.TOKEN);
    this.appUserService = appUserService;
    this.tokenGenerator = tokenGenerator;
  }

  @Override
  protected MessagePayload buildMessagePayloadForUser(AppUser appUser, Object[] args) {
    if (Optional.ofNullable(appUser.getToken()).isEmpty()) {
      String newToken = tokenGenerator.generateToken();
      appUserService.createToken(appUser, newToken);
      return createNewTokenPayload(newToken);
    }

    return createExistedTokenPayload(appUser.getToken().getToken());
  }

  @Override
  protected ProcessingResult processCallbackButtonUpdate(CallbackData callbackData, AppUser appUser) {
    Integer messageId = callbackData.messageId();
    if (callbackData.pressedButton().isBackButton()) {
      ProcessingResult.create(UserState.HOME_CONTROL, messageId);
    }
    return ProcessingResult.create(processedUserState, messageId);
  }

  private MessagePayload createNewTokenPayload(String newToken) {
    return MessagePayload.create(
        MessageTextCode.TOKEN_MESSAGE_NEW_TOKEN,
        List.of(
            MessageArgument.createTextArgument(newToken)
        ),
        List.of(
            CallbackButtonPayload.create(ButtonTextCode.BUTTON_BACK)
        )
    );
  }

  private MessagePayload createExistedTokenPayload(String token) {
    return MessagePayload.create(
        MessageTextCode.TOKEN_MESSAGE_EXISTED_TOKEN,
        List.of(
            MessageArgument.createTextArgument(token)
        ),
        List.of(
            CallbackButtonPayload.create(ButtonTextCode.BUTTON_BACK)
        )
    );
  }
}
