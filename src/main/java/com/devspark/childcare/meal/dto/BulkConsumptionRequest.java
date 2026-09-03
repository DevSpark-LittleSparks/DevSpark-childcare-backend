package com.devspark.childcare.meal.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * DTO for bulk saving/updating the entire classroom's meal logs at once.
 */
public record BulkConsumptionRequest(
        @NotEmpty(message = "Logs cannot be empty")
        @Valid List<ConsumptionLogRequest> logs
) {}