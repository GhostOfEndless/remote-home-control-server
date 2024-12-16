package org.example.service.handlers;

import java.util.List;
import java.util.Optional;
import org.example.entity.AppUser;
import org.example.entity.Token;
import org.example.service.AppUserService;
import org.example.service.CallbackAnswerSender;
import org.example.service.MessageSender;
import org.example.service.enums.ButtonTextCode;
import org.example.service.enums.MessageTextCode;
import org.example.service.enums.UserAdditionResult;
import org.example.service.enums.UserState;
import org.example.service.payload.CallbackData;
import org.example.service.payload.MessagePayload;
import org.example.service.payload.ProcessingResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserAdditionStateHandlerTest {

  @Mock
  private CallbackAnswerSender callbackSender;

  @Mock
  private MessageSender messageSender;

  @Mock
  private AppUserService appUserService;

  @InjectMocks
  private UserAdditionStateHandler userAdditionStateHandler;

  private AppUser appUser;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    appUser = new AppUser();
    appUser.setLocale("en");
    appUser.setLastMessageId(123);
  }

  @Test
  void testBuildMessagePayloadForUser_successAddition() {
    // Act
    MessagePayload payload = userAdditionStateHandler.buildMessagePayloadForUser(appUser, new Object[]{
        UserAdditionResult.SUCCESS});

    // Assert
    assertNotNull(payload);
    assertEquals(MessageTextCode.USER_ADDITION_MESSAGE_SUCCESS, payload.messageText());
    assertEquals(1, payload.buttons().size());
    assertEquals(ButtonTextCode.BUTTON_BACK.name(), payload.buttons().getFirst().code());
  }

  @Test
  void testBuildMessagePayloadForUser_userNotFound() {
    // Act
    MessagePayload payload = userAdditionStateHandler.buildMessagePayloadForUser(appUser, new Object[]{UserAdditionResult.USER_NOT_FOUND});

    // Assert
    assertNotNull(payload);
    assertEquals(MessageTextCode.USER_ADDITION_MESSAGE_USER_NOT_FOUND, payload.messageText());
    assertEquals(1, payload.buttons().size());
    assertEquals(ButtonTextCode.BUTTON_BACK.name(), payload.buttons().getFirst().code());
  }

  @Test
  void testBuildMessagePayloadForUser_userIsBusy() {
    // Act
    MessagePayload payload = userAdditionStateHandler.buildMessagePayloadForUser(appUser, new Object[]{UserAdditionResult.USER_IS_BUSY});

    // Assert
    assertNotNull(payload);
    assertEquals(MessageTextCode.USER_ADDITION_MESSAGE_USER_IS_BUSY, payload.messageText());
    assertEquals(1, payload.buttons().size());
    assertEquals(ButtonTextCode.BUTTON_BACK.name(), payload.buttons().getFirst().code());
  }

  @Test
  void testBuildMessagePayloadForUser_defaultAddition() {
    // Act
    MessagePayload payload = userAdditionStateHandler.buildMessagePayloadForUser(appUser, new Object[]{});

    // Assert
    assertNotNull(payload);
    assertEquals(MessageTextCode.USER_ADDITION_MESSAGE, payload.messageText());
    assertEquals(1, payload.buttons().size());
    assertEquals(ButtonTextCode.BUTTON_BACK.name(), payload.buttons().getFirst().code());
  }

  @Test
  void testProcessTextMessageUpdate_userNotFound() {
    // Arrange
    Message message = mock(Message.class);
    MessageEntity entity = mock(MessageEntity.class);
    when(entity.getType()).thenReturn("mention");
    when(entity.getText()).thenReturn("@unknownUser");
    when(message.getEntities()).thenReturn(List.of(entity));
    when(message.hasEntities()).thenReturn(true);
    when(appUserService.findByUsername("unknownUser")).thenReturn(Optional.empty());

    // Act
    ProcessingResult result = userAdditionStateHandler.processTextMessageUpdate(message, appUser);

    // Assert
    assertNotNull(result);
    assertEquals(UserState.USER_ADDITION, result.newState());
    verify(messageSender).updateUserMessage(eq(appUser), eq(0), any(MessagePayload.class), eq(UserState.USER_ADDITION));
  }

  @Test
  void testProcessTextMessageUpdate_userIsBusy() {
    // Arrange
    Message message = mock(Message.class);
    MessageEntity entity = mock(MessageEntity.class);
    when(entity.getType()).thenReturn("mention");
    when(entity.getText()).thenReturn("@busyUser");
    when(message.getEntities()).thenReturn(List.of(entity));
    when(message.hasEntities()).thenReturn(true);

    AppUser busyUser = new AppUser();
    busyUser.setToken(new Token(1L, "existingToken"));
    when(appUserService.findByUsername("busyUser")).thenReturn(Optional.of(busyUser));

    // Act
    ProcessingResult result = userAdditionStateHandler.processTextMessageUpdate(message, appUser);

    // Assert
    assertNotNull(result);
    assertEquals(UserState.USER_ADDITION, result.newState());
    verify(messageSender).updateUserMessage(eq(appUser), eq(0), any(MessagePayload.class), eq(UserState.USER_ADDITION));
  }

  @Test
  void testProcessTextMessageUpdate_successfulAddition() {
    // Arrange
    Message message = mock(Message.class);
    MessageEntity entity = mock(MessageEntity.class);
    when(entity.getType()).thenReturn("mention");
    when(entity.getText()).thenReturn("@newUser");
    when(message.getEntities()).thenReturn(List.of(entity));
    when(message.hasEntities()).thenReturn(true);

    AppUser newUser = new AppUser();
    newUser.setToken(null);
    when(appUserService.findByUsername("newUser")).thenReturn(Optional.of(newUser));

    // Act
    ProcessingResult result = userAdditionStateHandler.processTextMessageUpdate(message, appUser);

    // Assert
    assertNotNull(result);
    assertEquals(UserState.USER_ADDITION, result.newState());
    verify(appUserService).update(eq(newUser), eq(appUser.getToken()));
    verify(messageSender).updateUserMessage(eq(appUser), eq(0), any(MessagePayload.class), eq(UserState.USER_ADDITION));
  }

  @Test
  void testProcessCallbackButtonUpdate_backButton() {
    // Arrange
    CallbackData callbackData = mock(CallbackData.class);
    when(callbackData.pressedButton()).thenReturn(ButtonTextCode.BUTTON_BACK);
    when(callbackData.messageId()).thenReturn(123);

    // Act
    ProcessingResult result = userAdditionStateHandler.processCallbackButtonUpdate(callbackData, appUser);

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
    ProcessingResult result = userAdditionStateHandler.processCallbackButtonUpdate(callbackData, appUser);

    // Assert
    assertNotNull(result);
    assertEquals(UserState.USER_ADDITION, result.newState());
    assertEquals(123, result.messageId());
  }
}
