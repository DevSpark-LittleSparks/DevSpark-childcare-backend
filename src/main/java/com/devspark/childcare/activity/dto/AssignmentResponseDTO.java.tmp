package com.devspark.childcare.activity.dto;

import com.devspark.childcare.activity.enums.AssignmentStatus;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

// Architecture Rule: Always map extra fields needed by UI into DTOs[cite: 3]
public record AssignmentResponseDTO(
        UUID id,
        UUID teacherId,
        String teacherName,   // 💡 අලුතින් එකතු කළා
        UUID activityId,
        String activityName,  // 💡 අලුතින් එකතු කළා
        LocalDate assignedDate,
        LocalTime startTime,
        LocalTime endTime,
        AssignmentStatus status
) {}