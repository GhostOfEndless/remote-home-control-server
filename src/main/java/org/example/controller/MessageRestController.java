package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MessageRestController {

    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/send")
    public void sendMessage(@RequestBody String message) {
        messagingTemplate.convertAndSend("/topic/greetings", message);
    }
}
