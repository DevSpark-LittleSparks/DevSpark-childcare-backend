package com.devspark.childcare.comms.dto;

import com.devspark.childcare.comms.Notification;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponseDto {
    private String id;
    private String title;
    private String body;
    private Notification.Priority priority;
    private Notification.Type type;
    private boolean isRead;
    private LocalDateTime createdAt;
}
