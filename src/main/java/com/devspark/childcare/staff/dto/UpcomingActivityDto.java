package com.devspark.childcare.staff.dto;
import lombok.Builder; import lombok.Data;
@Data @Builder
public class UpcomingActivityDto {
    private String startTime;
    private String endTime;
    private String name;
    private String description;
    private String status;
}