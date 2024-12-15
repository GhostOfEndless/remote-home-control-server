package org.example.service;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.config.TelegramProperties;
import org.example.entity.AppUser;
import org.example.service.enums.UserState;
import org.example.service.handlers.PersonalUpdateHandler;
import org.example.utils.TelegramUtils;
import org.example.utils.enums.UpdateType;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonalUpdateProcessingService implements UpdateProcessingService {

  protected final AppUserService appUserService;
  private final HashMap<UserState, PersonalUpdateHandler> updateHandlerMap = new HashMap<>();
  private final List<PersonalUpdateHandler> updateHandlers;
  private final TelegramProperties telegramProperties;

  @PostConstruct
  public void init() {
    updateHandlers.forEach(handler ->
        updateHandlerMap.put(handler.getProcessedUserState(), handler)
    );
  }

  @Override
  public void process(UpdateType updateType, Update update) {
    User user = TelegramUtils.getUserFromUpdate(update);
    long userId = user.getId();
    var appUser = appUserService.findById(userId)
        .orElseGet(() -> appUserService.save(userId));

    Optional.ofNullable(appUser.getState()).ifPresentOrElse(
        state -> handleUpdate(updateType, update, appUser, state),
        () -> handleUpdate(updateType, update, appUser, UserState.START)
    );

    log.debug("Personal chat update: {}", update);
  }

  private void handleUpdate(UpdateType updateType, Update update, AppUser appUser, UserState userState) {
    var processingResult = updateHandlerMap.get(userState).handle(updateType, update, appUser);

    if (!processingResult.newState().equals(userState) || processingResult.newState().equals(UserState.START)) {
      var handler = updateHandlerMap.get(processingResult.newState());
      if (handler != null) {
        handler.goToState(appUser, processingResult.messageId(), processingResult.args());
      }
    }
  }
}
