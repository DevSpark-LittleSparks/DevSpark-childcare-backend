package com.devspark.childcare.auth.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PendingRequestDto {
    private String requestId;
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private String status;
    private LocalDateTime submittedAt;
    private String extraInfo;
}
