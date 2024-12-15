package org.example.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.AppUser;
import org.example.service.enums.UserState;
import org.example.service.payload.CallbackButtonPayload;
import org.example.service.payload.MessagePayload;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@NullMarked
@Component
@RequiredArgsConstructor
public class MessageSender {

  private final AppUserService appUserService;
  private final TextResourceService textResourceService;
  private final TelegramClientService telegramClientService;

  public void updateUserMessage(AppUser appUser, Integer messageId, MessagePayload payload, UserState state) {
    var keyboardMarkup = buildKeyboard(payload.buttons(), appUser.getLocale());
    String messageText = buildMessageText(appUser.getLocale(), payload);
    Long userId = appUser.getId();
    try {
      if (messageId == 0) {
        var sentMessage = telegramClientService.sendMessage(userId, messageText, keyboardMarkup);
        messageId = sentMessage.getMessageId();
      } else {
        telegramClientService.editMessage(userId, messageId, messageText, keyboardMarkup);
      }
      appUserService.update(appUser.getId(), messageId, state);
    } catch (TelegramApiException e) {
      log.error("Telegram API Error: {}", e.getMessage());
    }
  }

  private String buildMessageText(String userLocale, MessagePayload messagePayload) {
    String messageResourceCode = messagePayload.messageText().getResourceName();
    Object[] messageArgs = messagePayload.messageArgs()
        .stream()
        .map(argument -> argument.isResource()
            ? textResourceService.getText(argument.text(), userLocale)
            : argument.text()
        )
        .toArray();
    return textResourceService.getText(messageResourceCode, userLocale, messageArgs);
  }

  private InlineKeyboardMarkup buildKeyboard(List<CallbackButtonPayload> callbackButtons, String userLocale) {
    var listOfRows = callbackButtons.stream()
        .map(callbackButton -> {
              String buttonText = textResourceService.getText(callbackButton.text(), userLocale);
              var inlineKeyboardButton = new InlineKeyboardButton(buttonText);
              var inlineKeyboardRow = new InlineKeyboardRow(inlineKeyboardButton);
              inlineKeyboardButton.setCallbackData(callbackButton.code());
              return inlineKeyboardRow;
            }
        )
        .toList();
    return new InlineKeyboardMarkup(listOfRows);
  }
}
