package com.devspark.childcare.comms;

import com.devspark.childcare.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
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

        // Individual alerts addressed directly to this account.
        List<NotificationTarget> individualTargets = notificationTargetRepository.findByTargetRefId(userUuid);

        List<Notification> alerts;
        if (account.getRole() == com.devspark.childcare.auth.Account.Role.ADMIN) {
            // Admins see all SYSTEM and ADMIN_REQUEST notifications
            alerts = notificationRepository.findAll().stream()
                    .filter(n -> n.getType() == Notification.Type.SYSTEM || n.getType() == Notification.Type.ADMIN_REQUEST)
                    .collect(java.util.stream.Collectors.toList());
        } else {
            // Group broadcasts aimed at this role (or everyone) - matched via
            // NotificationTarget, not by scanning every BROADCAST notification,
            // so a "Parent Meetings" alert to ALL_STAFF never reaches parents.
            List<NotificationTarget> groupTargets = new java.util.ArrayList<>(
                    notificationTargetRepository.findByTargetTypeAndTargetRefIdIsNull(userRoleType));
            if (userRoleType != NotificationTarget.TargetType.ALL) {
                groupTargets.addAll(
                        notificationTargetRepository.findByTargetTypeAndTargetRefIdIsNull(NotificationTarget.TargetType.ALL));
            }

            alerts = java.util.stream.Stream.concat(groupTargets.stream(), individualTargets.stream())
                    .map(target -> notificationRepository.findById(target.getNotificationId()).orElse(null))
                    .filter(java.util.Objects::nonNull)
                    .filter(n -> n.getType() == Notification.Type.BROADCAST)
                    .distinct()
                    .collect(java.util.stream.Collectors.toList());
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

    @GetMapping("/sent")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<com.devspark.childcare.comms.dto.SentAlertResponseDto>> getSentAlerts() {
        List<Notification> broadcasts = notificationRepository.findByType(Notification.Type.BROADCAST);

        List<com.devspark.childcare.comms.dto.SentAlertResponseDto> response = broadcasts.stream()
                .map(notif -> {
                    NotificationTarget target = notificationTargetRepository
                            .findByNotificationId(notif.getNotificationId())
                            .stream().findFirst().orElse(null);

                    String targetLabel;
                    if (target == null) {
                        targetLabel = "Unknown";
                    } else if (target.getTargetRefId() != null) {
                        targetLabel = accountRepository.findById(target.getTargetRefId())
                                .map(com.devspark.childcare.auth.Account::getEmail)
                                .orElse("Unknown recipient");
                    } else {
                        targetLabel = switch (target.getTargetType()) {
                            case PARENT -> "All Parents";
                            case TEACHER -> "All Staff";
                            case ALL -> "Everyone";
                        };
                    }

                    return com.devspark.childcare.comms.dto.SentAlertResponseDto.builder()
                            .id(notif.getNotificationId().toString())
                            .title(notif.getTitle())
                            .body(notif.getBody())
                            .priority(notif.getPriority())
                            .targetLabel(targetLabel)
                            .createdAt(notif.getCreatedAt())
                            .build();
                })
                .sorted(java.util.Comparator.comparing(
                        com.devspark.childcare.comms.dto.SentAlertResponseDto::getCreatedAt,
                        java.util.Comparator.nullsLast(java.util.Comparator.reverseOrder())))
                .collect(Collectors.toList());

        return ApiResponse.success("Sent alerts fetched", response);
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
