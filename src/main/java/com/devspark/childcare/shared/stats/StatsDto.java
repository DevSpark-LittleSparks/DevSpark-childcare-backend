package com.devspark.childcare.shared.stats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatsDto {
    private long childrenEnrolled;
    private long expertStaff;
    private long happyFamilies;
    private long yearsOfExcellence;
}
