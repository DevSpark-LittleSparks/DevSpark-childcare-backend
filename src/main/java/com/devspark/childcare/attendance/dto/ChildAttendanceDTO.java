package com.devspark.childcare.attendance.dto;

import com.devspark.childcare.attendance.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChildAttendanceDTO {
    private UUID childId;
    private AttendanceStatus status;
    private LocalTime checkIn;
    private LocalTime checkOut;
    private String notes;
}