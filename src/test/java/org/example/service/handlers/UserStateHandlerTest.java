package org.example.service.handlers;

import java.util.Optional;
import org.example.entity.AppUser;
import org.example.entity.Token;
import org.example.service.AppUserService;
import org.example.service.CallbackAnswerSender;
import org.example.service.MessageSender;
import org.example.service.UserRole;
import org.example.service.enums.ButtonTextCode;
import org.example.service.enums.MessageTextCode;
import org.example.service.enums.UserState;
import org.example.service.payload.CallbackData;
import org.example.service.payload.MessagePayload;
import org.example.service.payload.ProcessingResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserStateHandlerTest {

  @Mock
  private CallbackAnswerSender callbackSender;

  @Mock
  private MessageSender messageSender;

  @Mock
  private AppUserService appUserService;

  private UserStateHandler userStateHandler;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    userStateHandler = new UserStateHandler(callbackSender, messageSender, appUserService);
  }

  @Test
  void buildMessagePayloadForUser_UserExists() {
    Token token = new Token(1L, "token");
    AppUser mockUser = new AppUser(1L, token, "John", "Doe", "username", null,
        null, "ru", UserRole.USER);
    when(appUserService.findById(1L)).thenReturn(Optional.of(mockUser));

    MessagePayload payload = userStateHandler.buildMessagePayloadForUser(mockUser, new Object[] {1L});

    assertNotNull(payload);
    assertEquals(MessageTextCode.USER_MESSAGE, payload.messageText());
    assertTrue(payload.messageArgs().getFirst().text().contains("John Doe"));
    assertEquals(2, payload.buttons().size());
  }

  @Test
  void buildMessagePayloadForUser_UserNotFound() {
    Token token = new Token(1L, "token");
    AppUser mockUser = new AppUser(1L, token, "John", "Doe", "username", null,
        null, "ru", UserRole.USER);
    when(appUserService.findById(1L)).thenReturn(Optional.empty());

    MessagePayload payload = userStateHandler.buildMessagePayloadForUser(mockUser, new Object[] {1L});

    assertNotNull(payload);
    assertEquals(MessageTextCode.USER_MESSAGE_NOT_FOUND, payload.messageText());
    assertEquals(1, payload.buttons().size());
    assertEquals(ButtonTextCode.BUTTON_BACK.name(), payload.buttons().getFirst().code());
  }

  @Test
  void processCallbackButtonUpdate_BackButton() {
    CallbackData callbackData = new CallbackData(123, null, ButtonTextCode.BUTTON_BACK, null);
    Token token = new Token(1L, "token");
    AppUser mockUser = new AppUser(1L, token, "John", "Doe", "username", null,
        null, "ru", UserRole.USER);

    ProcessingResult result = userStateHandler.processCallbackButtonUpdate(callbackData, mockUser);

    assertNotNull(result);
    assertEquals(UserState.USERS, result.newState());
    assertEquals(123, result.messageId());
  }

  @Test
  void processCallbackButtonUpdate_RemoveUser_Success() {
    CallbackData callbackData = new CallbackData(123, null, ButtonTextCode.USER_BUTTON_REMOVE, new String[] {"1"});
    Token token = new Token(1L, "token");
    AppUser adminUser = new AppUser(1L, token, "John", "Doe", "username", null,
        null, "ru", UserRole.ADMIN);

    doNothing().when(appUserService).removeTokenFromUser(1L);

    ProcessingResult result = userStateHandler.processCallbackButtonUpdate(callbackData, adminUser);

    assertNotNull(result);
    assertEquals(UserState.USERS, result.newState());
    assertEquals(123, result.messageId());
    verify(appUserService).removeTokenFromUser(1L);
  }

  @Test
  void processCallbackButtonUpdate_RemoveUser_NoPermission() {
    CallbackData callbackData = new CallbackData(123, null, ButtonTextCode.USER_BUTTON_REMOVE, new String[] {"1"});
    Token token = new Token(1L, "token");
    AppUser nonAdminUser = new AppUser(1L, token, "John", "Doe", "username", null,
        null, "ru", UserRole.USER);

    ProcessingResult result = userStateHandler.processCallbackButtonUpdate(callbackData, nonAdminUser);

    assertNotNull(result);
    assertEquals(UserState.START, result.newState());
    verify(callbackSender).showPermissionDeniedCallback(any(), eq(callbackData.callbackId()));
    verify(appUserService, never()).removeTokenFromUser(anyLong());
  }
}
