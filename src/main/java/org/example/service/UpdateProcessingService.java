package org.example.service;

import org.example.utils.enums.UpdateType;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateProcessingService {

  void process(UpdateType updateType, Update update);
}
