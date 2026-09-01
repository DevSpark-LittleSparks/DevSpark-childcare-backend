package com.devspark.childcare.parent.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyActivityHoursDto {
    private String activityName;
    private double hours;
}
