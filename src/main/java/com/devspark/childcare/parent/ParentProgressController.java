package com.devspark.childcare.parent;

import com.devspark.childcare.parent.dto.AttendanceStatsResponseDto;
import com.devspark.childcare.parent.dto.DailyEngagementDto;
import com.devspark.childcare.parent.dto.ProgressStatsResponseDto;
import com.devspark.childcare.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/progress/parent")
@RequiredArgsConstructor
public class ParentProgressController {

    private final ParentProgressService service;

    @GetMapping("/stats/{childId}")
    @PreAuthorize("hasRole('PARENT')")
    public ApiResponse<ProgressStatsResponseDto> stats(
            Principal principal, @PathVariable UUID childId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ApiResponse.success("OK", service.getStats(principal.getName(), childId, from, to));
    }

    @GetMapping("/engagement/{childId}")
    @PreAuthorize("hasRole('PARENT')")
    public ApiResponse<List<DailyEngagementDto>> engagement(
            Principal principal, @PathVariable UUID childId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ApiResponse.success("OK", service.getEngagement(principal.getName(), childId, from, to));
    }

    @GetMapping("/attendance/{childId}")
    @PreAuthorize("hasRole('PARENT')")
    public ApiResponse<AttendanceStatsResponseDto> attendance(
            Principal principal, @PathVariable UUID childId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ApiResponse.success("OK", service.getAttendance(principal.getName(), childId, from, to));
    }
}
