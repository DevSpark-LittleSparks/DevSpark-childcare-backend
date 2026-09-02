package com.devspark.childcare.chatbot.dto;

import lombok.Data;

@Data
public class ChatHistoryEntryDto {
    private String role;
    private String content;
}
