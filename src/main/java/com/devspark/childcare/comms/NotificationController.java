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
    private final org.springframework.messaging.simp.SimpMessagingTemplate messagingTemplate;

    @GetMapping("/my-alerts/{userId}")
    public ApiResponse<org.springframework.data.domain.Page<com.devspark.childcare.comms.dto.NotificationResponseDto>> getMyAlerts(
            @PathVariable("userId") String userId,
            @RequestParam("role") String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        System.out.println("DEBUG: getMyAlerts called with userId: " + userId + " (Type: " + userId.getClass().getName() + ")");
        
        // 0. Fetch Account by firebaseUid
        com.devspark.childcare.auth.Account account = accountRepository.findByFirebaseUid(userId)
                .orElse(null);
        
        if (account == null) {
            System.err.println("CRITICAL: Account not found for firebaseUid: " + userId);
            return ApiResponse.success("Account not found", org.springframework.data.domain.Page.empty());
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

        org.springframework.data.domain.PageRequest pageRequest = org.springframework.data.domain.PageRequest.of(page, size);
        int start = Math.min((int)pageRequest.getOffset(), response.size());
        int end = Math.min((start + pageRequest.getPageSize()), response.size());
        org.springframework.data.domain.Page<com.devspark.childcare.comms.dto.NotificationResponseDto> pageResult = 
            new org.springframework.data.domain.PageImpl<>(response.subList(start, end), pageRequest, response.size());

        return ApiResponse.success("Alerts fetched successfully", pageResult);
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

    @PostMapping("/broadcast")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> broadcastNotification(@RequestBody com.devspark.childcare.comms.dto.BroadcastRequestDto dto) {
        // Create the notification
        Notification notification = Notification.builder()
                .title(dto.getTitle())
                .body(dto.getBody())
                .type(Notification.Type.BROADCAST)
                .priority(dto.getPriority() != null ? dto.getPriority() : Notification.Priority.NORMAL)
                .build();
        
        notification = notificationRepository.save(notification);

        if (dto.getTargetType() == NotificationTarget.TargetType.PARENT || dto.getTargetType() == NotificationTarget.TargetType.TEACHER) {
            // It's targeted, so convert type to SYSTEM, wait, type is BROADCAST, but we want it targeted
            // Actually let's just make it SYSTEM if it's targeted, so global broadcast logic doesn't pick it up
            notification.setType(Notification.Type.SYSTEM);
            notificationRepository.save(notification);

            com.devspark.childcare.auth.Account.Role targetRole = 
                dto.getTargetType() == NotificationTarget.TargetType.PARENT ? 
                com.devspark.childcare.auth.Account.Role.PARENT : 
                com.devspark.childcare.auth.Account.Role.TEACHER;

            List<com.devspark.childcare.auth.Account> targetAccounts = accountRepository.findByRole(targetRole);
            
            final UUID notifId = notification.getNotificationId();
            List<NotificationTarget> targetsToSave = targetAccounts.stream()
                .map(acc -> NotificationTarget.builder()
                    .notificationId(notifId)
                    .targetType(dto.getTargetType())
                    .targetRefId(acc.getAccountId())
                    .build())
                .collect(Collectors.toList());
            
            notificationTargetRepository.saveAll(targetsToSave);
        } else {
            // Type.BROADCAST applies to everyone automatically
        }

        // Push real-time alert
        com.devspark.childcare.comms.dto.NotificationResponseDto wsResponse = com.devspark.childcare.comms.dto.NotificationResponseDto.builder()
                .id(notification.getNotificationId().toString())
                .title(notification.getTitle())
                .body(notification.getBody())
                .priority(notification.getPriority())
                .type(notification.getType())
                .isRead(false)
                .createdAt(notification.getCreatedAt())
                .build();
                
        if (dto.getTargetType() == NotificationTarget.TargetType.ALL) {
            messagingTemplate.convertAndSend("/topic/broadcasts", wsResponse);
        } else {
            // For targeted broadcasts, ideally we send to a role-specific topic like /topic/parents
            messagingTemplate.convertAndSend("/topic/" + dto.getTargetType().name().toLowerCase() + "s", wsResponse);
        }

        return ApiResponse.success("Broadcast sent successfully", null);
    }
}
