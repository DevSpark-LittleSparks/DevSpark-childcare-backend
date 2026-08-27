package com.devspark.childcare.parent.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressStatsResponseDto {
    private int daysPresent;
    private int activitiesCompleted;
    private String avgMood;
    private int level1Percent;
    private int level2Percent;
    private int level3Percent;
    private int level4Percent;
    private String mealsProvided;
    private int fullMealPercent;
    private int partialMealPercent;
    private int noMealPercent;
}
