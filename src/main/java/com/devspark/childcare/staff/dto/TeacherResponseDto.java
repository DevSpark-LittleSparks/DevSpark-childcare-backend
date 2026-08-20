package com.devspark.childcare.staff.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherResponseDto {
    private UUID teacherId;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private String status;
    private String phoneNumber;
    private String address;
    private LocalDateTime createdAt;
}
