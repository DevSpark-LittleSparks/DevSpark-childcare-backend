package com.devspark.childcare.activity.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * DTO for receiving Assignment requests from the Admin frontend.
 */
public record AssignmentRequestDTO(

        @NotNull(message = "Teacher ID is required")
        UUID teacherId,

        @NotNull(message = "Activity ID is required")
        UUID activityId,

        @NotNull(message = "Assigned date is required")
        LocalDate assignedDate,

        @NotNull(message = "Start time is required")
        LocalTime startTime,

        @NotNull(message = "End time is required")
        LocalTime endTime
) {}