package org.example.service.handlers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.example.entity.AppUser;
import org.example.service.CallbackAnswerSender;
import org.example.service.MessageSender;
import org.example.service.UserRole;
import org.example.service.enums.ButtonTextCode;
import org.example.service.enums.MessageTextCode;
import org.example.service.enums.UserState;
import org.example.service.payload.CallbackButtonPayload;
import org.example.service.payload.CallbackData;
import org.example.service.payload.MessagePayload;
import org.example.service.payload.ProcessingResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

class StartStateHandlerTest {

  @Mock
  private CallbackAnswerSender callbackSender;

  @Mock
  private MessageSender messageSender;

  @InjectMocks
  private StartStateHandler startStateHandler;

  private AppUser appUser;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    appUser = new AppUser();
    appUser.setRole(UserRole.USER);
    appUser.setLocale("en");
    appUser.setLastMessageId(123);
  }

  @Test
  void testBuildMessagePayloadForUser() {
    // Arrange
    Object[] args = new Object[]{};

    // Act
    MessagePayload payload = startStateHandler.buildMessagePayloadForUser(appUser, args);

    // Assert
    assertNotNull(payload);
    assertEquals(MessageTextCode.START_MESSAGE, payload.messageText());
    assertEquals(1, payload.buttons().size());
    assertEquals(ButtonTextCode.START_BUTTON_HOME_CONTROL.name(), payload.buttons().getFirst().code());
  }

  @Test
  void testProcessCallbackButtonUpdate_correctButton() {
    // Arrange
    CallbackData callbackData = mock(CallbackData.class);
    when(callbackData.pressedButton()).thenReturn(ButtonTextCode.START_BUTTON_HOME_CONTROL);
    when(callbackData.messageId()).thenReturn(123);

    // Act
    ProcessingResult result = startStateHandler.processCallbackButtonUpdate(callbackData, appUser);

    // Assert
    assertNotNull(result);
    assertEquals(UserState.HOME_CONTROL, result.newState());
    assertEquals(123, result.messageId());
  }

  @Test
  void testProcessCallbackButtonUpdate_incorrectButton() {
    // Arrange
    CallbackData callbackData = mock(CallbackData.class);
    when(callbackData.pressedButton()).thenReturn(ButtonTextCode.USERS_BUTTON_ADDITION);
    when(callbackData.messageId()).thenReturn(123);

    // Act
    ProcessingResult result = startStateHandler.processCallbackButtonUpdate(callbackData, appUser);

    // Assert
    assertNotNull(result);
    assertEquals(UserState.START, result.newState());
    assertEquals(123, result.messageId());
  }

  @Test
  void testGoToState() {
    // Arrange
    Integer messageId = 123;
    Object[] args = new Object[]{};
    MessagePayload expectedPayload = MessagePayload.create(
        MessageTextCode.START_MESSAGE,
        List.of(CallbackButtonPayload.create(ButtonTextCode.START_BUTTON_HOME_CONTROL))
    );

    // Act
    startStateHandler.goToState(appUser, messageId, args);

    // Assert
    ArgumentCaptor<MessagePayload> payloadCaptor = ArgumentCaptor.forClass(MessagePayload.class);
    verify(messageSender).updateUserMessage(eq(appUser), eq(messageId), payloadCaptor.capture(), eq(UserState.START));
    assertEquals(expectedPayload, payloadCaptor.getValue());
  }
}

