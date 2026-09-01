package com.devspark.childcare.parent.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyEngagementDto {
    private String day;
    private String date;
    private List<DailyActivityHoursDto> activities;
}
