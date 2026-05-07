package com.devspark.childcare.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByAccountAccountIdOrderByCreatedAtDesc(String accountId);
    long countByAccountAccountIdAndIsReadFalse(String accountId);
}
