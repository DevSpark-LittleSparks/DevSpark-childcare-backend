package com.devspark.childcare.auth.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParentChildViewDto {
    private String childId;
    private String name;
    private String fullName;
    private LocalDate dob;
    private String gender;
    private String bloodGroup;
    private String height;
    private String weight;
    private String address;
    private String specialNote;
    private String profileImage;
    private String enrolledDate;
}
