package com.devspark.childcare.auth.dto;

import lombok.Data;

@Data
public class ParentSignupRequestDto {
    private String firstName;
    private String lastName;
    private String nic;
    private String email;
    private String phone;
    private String address;
    private String relationship; // MOTHER, FATHER, GUARDIAN
    private String password;
}
