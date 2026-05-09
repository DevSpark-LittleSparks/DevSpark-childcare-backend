package com.devspark.childcare.staff;

import com.devspark.childcare.auth.SignupService;
import com.devspark.childcare.auth.dto.ChangePasswordRequestDto;
import com.devspark.childcare.shared.response.ApiResponse;
import com.devspark.childcare.staff.dto.TeacherProfileResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/teacher")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;
    private final SignupService signupService;

    @GetMapping("/profile")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<TeacherProfileResponseDto> getProfile(Principal principal) {
        return ApiResponse.success("Profile fetched", teacherService.getTeacherProfile(principal.getName()));
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<String> updateProfile(Principal principal, @RequestBody TeacherProfileResponseDto dto) {
        teacherService.updateTeacherProfile(principal.getName(), dto);
        return ApiResponse.success("Profile updated", null);
    }

    @PostMapping("/change-password")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<String> changePassword(Principal principal, @RequestBody ChangePasswordRequestDto dto) {
        signupService.changePassword(principal.getName(), dto);
        return ApiResponse.success("Password updated", null);
    }
}
