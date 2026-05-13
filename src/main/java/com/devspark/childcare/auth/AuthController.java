package com.devspark.childcare.auth;

import com.devspark.childcare.auth.dto.UserProfileResponseDto;
import com.devspark.childcare.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SignupService signupService;

    @GetMapping("/me")
    public ApiResponse<UserProfileResponseDto> getMe(Principal principal) {
        if (principal == null) {
            return ApiResponse.error("Not authenticated");
        }
        return ApiResponse.success("User profile fetched", signupService.getCurrentUserProfile(principal.getName()));
    }
}
