package com.devspark.childcare.auth;

import com.devspark.childcare.auth.dto.PendingRequestDto;
import com.devspark.childcare.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.devspark.childcare.staff.Teacher;
import com.devspark.childcare.staff.TeacherRepository;
import java.util.List;
import java.util.Map;
import com.devspark.childcare.auth.Parent;
import com.devspark.childcare.auth.ParentRepository;


@RestController
@RequestMapping("/api/v1/auth/admin")
@RequiredArgsConstructor
public class AdminController {

    private final SignupService signupService;
    private final AdminDashboardService adminDashboardService;
    private final AdminProfileService adminProfileService;
    private final TeacherRepository teacherRepository;
    private final ParentRepository parentRepository;

    // ─── Profile Management ───────────────    @GetMapping("/profile")
    @PreAuthorize("hasRole('ADMIN')")
    public com.devspark.childcare.shared.response.ApiResponse<com.devspark.childcare.auth.dto.AdminProfileDto> getProfile(java.security.Principal principal) {
        return com.devspark.childcare.shared.response.ApiResponse.success("Admin profile retrieved", adminProfileService.getAdminProfile(principal.getName()));
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('ADMIN')")
    public com.devspark.childcare.shared.response.ApiResponse<String> updateProfile(@RequestBody com.devspark.childcare.auth.dto.AdminProfileDto dto, java.security.Principal principal) {
        adminProfileService.updateAdminProfile(principal.getName(), dto);
        return com.devspark.childcare.shared.response.ApiResponse.success("Admin profile updated successfully", null);
    }

    // ─── Dashboard Stats & Broadcast ──────────────────────────────────────

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public com.devspark.childcare.shared.response.ApiResponse<Map<String, Long>> getDashboardStats() {
        return com.devspark.childcare.shared.response.ApiResponse.success("Dashboard statistics", adminDashboardService.getDashboardStats());
    }

    @PostMapping("/broadcast")
    @PreAuthorize("hasRole('ADMIN')")
    public com.devspark.childcare.shared.response.ApiResponse<String> broadcastAnnouncement(@RequestParam String title, @RequestParam String content) {
        adminDashboardService.broadcastAnnouncement(title, content);
        return com.devspark.childcare.shared.response.ApiResponse.success("Announcement broadcasted successfully to all parents.", null);
    }

    // ─── Approve ─────────────────────────────────────────────────────────

    @PostMapping("/approve-teacher/{requestId}")
    @PreAuthorize("hasRole('ADMIN')")
    public com.devspark.childcare.shared.response.ApiResponse<String> approveTeacher(@PathVariable String requestId) {
        signupService.approveTeacherRequest(requestId);
        return com.devspark.childcare.shared.response.ApiResponse.success("Teacher request approved. OTP sent to applicant email.", null);
    }

    @PostMapping("/approve-parent/{requestId}")
    @PreAuthorize("hasRole('ADMIN')")
    public com.devspark.childcare.shared.response.ApiResponse<String> approveParent(@PathVariable String requestId) {
        signupService.approveParentRequest(requestId);
        return com.devspark.childcare.shared.response.ApiResponse.success("Parent request approved. OTP sent to applicant email.", null);
    }

    @PostMapping("/approve-director/{requestId}")
    @PreAuthorize("hasRole('ADMIN')")
    public com.devspark.childcare.shared.response.ApiResponse<String> approveDirector(@PathVariable String requestId) {
        signupService.approveDirectorRequest(requestId);
        return com.devspark.childcare.shared.response.ApiResponse.success("Director request approved. OTP sent to applicant email.", null);
    }

    // ─── Reject ───────────────────────────────────────────────────────────

    @PostMapping("/reject-teacher/{requestId}")
    @PreAuthorize("hasRole('ADMIN')")
    public com.devspark.childcare.shared.response.ApiResponse<String> rejectTeacher(@PathVariable String requestId,
                                              @RequestParam(defaultValue = "Your application was not approved.") String reason) {
        signupService.rejectTeacherRequest(requestId, reason);
        return com.devspark.childcare.shared.response.ApiResponse.success("Teacher request rejected.", null);
    }

    @PostMapping("/reject-parent/{requestId}")
    @PreAuthorize("hasRole('ADMIN')")
    public com.devspark.childcare.shared.response.ApiResponse<String> rejectParent(@PathVariable String requestId,
                                             @RequestParam(defaultValue = "Your application was not approved.") String reason) {
        signupService.rejectParentRequest(requestId, reason);
        return com.devspark.childcare.shared.response.ApiResponse.success("Parent request rejected.", null);
    }

    @PostMapping("/reject-director/{requestId}")
    @PreAuthorize("hasRole('ADMIN')")
    public com.devspark.childcare.shared.response.ApiResponse<String> rejectDirector(@PathVariable String requestId,
                                               @RequestParam(defaultValue = "Your application was not approved.") String reason) {
        signupService.rejectDirectorRequest(requestId, reason);
        return com.devspark.childcare.shared.response.ApiResponse.success("Director request rejected.", null);
    }

    // ─── Pending Requests ─────────────────────────────────────────────────

    @GetMapping("/pending-teachers")
    @PreAuthorize("hasRole('ADMIN')")
    public com.devspark.childcare.shared.response.ApiResponse<List<PendingRequestDto>> getPendingTeachers() {
        return com.devspark.childcare.shared.response.ApiResponse.success("Pending teacher requests", signupService.getPendingTeacherRequests());
    }

    @GetMapping("/pending-parents")
    @PreAuthorize("hasRole('ADMIN')")
    public com.devspark.childcare.shared.response.ApiResponse<List<PendingRequestDto>> getPendingParents() {
        return com.devspark.childcare.shared.response.ApiResponse.success("Pending parent requests", signupService.getPendingParentRequests());
    }

    @GetMapping("/pending-directors")
    @PreAuthorize("hasRole('ADMIN')")
    public com.devspark.childcare.shared.response.ApiResponse<List<PendingRequestDto>> getPendingDirectors() {
        return com.devspark.childcare.shared.response.ApiResponse.success("Pending director requests", signupService.getPendingDirectorRequests());
    }

    @GetMapping("/all-teachers")
    @PreAuthorize("hasRole('ADMIN')")
    public com.devspark.childcare.shared.response.ApiResponse<List<Teacher>> getAllTeachers() {
        return com.devspark.childcare.shared.response.ApiResponse.success("Fetched all teachers", teacherRepository.findAll());
    }

    @GetMapping("/all-parents")
    @PreAuthorize("hasRole('ADMIN')")
    public com.devspark.childcare.shared.response.ApiResponse<List<Parent>> getAllParents() {
        return com.devspark.childcare.shared.response.ApiResponse.success("Fetched all parents", parentRepository.findAll());
    }
}

