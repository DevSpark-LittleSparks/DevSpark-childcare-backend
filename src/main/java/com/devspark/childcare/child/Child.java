package com.devspark.childcare.child;

import com.devspark.childcare.shared.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;

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
    @Column(name = "child_id", columnDefinition = "CHAR(36)")
    private String childId;

    /**
     * Linked after parent completes signup and OTP verification.
     * Null until parent account is activated.
     */
    @Column(name = "parent_id", columnDefinition = "CHAR(36)")
    private String parentId;

    /**
     * Pre-registered by admin during admissions.
     * Used to validate parent signup requests — only this email can sign up for this child.
     */
    @Column(name = "guardian_email", nullable = false, length = 150)
    private String guardianEmail;

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

    public enum Gender {
        MALE, FEMALE, OTHER
    }
}
