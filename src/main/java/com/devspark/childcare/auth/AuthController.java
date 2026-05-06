package com.devspark.childcare.auth;

import com.devspark.childcare.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    @GetMapping("/me")
    public ApiResponse<UserDetails> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        return ApiResponse.success("User profile fetched successfully", userDetails);
    }
}
