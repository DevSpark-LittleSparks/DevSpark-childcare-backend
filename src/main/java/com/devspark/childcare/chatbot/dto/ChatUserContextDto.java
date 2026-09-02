package com.devspark.childcare.chatbot.dto;

import lombok.Data;

@Data
public class ChatUserContextDto {
    private String role;
    private String name;
    private String currentPage;
}
