package com.devspark.childcare.meal;

import com.devspark.childcare.meal.dto.BulkConsumptionRequest;
import com.devspark.childcare.meal.dto.ConsumptionLogResponse;
import com.devspark.childcare.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/meals/consumption")
@RequiredArgsConstructor
public class MealConsumptionController {

    private final MealConsumptionService service;

    @PostMapping("/bulk")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ROLE_ADMIN', 'STAFF', 'ROLE_STAFF', 'TEACHER', 'ROLE_TEACHER')")
    public ApiResponse<Void> submitBulkLogs(@Valid @RequestBody BulkConsumptionRequest request) {
        service.saveBulkLogs(request);
        return ApiResponse.<Void>success("Meal logs saved successfully", null);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ROLE_ADMIN', 'STAFF', 'ROLE_STAFF', 'TEACHER', 'ROLE_TEACHER')")
    public ApiResponse<List<ConsumptionLogResponse>> getLogsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<ConsumptionLogResponse> logs = service.getLogsForDate(date);
        return ApiResponse.<List<ConsumptionLogResponse>>success("Logs fetched successfully", logs);
    }
}