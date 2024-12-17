package org.example.service.handlers;

import org.example.entity.AppUser;
import org.example.entity.Token;
import org.example.service.AppUserService;
import org.example.service.CallbackAnswerSender;
import org.example.service.MessageSender;
import org.example.service.TokenGenerator;
import org.example.service.TokenService;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class TokenStateHandlerTest {

  @Mock
  private CallbackAnswerSender callbackSender;

  @Mock
  private MessageSender messageSender;

  @Mock
  private AppUserService appUserService;

  @Mock
  private TokenGenerator tokenGenerator;

  @Mock
  private TokenService tokenService;

  @InjectMocks
  private TokenStateHandler tokenStateHandler;

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
    String generatedToken = "new-token";
    Token savedToken = new Token(1L, generatedToken);

    when(tokenGenerator.generateToken()).thenReturn(generatedToken);
    when(tokenService.save(generatedToken)).thenReturn(savedToken);

    // Act
    MessagePayload payload = tokenStateHandler.buildMessagePayloadForUser(appUser, new Object[]{});

    // Assert
    assertNotNull(payload);
    assertEquals(MessageTextCode.TOKEN_MESSAGE_NEW_TOKEN, payload.messageText());
    assertEquals(1, payload.messageArgs().size());
    assertEquals(generatedToken, payload.messageArgs().getFirst().text());
    assertEquals(1, payload.messageArgs().size());
    assertEquals(ButtonTextCode.BUTTON_BACK.name(), payload.buttons().getFirst().code());

    verify(appUserService).update(appUser, savedToken, UserRole.ADMIN);
  }

  @Test
  void testBuildMessagePayloadForUser_existingToken() {
    // Arrange
    String existingToken = "existing-token";
    Token token = new Token(1L, existingToken);
    appUser.setToken(token);

    // Act
    MessagePayload payload = tokenStateHandler.buildMessagePayloadForUser(appUser, new Object[]{});

    // Assert
    assertNotNull(payload);
    assertEquals(MessageTextCode.TOKEN_MESSAGE_EXISTED_TOKEN, payload.messageText());
    assertEquals(1, payload.messageArgs().size());
    assertEquals(existingToken, payload.messageArgs().getFirst().text());
    assertEquals(1, payload.buttons().size());
    assertEquals(ButtonTextCode.BUTTON_BACK.name(), payload.buttons().getFirst().code());

    verifyNoInteractions(tokenGenerator, tokenService, appUserService);
  }

  @Test
  void testProcessCallbackButtonUpdate_backButton() {
    // Arrange
    CallbackData callbackData = mock(CallbackData.class);
    when(callbackData.pressedButton()).thenReturn(ButtonTextCode.BUTTON_BACK);
    when(callbackData.messageId()).thenReturn(123);

    // Act
    ProcessingResult result = tokenStateHandler.processCallbackButtonUpdate(callbackData, appUser);

    // Assert
    assertNotNull(result);
    assertEquals(UserState.HOME_CONTROL, result.newState());
    assertEquals(123, result.messageId());
  }

  @Test
  void testProcessCallbackButtonUpdate_defaultButton() {
    // Arrange
    CallbackData callbackData = mock(CallbackData.class);
    when(callbackData.pressedButton()).thenReturn(ButtonTextCode.USERS_BUTTON_ADDITION);
    when(callbackData.messageId()).thenReturn(123);

    // Act
    ProcessingResult result = tokenStateHandler.processCallbackButtonUpdate(callbackData, appUser);

    // Assert
    assertNotNull(result);
    assertEquals(UserState.TOKEN, result.newState());
    assertEquals(123, result.messageId());
  }
}
