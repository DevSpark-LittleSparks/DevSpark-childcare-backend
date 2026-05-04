package com.devspark.childcare.child;

import com.devspark.childcare.auth.Account; // Wait, parent is linked to Account or Parent?
import com.devspark.childcare.auth.Account;
import com.devspark.childcare.auth.Account; // Let's check child SQL
// CONSTRAINT fk_child_parent FOREIGN KEY (parent_id) REFERENCES parent (parent_id)

import com.devspark.childcare.auth.Account; // I need to move Parent entity too if it's in auth
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
    @Column(name = "child_id", columnDefinition = "CHAR(36)")
    private String childId;

    // We'll need the Parent entity here. I'll move it to auth or guardian.
    // Let's assume it's in com.devspark.childcare.auth for now.
    
    @Column(name = "parent_id", columnDefinition = "CHAR(36)")
    private String parentId; // For now using ID to avoid circular dependency or missing class during move

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
