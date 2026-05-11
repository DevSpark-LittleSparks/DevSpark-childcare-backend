package com.devspark.childcare.comms;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification_read")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRead {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "read_id", updatable = false, nullable = false)
    private UUID readId;

    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Column(name = "notification_id", nullable = false)
    private UUID notificationId;

    @Column(name = "read_at", nullable = false)
    private LocalDateTime readAt;

    @PrePersist
    protected void onCreate() {
        readAt = LocalDateTime.now();
    }
}
