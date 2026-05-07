package com.devspark.childcare.auth.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherProfileDto {
    private String name;
    private String email;
    private String role;
    private String phone;
    private String address;
    private String bio;
    private String designation;
    private String profileImage;
}
