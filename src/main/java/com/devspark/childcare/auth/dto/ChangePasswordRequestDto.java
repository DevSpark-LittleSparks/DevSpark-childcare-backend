package com.devspark.childcare.auth.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequestDto {
    private String currentPassword;
    private String newPassword;
}
