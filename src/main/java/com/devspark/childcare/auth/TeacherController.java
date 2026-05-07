package com.devspark.childcare.auth;

import com.devspark.childcare.auth.dto.TeacherProfileDto;
import com.devspark.childcare.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/auth/teacher")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherProfileService teacherProfileService;

    @GetMapping("/profile")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<TeacherProfileDto> getProfile(Principal principal) {
        return ApiResponse.success("Teacher profile retrieved", teacherProfileService.getTeacherProfile(principal.getName()));
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<String> updateProfile(@RequestBody TeacherProfileDto dto, Principal principal) {
        teacherProfileService.updateTeacherProfile(principal.getName(), dto);
        return ApiResponse.success("Teacher profile updated successfully", null);
    }
}
