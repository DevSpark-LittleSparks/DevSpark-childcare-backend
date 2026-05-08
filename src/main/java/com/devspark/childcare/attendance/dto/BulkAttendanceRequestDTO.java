package com.devspark.childcare.attendance.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class BulkAttendanceRequestDTO {
    private LocalDate date;
    private UUID recordedBy;
    private List<ChildAttendanceDTO> attendances;
}