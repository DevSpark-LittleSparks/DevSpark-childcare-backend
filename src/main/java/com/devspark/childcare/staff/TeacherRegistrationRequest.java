package com.devspark.childcare.staff;

import com.devspark.childcare.shared.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "teacher_registration_request")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLRestriction("deleted = false")
public class TeacherRegistrationRequest extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "request_id", columnDefinition = "CHAR(36)")
    private String requestId;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Designation designation;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status = RequestStatus.PENDING;


    public enum Designation {
        SENIOR, JUNIOR
    }

    public enum RequestStatus {
        PENDING, APPROVED, REJECTED
    }
}
