package com.devspark.childcare.admin;

import com.devspark.childcare.admin.dto.DailyActivityDto;
import com.devspark.childcare.admin.dto.DailyProgressEntryDto;
import com.devspark.childcare.child.dto.ChildSummaryDto;
import com.devspark.childcare.parent.dto.AttendanceStatsResponseDto;
import com.devspark.childcare.parent.dto.DailyEngagementDto;
import com.devspark.childcare.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/progress/admin")
@RequiredArgsConstructor
public class AdminLearningController {

    private final AdminLearningService service;

    @GetMapping("/daily")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<DailyProgressEntryDto>> dailyProgress(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ApiResponse.success("OK", service.getDailyProgress(date));
    }

    @GetMapping("/activities")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<DailyActivityDto>> dailyActivities(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ApiResponse.success("OK", service.getDailyActivities(date));
    }

    @GetMapping("/children")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<ChildSummaryDto>> children() {
        return ApiResponse.success("OK", service.getAllChildren());
    }

    @GetMapping("/child/{childId}/engagement")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<DailyEngagementDto>> childEngagement(
            @PathVariable UUID childId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ApiResponse.success("OK", service.getChildEngagement(childId, from, to));
    }

    @GetMapping("/child/{childId}/attendance")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<AttendanceStatsResponseDto> childAttendance(
            @PathVariable UUID childId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ApiResponse.success("OK", service.getChildAttendance(childId, from, to));
    }
}
