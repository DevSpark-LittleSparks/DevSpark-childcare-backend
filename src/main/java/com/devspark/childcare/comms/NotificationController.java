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
    private final com.devspark.childcare.auth.AccountRepository accountRepository;
    private final NotificationReadRepository notificationReadRepository;

    @GetMapping("/my-alerts/{userId}")
    public ApiResponse<List<com.devspark.childcare.comms.dto.NotificationResponseDto>> getMyAlerts(
            @PathVariable("userId") String userId,
            @RequestParam("role") String role) {
        
        System.out.println("DEBUG: getMyAlerts called with userId: " + userId + " (Type: " + userId.getClass().getName() + ")");
        
        // 0. Fetch Account by firebaseUid
        com.devspark.childcare.auth.Account account = accountRepository.findByFirebaseUid(userId)
                .orElse(null);
        
        if (account == null) {
            System.err.println("CRITICAL: Account not found for firebaseUid: " + userId);
            return ApiResponse.success("Account not found", new java.util.ArrayList<>());
        }

        UUID userUuid = account.getAccountId();
        NotificationTarget.TargetType userRoleType = NotificationTarget.TargetType.valueOf(role.toUpperCase());
        
        // 1. Fetch targets
        List<NotificationTarget> allTargets = notificationTargetRepository.findByTargetRefId(userUuid);
        
        List<Notification> alerts;
        if (account.getRole() == com.devspark.childcare.auth.Account.Role.ADMIN) {
            // Admins see all SYSTEM and ADMIN_REQUEST notifications
            alerts = notificationRepository.findAll().stream()
                    .filter(n -> n.getType() == Notification.Type.SYSTEM || n.getType() == Notification.Type.ADMIN_REQUEST)
                    .collect(java.util.stream.Collectors.toList());
        } else {
            // Others see broadcasts + targeted notifications
            List<Notification> globalBroadcasts = notificationRepository.findByType(Notification.Type.BROADCAST);
            alerts = allTargets.stream()
                    .map(target -> notificationRepository.findById(target.getNotificationId()).orElse(null))
                    .filter(java.util.Objects::nonNull)
                    .collect(java.util.stream.Collectors.toList());
            alerts.addAll(globalBroadcasts);
        }

        List<com.devspark.childcare.comms.dto.NotificationResponseDto> response = alerts.stream()
                .map(notif -> {
                    boolean alreadyRead = notificationReadRepository.existsByAccountIdAndNotificationId(userUuid, notif.getNotificationId());

                    return com.devspark.childcare.comms.dto.NotificationResponseDto.builder()
                            .id(notif.getNotificationId().toString())
                            .title(notif.getTitle())
                            .body(notif.getBody())
                            .priority(notif.getPriority())
                            .type(notif.getType())
                            .isRead(alreadyRead)
                            .createdAt(notif.getCreatedAt())
                            .build();
                })
                .sorted(java.util.Comparator.comparing(com.devspark.childcare.comms.dto.NotificationResponseDto::getCreatedAt, java.util.Comparator.nullsLast(java.util.Comparator.reverseOrder())))
                .collect(Collectors.toList());

        return ApiResponse.success("Alerts fetched successfully", response);
    }

    @PutMapping("/{id}/read")
    public ApiResponse<Object> markAsRead(@PathVariable("id") String id, @RequestParam("userId") String userId) {
        com.devspark.childcare.auth.Account account = accountRepository.findByFirebaseUid(userId).orElse(null);
        if (account != null) {
            UUID notificationUuid = UUID.fromString(id);

            if (!notificationReadRepository.existsByAccountIdAndNotificationId(account.getAccountId(), notificationUuid)) {
                NotificationRead readRecord = NotificationRead.builder()
                        .accountId(account.getAccountId())
                        .notificationId(notificationUuid)
                        .build();
                notificationReadRepository.save(readRecord);
            }
        }

        return ApiResponse.success("Marked as read successfully", null);
    }

    @PostMapping("/submit-request")
    public ApiResponse<String> submitAdminRequest(@RequestParam String userId, @RequestParam String type, @RequestBody String description) {
        com.devspark.childcare.auth.Account account = accountRepository.findByFirebaseUid(userId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        Notification requestNotif = Notification.builder()
                .title("Admin Request: " + type)
                .body("From: " + account.getEmail() + " (" + account.getRole() + ")\n\n" + description)
                .type(Notification.Type.ADMIN_REQUEST)
                .priority(Notification.Priority.NORMAL)
                .build();

        notificationRepository.save(requestNotif);
        return ApiResponse.success("Request submitted to administration", null);
    }
}
