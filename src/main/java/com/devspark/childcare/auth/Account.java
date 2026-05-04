package com.devspark.childcare.auth;

import com.devspark.childcare.shared.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "account")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLRestriction("deleted = false")
public class Account extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "account_id", columnDefinition = "CHAR(36)")
    private String accountId;

    @Column(name = "firebase_uid", unique = true, length = 128)
    private String firebaseUid;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Builder.Default
    @Column(name = "is_verified", nullable = false)
    private boolean verified = false;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ACTIVE;

    @PrePersist
    public void prePersist() {
        if (accountId == null) {
            accountId = UUID.randomUUID().toString();
        }
    }

    public enum Role {
        ADMIN, TEACHER, PARENT
    }

    public enum Status {
        ACTIVE, INACTIVE, SUSPENDED
    }
}
