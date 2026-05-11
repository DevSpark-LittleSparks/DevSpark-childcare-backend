package com.devspark.childcare.meal.repository;

import com.devspark.childcare.meal.entity.MealConsumptionLog;
import com.devspark.childcare.meal.enums.MealType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MealConsumptionLogRepository extends JpaRepository<MealConsumptionLog, UUID> {

    // Check if a log already exists for a child, on a specific date, for a specific meal
    Optional<MealConsumptionLog> findByChildIdAndDateAndMealType(UUID childId, LocalDate date, MealType mealType);

    // Find all meal logs for a specific date (for the teacher to get the list of children who ate on that day)
    List<MealConsumptionLog> findByDate(LocalDate date);
}
