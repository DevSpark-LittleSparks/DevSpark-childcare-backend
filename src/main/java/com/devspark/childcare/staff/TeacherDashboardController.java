package com.devspark.childcare.staff;

import com.devspark.childcare.shared.response.ApiResponse;
import com.devspark.childcare.staff.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/teacher/dashboard")
@RequiredArgsConstructor
public class TeacherDashboardController {

    private final TeacherDashboardService service;

    @GetMapping("/class-status")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<ClassStatusDto> classStatus(Principal p) {
        return ApiResponse.success("OK", service.getClassStatus(p.getName()));
    }

    @GetMapping("/safety-alerts")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<List<SafetyAlertDto>> safetyAlerts(Principal p) {
        return ApiResponse.success("OK", service.getSafetyAlerts(p.getName()));
    }

    @GetMapping("/parent-messages")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<List<ParentMessageDto>> parentMessages(Principal p) {
        return ApiResponse.success("OK", service.getParentMessages(p.getName()));
    }

    @GetMapping("/upcoming-activities")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<List<UpcomingActivityDto>> upcomingActivities(Principal p) {
        return ApiResponse.success("OK", service.getUpcomingActivities(p.getName()));
    }

    @GetMapping("/activity-logs")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<List<ActivityLogDto>> activityLogs(
            Principal p,
            @RequestParam(required = false) String sortBy) {
        return ApiResponse.success("OK", service.getActivityLogs(p.getName(), sortBy));
    }
}