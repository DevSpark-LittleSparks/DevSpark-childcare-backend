package com.devspark.childcare.meal.dto;

import java.time.LocalDate;
import java.util.UUID;

public record MealMenuResponse(
        UUID menuId,
        LocalDate date,
        String breakfastDetails,
        String lunchDetails,
        String eveningSnackDetails
) {}