package org.example.service.payload;

import org.example.service.enums.ButtonTextCode;

public record CallbackData(
    Integer messageId,
    String callbackId,
    ButtonTextCode pressedButton,
    String[] args
) {
}
