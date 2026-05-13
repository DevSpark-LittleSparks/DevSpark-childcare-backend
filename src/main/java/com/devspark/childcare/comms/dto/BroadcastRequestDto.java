package com.devspark.childcare.comms.dto;

import com.devspark.childcare.comms.Notification;
import com.devspark.childcare.comms.NotificationTarget;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BroadcastRequestDto {
    private String title;
    private String body;
    private Notification.Priority priority;
    private NotificationTarget.TargetType targetType;
}
