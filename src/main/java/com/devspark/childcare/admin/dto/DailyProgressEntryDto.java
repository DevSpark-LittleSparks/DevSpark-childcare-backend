package com.devspark.childcare.admin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyProgressEntryDto {
    private String date;

    @JsonProperty("Excellent")
    private int excellent;

    @JsonProperty("Good")
    private int good;

    @JsonProperty("VeryGood")
    private int veryGood;

    @JsonProperty("Weak")
    private int weak;
}
