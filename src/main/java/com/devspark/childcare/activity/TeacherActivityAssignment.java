package com.devspark.childcare.activity;

import com.devspark.childcare.activity.enums.AssignmentStatus;
import com.devspark.childcare.shared.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Entity representing the 'teacher_activity_assignment' table in the database.
 * Follows the strict DevSpark Architecture rules (UUIDs, Soft Deletes, Audit).
 */
@Entity
@Table(name = "teacher_activity_assignment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLRestriction("deleted = false")
public class TeacherActivityAssignment extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "assignment_id", updatable = false, nullable = false)
    private UUID id;

    // Storing IDs directly to decouple from the Teacher module
    @Column(name = "teacher_id", nullable = false)
    private UUID teacherId;

    @Column(name = "activity_id", nullable = false)
    private UUID activityId;

    @Column(name = "assigned_date", nullable = false)
    private LocalDate assignedDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    // FIXED: Changed from PENDING to DRAFT to match the updated Master Plan Enum
    private AssignmentStatus status = AssignmentStatus.DRAFT;

    @Column(name = "current_load", nullable = false)
    @Builder.Default
    private int currentLoad = 0;

    @Column(name = "deleted", nullable = false)
    @Builder.Default
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}