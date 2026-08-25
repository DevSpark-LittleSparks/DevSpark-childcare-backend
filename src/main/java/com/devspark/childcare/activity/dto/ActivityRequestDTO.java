package com.devspark.childcare.activity.dto;

import com.devspark.childcare.activity.enums.ActivityCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO for receiving Activity creation/update requests from the Frontend.
 * Uses Jakarta Validation to ensure data integrity.
 */
public record ActivityRequestDTO(

        @NotBlank(message = "Activity name is required")
        @Size(max = 150, message = "Activity name must not exceed 150 characters")
        String name,

        // Changed from String to ActivityCategory and added @NotNull
        @NotNull(message = "Category is required")
        ActivityCategory category,

        String description,

        String materialsNeeded
) {}