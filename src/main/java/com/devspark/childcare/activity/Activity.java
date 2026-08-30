package com.devspark.childcare.activity;
import com.devspark.childcare.activity.enums.ActivityCategory;
import com.devspark.childcare.shared.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing the 'activity' table in the database.
 * Follows the strict DevSpark Architecture rules (UUIDs, Soft Deletes, Audit).
 */
@Entity
@Table(name = "activity")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLRestriction("deleted = false")
public class Activity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "activity_id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "activity_name", nullable = false, length = 150)
    private String name;

    // Modified to use Enum and added @Enumerated to map as String in DB
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 100)
    private ActivityCategory category;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "required_items", columnDefinition = "TEXT")
    private String materialsNeeded;

    /*@Column(name = "test", columnDefinition = "TEXT")
    private String test;*/

    @Column(name = "deleted", nullable = false)
    @Builder.Default
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}