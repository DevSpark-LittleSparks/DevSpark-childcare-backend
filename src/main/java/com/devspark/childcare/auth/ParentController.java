package com.devspark.childcare.auth;

import com.devspark.childcare.auth.dto.ChangePasswordRequestDto;
import com.devspark.childcare.auth.dto.ParentProfileResponseDto;
import com.devspark.childcare.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/parent")
@RequiredArgsConstructor
public class ParentController {

    private final ParentService parentService;
    private final SignupService signupService;

    @GetMapping("/profile")
    @PreAuthorize("hasRole('PARENT')")
    public ApiResponse<ParentProfileResponseDto> getProfile(Principal principal) {
        return ApiResponse.success("Profile fetched", parentService.getParentProfile(principal.getName()));
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('PARENT')")
    public ApiResponse<String> updateProfile(Principal principal, @RequestBody ParentProfileResponseDto dto) {
        parentService.updateParentProfile(principal.getName(), dto);
        return ApiResponse.success("Profile updated", null);
    }

    @PostMapping("/change-password")
    @PreAuthorize("hasRole('PARENT')")
    public ApiResponse<String> changePassword(Principal principal, @RequestBody ChangePasswordRequestDto dto) {
        signupService.changePassword(principal.getName(), dto);
        return ApiResponse.success("Password updated", null);
    }
}
