package com.devspark.childcare.staff.dto;
import lombok.Builder; import lombok.Data;
@Data @Builder
public class SafetyAlertDto {
    private String childName;
    private String condition;
}