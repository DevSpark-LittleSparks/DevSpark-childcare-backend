package com.devspark.childcare.comms;

import com.devspark.childcare.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final NotificationTargetRepository notificationTargetRepository;

    @GetMapping("/my-alerts/{userId}")
    public ApiResponse<List<String>> getMyAlerts(@PathVariable String userId) {
        UUID userUuid = UUID.fromString(userId);
        List<NotificationTarget> targets = notificationTargetRepository.findByTargetRefId(userUuid);
        
        List<String> messages = targets.stream()
                .map(target -> notificationRepository.findById(target.getNotificationId())
                        .map(Notification::getBody)
                        .orElse("Message not found"))
                .collect(Collectors.toList());

        return ApiResponse.success("Alerts fetched successfully", messages);
    }
}
