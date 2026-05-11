package com.devspark.childcare.meal.dto;

import com.devspark.childcare.meal.enums.ConsumptionStatus;
import com.devspark.childcare.meal.enums.MealType;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

public record MealLogRequestDTO(

        @NotNull(message = "Child ID is required")
        UUID childId,

        @NotNull(message = "Date is required")
        LocalDate date,

        @NotNull(message = "Meal type is required")
        MealType mealType,

        @NotNull(message = "Consumption status is required")
        ConsumptionStatus consumptionStatus,

        // Note is optional, so no @NotNull validation needed
        String note
) {}