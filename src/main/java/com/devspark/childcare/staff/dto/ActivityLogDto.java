package com.devspark.childcare.staff.dto;
import lombok.Builder; import lombok.Data;
@Data @Builder
public class ActivityLogDto {
    private String logId;
    private String childName;
    private String childImage;
    private String logType;
    private String detail;
    private String time;
}