package com.devspark.childcare.activity;

import com.devspark.childcare.activity.enums.ProgressLevel;
import com.devspark.childcare.shared.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing the 'activity_progress_log' table in the database.
 * Used by teachers to log the progress of a child for a specific activity assignment.
 */
@Entity
@Table(name = "activity_progress_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLRestriction("deleted = false")
public class ActivityProgressLog extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "log_id", updatable = false, nullable = false)
    private UUID id;

    // Storing IDs directly to keep it decoupled
    @Column(name = "assignment_id", nullable = false)
    private UUID assignmentId;

    @Column(name = "child_id", nullable = false)
    private UUID childId;

    @Enumerated(EnumType.STRING)
    @Column(name = "grading_level", nullable = false)
    @Builder.Default
    private ProgressLevel gradingLevel = ProgressLevel.PENDING;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "deleted", nullable = false)
    @Builder.Default
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}