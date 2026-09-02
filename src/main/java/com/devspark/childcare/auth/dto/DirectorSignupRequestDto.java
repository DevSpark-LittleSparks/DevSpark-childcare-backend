package com.devspark.childcare.auth.dto;

import lombok.Data;

@Data
public class DirectorSignupRequestDto {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private String address;
    private String designation;
    private String centerAddress;
    private String branchName;
}
