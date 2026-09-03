package com.devspark.childcare.meal.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;


public record WeeklyMenuRequest(
        @NotEmpty(message = "Weekly menu list cannot be empty")
        @Valid List<MealMenuCreateRequest> menus
) {}