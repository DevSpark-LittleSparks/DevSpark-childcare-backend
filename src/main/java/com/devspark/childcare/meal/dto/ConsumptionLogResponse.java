package com.devspark.childcare.meal.dto;

import com.devspark.childcare.meal.ConsumptionStatus;
import com.devspark.childcare.meal.MealType;
import java.util.UUID;

/**
 * Response DTO to send existing meal logs back to the frontend for editing.
 */
public record ConsumptionLogResponse(
        UUID childId,
        UUID menuId,
        MealType mealType,
        ConsumptionStatus consumptionStatus,
        String note
) {}