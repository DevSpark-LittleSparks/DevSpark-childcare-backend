package com.devspark.childcare.comms.dto;

import com.devspark.childcare.comms.Notification;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SentAlertResponseDto {
    private String id;
    private String title;
    private String body;
    private Notification.Priority priority;
    private String targetLabel;
    private LocalDateTime createdAt;
}
