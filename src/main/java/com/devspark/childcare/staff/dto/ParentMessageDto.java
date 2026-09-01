package com.devspark.childcare.staff.dto;
import lombok.Builder; import lombok.Data;
@Data @Builder
public class ParentMessageDto {
    private String  parentName;
    private String  preview;
    private String  time;
    private boolean unread;
}