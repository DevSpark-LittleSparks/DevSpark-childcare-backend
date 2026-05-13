package com.devspark.childcare.comms;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.Optional;

public interface NotificationReadRepository extends JpaRepository<NotificationRead, UUID> {
    boolean existsByAccountIdAndNotificationId(UUID accountId, UUID notificationId);
    Optional<NotificationRead> findByAccountIdAndNotificationId(UUID accountId, UUID notificationId);
}
