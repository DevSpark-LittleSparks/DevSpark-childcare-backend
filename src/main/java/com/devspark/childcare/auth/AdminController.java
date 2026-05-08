package com.devspark.childcare.auth;

import com.devspark.childcare.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth/admin")
@RequiredArgsConstructor
public class AdminController {

    private final SignupService signupService;
    private final com.devspark.childcare.child.ChildService childService;

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
    @GetMapping("/all-parents")
    @PreAuthorize("hasRole('ADMIN')")
    public com.devspark.childcare.shared.response.ApiResponse<java.util.List<?>> getAllParents() {
        return com.devspark.childcare.shared.response.ApiResponse.success("Parents fetched successfully", signupService.getAllParents());
    }

    @DeleteMapping("/parent/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> deleteParent(@PathVariable String id) {
        signupService.deleteParent(id);
        return ApiResponse.success("Parent deleted successfully", null);
    }

    @DeleteMapping("/child/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> deleteChild(@PathVariable String id) {
        signupService.deleteChild(id);
        return ApiResponse.success("Child deleted successfully", null);
    }

    @GetMapping("/child/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<com.devspark.childcare.child.dto.ChildResponseDto> getChildById(@PathVariable String id) {
        return ApiResponse.success("Child fetched successfully", childService.getChildById(java.util.UUID.fromString(id)));
    }

    @PutMapping("/child/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> updateChild(@PathVariable String id, @RequestBody com.devspark.childcare.child.dto.ChildResponseDto dto) {
        childService.updateChild(java.util.UUID.fromString(id), dto);
        return ApiResponse.success("Child updated successfully", null);
    }

    @GetMapping("/all-teachers")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<?>> getAllTeachers() {
        return ApiResponse.success("Teachers fetched successfully", signupService.getAllTeachers());
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<com.devspark.childcare.auth.dto.AdminStatsDto> getStats() {
        return ApiResponse.success("Stats fetched successfully", signupService.getAdminStats());
    }

    @GetMapping("/pending-teachers")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<com.devspark.childcare.auth.dto.PendingRequestDto>> getPendingTeachers() {
        return ApiResponse.success("Pending teachers fetched successfully", signupService.getPendingTeacherRequests());
    }

    @GetMapping("/pending-parents")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<com.devspark.childcare.auth.dto.PendingRequestDto>> getPendingParents() {
        return ApiResponse.success("Pending parents fetched successfully", signupService.getPendingParentRequests());
    }

    @GetMapping("/pending-directors")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<com.devspark.childcare.auth.dto.PendingRequestDto>> getPendingDirectors() {
        return ApiResponse.success("Pending directors fetched successfully", signupService.getPendingDirectorRequests());
    }

    @PostMapping("/approve-director/{requestId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> approveDirector(@PathVariable String requestId) {
        signupService.approveDirectorRequest(requestId);
        return ApiResponse.success("Director request approved and OTP sent.", null);
    }

    @PostMapping("/reject-teacher/{requestId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> rejectTeacher(@PathVariable String requestId, @RequestParam(required = false) String reason) {
        signupService.rejectTeacherRequest(requestId, reason);
        return ApiResponse.success("Teacher request rejected.", null);
    }

    @PostMapping("/reject-parent/{requestId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> rejectParent(@PathVariable String requestId, @RequestParam(required = false) String reason) {
        signupService.rejectParentRequest(requestId, reason);
        return ApiResponse.success("Parent request rejected.", null);
    }

    @PostMapping("/reject-director/{requestId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> rejectDirector(@PathVariable String requestId, @RequestParam(required = false) String reason) {
        signupService.rejectDirectorRequest(requestId, reason);
        return ApiResponse.success("Director request rejected.", null);
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<com.devspark.childcare.auth.dto.AdminProfileResponseDto> getProfile(java.security.Principal principal) {
        return ApiResponse.success("Admin profile fetched", signupService.getAdminProfile(principal.getName()));
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> updateProfile(java.security.Principal principal, @RequestBody com.devspark.childcare.auth.dto.AdminProfileResponseDto dto) {
        signupService.updateAdminProfile(principal.getName(), dto);
        return ApiResponse.success("Admin profile updated successfully", null);
    }

    @PostMapping("/change-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> changePassword(java.security.Principal principal, @RequestBody com.devspark.childcare.auth.dto.ChangePasswordRequestDto dto) {
        signupService.changePassword(principal.getName(), dto);
        return ApiResponse.success("Password updated successfully", null);
    }
}
