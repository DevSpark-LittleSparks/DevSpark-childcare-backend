package com.devspark.childcare.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParentResponseDto {
    private UUID parentId;
    private String fullName;
    private String email;
    private String phone;
    private String nic;
    private String relationship;
    private String status;
    private String profilePic;
    private AccountDto account;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountDto {
        private String email;
        private String status;
    }
}
