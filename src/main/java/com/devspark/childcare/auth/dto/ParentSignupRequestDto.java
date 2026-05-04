package com.devspark.childcare.auth.dto;

import lombok.Data;

@Data
public class ParentSignupRequestDto {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private String address;
    private String nic;
    private String relationship;
    
    // Child info from frontend
    private String childName;
    private String dob;
    private String gender;
}
