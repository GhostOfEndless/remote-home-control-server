package org.example.handler;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

@NullMarked
@Slf4j
public class SessionHandler implements WebSocketHandler {

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("Connection established on session: {}", session.getId());
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String msg = (String) message.getPayload();
        log.info("Message: {}", message);
        session.sendMessage(new TextMessage("Started processing tutorial: " + session + " - " + msg));
        Thread.sleep(1000);
        session.sendMessage(new TextMessage("Completed processing tutorial: " + msg));
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.info("Exception occurred: {} on session: {}", exception.getMessage(), session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        log.info("Connection closed on session: {} with status: {}", session.getId(), closeStatus.getCode());
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
