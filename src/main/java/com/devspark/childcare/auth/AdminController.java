package com.devspark.childcare.auth;

import com.devspark.childcare.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth/admin")
@RequiredArgsConstructor
public class AdminController {

    private final SignupService signupService;

    @PostMapping("/approve-teacher/{requestId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> approveTeacher(@PathVariable String requestId) {
        signupService.approveTeacherRequest(requestId);
        return ApiResponse.success("Teacher request approved and OTP sent.", null);
    }

    @PostMapping("/approve-parent/{requestId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> approveParent(@PathVariable String requestId) {
        signupService.approveParentRequest(requestId);
        return ApiResponse.success("Parent request approved and OTP sent.", null);
    }
}
