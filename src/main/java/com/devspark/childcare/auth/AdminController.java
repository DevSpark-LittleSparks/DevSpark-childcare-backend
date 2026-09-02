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
    private final com.devspark.childcare.comms.AnnouncementService announcementService;

    @PostMapping("/broadcast")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> broadcast(@RequestBody com.devspark.childcare.comms.dto.BroadcastRequestDto dto) {
        // Send global broadcast message
        announcementService.broadcast(dto);
        return ApiResponse.success("Announcement broadcasted successfully.", null);
    }

    @GetMapping("/alert-recipients")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<com.devspark.childcare.comms.dto.AlertRecipientDto>> getAlertRecipients() {
        return ApiResponse.success("Recipients fetched successfully", signupService.getAlertRecipients());
    }

    // 3. Approve Teacher
    @PostMapping("/approve-teacher/{requestId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> approveTeacher(@PathVariable String requestId) {
        signupService.approveTeacherRequest(requestId);
        return ApiResponse.success("Teacher request approved and OTP sent.", null);
    }

    // 4. Approve Parent
    @PostMapping("/approve-parent/{requestId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> approveParent(@PathVariable String requestId) {
        // Approve parent and trigger OTP
        signupService.approveParentRequest(requestId);
        return ApiResponse.success("Parent request approved and OTP sent.", null);
    }

    @GetMapping("/all-parents")
    @PreAuthorize("hasRole('ADMIN')")
    public com.devspark.childcare.shared.response.ApiResponse<org.springframework.data.domain.Page<com.devspark.childcare.auth.dto.ParentResponseDto>> getAllParents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return com.devspark.childcare.shared.response.ApiResponse.success("Parents fetched successfully",
                signupService.getAllParents(page, size));
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
        return ApiResponse.success("Child fetched successfully",
                childService.getChildById(java.util.UUID.fromString(id)));
    }

    @PutMapping("/child/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> updateChild(@PathVariable String id,
            @RequestBody com.devspark.childcare.child.dto.ChildResponseDto dto) {
        childService.updateChild(java.util.UUID.fromString(id), dto);
        return ApiResponse.success("Child updated successfully", null);
    }

    // 5. Get All Teachers
    @GetMapping("/all-teachers")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<org.springframework.data.domain.Page<com.devspark.childcare.staff.dto.TeacherResponseDto>> getAllTeachers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success("Teachers fetched successfully", signupService.getAllTeachers(page, size));
    }

    @GetMapping("/all-children")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<org.springframework.data.domain.Page<com.devspark.childcare.child.dto.ChildResponseDto>> getAllChildren(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success("Children fetched successfully", childService.getAllChildren(page, size));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<com.devspark.childcare.auth.dto.AdminStatsDto> getStats() {
        // Fetch dashboard summary stats
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
        // List pending parent requests
        return ApiResponse.success("Pending parents fetched successfully", signupService.getPendingParentRequests());
    }

    @GetMapping("/pending-directors")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<com.devspark.childcare.auth.dto.PendingRequestDto>> getPendingDirectors() {
        return ApiResponse.success("Pending directors fetched successfully",
                signupService.getPendingDirectorRequests());
    }

    @PostMapping("/approve-director/{requestId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> approveDirector(@PathVariable String requestId) {
        signupService.approveDirectorRequest(requestId);
        return ApiResponse.success("Director request approved and OTP sent.", null);
    }

    @PostMapping("/reject-teacher/{requestId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> rejectTeacher(@PathVariable String requestId,
            @RequestParam(required = false) String reason) {
        signupService.rejectTeacherRequest(requestId, reason);
        return ApiResponse.success("Teacher request rejected.", null);
    }

    @PostMapping("/reject-parent/{requestId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> rejectParent(@PathVariable String requestId,
            @RequestParam(required = false) String reason) {
        // Reject request with reason
        signupService.rejectParentRequest(requestId, reason);
        return ApiResponse.success("Parent request rejected.", null);
    }

    @PostMapping("/reject-director/{requestId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> rejectDirector(@PathVariable String requestId,
            @RequestParam(required = false) String reason) {
        signupService.rejectDirectorRequest(requestId, reason);
        return ApiResponse.success("Director request rejected.", null);
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<com.devspark.childcare.auth.dto.AdminProfileResponseDto> getProfile(
            java.security.Principal principal) {
        return ApiResponse.success("Admin profile fetched", signupService.getAdminProfile(principal.getName()));
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> updateProfile(java.security.Principal principal,
            @RequestBody com.devspark.childcare.auth.dto.AdminProfileResponseDto dto) {
        signupService.updateAdminProfile(principal.getName(), dto);
        return ApiResponse.success("Admin profile updated successfully", null);
    }

    @PostMapping("/change-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> changePassword(java.security.Principal principal,
            @RequestBody com.devspark.childcare.auth.dto.ChangePasswordRequestDto dto) {
        signupService.changePassword(principal.getName(), dto);
        return ApiResponse.success("Password updated successfully", null);
    }
}
