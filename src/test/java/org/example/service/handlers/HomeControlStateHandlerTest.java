package org.example.service.handlers;

import org.example.entity.AppUser;
import org.example.entity.Token;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HomeControlStateHandlerTest {

  @Mock
  private CallbackAnswerSender callbackSender;

  @Mock
  private MessageSender messageSender;

  @InjectMocks
  private HomeControlStateHandler homeControlStateHandler;

  private AppUser appUser;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    appUser = new AppUser();
    appUser.setLocale("en");
    appUser.setLastMessageId(123);
  }

  @Test
  void testBuildMessagePayloadForUser_noToken() {
    // Arrange
    appUser.setToken(null);

    // Act
    MessagePayload payload = homeControlStateHandler.buildMessagePayloadForUser(appUser, new Object[]{});

    // Assert
    assertNotNull(payload);
    assertEquals(MessageTextCode.HOME_CONTROL_MESSAGE_TOKEN_NOT_FOUND, payload.messageText());
    assertEquals(2, payload.buttons().size());
    assertEquals(ButtonTextCode.HOME_CONTROL_BUTTON_GENERATE_TOKEN.name(), payload.buttons().get(0).code());
    assertEquals(ButtonTextCode.BUTTON_BACK.name(), payload.buttons().get(1).code());
  }

  @Test
  void testBuildMessagePayloadForUser_userRole() {
    // Arrange
    appUser.setToken(new Token(1L, "some-data"));
    appUser.setRole(UserRole.USER);

    // Act
    MessagePayload payload = homeControlStateHandler.buildMessagePayloadForUser(appUser, new Object[]{});

    // Assert
    assertNotNull(payload);
    assertEquals(MessageTextCode.HOME_CONTROL_MESSAGE_USER, payload.messageText());
    assertEquals(2, payload.buttons().size());
    assertEquals(ButtonTextCode.HOME_CONTROL_BUTTON_DEVICES.name(), payload.buttons().get(0).code());
    assertEquals(ButtonTextCode.BUTTON_BACK.name(), payload.buttons().get(1).code());
  }

  @Test
  void testBuildMessagePayloadForUser_adminRole() {
    // Arrange
    appUser.setToken(new Token(1L, "some-data"));
    appUser.setRole(UserRole.ADMIN);

    // Act
    MessagePayload payload = homeControlStateHandler.buildMessagePayloadForUser(appUser, new Object[]{});

    // Assert
    assertNotNull(payload);
    assertEquals(MessageTextCode.HOME_CONTROL_MESSAGE_ADMIN, payload.messageText());
    assertEquals(4, payload.buttons().size());
    assertEquals(ButtonTextCode.HOME_CONTROL_BUTTON_USERS.name(), payload.buttons().get(0).code());
    assertEquals(ButtonTextCode.HOME_CONTROL_BUTTON_DEVICES.name(), payload.buttons().get(1).code());
    assertEquals(ButtonTextCode.HOME_CONTROL_BUTTON_SHOW_TOKEN.name(), payload.buttons().get(2).code());
    assertEquals(ButtonTextCode.BUTTON_BACK.name(), payload.buttons().get(3).code());
  }

  @Test
  void testProcessCallbackButtonUpdate_backButton() {
    // Arrange
    CallbackData callbackData = mock(CallbackData.class);
    when(callbackData.pressedButton()).thenReturn(ButtonTextCode.BUTTON_BACK);
    when(callbackData.messageId()).thenReturn(123);

    // Act
    ProcessingResult result = homeControlStateHandler.processCallbackButtonUpdate(callbackData, appUser);

    // Assert
    assertNotNull(result);
    assertEquals(UserState.START, result.newState());
    assertEquals(123, result.messageId());
  }

  @Test
  void testProcessCallbackButtonUpdate_tokenButton() {
    // Arrange
    CallbackData callbackData = mock(CallbackData.class);
    when(callbackData.pressedButton()).thenReturn(ButtonTextCode.HOME_CONTROL_BUTTON_SHOW_TOKEN);
    when(callbackData.messageId()).thenReturn(123);

    // Act
    ProcessingResult result = homeControlStateHandler.processCallbackButtonUpdate(callbackData, appUser);

    // Assert
    assertNotNull(result);
    assertEquals(UserState.TOKEN, result.newState());
    assertEquals(123, result.messageId());
  }

  @Test
  void testProcessCallbackButtonUpdate_usersButton() {
    // Arrange
    CallbackData callbackData = mock(CallbackData.class);
    when(callbackData.pressedButton()).thenReturn(ButtonTextCode.HOME_CONTROL_BUTTON_USERS);
    when(callbackData.messageId()).thenReturn(123);

    // Act
    ProcessingResult result = homeControlStateHandler.processCallbackButtonUpdate(callbackData, appUser);

    // Assert
    assertNotNull(result);
    assertEquals(UserState.USERS, result.newState());
    assertEquals(123, result.messageId());
  }

  @Test
  void testProcessCallbackButtonUpdate_defaultButton() {
    // Arrange
    CallbackData callbackData = mock(CallbackData.class);
    when(callbackData.pressedButton()).thenReturn(ButtonTextCode.USER_BUTTON_REMOVE);
    when(callbackData.messageId()).thenReturn(123);

    // Act
    ProcessingResult result = homeControlStateHandler.processCallbackButtonUpdate(callbackData, appUser);

    // Assert
    assertNotNull(result);
    assertEquals(UserState.HOME_CONTROL, result.newState());
    assertEquals(123, result.messageId());
  }
}
