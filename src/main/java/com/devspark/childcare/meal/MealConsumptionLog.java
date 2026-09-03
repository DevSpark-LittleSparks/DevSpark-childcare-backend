package com.devspark.childcare.meal;

import com.devspark.childcare.shared.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "meal_consumption_log", uniqueConstraints = {
        @UniqueConstraint(name = "uk_consumption_child_menu_meal", columnNames = {"child_id", "menu_id", "meal_type"})
})
@SQLRestriction("deleted = false")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealConsumptionLog extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "consumption_id", updatable = false, nullable = false)
    private UUID consumptionId;

    @Column(name = "child_id", nullable = false)
    private UUID childId;

    @Column(name = "menu_id", nullable = false)
    private UUID menuId;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type", nullable = false)
    private MealType mealType;

    @Enumerated(EnumType.STRING)
    @Column(name = "consumption_status", nullable = false)
    private ConsumptionStatus consumptionStatus;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}