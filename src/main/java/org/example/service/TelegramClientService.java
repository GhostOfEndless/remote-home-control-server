package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
@Service
@NullMarked
@RequiredArgsConstructor
public class TelegramClientService {

  private static final String MESSAGE_TEXT_PARSE_MODE = "MarkdownV2";
  private final TelegramClient telegramClient;

  public Message sendMessage(Long chatId, String message, InlineKeyboardMarkup replyMarkup)
      throws TelegramApiException {
    var sendMessage = SendMessage.builder()
        .chatId(chatId)
        .text(message)
        .replyMarkup(replyMarkup)
        .parseMode(MESSAGE_TEXT_PARSE_MODE)
        .build();
    return telegramClient.execute(sendMessage);
  }

  public void editMessage(Long chatId, Integer messageId, String message, InlineKeyboardMarkup replyMarkup)
      throws TelegramApiException {
    var editMessage = EditMessageText.builder()
        .messageId(messageId)
        .replyMarkup(replyMarkup)
        .chatId(chatId)
        .text(message)
        .parseMode(MESSAGE_TEXT_PARSE_MODE)
        .build();
    telegramClient.execute(editMessage);
  }

  public void sendCallbackAnswer(String text, String callbackQueryId, boolean isAlert) {
    try {
      var callbackAnswer = AnswerCallbackQuery.builder()
          .callbackQueryId(callbackQueryId)
          .showAlert(isAlert)
          .text(text)
          .build();
      telegramClient.execute(callbackAnswer);
    } catch (TelegramApiException e) {
      log.error(e.getMessage());
    }
  }
}
