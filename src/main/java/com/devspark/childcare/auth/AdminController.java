package com.devspark.childcare.auth;

import com.devspark.childcare.auth.dto.PendingRequestDto;
import com.devspark.childcare.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/auth/admin")
@RequiredArgsConstructor
    private final SignupService signupService;
    private final AdminDashboardService adminDashboardService;

    // ─── Dashboard Stats & Broadcast ──────────────────────────────────────

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Map<String, Long>> getDashboardStats() {
        return ApiResponse.success("Dashboard statistics", adminDashboardService.getDashboardStats());
    }

    @PostMapping("/broadcast")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> broadcastAnnouncement(@RequestParam String title, @RequestParam String content) {
        adminDashboardService.broadcastAnnouncement(title, content);
        return ApiResponse.success("Announcement broadcasted successfully to all parents.", null);
    }


    // ─── Approve ─────────────────────────────────────────────────────────

    @PostMapping("/approve-teacher/{requestId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> approveTeacher(@PathVariable String requestId) {
        signupService.approveTeacherRequest(requestId);
        return ApiResponse.success("Teacher request approved. OTP sent to applicant email.", null);
    }

    @PostMapping("/approve-parent/{requestId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> approveParent(@PathVariable String requestId) {
        signupService.approveParentRequest(requestId);
        return ApiResponse.success("Parent request approved. OTP sent to applicant email.", null);
    }

    @PostMapping("/approve-director/{requestId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> approveDirector(@PathVariable String requestId) {
        signupService.approveDirectorRequest(requestId);
        return ApiResponse.success("Director request approved. OTP sent to applicant email.", null);
    }

    // ─── Reject ───────────────────────────────────────────────────────────

    @PostMapping("/reject-teacher/{requestId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> rejectTeacher(@PathVariable String requestId,
                                              @RequestParam(defaultValue = "Your application was not approved.") String reason) {
        signupService.rejectTeacherRequest(requestId, reason);
        return ApiResponse.success("Teacher request rejected.", null);
    }

    @PostMapping("/reject-parent/{requestId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> rejectParent(@PathVariable String requestId,
                                             @RequestParam(defaultValue = "Your application was not approved.") String reason) {
        signupService.rejectParentRequest(requestId, reason);
        return ApiResponse.success("Parent request rejected.", null);
    }

    @PostMapping("/reject-director/{requestId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> rejectDirector(@PathVariable String requestId,
                                               @RequestParam(defaultValue = "Your application was not approved.") String reason) {
        signupService.rejectDirectorRequest(requestId, reason);
        return ApiResponse.success("Director request rejected.", null);
    }

    // ─── Pending Requests ─────────────────────────────────────────────────

    @GetMapping("/pending-teachers")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<PendingRequestDto>> getPendingTeachers() {
        return ApiResponse.success("Pending teacher requests", signupService.getPendingTeacherRequests());
    }

    @GetMapping("/pending-parents")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<PendingRequestDto>> getPendingParents() {
        return ApiResponse.success("Pending parent requests", signupService.getPendingParentRequests());
    }

    @GetMapping("/pending-directors")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<PendingRequestDto>> getPendingDirectors() {
        return ApiResponse.success("Pending director requests", signupService.getPendingDirectorRequests());
    }
}

