package com.devspark.childcare.activity.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

/**
 * DTO for receiving multiple progress logs at once when a teacher clicks "Save All".
 */
public record BatchLogRequestDTO(
        @NotNull(message = "Assignment ID is required")
        UUID assignmentId,

        @NotEmpty(message = "Log list cannot be empty")
        @Valid
        List<LogRequestDTO> logs
) {}