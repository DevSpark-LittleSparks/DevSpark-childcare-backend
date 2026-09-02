package com.devspark.childcare.meal.dto;


import com.devspark.childcare.meal.ConsumptionStatus;
import com.devspark.childcare.meal.MealType;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for an individual child's meal tracking entry.
 */
public record ConsumptionLogRequest(
        @NotNull(message = "Child ID is required") UUID childId,
        @NotNull(message = "Menu ID is required") UUID menuId,
        @NotNull(message = "Meal type is required") MealType mealType,
        @NotNull(message = "Consumption status is required") ConsumptionStatus consumptionStatus,
        String note,
        @NotNull(message = "Date is required") LocalDate date
) {}