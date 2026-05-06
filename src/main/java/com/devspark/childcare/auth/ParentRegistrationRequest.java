package com.devspark.childcare.auth;

import com.devspark.childcare.shared.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "parent_registration_request")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLRestriction("deleted = false")
public class ParentRegistrationRequest extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "request_id", columnDefinition = "CHAR(36)")
    private String requestId;

    // Parent info
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false, length = 20)
    private String nic;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Relationship relationship;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    // Child info
    @Column(name = "child_first_name", length = 100)
    private String childFirstName;

    @Column(name = "child_last_name", length = 100)
    private String childLastName;

    @Column(name = "child_dob")
    private java.time.LocalDate childDob;

    @Enumerated(EnumType.STRING)
    @Column(name = "child_gender")
    private Gender childGender;

    @Column(name = "child_blood_group", length = 5)
    private String childBloodGroup;

    @Column(name = "child_weight")
    private java.math.BigDecimal childWeight;

    @Column(name = "child_height")
    private java.math.BigDecimal childHeight;

    @Column(name = "child_special_note", columnDefinition = "TEXT")
    private String childSpecialNote;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status = RequestStatus.PENDING;


    public enum Relationship {
        MOTHER, FATHER, GUARDIAN
    }

    public enum Gender {
        MALE, FEMALE, OTHER
    }

    public enum RequestStatus {
        PENDING, APPROVED, REJECTED
    }
}
