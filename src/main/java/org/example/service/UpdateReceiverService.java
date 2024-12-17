package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.utils.TelegramUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateReceiverService {

  private final PersonalUpdateProcessingService personalUpdateProcessingService;

  public void listenUpdates(@NonNull Update update) {
    processUpdate(update);
  }

  private void processUpdate(Update update) {
    var updateType = TelegramUtils.determineUpdateType(update);
    personalUpdateProcessingService.process(updateType, update);
  }
}
