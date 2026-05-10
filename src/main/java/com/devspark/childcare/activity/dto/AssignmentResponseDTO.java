package com.devspark.childcare.activity.dto;

import com.devspark.childcare.activity.enums.AssignmentStatus;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * DTO for sending Assignment details back to the frontend.
 */
public record AssignmentResponseDTO(
        UUID id,
        UUID teacherId,
        UUID activityId,
        LocalDate assignedDate,
        LocalTime startTime,
        LocalTime endTime,
        AssignmentStatus status
) {}