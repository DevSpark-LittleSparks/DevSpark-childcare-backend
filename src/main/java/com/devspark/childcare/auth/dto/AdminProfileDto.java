package com.devspark.childcare.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminProfileDto {
    private String name;
    private String email;
    private String role;
    private String phone1;
    private String phone2;
    private String address;
    private String centerName;
    private String capacity;
    private String profileImage;
}
