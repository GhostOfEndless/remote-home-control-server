package org.example.service.handlers;

import java.util.List;
import org.example.entity.AppUser;
import org.example.service.AppUserService;
import org.example.service.CallbackAnswerSender;
import org.example.service.MessageSender;
import org.example.service.UserRole;
import org.example.service.enums.ButtonTextCode;
import org.example.service.enums.MessageTextCode;
import org.example.service.enums.UserState;
import org.example.service.payload.CallbackData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UsersStateHandlerTest {

  @Mock
  private CallbackAnswerSender callbackSender;

  @Mock
  private MessageSender messageSender;

  @Mock
  private AppUserService appUserService;

  @InjectMocks
  private UsersStateHandler usersStateHandler;

  private AppUser appUser;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    appUser = new AppUser();
    appUser.setId(1L);
    appUser.setRole(UserRole.ADMIN);
  }

  @Test
  void testBuildMessagePayloadForUser_withUsers() {
    AppUser anotherUser = new AppUser();
    anotherUser.setId(2L);
    when(appUserService.findAllByTokenWithoutCurrent(appUser.getId(), appUser.getToken()))
        .thenReturn(List.of(anotherUser));

    var payload = usersStateHandler.buildMessagePayloadForUser(appUser, new Object[]{});

    verify(appUserService).findAllByTokenWithoutCurrent(appUser.getId(), appUser.getToken());
    assertNotNull(payload);
    assertEquals(MessageTextCode.USERS_MESSAGE, payload.messageText());
    assertTrue(payload.buttons().stream()
        .anyMatch(button -> button.code().equals(ButtonTextCode.USERS_BUTTON_ADDITION.name())));
    assertTrue(payload.buttons().stream()
        .anyMatch(button -> button.code().equals(ButtonTextCode.BUTTON_BACK.name())));
  }

  @Test
  void testBuildMessagePayloadForUser_noUsers() {
    when(appUserService.findAllByTokenWithoutCurrent(appUser.getId(), appUser.getToken()))
        .thenReturn(List.of());

    var payload = usersStateHandler.buildMessagePayloadForUser(appUser, new Object[]{});

    verify(appUserService).findAllByTokenWithoutCurrent(appUser.getId(), appUser.getToken());
    assertNotNull(payload);
    assertEquals(MessageTextCode.USERS_MESSAGE, payload.messageText());
    assertTrue(payload.buttons().stream()
        .anyMatch(button -> button.code().equals(ButtonTextCode.USERS_BUTTON_ADDITION.name())));
    assertTrue(payload.buttons().stream()
        .anyMatch(button -> button.code().equals(ButtonTextCode.BUTTON_BACK.name())));
  }

  @Test
  void testProcessCallbackButtonUpdate_buttonBack() {
    CallbackData callbackData = mock(CallbackData.class);
    when(callbackData.messageId()).thenReturn(1);
    when(callbackData.pressedButton()).thenReturn(ButtonTextCode.BUTTON_BACK);

    var result = usersStateHandler.processCallbackButtonUpdate(callbackData, appUser);

    assertEquals(UserState.HOME_CONTROL, result.newState());
    assertEquals(1, result.messageId());
  }

  @Test
  void testProcessCallbackButtonUpdate_usersButtonAddition_asAdmin() {
    CallbackData callbackData = mock(CallbackData.class);
    when(callbackData.messageId()).thenReturn(1);
    when(callbackData.pressedButton()).thenReturn(ButtonTextCode.USERS_BUTTON_ADDITION);

    var result = usersStateHandler.processCallbackButtonUpdate(callbackData, appUser);

    assertEquals(UserState.USER_ADDITION, result.newState());
    assertEquals(1, result.messageId());
  }

  @Test
  void testProcessCallbackButtonUpdate_usersButtonAddition_asUser() {
    appUser.setRole(UserRole.USER);

    CallbackData callbackData = mock(CallbackData.class);
    when(callbackData.messageId()).thenReturn(1);
    when(callbackData.pressedButton()).thenReturn(ButtonTextCode.USERS_BUTTON_ADDITION);

    var result = usersStateHandler.processCallbackButtonUpdate(callbackData, appUser);

    verify(callbackSender).showPermissionDeniedCallback(appUser.getLocale(), callbackData.callbackId());
    assertEquals(UserState.START, result.newState());
    assertEquals(1, result.messageId());
  }

  @Test
  void testProcessCallbackButtonUpdate_userButtonUser() {
    CallbackData callbackData = mock(CallbackData.class);
    when(callbackData.messageId()).thenReturn(1);
    when(callbackData.pressedButton()).thenReturn(ButtonTextCode.USER_BUTTON_USER);
    when(callbackData.getUserId()).thenReturn(2L);

    var result = usersStateHandler.processCallbackButtonUpdate(callbackData, appUser);

    assertEquals(UserState.USER, result.newState());
    assertEquals(1, result.messageId());
    assertEquals(2L, result.args()[0]);
  }

  @Test
  void testProcessCallbackButtonUpdate_unknownButton() {
    CallbackData callbackData = mock(CallbackData.class);
    when(callbackData.messageId()).thenReturn(1);
    when(callbackData.pressedButton()).thenReturn(ButtonTextCode.START_BUTTON_HOME_CONTROL);

    var result = usersStateHandler.processCallbackButtonUpdate(callbackData, appUser);

    assertEquals(UserState.USERS, result.newState());
    assertEquals(1, result.messageId());
  }
}
