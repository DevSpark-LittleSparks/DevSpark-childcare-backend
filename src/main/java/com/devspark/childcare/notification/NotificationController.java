package com.devspark.childcare.notification;

import com.devspark.childcare.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final com.devspark.childcare.auth.AccountRepository accountRepository;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<List<Notification>> getMyNotifications(Principal principal) {
        return accountRepository.findByEmail(principal.getName())
                .map(account -> ApiResponse.success("Notifications retrieved", 
                    notificationRepository.findByAccountAccountIdOrderByCreatedAtDesc(account.getAccountId())))
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    @GetMapping("/unread-count")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Long> getUnreadCount(Principal principal) {
        return accountRepository.findByEmail(principal.getName())
                .map(account -> ApiResponse.success("Unread count retrieved", 
                    notificationRepository.countByAccountAccountIdAndIsReadFalse(account.getAccountId())))
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    @PutMapping("/{id}/read")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<String> markAsRead(@PathVariable Long id) {
        notificationRepository.findById(id).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
        return ApiResponse.success("Notification marked as read", null);
    }
}
