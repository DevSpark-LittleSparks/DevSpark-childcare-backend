package com.devspark.childcare.staff.dto;
import lombok.Builder; import lombok.Data;
@Data @Builder
public class ClassStatusDto {
    private int checkedIn;
    private int expected;
    private int checkedInPercent;
    private int expectedPercent;
}