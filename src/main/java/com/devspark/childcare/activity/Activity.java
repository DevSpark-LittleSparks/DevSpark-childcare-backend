package com.devspark.childcare.activity;

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
@Table(name = "activity") // V1 ---table name
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLRestriction("deleted = false") // README Rule: Automatically ignore soft-deleted records
public class Activity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    // CRITICAL: Name must exactly match the column in V1__initial_schema_full.sql
    @Column(name = "activity_id", updatable = false, nullable = false)
    private UUID id;

    // Maps to 'activity_name' in DB, but we use 'name' in Java for cleaner code
    @Column(name = "activity_name", nullable = false, length = 150)
    private String name;

    // Added via V9 migration
    @Column(name = "category", nullable = false, length = 100)
    private String category;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // Maps to 'required_items' in DB, but we use 'materialsNeeded' in Java
    @Column(name = "required_items", columnDefinition = "TEXT")
    private String materialsNeeded;

    // Soft delete columns required by the architecture
    @Column(name = "deleted", nullable = false)
    @Builder.Default
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}