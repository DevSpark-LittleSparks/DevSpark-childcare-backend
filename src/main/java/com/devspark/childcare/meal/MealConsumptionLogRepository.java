package com.devspark.childcare.meal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MealConsumptionLogRepository extends JpaRepository<MealConsumptionLog, UUID> {
    Optional<MealConsumptionLog> findByChildIdAndMenuIdAndMealTypeAndDeletedFalse(
            UUID childId, UUID menuId, MealType mealType);

    List<MealConsumptionLog> findByDateAndDeletedFalse(LocalDate date);
}