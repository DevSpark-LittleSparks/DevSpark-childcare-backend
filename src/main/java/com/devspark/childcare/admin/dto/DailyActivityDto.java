package com.devspark.childcare.admin.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyActivityDto {
    private String activityName;
    private String time;
    private String teacherName;
    private String teacherRole;
    private int studentCount;
    private String participationStatus;
}
