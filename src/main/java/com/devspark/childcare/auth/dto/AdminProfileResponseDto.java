package com.devspark.childcare.auth.dto;

import lombok.*;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminProfileResponseDto {
    private UUID adminId;
    private String fullName;
    private String email;
    private String profilePic;
    private String role;
    private String phone1;
    private String phone2;
    private String address;
    private String centerName;
    private String capacity;
}
