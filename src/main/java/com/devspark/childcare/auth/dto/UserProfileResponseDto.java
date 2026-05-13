package com.devspark.childcare.auth.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponseDto {
    private String fullName;
    private String email;
    private String role;
    private String profilePic;
}
