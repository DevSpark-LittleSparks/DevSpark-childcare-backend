package com.devspark.childcare.meal.entity;

import com.devspark.childcare.shared.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "meal_menu")
@Getter
@Setter
@SQLRestriction("deleted = false")
public class MealMenu extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "menu_id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "date", nullable = false, unique = true)
    private LocalDate date;

    @Column(name = "breakfast_details", columnDefinition = "TEXT")
    private String breakfastDetails;

    @Column(name = "lunch_details", columnDefinition = "TEXT")
    private String lunchDetails;

    @Column(name = "evening_snack_details", columnDefinition = "TEXT")
    private String eveningSnackDetails;

    @Column(nullable = false)
    private boolean deleted = false;
    private java.time.LocalDateTime deletedAt;
}