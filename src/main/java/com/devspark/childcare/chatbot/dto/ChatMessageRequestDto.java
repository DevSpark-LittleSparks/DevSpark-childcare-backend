package com.devspark.childcare.chatbot.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChatMessageRequestDto {
    private String message;
    private ChatUserContextDto context;
    private List<ChatHistoryEntryDto> history;
}
