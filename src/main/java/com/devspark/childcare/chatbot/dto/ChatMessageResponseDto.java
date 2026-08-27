package com.devspark.childcare.chatbot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatMessageResponseDto {
    private boolean success;
    private String reply;
}
