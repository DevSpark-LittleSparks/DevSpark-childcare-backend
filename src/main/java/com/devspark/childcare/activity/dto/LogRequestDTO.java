package com.devspark.childcare.activity.dto;

import com.devspark.childcare.activity.enums.ProgressLevel;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * DTO for receiving a single progress log for a child.
 */
public record LogRequestDTO(
        @NotNull(message = "Child ID is required")
        UUID childId,

        @NotNull(message = "Progress level is required")
        ProgressLevel progressLevel,

        String note
) {}