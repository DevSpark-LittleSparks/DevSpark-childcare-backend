package com.devspark.childcare.parent.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceStatsResponseDto {
    private int presentDays;
    private int absentDays;
    private int halfDays;
    private double attendanceRate;
}
