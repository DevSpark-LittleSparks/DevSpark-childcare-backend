package com.devspark.childcare.auth;

import com.devspark.childcare.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SignupService signupService;

    @GetMapping("/me")
    public ApiResponse<UserDetails> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        return ApiResponse.success("User profile fetched successfully", userDetails);
    }

    @PostMapping("/forgot-password")
    public ApiResponse<String> forgotPassword(@RequestParam String email) {
        signupService.processForgotPassword(email);
        return ApiResponse.success("If an account exists for this email, a password reset link has been sent.", null);
    }
}
