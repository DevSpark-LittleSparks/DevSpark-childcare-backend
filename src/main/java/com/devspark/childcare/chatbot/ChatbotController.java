package com.devspark.childcare.chatbot;

import com.devspark.childcare.chatbot.dto.ChatMessageRequestDto;
import com.devspark.childcare.chatbot.dto.ChatMessageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;

    @PostMapping("/message")
    public ChatMessageResponseDto sendMessage(@RequestBody ChatMessageRequestDto request) {
        return chatbotService.handle(request);
    }
}
