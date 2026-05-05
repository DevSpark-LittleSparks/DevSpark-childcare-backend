package com.devspark.childcare.auth.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Generic DTO for returning pending signup requests to the admin dashboard.
 * Works for teacher, parent, and director requests.
 */
@Data
@Builder
public class PendingRequestDto {

    private String requestId;
    private String fullName;
    private String email;
    private String phone;
    private String role;        // "TEACHER" | "PARENT" | "DIRECTOR"
    private String status;      // "PENDING" | "APPROVED" | "REJECTED"
    private LocalDateTime submittedAt;

    // Extra info shown in admin details panel
    private String extraInfo;   // e.g. experience for teacher, childName for parent, centerName for director
}
