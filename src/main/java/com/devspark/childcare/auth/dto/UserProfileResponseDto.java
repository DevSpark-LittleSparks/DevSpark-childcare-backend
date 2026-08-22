package com.devspark.childcare.auth.dto;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponseDto {
    private String fullName;
    private String email;
    private String role;
    private String profilePic;
    private UUID accountId;
    private UUID parentId; // only set when role is PARENT
}
