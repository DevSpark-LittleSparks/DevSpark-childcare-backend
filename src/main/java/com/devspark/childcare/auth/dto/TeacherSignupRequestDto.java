package com.devspark.childcare.auth.dto;

import lombok.Data;

@Data
public class TeacherSignupRequestDto {
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String designation; // SENIOR or JUNIOR
    private String password;
}
