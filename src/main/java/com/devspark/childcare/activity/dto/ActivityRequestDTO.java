package com.devspark.childcare.activity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for receiving Activity creation/update requests from the Frontend.
 * Uses Jakarta Validation to ensure data integrity before it reaches the Service layer.
 */
public record ActivityRequestDTO(

        @NotBlank(message = "Activity name is required")
        @Size(max = 150, message = "Activity name must not exceed 150 characters")
        String name,

        @NotBlank(message = "Category is required")
        @Size(max = 100, message = "Category must not exceed 100 characters")
        String category,

        String description,

        String materialsNeeded
) {}