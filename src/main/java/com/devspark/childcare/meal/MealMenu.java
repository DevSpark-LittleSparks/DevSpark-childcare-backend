package com.devspark.childcare.meal;

import com.devspark.childcare.shared.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing daily meal menus configured by the administrator.
 * Extends AuditableEntity to automatically track created/updated metadata[cite: 4].
 */
@Entity
@Table(name = "meal_menu")
@SQLRestriction("deleted = false") // Enforces soft-deletion architecture rule globally[cite: 4]
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealMenu extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "menu_id", updatable = false, nullable = false)
    private UUID menuId;

    @Column(nullable = false, unique = true)
    private LocalDate date;

    @Column(name = "breakfast_details")
    private String breakfastDetails;

    @Column(name = "lunch_details")
    private String lunchDetails;

    @Column(name = "evening_snack_details")
    private String eveningSnackDetails;

    @Column(nullable = false)
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}