package com.devspark.childcare.meal.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record MealMenuCreateRequest(
        @NotNull(message = "Date is required")
        LocalDate date,

        String breakfastDetails,
        String lunchDetails,
        String eveningSnackDetails
) {}