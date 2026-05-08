package com.devspark.childcare.attendance.dto;

import com.devspark.childcare.attendance.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkAttendanceResponseDTO {
    private UUID id;
    private UUID childId;
    private AttendanceStatus status;
    private LocalDate date;
    private LocalTime checkIn;
    private LocalTime checkOut;
    private String notes;
}