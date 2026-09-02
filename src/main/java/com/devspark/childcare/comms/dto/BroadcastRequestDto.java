package com.devspark.childcare.comms.dto;

import com.devspark.childcare.comms.Notification;
import com.devspark.childcare.comms.NotificationTarget;
import lombok.*;

import java.util.UUID;

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

    // Set only when targeting a single recipient (targetType PARENT/TEACHER +
    // this account id); null means "everyone in targetType" (a group broadcast).
    private UUID targetRefId;
}
