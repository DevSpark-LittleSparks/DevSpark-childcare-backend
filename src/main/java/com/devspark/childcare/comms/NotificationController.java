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
        List<NotificationTarget> personalTargets = notificationTargetRepository.findByTargetRefId(userUuid);
        List<NotificationTarget> roleBroadcasts = notificationTargetRepository.findByTargetTypeAndTargetRefIdIsNull(userRoleType);
        List<NotificationTarget> globalBroadcasts = notificationTargetRepository.findByTargetTypeAndTargetRefIdIsNull(NotificationTarget.TargetType.ALL);

        java.util.Set<NotificationTarget> allTargets = new java.util.HashSet<>();
        allTargets.addAll(personalTargets);
        allTargets.addAll(roleBroadcasts);
        allTargets.addAll(globalBroadcasts);

        List<com.devspark.childcare.comms.dto.NotificationResponseDto> response = allTargets.stream()
                .map(target -> {
                    return notificationRepository.findById(target.getNotificationId())
                            .map(notif -> com.devspark.childcare.comms.dto.NotificationResponseDto.builder()
                                    .id(notif.getNotificationId().toString())
                                    .title(notif.getTitle())
                                    .body(notif.getBody())
                                    .priority(notif.getPriority())
                                    .type(notif.getType())
                                    .isRead(notificationReadRepository.existsByAccountIdAndNotificationId(userUuid, notif.getNotificationId()))
                                    .createdAt(notif.getCreatedAt())
                                    .build());
                })
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .sorted(java.util.Comparator.comparing(com.devspark.childcare.comms.dto.NotificationResponseDto::getCreatedAt, java.util.Comparator.nullsLast(java.util.Comparator.reverseOrder())))
                .collect(Collectors.toList());

        return ApiResponse.success("Alerts fetched successfully", response);
    }

    @PutMapping("/{id}/read")
    public ApiResponse<Object> markAsRead(@PathVariable("id") String id, @RequestParam("userId") String userId) {
        System.out.println("DEBUG: markAsRead for ID: " + id + ", User: " + userId);
        
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
}
