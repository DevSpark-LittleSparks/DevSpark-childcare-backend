package com.devspark.childcare.staff.dto;

import lombok.*;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherProfileResponseDto {
    private UUID teacherId;
    private String fullName;
    private String email;
    private String profilePicture;
    private String role;
    private String designation;
    private String phone;
    private String address;
    private int maxDailyActivities;
}
