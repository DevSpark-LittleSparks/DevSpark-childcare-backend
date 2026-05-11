package com.devspark.childcare.meal.entity;

import com.devspark.childcare.child.Child;
import com.devspark.childcare.meal.enums.ConsumptionStatus;
import com.devspark.childcare.meal.enums.MealType;
import com.devspark.childcare.shared.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "meal_consumption_log")
@Getter
@Setter
@SQLRestriction("deleted = false")
public class MealConsumptionLog extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "consumption_id", updatable = false, nullable = false)
    private UUID id;

    // Connect to the Child Entity to get child details
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id", nullable = false)
    private Child child;

    // Connect to the Menu (stored as menu_id in the Database)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private MealMenu mealMenu;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type", nullable = false)
    private MealType mealType;

    @Enumerated(EnumType.STRING)
    @Column(name = "consumption_status", nullable = false)
    private ConsumptionStatus consumptionStatus;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(nullable = false)
    private boolean deleted = false;
    private java.time.LocalDateTime deletedAt;
}