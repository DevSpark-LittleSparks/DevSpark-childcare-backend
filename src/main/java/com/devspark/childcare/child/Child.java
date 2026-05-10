package com.devspark.childcare.child;

import com.devspark.childcare.shared.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "child")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLRestriction("deleted = false")
public class Child extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "child_id", updatable = false, nullable = false)
    private UUID childId; // Changed from String to UUID, removed CHAR(36)

    @Column(name = "parent_id")
    private UUID parentId; // This should also be UUID instead of String

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false)
    private LocalDate dob;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(name = "blood_group", length = 5)
    private String bloodGroup;

    @Column(precision = 5, scale = 2)
    private java.math.BigDecimal weight;

    @Column(precision = 5, scale = 2)
    private java.math.BigDecimal height;

    @Column(name = "special_note", columnDefinition = "TEXT")
    private String specialNote;

    @Column(name = "profile_pic", length = 500)
    private String profilePic;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChildStatus status = ChildStatus.ENROLLED;

    public enum Gender {
        MALE, FEMALE, OTHER
    }
}